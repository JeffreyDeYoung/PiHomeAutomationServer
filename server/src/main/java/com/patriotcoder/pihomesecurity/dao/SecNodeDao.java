package com.patriotcoder.pihomesecurity.dao;

import com.ampliciti.db.docussandra.javasdk.Config;
import com.ampliciti.db.docussandra.javasdk.SDKUtils;
import com.ampliciti.db.docussandra.javasdk.dao.DocumentDao;
import com.ampliciti.db.docussandra.javasdk.dao.QueryDao;
import com.ampliciti.db.docussandra.javasdk.dao.impl.DocumentDaoImpl;
import com.ampliciti.db.docussandra.javasdk.dao.impl.QueryDaoImpl;
import com.ampliciti.db.docussandra.javasdk.exceptions.RESTException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.patriotcoder.pihomesecurity.Constants;
import com.patriotcoder.pihomesecurity.dataobjects.PiHomeConfig;
import com.patriotcoder.pihomesecurity.dataobjects.SecNode;
import com.patriotcoder.pihomesecurity.dataobjects.SecNodeWithId;
import com.pearson.docussandra.domain.objects.Document;
import com.pearson.docussandra.domain.objects.Query;
import com.pearson.docussandra.domain.objects.QueryResponseWrapper;
import com.pearson.docussandra.domain.objects.Table;
import com.pearson.docussandra.exception.IndexParseException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.parser.ParseException;

/**
 * Dao for interacting with security nodes stored in Docussandra.
 *
 * @author https://github.com/JeffreyDeYoung
 */
public class SecNodeDao
{

    /**
     * Dao for querying Docussandra.
     */
    private QueryDao queryDao;

    /**
     * Dao for interacting with Documents in Docussandra.
     */
    private DocumentDao documentDao;

    /**
     * Docussandra Table that we are storing our nodes in.
     */
    private final Table table;

    public SecNodeDao(String docussandraUrl)
    {
        Config docussandraConfig = new Config(docussandraUrl);
        this.queryDao = new QueryDaoImpl(docussandraConfig);
        this.documentDao = new DocumentDaoImpl(docussandraConfig);
        this.table = new Table();
        this.table.setDatabaseByString(Constants.DB);
        this.table.setName(Constants.NODES_TABLE);
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
        q.setDatabase(Constants.DB);
        q.setTable(table.getName());
        q.setWhere("running = 'True'");
        QueryResponseWrapper qrw = queryDao.query(q);
        if (!qrw.isEmpty())
        {
            ObjectReader secNodeReader = SDKUtils.getObjectMapper().reader(SecNode.class);
            List<SecNode> runningNodes = new ArrayList<>(qrw.size());
            for (Document d : qrw)
            {
                runningNodes.add(secNodeReader.readValue(d.getObjectAsString()));
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
        q.setDatabase(Constants.DB);
        q.setTable(table.getName());
        q.setWhere("running = 'False'");
        QueryResponseWrapper qrw = queryDao.query(q);
        if (!qrw.isEmpty())
        {
            ObjectReader secNodeReader = SDKUtils.getObjectMapper().reader(SecNode.class);
            List<SecNode> stoppedNodes = new ArrayList<>(qrw.size());
            for (Document d : qrw)
            {
                stoppedNodes.add(secNodeReader.readValue(d.getObjectAsString()));
            }
            return stoppedNodes;
        }
        return new ArrayList<>();//no results to return
    }

    public void saveNode(SecNode node) throws IOException, IndexParseException, ParseException, RESTException
    {
        if (node instanceof SecNodeWithId)//if it's comming in with an id already
        {
            updateNode((SecNodeWithId) node);//we know it exists already, so we will just update.
        } else//if it doesn't have an id, we don't know if it exists or not
        {
            SecNodeWithId nodeFetch = getNodeByName(node.getName());//so let's try to fetch
            if (nodeFetch != null)//if we get a response
            {
                //we can do an update (because it exists)
                SecNodeWithId toUpdate = (SecNodeWithId) node;//cast our node we are updating so we can set the id as well
                toUpdate.setId(nodeFetch.getId());//set the id from the fetch to the node
                updateNode(nodeFetch);//update the node
            } else//no response means this doesn't exist
            {
                createNode(node);//so we need to create
            }
        }

    }

    private void updateNode(SecNodeWithId node) throws IOException, ParseException, RESTException
    {
        Document doc = new Document();
        doc.setObjectAsString(SDKUtils.createJSON(node));
        doc.setUuid(node.getId());
        documentDao.update(doc);
    }

    private void createNode(SecNode node) throws IOException, ParseException, RESTException
    {
        Document doc = new Document();
        doc.setObjectAsString(SDKUtils.createJSON(node));
        documentDao.create(table, doc);
    }

    public SecNodeWithId getNodeByName(String nodeName) throws IOException, IndexParseException, ParseException, RESTException
    {
        Query q = new Query();
        q.setDatabase(Constants.DB);
        q.setTable(Constants.NODES_TABLE);
        q.setDatabase(Constants.DB);
        q.setWhere("name = '" + nodeName + "'");
        QueryResponseWrapper qrw = queryDao.query(q);
        if (qrw.isEmpty())
        {
            return null;
        } else
        {
            ObjectReader secNodeReader = SDKUtils.getObjectMapper().reader(SecNodeWithId.class);
            SecNodeWithId toReturn = secNodeReader.readValue(qrw.get(0).getObjectAsString());
            toReturn.setId(qrw.get(0).getUuid());
            return toReturn;
        }
    }
}
