package com.patriotcoder.pihomesecurity.threads;

import com.docussandra.javasdk.Config;
import com.docussandra.javasdk.dao.TableDao;
import com.docussandra.javasdk.dao.impl.TableDaoImpl;
import com.docussandra.javasdk.exceptions.RESTException;
import com.patriotcoder.pihomesecurity.Constants;
import com.patriotcoder.pihomesecurity.dataobjects.PiHomeConfig;
import com.patriotcoder.pihomesecurity.notifiers.Notifier;
import com.patriotcoder.pihomesecurity.threads.abs.CheckerThread;
import com.patriotcoder.pihomesecurity.utils.PiHomeSecUtils;
import com.strategicgains.docussandra.domain.objects.Database;
import com.strategicgains.docussandra.domain.objects.Table;
import org.apache.log4j.Logger;

/**
 * Checks Docussandra to see if any nodes have changed their status.
 *
 * @author https://github.com/JeffreyDeYoung
 */
public class DocussandraCheckThread extends CheckerThread
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
     * Docussandra TableDao -- used as a basic check to confirm we can talk to
     * Docussandra.
     */
    private TableDao tableDao;

    /**
     * Constructor.
     *
     * @param docussandraUrl URl for Docussandra.
     * @param notifiers Notifiers to notify if there is a problem.
     */
    public DocussandraCheckThread(String docussandraUrl, Notifier[] notifiers)
    {
        super(10000, 3);
        this.docussandraUrl = docussandraUrl;
        this.notifiers = notifiers;
        Config docussandraConfig = new Config(PiHomeConfig.getDocussandraUrl());
        this.tableDao = new TableDaoImpl(docussandraConfig);
    }

    @Override
    public boolean runCheck()
    {
        Table t = new Table();
        t.database(new Database(Constants.DB));
        t.name(Constants.NODES_TABLE);
        try
        {
            return tableDao.exists(t.getId());//we can connect and the table exists or doesn't
        } catch (RESTException e)
        {
            return false;//we can't connect
        }
    }

    @Override
    public void triggerSensorProblem()
    {
        String message = "Could not connect to Docussandra (or database not setup correctly)!: " + docussandraUrl;
        logger.info(message);
        PiHomeSecUtils.doBulkNotify(notifiers, message);
    }

    @Override
    public void triggerSensorRestored()
    {
        String message = "Connection restored to Docussandra: " + docussandraUrl;
        logger.info(message);
        PiHomeSecUtils.doBulkNotify(notifiers, message);
    }

    @Override
    public String getCheckName()
    {
        return "Checking connection to Docussandra: " + docussandraUrl;
    }

}
