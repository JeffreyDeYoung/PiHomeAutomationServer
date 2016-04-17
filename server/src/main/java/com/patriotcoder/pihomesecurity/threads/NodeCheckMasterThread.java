package com.patriotcoder.pihomesecurity.threads;

import com.docussandra.javasdk.Config;
import com.docussandra.javasdk.dao.impl.QueryDaoImpl;
import com.patriotcoder.pihomesecurity.dao.SecNodeDao;
import com.patriotcoder.pihomesecurity.dataobjects.PiHomeConfig;
import com.patriotcoder.pihomesecurity.notifiers.Notifier;
import com.patriotcoder.pihomesecurity.threads.abs.CheckerThread;
import com.patriotcoder.pihomesecurity.utils.PiHomeSecUtils;
import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 * Thread that constantly checks (via ping) to confirm that the nodes have not
 * gone off line (ie, make sure someone hasn't unplugged one/all of them to
 * disable them as a security device.)
 *
 * @author https://github.com/JeffreyDeYoung
 */
public class NodeCheckMasterThread extends CheckerThread
{

    /**
     * Logger for this class.
     */
    public Logger logger = Logger.getLogger(this.getClass());

    /**
     * URL for communicating to Docussandra via REST.
     */
    private String docussandraUrl;

    /**
     * Notifiers to notify in the event of a problem.
     */
    private Notifier[] notifiers;

    /**
     * Docussandra QueryDao -- used to check to see which nodes are currently
     * registered.
     */
    private SecNodeDao secNodeDao;
    
    /**
     * Map of checker threads that continously check 
     */
    private HashMap<String, NodeCheckThread> threads = new HashMap<>();

    /**
     * Singleton instance of this class.
     */
    private NodeCheckMasterThread instance = null;

    /**
     * Builder method for this singleton class. Use if you want to get an
     * instance of this class. (May return a new or existing object.)
     *
     * @param docussandraUrl URl for Docussandra.
     * @param notifiers Notifiers to notify if there is a problem.
     * @return An instance of NodeCheckMasterThread.
     */
    public NodeCheckMasterThread getInstance(String docussandraUrl, Notifier[] notifiers)
    {
        if (instance == null)
        {
            instance = new NodeCheckMasterThread(docussandraUrl, notifiers);
        }
        return instance;
    }

    /**
     * Private constructor (singleton).
     *
     * @param docussandraUrl URl for Docussandra.
     * @param notifiers Notifiers to notify if there is a problem.
     */
    private NodeCheckMasterThread(String docussandraUrl, Notifier[] notifiers)
    {
        super(5000, 3);
        this.docussandraUrl = docussandraUrl;
        this.notifiers = notifiers;
        this.secNodeDao = new SecNodeDao(docussandraUrl);
    }

    @Override
    public boolean runCheck()
    {
        throw new UnsupportedOperationException("Not done yet.");
    }

    @Override
    public void triggerSensorProblem()
    {
        String message = "Could not connect to Docussandra to get currently registered nodes!: " + docussandraUrl;
        logger.info(message);
        PiHomeSecUtils.doBulkNotify(notifiers, message);
    }

    @Override
    public void triggerSensorRestored()
    {
        String message = "Connection restored to Docussandra (for node querying): " + docussandraUrl;
        logger.info(message);
        PiHomeSecUtils.doBulkNotify(notifiers, message);
    }

    @Override
    public String getCheckName()
    {
        return "Resyncing node list: " + docussandraUrl;
    }
}
