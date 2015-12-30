package com.patriotcoder.pihomesecurity.utils;

import com.patriotcoder.pihomesecurity.notifiers.Notifier;

/**
 * Utility class for this application.
 * @author jeffrey
 */
public class PiHomeSecUtils
{
    /**
     * Notifies an array of notifiers.
     * @param notifiers Notifiers to notify with the message.
     * @param message Message to tell the notifiers to send.
     */
    public static void doBulkNotify(Notifier[] notifiers, String message){
        for(Notifier n : notifiers){
            n.doNotify(message);
        }
    }
}
