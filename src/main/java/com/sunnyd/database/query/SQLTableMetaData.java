package com.sunnyd.database.query;

import com.sunnyd.database.Connector;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLTableMetaData
{

  static final Logger logger = LoggerFactory.getLogger(SQLTableMetaData.class);

  /**
   * Given a table name, and a field name, check if this list contains
   * an unique key constraint. Not very efficient for batch query. Field name
   * is expected to be in the SQL (i.e. underscore format), and the unique
   * key constraint must contain the name of the field by convention. Otherwise
   * it will not work.
   *
   * @param tableName
   * @param field
   * @return
   */
  public static boolean hasUniqueKeyConstraint(String tableName, String field)
  {
    for (String fieldConstraint : SQLTableMetaData.getTableUniqueConstraint(tableName))
      if (fieldConstraint.contains(field))
        return true;

    return false;
  }

  /**
   * Given a table name, this method will return a list of all the table
   * attributes which have an unique key constraint.
   *
   * @param tableName
   * @return
   */
  public static List<String> getTableUniqueConstraint(String tableName)
  {
    Connection connection = null;
    List<String> uniqueField = new ArrayList<>();
    try
    {
      connection = Connector.getConnection();
      DatabaseMetaData metadata = connection.getMetaData();
      String url[] = StringUtils.split(metadata.getURL(), "/");
      String databaseName = url[url.length - 1];

      Statement stmt = connection.createStatement();
      logger.info("SELECT * FROM information_schema.table_constraints "
          + "WHERE CONSTRAINT_SCHEMA = '" + databaseName
          + "' AND TABLE_NAME = '" + tableName
          + "' AND CONSTRAINT_TYPE = 'UNIQUE'");

      ResultSet rs = stmt.executeQuery(
          "SELECT * FROM information_schema.table_constraints "
              + "WHERE CONSTRAINT_SCHEMA = '" + databaseName
              + "' AND TABLE_NAME = '" + tableName
              + "' AND CONSTRAINT_TYPE = 'UNIQUE'");

      while (rs.next())
      {
        uniqueField.add(rs.getString("CONSTRAINT_NAME"));
        logger.info(rs.getString("CONSTRAINT_NAME"));
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }

    return uniqueField;
  }
}
