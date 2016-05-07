package com.patriotcoder.pihomesecurity;

import com.docussandra.javasdk.Config;
import com.docussandra.javasdk.dao.DatabaseDao;
import com.docussandra.javasdk.dao.IndexDao;
import com.docussandra.javasdk.dao.TableDao;
import com.docussandra.javasdk.dao.impl.DatabaseDaoImpl;
import com.docussandra.javasdk.dao.impl.IndexDaoImpl;
import com.docussandra.javasdk.dao.impl.TableDaoImpl;
import com.docussandra.testhelpers.TestDocussandraManager;
import com.patriotcoder.pihomesecurity.dataobjects.PiHomeConfig;
import com.pearson.docussandra.domain.objects.Identifier;
import com.pearson.docussandra.domain.objects.IndexIdentifier;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * Unit test for simple App.
 */
public class MainTest
        extends TestCase
{

    private Config config;
    PiHomeConfig piConfig;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MainTest(String testName) throws Exception
    {
        super(testName);
        TestDocussandraManager.getManager().ensureTestDocussandraRunning(true);
        String docussandraUrl = "http://localhost:19080/";
        config = new Config(docussandraUrl);
        piConfig = new PiHomeConfig();
        piConfig.setDocussandraUrl(docussandraUrl);
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {
    }

    /**
     * Test of setUpDocussandra method, of class Main.
     */
    @org.junit.Test
    public void testSetUpDocussandra() throws Exception
    {
        System.out.println("setUpDocussandra");
        Main.setUpDocussandra(piConfig);
        DatabaseDao dbDao = new DatabaseDaoImpl(config);
        TableDao tbDao = new TableDaoImpl(config);
        IndexDao indexDao = new IndexDaoImpl(config);
        assertTrue(dbDao.exists(new Identifier(Constants.DB)));
        assertTrue(tbDao.exists(new Identifier(Constants.DB, Constants.ACTOR_ABILITY_STATUS_TABLE)));
        assertTrue(tbDao.exists(new Identifier(Constants.DB, Constants.NODES_TABLE)));
        assertTrue(indexDao.exists(new IndexIdentifier(Constants.DB, Constants.NODES_TABLE, Constants.NODES_TABLE_NAME_INDEX)));
        assertTrue(indexDao.exists(new IndexIdentifier(Constants.DB, Constants.NODES_TABLE, Constants.NODES_TABLE_RUNNING_INDEX)));
        assertTrue(indexDao.exists(new IndexIdentifier(Constants.DB, Constants.ACTOR_ABILITY_STATUS_TABLE, Constants.ACTOR_ABILITY_STATUS_NAME_INDEX)));

        Main.setUpDocussandra(piConfig);//run it again to make sure our results are the same     
        assertTrue(dbDao.exists(new Identifier(Constants.DB)));
        assertTrue(tbDao.exists(new Identifier(Constants.DB, Constants.ACTOR_ABILITY_STATUS_TABLE)));
        assertTrue(tbDao.exists(new Identifier(Constants.DB, Constants.NODES_TABLE)));
        assertTrue(indexDao.exists(new IndexIdentifier(Constants.DB, Constants.NODES_TABLE, Constants.NODES_TABLE_NAME_INDEX)));
        assertTrue(indexDao.exists(new IndexIdentifier(Constants.DB, Constants.NODES_TABLE, Constants.NODES_TABLE_RUNNING_INDEX)));
        assertTrue(indexDao.exists(new IndexIdentifier(Constants.DB, Constants.ACTOR_ABILITY_STATUS_TABLE, Constants.ACTOR_ABILITY_STATUS_NAME_INDEX)));
    }
}
