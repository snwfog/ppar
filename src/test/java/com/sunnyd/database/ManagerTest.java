package com.sunnyd.database;

import com.google.common.collect.Table;
import com.sunnyd.database.fixtures.Prep;
import com.sunnyd.database.query.ResultTable;
import com.sunnyd.models.Peer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ManagerTest extends DatabaseTestSetup
{
  final Logger logger = LoggerFactory.getLogger(ManagerTest.class);

  private static final String tableName = "peers";

  @BeforeTest
  public void prepTable() throws SQLException
  {
    logger.info("Cleaning records and reset increment key...");
    Prep.init(tableName);
    Prep.purgeAllRecord(tableName, false);
    Prep.resetPrimaryKey(tableName);
  }

  @Test
  public void saveTest() throws SQLException
  {
    Peer p = new Peer();
    p.setFirstName("Charles");
    p.setLastName("Yang");
    p.setEmail("donchoa@gmail.com");
    p.setUserName("snwfog");
    p.setPassword("4321");
    p.setPoint(9000);
    p.setPersonalWebsite("http://charlescy.com");

    Assert.assertTrue(p.save());
  }

  @Test(dependsOnMethods = {"saveTest"})
  public void findTest() throws SQLException
  {
    String sql = String.format("SELECT * FROM %s WHERE id = %s", tableName, 1);
    ResultSet rs = exec.executeQuery(sql);
    rs.next();
    Map<String, Object> peerAttributes = Manager.convertSQLToJava(rs);
    Peer p = new Peer(peerAttributes);
    Assert.assertEquals(p.getFirstName(), "Charles");
    Assert.assertEquals(p.getLastName(), "Yang");
    Assert.assertEquals(p.getEmail(), "donchoa@gmail.com");
    Assert.assertEquals(p.getUserName(), "snwfog");
    Assert.assertEquals(p.getPassword(), "1234");
    Assert.assertEquals(p.getPoint(), new Integer(9000));
    Assert.assertEquals(p.getPersonalWebsite(), "http://charlescy.com");
  }

//  @Test(enabled = false)
//  public void findAllTest() throws SQLException
//  {
//
//  }
//
//  @Test(dependsOnMethods = {"findTest"})
//  public void updateTest() throws SQLException
//  {
//    String[] newNames = {"Quang", "Mike", "Bobby", "Wais", "Mike"};
//    for (int id = 1; id <= newNames.length; id++)
//    {
//      Map<String, Object> update = new HashMap<String, Object>();
//      update.put("name", newNames[id - 1]);
//      // Should use Map as the generic method parameter
//      assert Manager.update(id, tableName, (HashMap<String, Object>) update);
//    }
//
//    String sql = String.format("SELECT * FROM %s", tableName);
//    ResultSet rs = exec.executeQuery(sql);
//    ResultTable rt = new ResultTable(rs);
//    Table<String, Integer, String> table = rt.getTable();
//
//    for (int id = 1; id <= names.length; id++)
//    {
//      Map<String, Object> result = Manager.find(id, tableName);
//      for (String field : result.keySet())
//      {
//        switch (field)
//        {
//        case "id":
//          Assert.assertEquals(result.get(field), id);
//          break;
//        case "name":
//          Assert.assertEquals(table.get(field, id), newNames[id - 1]);
//          break;
//        case "creation_date":
//        case "last_modified_date":
//          break;
//        default:
//          break;
//        }
//      }
//    }
//  }
//
//  @Test(dependsOnMethods = {"updateTest"})
//  public void destroyTest() throws SQLException
//  {
//    for (int i = 1; i <= names.length; i++)
//    {
//      assert Manager.destroy(i, tableName);
//
//      String sql = String.format("SELECT * FROM %s", tableName);
//      ResultSet rs = exec.executeQuery(sql);
//      ResultTable rt = new ResultTable(rs);
//      String msg = String.format("Table should have %s records left.", rt.countAllRecord());
//      Assert.assertEquals(rt.countAllRecord(), names.length - i, msg);
//    }
//  }

  @Test
  public void toCamelCaseTest()
  {
    Assert.assertEquals(Manager.toCamelCase("hello_world"), "helloWorld");
  }

  @Test
  public void toUnderscoreCaseTest()
  {
    Assert.assertEquals(Manager.toUnderscoreCase("helloWorld"), "hello_world");
    Assert.assertEquals(Manager.toUnderscoreCase("ByeWorld"), "bye_world");
  }

  /**
   @Test // no test for private method. temp comment out
   public void convertJavaSQLTest() {
   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   Date d = null;
   try {
   d = sdf.parse("2013-08-15 01:24:43");
   //System.out.println("here" + d.toString());
   } catch (ParseException e) {
   // TODO Auto-generated catch block
   e.printStackTrace();
   }

   HashMap<String, Object> input = new HashMap<String, Object>();
   input.put("k1", "somestring");
   input.put("k2", false);
   input.put("k3", 123);
   input.put("k4", d);

   HashMap<String, String> output;
   output = Manager.convertJavaToSQL(input);

   Assert.assertEquals(output.get("k1"), "'somestring'");
   Assert.assertEquals(output.get("k2"), "'false'");
   Assert.assertEquals(output.get("k3"), "123");
   Assert.assertEquals(output.get("k4"), "'2013-08-15 01:24:43'");
   }
   */

}
