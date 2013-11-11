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
  @Test(enabled = false)
  public void connectionTest() throws MySQLSyntaxErrorException
  {
    SSHjdbcSession connection = null;
    Statement stmt = null;
    ResultSet rs = null;

    try
    {
      connection = Connector.getConnection();
      stmt = connection.getConnection().createStatement();
      rs = stmt.executeQuery("SELECT * from soen387l where id = 1");

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
