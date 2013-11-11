package com.sunnyd.database.query;

import com.sunnyd.database.Connector;
import com.sunnyd.database.SSHjdbcSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class is a wrapper around the standard Statement executor from java.sql
 */
public class QueryExecutor
{
  static final Logger logger = LoggerFactory.getLogger(QueryExecutor.class);

  private static SSHjdbcSession conn;
  private static QueryExecutor exec;

  private QueryExecutor() throws SQLException
  {
    conn = Connector.getConnection();
  }

  public static QueryExecutor getInstance()
  {
    try
    {
      if (exec == null)
        exec = new QueryExecutor();
    }
    catch (SQLException e)
    {
      logger.error("Could not create QueryExecutor instance: " + e);
    }

    return exec;
  }

  public ResultSet executeQuery(String sql) throws SQLException
  {
    return conn.getConnection().createStatement().executeQuery(sql);
  }
}
