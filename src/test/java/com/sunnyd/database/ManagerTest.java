package com.sunnyd.database;

import java.sql.Connection;

import com.sunnyd.database.fixtures.Prep;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: snw
 * Date: 2013-10-04
 * Time: 8:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class ManagerTest extends DatabaseSetup
{
  private static final String tableName = "database_manager_test";

  @BeforeClass
  public void init() throws SQLException
  {
    Prep.init(tableName);
  }

  @BeforeTest
  public void prepTable() throws SQLException
  {
    Prep.purgeAllRecord(tableName);
    Prep.resetPrimaryKey(tableName);
  }

  @Test
  public void saveTest() throws SQLException
  {
    HashMap<String, Object> hashMap = new HashMap<String, Object>()
    {
      {
        put("NAME", 34);
      }
    };

    Manager.save(tableName, hashMap);
    Manager.save(tableName, hashMap);
    Manager.save(tableName, hashMap);
  }

  @Test
  public void destroyTest() throws SQLException
  {
    int testRecords = 5;
    Prep.insertTestRecord(testRecords, "database_manager_test", true, true);

    for (int i = 1; i <= testRecords; i++)
      Manager.destroy(i, tableName);

    // Get the result set from this table
    ResultSet rs = stmt.executeQuery(String.format("SELECT COUNT(*) AS recordCount FROM `%s`", tableName));
    int resultCount;
    while (rs.next())
    {
      resultCount = rs.getInt("recordCount");
      Assert.assertEquals(resultCount, 0, "Table should be empty");
    }
  }
}
