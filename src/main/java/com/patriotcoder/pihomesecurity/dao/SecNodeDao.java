package com.patriotcoder.pihomesecurity.dao;

import com.docussandra.javasdk.Config;
import com.docussandra.javasdk.SDKUtils;
import com.docussandra.javasdk.dao.QueryDao;
import com.docussandra.javasdk.dao.impl.QueryDaoImpl;
import com.docussandra.javasdk.domain.DocumentResponse;
import com.docussandra.javasdk.exceptions.RESTException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.patriotcoder.pihomesecurity.Constants;
import com.patriotcoder.pihomesecurity.dataobjects.PiHomeConfig;
import com.patriotcoder.pihomesecurity.dataobjects.SecNode;
import com.strategicgains.docussandra.domain.objects.Document;
import com.strategicgains.docussandra.domain.objects.Query;
import com.strategicgains.docussandra.domain.objects.QueryResponseWrapper;
import com.strategicgains.docussandra.exception.IndexParseException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.parser.ParseException;

/**
 * Dao for interacting with security nodes stored in Docussandra.
 *
 * @author jeffrey
 */
public class SecNodeDao
{

    /**
     * Dao for querying Docussandra.
     */
    private QueryDao queryDao;

    public SecNodeDao(String docussandraUrl)
    {
        Config docussandraConfig = new Config(PiHomeConfig.getDocussandraUrl());
        this.queryDao = new QueryDaoImpl(docussandraConfig);
    }

    /**
     * Gets the currently running nodes.
     *
     * @return A list of currently running sec nodes.
     * @throws IOException
     * @throws IndexParseException
     * @throws ParseException
     * @throws RESTException
     */
    public List<SecNode> getRunningNodes() throws IOException, IndexParseException, ParseException, RESTException
    {
        Query q = new Query();
        q.setTable(Constants.SENSOR_NODES_TABLE);
        q.setWhere("running = 'True'");
        QueryResponseWrapper qrw = queryDao.query(Constants.DB, q);
        if (!qrw.isEmpty())
        {
            ObjectReader secNodeReader = SDKUtils.getObjectMapper().reader(SecNode.class);
            List<SecNode> runningNodes = new ArrayList<>(qrw.size());
            for (Document d : qrw)
            {
                runningNodes.add(secNodeReader.readValue(d.objectAsString()));
            }
            return runningNodes;
        }
        return new ArrayList<>();//no results to return
    }

    /**
     * Gets the currently stopped nodes.
     *
     * @return A list of currently running sec nodes.
     * @throws IOException
     * @throws IndexParseException
     * @throws ParseException
     * @throws RESTException
     */
    public List<SecNode> getStoppedNodes() throws IOException, IndexParseException, ParseException, RESTException
    {
        Query q = new Query();
        q.setTable(Constants.SENSOR_NODES_TABLE);
        q.setWhere("running = 'False'");
        QueryResponseWrapper qrw = queryDao.query(Constants.DB, q);
        if (!qrw.isEmpty())
        {
            ObjectReader secNodeReader = SDKUtils.getObjectMapper().reader(SecNode.class);
            List<SecNode> stoppedNodes = new ArrayList<>(qrw.size());
            for (Document d : qrw)
            {
                stoppedNodes.add(secNodeReader.readValue(d.objectAsString()));
            }
            return stoppedNodes;
        }
        return new ArrayList<>();//no results to return
    }
}
