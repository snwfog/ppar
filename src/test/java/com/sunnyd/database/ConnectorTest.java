package com.sunnyd.database;

import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectorTest
{
  @Test
  public void connectionTest()
  {
    Connection connection = null;
    Statement stmt = null;
    ResultSet rs = null;

    try
    {
      connection = Connector.getConnection();
      stmt = connection.createStatement();
      rs = stmt.executeQuery("SELECT * from testdata where id =1");

      if (rs.next())
      {
        System.out.println(rs.getString("foo"));
      }

    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }
}
