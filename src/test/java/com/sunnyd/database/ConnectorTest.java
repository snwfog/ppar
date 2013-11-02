package com.sunnyd.database;

import com.google.common.base.Throwables;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public class ConnectorTest
{
  @Test(expectedExceptions = RuntimeException.class)
  public void connectionTest() throws MySQLSyntaxErrorException
  {
    Handle handle = null;

    try
    {
      handle = Connector.getHandleInstance();
      Query<Map<String, Object>> q = handle.createQuery("SELECT * from ppar where id = 1");

//      if (rs.next())
//      {
//        System.out.println(rs.getString("foo"));
//      }
    }
    catch (Throwable t)
    {
      if (t instanceof MySQLSyntaxErrorException)
        Throwables.propagate(t);
    }
  }
}
