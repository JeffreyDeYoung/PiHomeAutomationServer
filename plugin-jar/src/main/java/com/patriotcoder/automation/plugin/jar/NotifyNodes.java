package com.patriotcoder.automation.plugin.jar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.mongodb.util.JSON;
import com.patriotcoder.pihomesecurity.Constants;
import com.pearson.docussandra.config.Configuration;
import com.pearson.docussandra.domain.objects.Document;
import com.pearson.docussandra.domain.objects.Query;
import com.pearson.docussandra.domain.objects.QueryResponseWrapper;
import com.pearson.docussandra.plugininterfaces.NotifierPlugin;
import com.pearson.docussandra.service.QueryService;
import java.io.IOException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.bson.BSONObject;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Docussandra plugin that notifies nodes of relevent changes. (Must be placed
 * in the home directory of the Docussandra user.)
 *
 * @author https://github.com/JeffreyDeYoung
 */
public class NotifyNodes extends NotifierPlugin
{

    /**
     * Logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(NotifyNodes.class);
    
    /**
     * Serializer.
     */
    private final ObjectReader r = new ObjectMapper().reader(ServerSideAction.class);

    @Override
    public void doNotify(MutateType type, Document document)
    {
        try
        {
            if (type.equals(MutateType.CREATE) || type.equals(MutateType.UPDATE))
            {
                if (document.getDatabaseName().equals(Constants.DB) && document.getTableName().equals(Constants.ACTOR_ABILITY_STATUS_TABLE))
                {
                    ServerSideAction action = (ServerSideAction) r.readValue(JSON.serialize(document.getObject()));
                    logger.debug("Actor: " + action.getActorName());
                    logger.debug("Ability: " + action.getAbilityName());
                    logger.debug("State: " + action.getState());
                    QueryService qs = Configuration.getQueryService();
                    
                    Query q = new Query();
                    q.setDatabase(Constants.DB);
                    q.setTable(Constants.NODES_TABLE);
                    q.setWhere("name = '"+action.getActorName()+"'");
                    QueryResponseWrapper res = qs.query(Constants.DB, q);
                    BSONObject actorBSON = res.get(0).getObject();
                    String ip = (String)actorBSON.get("ip");
                    logger.info("Need to notify: " + ip + " with action: " + action.toString());
                    JSONObject toPost = new JSONObject();
                    toPost.put("name", action.getAbilityName());
                    toPost.put("state", action.getState());
                    doPostCall("http://" + ip + ":8080/", toPost);
                    
                }
            }
        } catch (Exception e)
        {
            logger.error("Could not notify!", e);
        }
    }

    @Override
    public String getPluginName()
    {
        return "Node-Notifier";
    }
    
    /**
     * Does an HTTP POST call.
     *
     * @param url URL to post to.
     * @param toPost JSONObject of data to POST.
     * @throws Exception
     */
    private void doPostCall(String url, JSONObject toPost) throws Exception
    {
        logger.debug("Attempting to POST: " + url + ", payload: " + toPost.toJSONString());
        HttpPost request = new HttpPost(url);
        RequestConfig rc = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(5000).setConnectionRequestTimeout(5000).build();
        request.setConfig(rc);
        // add request header
        request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        //add auth if specified
//        if (config.getSecToken() != null)
//        {
//            request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + config.getSecToken());
//        }
        String responseString = "";
        try
        {
            //add the post data
            request.setEntity(new StringEntity(toPost.toJSONString()));
            HttpClient client = HttpClientBuilder.create().build();
            HttpResponse response = client.execute(request);
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode < 200 || responseCode >= 300)
            {
                throw new Exception("Error when doing a POST call agaist: " + url + ". JSON posted: " + toPost.toJSONString());
            }

        } catch (ParseException pe)
        {
            throw new Exception("Could not parse JSON: " + responseString, pe);
        } catch (IOException e)
        {
            throw new Exception("Problem contacting REST service for POST", e);
        } finally
        {
            request.reset();
        }

    }


}
