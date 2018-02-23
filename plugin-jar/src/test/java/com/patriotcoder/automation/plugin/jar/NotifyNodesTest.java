/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package com.patriotcoder.automation.plugin.jar;

import com.docussandra.testhelpers.TestDocussandraManager;
import com.patriotcoder.automation.pihomeautomationactor.InitUtils;
import com.patriotcoder.automation.pihomeautomationactor.dataobject.ActorAbility;
import com.patriotcoder.automation.pihomeautomationactor.dataobject.PiActorConfig;
import com.patriotcoder.pihomesecurity.Constants;
import com.patriotcoder.pihomesecurity.Main;
import com.patriotcoder.pihomesecurity.dataobjects.PiHomeConfig;
import com.pearson.docussandra.domain.objects.Document;
import com.pearson.docussandra.plugininterfaces.NotifierPlugin;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jeffrey DeYoung
 */
public class NotifyNodesTest {

  public NotifyNodesTest() {}

  @BeforeClass
  public static void setUpClass() {}

  @AfterClass
  public static void tearDownClass() {}

  @Before
  public void setUp() {}

  @After
  public void tearDown() {}

  /**
   * Test of doNotify method, of class NotifyNodes.
   */
  @Test
  public void testDoNotify() throws Exception {
    System.out.println("doNotify");
    String docussandraUrl = "http://localhost:19080/";

    TestDocussandraManager.getManager().ensureTestDocussandraRunning(true);
    PiHomeConfig serverConfig = new PiHomeConfig();
    serverConfig.setDocussandraUrl(docussandraUrl);
    Main.setUpDocussandra(serverConfig);
    // end server setup

    // datasetup
    InitUtils.selfRegister(
        new PiActorConfig("BarnActor", "Barn", docussandraUrl, new ArrayList<ActorAbility>()));

    // call
    NotifierPlugin.MutateType type = NotifierPlugin.MutateType.CREATE;
    Document document = new Document();
    document.setTable(Constants.DB, Constants.ACTOR_ABILITY_STATUS_TABLE);
    document.setObjectAsString("{\"name\":\"BarnActor_MainAir\", \"state\":\"OFF\"}");
    NotifyNodes instance = new NotifyNodes();
    instance.doNotify(type, document);
  }

  /**
   * Test of getPluginName method, of class NotifyNodes.
   */
  @Test
  public void testGetPluginName() {
    System.out.println("getPluginName");
    NotifyNodes instance = new NotifyNodes();
    String expResult = "Node-Notifier";
    String result = instance.getPluginName();
    assertEquals(expResult, result);
  }

}
