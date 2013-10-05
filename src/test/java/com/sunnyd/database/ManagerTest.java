package com.sunnyd.database;

import java.sql.Connection;

import com.sunnyd.database.fixtures.Prep;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created with IntelliJ IDEA.
 * User: snw
 * Date: 2013-10-04
 * Time: 8:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class ManagerTest
{
  private static final String tableName = "database_manager_test";
  private Connection conn;
  private Statement stmt;

  @BeforeClass
  public void init() throws SQLException
  {
    conn = Connector.getConnection();
    stmt = conn.createStatement();

    // Prep a database table for testing
    Prep.init(tableName);
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
