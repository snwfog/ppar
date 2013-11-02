package com.sunnyd.database.query;


import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public class ResultTable
{
  static final Logger logger = LoggerFactory.getLogger(ResultTable.class);
  // HashBasedTable<Column, Row, Value>
  // Columns are the id rows, while rows are the field name
  // Column set is all the records, while row set is all the fields name
  private Table<String, Integer, String> table;

  public ResultTable(ResultSet rs)
  {
    table = HashBasedTable.create();
    init(rs);
  }

  public Table<String, Integer, String> getTable() { return table; }

  private void init(ResultSet rs)
  {
    try
    {
      ResultSetMetaData meta = rs.getMetaData();
      int columnsCount = meta.getColumnCount();
      // Map of the columns name to the columns type
      Map<String, String> columnsName = new HashMap<String, String>(columnsCount);
      for (int i = 1; i <= columnsCount; i++)
        columnsName.put(meta.getColumnName(i), meta.getColumnTypeName(i));

      for (int id = 1; rs.next(); id++)
        for (String name : columnsName.keySet())
          table.put(name, id, rs.getString(name));
    }
    catch (SQLException e)
    {
      logger.error("Failed creating Table from ResultSet.");
    }
  }

  public int countAllRecord()
  {
    return table.columnKeySet().size();
  }

  public Set<Integer> getRowKeySet()
  {
    return table.columnKeySet();
  }

  public Set<String> getColumnKeySet()
  {
    return table.rowKeySet();
  }

  public Map<Integer, String> getRawColumnAsMap(String columnName)
  {
    return table.row(columnName);
  }

  public List<String> getRawColumnAsList(String columnName)
  {
    return new ArrayList<String>(this.getRawColumnAsMap(columnName).values());
  }
}
