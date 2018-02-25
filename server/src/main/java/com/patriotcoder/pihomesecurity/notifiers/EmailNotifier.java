package com.patriotcoder.pihomesecurity.notifiers;

import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.log4j.Logger;

/**
 * Notifier that notifies via email.
 *
 * @author https://github.com/JeffreyDeYoung
 */
public class EmailNotifier implements Notifier {

  public Logger logger = Logger.getLogger(this.getClass());

  private String to;

  private String from;

  private String host;

  private Properties properties;

  private String username;

  private String password;

  /**
   * Constructor.
   *
   * @param to Who to send the email to.
   * @param from Who the email should appear to be sent from (human readable;
   * not an address).
   * @param host SMTP host for the email.
   * @param port SMTP port for the email.
   * @param username SMTP username for the email.
   * @param password SMTP password for the email.
   */
  public EmailNotifier(String to, String from, String host, int port, String username,
          String password) {
    this.to = to;
    this.from = from;
    this.host = host;
    this.username = username;
    this.password = password;
    // Get system properties
    properties = System.getProperties();
    // Setup mail server
    properties.put("mail.smtp.host", "smtp.gmail.com");
    properties.put("mail.smtp.socketFactory.port", "465");
    properties.put("mail.smtp.socketFactory.class",
            "javax.net.ssl.SSLSocketFactory");
    properties.put("mail.smtp.auth", "true");
    properties.put("mail.smtp.port", "465");
  }

  /**
   * Sends a notification email.
   *
   * @param text Text of notification to send in the email body.
   */
  @Override
  public void doNotify(String text) {

    // prepend date for easy viewing.
    text = "[" + new Date().toString() + "] " + text;

    logger.debug("Sending email to: " + to + " with body: " + text);

    // Get the default Session object.
    Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
      }
    });
    try {
      // Create a default MimeMessage object.
      MimeMessage message = new MimeMessage(session);

      // Set From: header field of the header.
      message.setFrom(new InternetAddress(from));

      // Set To: header field of the header.
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

      // Set Subject: header field
      message.setSubject(text);

      // Now set the actual message
      message.setText(text);

      // Send message
      Transport.send(message);
      logger.debug("Sent message successfully...");
    } catch (MessagingException e) {
      logger.error("Could not send email", e);
    }

  }

}
