package com.sunnyd.database.fixtures;

import com.google.common.base.Throwables;
import com.sunnyd.database.Connector;
import org.skife.jdbi.v2.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class Prep
{
  final static Logger logger = LoggerFactory.getLogger(Prep.class);

  private static Handle handle;
  private static Statement stmt;

  static
  {
    handle = Connector.getHandleInstance();
  }

  public static void init(String tableName) throws SQLException
  {
    // Check if table exists
    DatabaseMetaData meta = handle.getConnection().getMetaData();
    ResultSet rs = meta.getTables(null, null, tableName, new String[]{"TABLE"});
    if (rs.first())
      logger.info(String.format(" Found table %s", tableName));
    else
    {

      String msg = String.format("Could not find table %s", tableName);
      logger.error(msg);
      logger.error("You probably need to create the table %s", tableName);
      throw new SQLException(msg);
    }
  }

  public static void resetPrimaryKey(String tableName)
      throws SQLException
  {
    logger.info(String.format("Resetting primary key id for table %s", tableName));
    String qString = String.format("ALTER TABLE `%s` AUTO_INCREMENT = 1", tableName);
    handle.createStatement(qString).execute();
  }

  public static void purgeAllRecord(String tableName, boolean checkConstraint)
      throws SQLException
  {
    if (!checkConstraint)
    {
      logger.info("Disable foreign key constraints check.");
      String qString = "SET foreign_key_checks = 0";
      handle.createStatement(qString).execute();
    }

    logger.info(String.format("Removing all record from %s", tableName));
    handle.createStatement(String.format("DELETE FROM `%s`", tableName)).execute();

    if (!checkConstraint)
    {
      logger.info("Reenable foreign key constraints check.");
      String qString = "SET foreign_key_checks = 0";
      handle.createStatement(qString).execute();
    }
  }

  public static void insertTestRecord(int amount, String tableName, boolean withPurge, boolean withResetIncrement)
      throws SQLException
  {
    if (withPurge) Prep.purgeAllRecord(tableName, true);
    if (withResetIncrement) Prep.resetPrimaryKey(tableName);

    logger.info("Inserting %i elements into table %s", amount, tableName);
    for (int i = 0; i < amount; i++)
    {
      stmt.executeUpdate(
          String.format("INSERT INTO `%s` (`name`) VALUES ('foobar')", tableName));
    }
  }
}
