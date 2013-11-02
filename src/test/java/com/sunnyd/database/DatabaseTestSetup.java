package com.sunnyd.database;

import org.skife.jdbi.v2.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseTestSetup
{

  final Logger logger = LoggerFactory.getLogger(DatabaseTestSetup.class);
  protected Handle handle;
  protected Statement stmt;

  @BeforeClass
  public void setUp() throws SQLException
  {
    handle = Connector.getHandleInstance();
  }

  @AfterClass
  public void tearDown() throws SQLException
  {
    logger.info(" Closing JDBC active connection. Good Bye!");
    handle.close();
  }

}
