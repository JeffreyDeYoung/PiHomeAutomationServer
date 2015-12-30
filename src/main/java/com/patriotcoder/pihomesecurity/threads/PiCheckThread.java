package com.patriotcoder.pihomesecurity.threads;

import com.patriotcoder.pihomesecurity.dataobjects.Pi;
import com.patriotcoder.pihomesecurity.notifiers.Notifier;
import com.patriotcoder.pihomesecurity.threads.abs.CheckerThread;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;

/**
 * Thread that constantly checks to confirm that the pi has not gone off line
 * (ie, make sure someone hasn't unplugged it to disable it as a security
 * device.)
 *
 * @author jeffrey
 */
public class PiCheckThread extends CheckerThread
{

    public Logger logger = Logger.getLogger(this.getClass());

    private Pi pi;

    private InetAddress inet = null;

    private Notifier notifier;

    public PiCheckThread(Pi pi, Notifier notifier)
    {
        super(5000, 3);
        this.pi = pi;
        this.notifier = notifier;
        try
        {
            inet = InetAddress.getByName(pi.getIp());
            logger.debug("Starting checking of: " + pi.getIp());
        } catch (UnknownHostException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean runCheck()
    {
        try
        {
            return inet.isReachable(5000);
        } catch (IOException e)
        {
            logger.info("Problem pinging server", e);
            return false;
        }
    }

    @Override
    public void triggerSensorProblem()
    {
        String message = "Could not connect to pi: " + pi.toString();
        logger.info(message);
        notifier.doNotify(message);
    }

    @Override
    public void triggerSensorRestored()
    {
        String message = "Connection restored to pi: " + pi.toString();
        logger.info(message);
        notifier.doNotify(message);
    }

    @Override
    public String getCheckName()
    {
        return "Checking connection to: " + pi.toString();
    }

}
