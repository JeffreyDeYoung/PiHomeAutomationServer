package com.patriotcoder.pihomesecurity.notifiers;

/**
 * Notifier interface.
 * 
 * @author https://github.com/JeffreyDeYoung
 */
public interface Notifier {

  /**
   * Sends a notification.
   * 
   * @param text Text of notification.
   */
  public void doNotify(String text);

}
