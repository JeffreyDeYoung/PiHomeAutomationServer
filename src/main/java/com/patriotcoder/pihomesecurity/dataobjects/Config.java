package com.patriotcoder.pihomesecurity.dataobjects;

/**
 * Class that represents the applications current configuration.
 * @author jeffrey
 */
public class Config
{
    /**
     * SMTP server to use for notifications.
     */
    private static String smtpServer;
    /**
     * SMTP port to use for notifications.
     */
    private static int smtpPort;
    /**
     * SMTP user to use for notifications.
     */
    private static String smtpUser;
    /**
     * SMTP password to use for notifications.
     */
    private static String smtpPassword;
    /**
     * Email addresses to send notifications to.
     */
    private static String[] emailto;

    /**
     * SMTP server to use for notifications.
     * @return the smtpServer
     */
    public static String getSmtpServer()
    {
        return smtpServer;
    }

    /**
     * SMTP server to use for notifications.
     * @param aSmtpServer the smtpServer to set
     */
    public void setSmtpServer(String aSmtpServer)
    {
        smtpServer = aSmtpServer;
    }

    /**
     * SMTP port to use for notifications.
     * @return the smtpPort
     */
    public static int getSmtpPort()
    {
        return smtpPort;
    }

    /**
     * SMTP port to use for notifications.
     * @param aSmtpPort the smtpPort to set
     */
    public void setSmtpPort(String aSmtpPort)
    {
        smtpPort = Integer.parseInt(aSmtpPort);
    }

    /**
     * SMTP user to use for notifications.
     * @return the smtpUser
     */
    public static String getSmtpUser()
    {
        return smtpUser;
    }

    /**
     * SMTP user to use for notifications.
     * @param aSmtpUser the smtpUser to set
     */
    public void setSmtpUser(String aSmtpUser)
    {
        smtpUser = aSmtpUser;
    }

    /**
     * SMTP password to use for notifications.
     * @return the smtpPassword
     */
    public static String getSmtpPassword()
    {
        return smtpPassword;
    }

    /**
     * SMTP password to use for notifications.
     * @param aSmtpPassword the smtpPassword to set
     */
    public void setSmtpPassword(String aSmtpPassword)
    {
        smtpPassword = aSmtpPassword;
    }

    /**
     * Email addresses to send notifications to.
     * @return the emailto
     */
    public static String[] getEmailto()
    {
        return emailto;
    }

    /**
     * Email addresses to send notifications to.
     * @param aEmailto the emailto to set
     */
    public void setEmailto(String[] aEmailto)
    {
        emailto = aEmailto;
    }
}
