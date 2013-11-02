package com.sunnyd.database;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Connector
{
  static final Logger logger = LoggerFactory.getLogger(Connector.class);

  final static String driverClass = "com.mysql.jdbc.Driver";
  private static String database = "ppardb";
  private static String host = "127.0.0.1";
  private static String port = "3306";
  private static String username = "root";
  private static String password = "root";
  private static String url;
  //static reference to itself
  private static DBI instance;

  //private constructor
  private Connector()
  {
    // Set the URL string for the JDBC connection
//    database = prop.getProperty("database", database);
//    host = prop.getProperty("host", host);
//    port = prop.getProperty("port", port);
//    username = prop.getProperty("username", username);
//    password = prop.getProperty("password", password);
//    url = String.format("jdbc:mysql://%s:%s/%s", host, port, database);
  }

  public static Handle getHandleInstance()
  {
    if (instance == null)
      instance = new DBI(url, username, password);
    String msg = String.format("Creating a database handle @ %s, %s, %s.", url, username, password);
    logger.info(msg);

    return instance.open();
  }
}
