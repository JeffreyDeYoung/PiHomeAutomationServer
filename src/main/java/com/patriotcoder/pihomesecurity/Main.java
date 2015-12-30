package com.patriotcoder.pihomesecurity;

import com.patriotcoder.pihomesecurity.dataobjects.Config;
import com.patriotcoder.pihomesecurity.dataobjects.Pi;
import com.patriotcoder.pihomesecurity.notifiers.EmailNotifier;
import com.patriotcoder.pihomesecurity.notifiers.Notifier;
import com.patriotcoder.pihomesecurity.threads.PiCheckThread;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * Main class.
 *
 */
public class Main
{

    /**
     * Logger for this class.
     */
    public static Logger logger = Logger.getLogger(Main.class);

    /**
     * Main method. Only one argument expected; a path to a properties file.
     * Checks the arguments, then calls the constructor for this class.
     *
     * @param args A single path to the properties file used for this app.
     */
    public static void main(String[] args)
    {
        String startupMessage = "Starting PiHomeSecurity Server! With path to properties of: ";
        boolean props = true;
        if (args.length != 1)
        {
            startupMessage += "NONE. Will not start!";
            props = false;
        } else
        {
            startupMessage += args[0];
        }
        System.out.println(startupMessage);
        logger.info(startupMessage);
        if (!props)
        {
            System.exit(-1);
        }
        File propertiesFile = new File(args[0]);
        if (!propertiesFile.exists() || !propertiesFile.isFile())
        {
            String message = "Properties file at the path: " + args[0] + " does not exist. Application cannot start.";
            System.err.println(message);
            logger.error(message);
            System.exit(-1);
        }
        Main main = new Main(propertiesFile);
        main.run();
    }

    /**
     * File reference to the properties file for this class.
     */
    private final File propertiesFile;

    /**
     * Constructor.
     *
     * @param propertiesFile Properties file object for this app.
     */
    public Main(File propertiesFile)
    {
        this.propertiesFile = propertiesFile;
    }

    /**
     * Actually starts the application running.
     */
    public void run()
    {
        Config config;
        try
        {
            config = generateConfig(propertiesFile);
        } catch (IOException e)
        {
            logger.error("Could not start up application. Problem reading or parsing properties file.", e);
            e.printStackTrace();//generally not a good idea, but this would indicate a startup problem and we want to be verbose as possible.
            System.exit(-1);
        }

        Notifier[] notifiers = new Notifier[Config.getEmailTo().length];
        for(int i = 0; i < notifiers.length; i++){
            notifiers[i] = new EmailNotifier(Config.getEmailTo()[i], "PiHomeSec", Config.getSmtpServer(), Config.getSmtpPort(), Config.getSmtpUser(), Config.getSmtpPassword());
        }

        //n.doNotify("This is a test message.");
        Thread checkerThread = new PiCheckThread(new Pi("10.0.0.20", "First Pi"), notifiers);
        checkerThread.start();
    }

    /**
     * Generates the config object from a properties file.
     *
     * @param propertiesFile Properties file for this application.
     * @return A config object for this app.
     * @throws FileNotFoundException If the properties file cannot be found or
     * isn't readable. This probably shouldn't ever happen due to previous
     * checks.
     * @throws IOException If the properties file cannot be read for some
     * reason. This probably shouldn't ever happen due to previous checks.
     */
    private Config generateConfig(File propertiesFile) throws FileNotFoundException, IOException
    {
        Properties p = parseProperties(propertiesFile);
        Config config = new Config();
        config.setSmtpServer(p.getProperty("pi.sec.smtp.server"));
        config.setSmtpPort(p.getProperty("pi.sec.smtp.port"));
        config.setSmtpUser(p.getProperty("pi.sec.smtp.user"));
        config.setSmtpPassword(p.getProperty("pi.sec.smtp.password"));

        //get the email addresses and trim; removing any blanks
        List<String> emailTo = Arrays.asList(p.getProperty("pi.sec.email.to", "").split(","));
        int i = 0;
        for (Iterator<String> iterator = emailTo.iterator(); iterator.hasNext();)
        {
            String email = iterator.next();
            if (email.isEmpty())
            {
                // Remove the current element from the iterator and the list.
                iterator.remove();
            } else
            {
                //trim, keeping track of the current position
                email = email.trim();
                emailTo.set(i, email);
                i++;
            }
        }
        config.setEmailTo(emailTo.toArray(new String[emailTo.size()]));
        return config;

    }

    /**
     * Parses a property file from a File object.
     *
     * @param propertiesFile File to parse the properties from.
     * @return Properties object from the file.
     * @throws FileNotFoundException If the properties file cannot be found or
     * isn't readable. This probably shouldn't ever happen due to previous
     * checks.
     * @throws IOException If the properties file cannot be read for some
     * reason. This probably shouldn't ever happen due to previous checks.
     */
    private Properties parseProperties(File propertiesFile) throws FileNotFoundException, IOException
    {
        Properties toReturn = new Properties();
        InputStream propsInputStream = new FileInputStream(propertiesFile);
        toReturn.load(propsInputStream);
        return toReturn;
    }

}
