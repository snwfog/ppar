package com.sunnyd.database;

//Step 1: Use interfaces from java.sql package

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connector
{
  public static final String URL = "jdbc:mysql://127.0.0.1:8889/ppardb?connectTimeout=3000&socketTimeout=3000";
  public static final String USER = "root";
  public static final String PASSWORD = "root";
  public static final String DRIVER_CLASS = "com.mysql.jdbc.Driver";
  //static reference to itself
  private static Connector instance = new Connector();

  //private constructor
  private Connector()
  {
    try
    {
      //Step 2: Load MySQL Java driver
      Class.forName(DRIVER_CLASS);
    }
    catch (ClassNotFoundException e)
    {
      e.printStackTrace();
    }
  }

  public static Connection getConnection() throws SQLException
  {
    return instance.createConnection();
  }

  private Connection createConnection() throws SQLException
  {

    Connection connection = null;
    //Step 3: Establish Java MySQL connection
    connection = DriverManager.getConnection(URL, USER, PASSWORD);

    return connection;
  }
}