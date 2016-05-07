package com.patriotcoder.pihomesecurity.dao;

import com.docussandra.javasdk.Config;
import com.docussandra.testhelpers.TestDocussandraManager;
import com.github.cassandradockertesthelper.AbstractCassandraDockerParameterizedTest;
import com.patriotcoder.pihomesecurity.Main;
import com.patriotcoder.pihomesecurity.dataobjects.PiHomeConfig;
import com.patriotcoder.pihomesecurity.dataobjects.SecNode;
import com.patriotcoder.pihomesecurity.dataobjects.SecNodeWithId;
import java.io.File;
import java.util.List;
import org.junit.Ignore;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.fail;
import org.junit.Test;

/**
 *
 * @author https://github.com/JeffreyDeYoung
 */
public class SecNodeDaoTest extends AbstractCassandraDockerParameterizedTest
{

    private Config config;
    private SecNodeDao instance;

    /**
     * Constructor. The file parameter is provided by the parameterized tests
     * (defined by generateParameters in the parent class).
     *
     * @param dockerFile Parameter for this test. It's the docker file that
     * represents the version of Cassandra we are testing.
     */
    public SecNodeDaoTest(File dockerFile) throws Exception
    {
        super(dockerFile);//call to super class to actually setup this test.
        //^^TODO: this isn't right; we are not using Docker here
        TestDocussandraManager.getManager().ensureTestDocussandraRunning(true);
        String docussandraUrl = "http://localhost:19080/";
        config = new Config(docussandraUrl);
        PiHomeConfig piConfig = new PiHomeConfig();
        piConfig.setDocussandraUrl(docussandraUrl);
        Main.setUpDocussandra(piConfig);
        instance = new SecNodeDao(config.getBaseUrl());
    }

    /**
     * Test of getRunningNodes method, of class SecNodeDao.
     */
    @Ignore
    public void testGetRunningNodes() throws Exception
    {
        System.out.println("getRunningNodes");
        SecNodeDao instance = null;
        List<SecNode> expResult = null;
        List<SecNode> result = instance.getRunningNodes();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getStoppedNodes method, of class SecNodeDao.
     */
    @Ignore
    public void testGetStoppedNodes() throws Exception
    {
        System.out.println("getStoppedNodes");
        SecNodeDao instance = null;
        List<SecNode> expResult = null;
        List<SecNode> result = instance.getStoppedNodes();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of saveNode method, of class SecNodeDao.
     */
    @Test
    public void CRUDNodeTest() throws Exception
    {
        System.out.println("CRUDNode");
        SecNode node = new SecNode("127.0.0.1", "testName!");
        assertNull(instance.getNodeByName(node.getName()));
        instance.saveNode(node);
        assertNotNull(instance.getNodeByName(node.getName()));

    }

    /**
     * Test of getNodeByName method, of class SecNodeDao.
     */
    @Ignore
    public void testGetNodeByName() throws Exception
    {
        System.out.println("getNodeByName");
        String nodeName = "";
        SecNodeDao instance = null;
        SecNodeWithId expResult = null;
        SecNodeWithId result = instance.getNodeByName(nodeName);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
