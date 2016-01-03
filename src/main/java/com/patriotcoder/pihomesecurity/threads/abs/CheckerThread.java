package com.patriotcoder.pihomesecurity.threads.abs;

import org.apache.log4j.Logger;

/**
 * Abstract Thread that checks the state of a child class and triggers an action
 * if needed.
 *
 * @author jeffrey
 */
public abstract class CheckerThread extends Thread {

    /**
     * How long between checks.
     */
    public final long retryTime;

    /**
     * Number of times a check will fail sequentially before it triggers.
     */
    public final long failuresBeforeTrigger;

    /**
     * Logger for this class.
     */
    public Logger logger = Logger.getLogger(this.getClass());

    /**
     * Constructor.
     *
     * @param retryTime How long between checks.
     * @param failuresBeforeTrigger Number of times a check will fail before it
     * triggers.
     */
    public CheckerThread(long retryTime, long failuresBeforeTrigger) {
        this.retryTime = retryTime;
        this.failuresBeforeTrigger = failuresBeforeTrigger;
    }

    @Override
    public void run() {
        logger.info("Starting check of: " + getCheckName());
        boolean currentSucessState = true;
        boolean alertTriggered = false;
        int failureCount = 0;
        while (true) {
            boolean res = runCheck();
            if (res) {
                if (!currentSucessState) {
                    logger.info(getCheckName() + " is back to normal.");
                    if (alertTriggered) {//don't send a restored unless we sent a failure
                        triggerSensorRestored();
                    }
                    alertTriggered = false;                    
                }
                currentSucessState = true;
                failureCount = 0;
            } else {
                failureCount++;
                logger.info(getCheckName() + " has failed: " + failureCount + " times.");
                if (!alertTriggered && failureCount >= failuresBeforeTrigger) {
                    alertTriggered = true;
                    triggerSensorProblem();
                }
                currentSucessState = false;
            }
            //wait and try again
            try {
                if (logger.isTraceEnabled()) {
                    logger.trace("Waiting " + retryTime + " before trying: " + getCheckName() + " again.");
                }
                Thread.sleep(retryTime);
            } catch (InterruptedException e) {;
            }
        }
    }

    /**
     * Runs a check on the status of a sensor.
     *
     * @return True if the check passed; false otherwise.
     */
    public abstract boolean runCheck();

    /**
     * Trigger response to a sensor failure.
     */
    public abstract void triggerSensorProblem();

    /**
     * Trigger response to a sensor that was previously failed, but is now
     * passing.
     */
    public abstract void triggerSensorRestored();

    /**
     * Name of the check that this is running.
     *
     * @return Human readable (preferably unique) string describing this test
     * briefly.
     */
    public abstract String getCheckName();

}
