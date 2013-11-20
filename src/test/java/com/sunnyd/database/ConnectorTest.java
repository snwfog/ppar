package com.sunnyd.database;

import com.google.common.base.Throwables;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectorTest
{
  @Test(expectedExceptions = RuntimeException.class)
  public void connectionTest() throws MySQLSyntaxErrorException
  {
    Connection connection = null;
    Statement stmt = null;
    ResultSet rs = null;

    try
    {
      connection = Connector.getConnection();
      stmt = connection.createStatement();
      rs = stmt.executeQuery("SELECT * from ppar where id = 1");

      if (rs.next())
      {
        System.out.println(rs.getString("foo"));
      }
    }
    catch (Throwable t)
    {
      if (t instanceof MySQLSyntaxErrorException)
        Throwables.propagate(t);
    }
  }
}
