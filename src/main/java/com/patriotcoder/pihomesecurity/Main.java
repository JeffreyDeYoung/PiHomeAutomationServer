package com.patriotcoder.pihomesecurity;

import com.docussandra.javasdk.Config;
import com.docussandra.javasdk.dao.DatabaseDao;
import com.docussandra.javasdk.dao.TableDao;
import com.docussandra.javasdk.dao.impl.DatabaseDaoImpl;
import com.docussandra.javasdk.dao.impl.TableDaoImpl;
import com.docussandra.javasdk.exceptions.RESTException;
import com.patriotcoder.pihomesecurity.dataobjects.PiHomeConfig;
import com.patriotcoder.pihomesecurity.dataobjects.Pi;
import com.patriotcoder.pihomesecurity.notifiers.EmailNotifier;
import com.patriotcoder.pihomesecurity.notifiers.Notifier;
import com.patriotcoder.pihomesecurity.threads.PiCheckThread;
import com.patriotcoder.pihomesecurity.utils.PiHomeSecUtils;
import com.strategicgains.docussandra.domain.objects.Database;
import com.strategicgains.docussandra.domain.objects.Identifier;
import com.strategicgains.docussandra.domain.objects.Table;
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
import org.json.simple.parser.ParseException;

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
        PiHomeConfig config;
        try
        {
            config = generateConfig(propertiesFile);
        } catch (IOException e)
        {
            logger.error("Could not start up application. Problem reading or parsing properties file.", e);
            e.printStackTrace();//generally not a good idea, but this would indicate a startup problem and we want to be verbose as possible.
            System.exit(-1);
        }

        Notifier[] notifiers = new Notifier[PiHomeConfig.getEmailTo().length];
        for (int i = 0; i < notifiers.length; i++)
        {
            notifiers[i] = new EmailNotifier(PiHomeConfig.getEmailTo()[i], "PiHomeSec", PiHomeConfig.getSmtpServer(), PiHomeConfig.getSmtpPort(), PiHomeConfig.getSmtpUser(), PiHomeConfig.getSmtpPassword());
        }

        try
        {
            setUpDocussandra();
        } catch (RESTException | ParseException | IOException e)
        {
            String errorMessage = "Problem connecting to or parsing response from Docussandra. Cannot start Pi Home Automation server application without this database access.";
            logger.error(errorMessage, e);
            PiHomeSecUtils.doBulkNotify(notifiers, errorMessage);
            System.err.println(errorMessage);
            System.exit(-1);
        }

        Thread checkerThread = new PiCheckThread(new Pi("10.0.0.20", "First Pi"), notifiers);
        checkerThread.start();
    }

    /**
     * Sets up Docussandra to be ready to accept data from this application.
     *
     * @throws RESTException If we can't connect to Docussandra.
     * @throws ParseException If we can't process the responses from
     * Docussandra. (Unlikely.)
     * @throws IOException If there is an IO problem connecting to Docussandra.
     */
    private void setUpDocussandra() throws RESTException, ParseException, IOException
    {
        //set up docussandra database (if not already established)
        Config docussandraConfig = new Config(PiHomeConfig.getDocussandraUrl());
        DatabaseDao dbDao = new DatabaseDaoImpl(docussandraConfig);
        Database db = new Database(Constants.DB);
        db.description("This is a database for storing information related to Pi Home Automation.");
        if (!dbDao.exists(db.getId()))
        {
            dbDao.create(db);
        }
        TableDao tbDao = new TableDaoImpl(docussandraConfig);
        Table sensorNodesTable = new Table();
        sensorNodesTable.database(db);
        sensorNodesTable.name(Constants.SENSOR_NODES_TABLE);
        sensorNodesTable.description("This table holds information about all of our sensor nodes for the Pi Home Automation Application.");
        if (!tbDao.exists(db, sensorNodesTable.getId()))
        {
            tbDao.create(db, sensorNodesTable);
        }
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
    private PiHomeConfig generateConfig(File propertiesFile) throws FileNotFoundException, IOException
    {
        Properties p = parseProperties(propertiesFile);
        PiHomeConfig config = new PiHomeConfig();
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
        config.setDocussandraUrl(p.getProperty("pi.sec.docussandra.url"));
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
