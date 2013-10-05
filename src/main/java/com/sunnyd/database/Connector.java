package com.sunnyd.database;

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
  private static Connector instance = new Connector();

  //private constructor
  private Connector()
  {
    // Initiate the URL from properties files or fall back to default
    Properties prop = new Properties();
    try
    {
      // Load the files from the default path
      // Which is located at resources/config
      prop.load(ClassLoader.getSystemResourceAsStream("config/database.properties"));

      // Statically init the JDBC class
      Class.forName(driverClass);

      // Set the URL string for the JDBC connection
      database = prop.getProperty("database", database);
      host = prop.getProperty("host", host);
      port = prop.getProperty("port", port);
      username = prop.getProperty("username", username);
      password = prop.getProperty("password", password);
      url = String.format("jdbc:mysql://%s:%s/%s", host, port, database);
    }
    catch (IOException e)
    {
      logger.error("Could not load the database.properties file.");
    }
    catch (ClassNotFoundException e)
    {
      logger.error("Could not find the JDBC library.");
    }
  }

  public static Connection getConnection() throws SQLException
  {
    return instance.createConnection();
  }

  private Connection createConnection() throws SQLException
  {

    return DriverManager.getConnection(url, username, password);
  }
}