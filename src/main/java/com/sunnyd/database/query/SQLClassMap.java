package com.sunnyd.database.query;

import com.google.common.collect.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class SQLClassMap
{
  private static BiMap<Class, String> classMap;
  private static SQLClassMap instance;

  static
  {
    classMap = HashBiMap.create();

    classMap.put(Integer.class, "INT");
    classMap.put(Integer.class, "TINYINT");
    classMap.put(Long.class, "INT UNSIGNED"); // Long hold larger int value
    classMap.put(String.class, "VARCHAR");
    classMap.put(Date.class, "DATETIME");
    classMap.put(Date.class, "TIMESTMP");
  }

  private SQLClassMap() {}
  public static SQLClassMap getInstance()
  {
    if (instance == null)
      instance = new SQLClassMap();
    return instance;
  }

  public Class getJavaType(String sqlType)
  {
    return classMap.inverse().get(sqlType);
  }

  public String getSQLType(Class klazz)
  {
    return classMap.get(klazz);
  }
}
