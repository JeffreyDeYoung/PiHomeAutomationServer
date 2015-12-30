package com.patriotcoder.pihomesecurity.notifiers;

/**
 * Notifier interface.
 * @author jeffrey
 */
public interface Notifier {
    
    /**
     * Sends a notification.
     * @param text Text of notification.
     */
    public void doNotify(String text);
    
}
