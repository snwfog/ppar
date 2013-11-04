package com.sunnyd.helper;

import com.sunnyd.database.Manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Inflector
{
  public static String plurialize(Class<?> classObject)
  {
    return getClassTableName(classObject);
  }

  public static String getClassTableName(Class<?> classObject)
  {
    String className = classObject.getSimpleName();
    return getClassTableName(className);
  }

  public static String getClassTableName(String className)
  {
    if (className.contains("."))
    {
      String[] canonicalNameSplit = className.split("\\.");
      className = canonicalNameSplit[canonicalNameSplit.length - 1];
    }
    className = Manager.toUnderscoreCase(className);

    //suffix concat to ending of string
    Map<String, String> addSuffixes = new HashMap<String, String>();

    //suffixes replaces the ending of string
    Map<String, String> replaceSuffixes = new HashMap<String, String>();

    //Add suffix here
    addSuffixes.put("es", "s,x,z,ch,sh");
    replaceSuffixes.put("ies", "y");

    //Concat suffix
    Iterator<Map.Entry<String, String>> iter = addSuffixes.entrySet().iterator();
    String name = processSuffix(iter, className, false);
    if (className != name)
    {
      return name;
    }

    //replace with suffix
    iter = replaceSuffixes.entrySet().iterator();
    name = processSuffix(iter, className, true);
    if (className != name)
    {
      return name;
    }

    //Default
    return className + "s";
  }

  private static String processSuffix(Iterator<Map.Entry<String, String>> iter, String name, boolean replace)
  {
    while (iter.hasNext())
    {
      Map.Entry<String, String> pairs = (Map.Entry<String, String>) iter.next();
      for (String ending : pairs.getValue().split(","))
      {
        if (name.endsWith(ending))
        {
          String suffix = pairs.getKey();
          name = replace ? name.substring(0, name.length() - ending.length()).concat(suffix) : name + suffix;
          return name;
        }
      }
    }
    return name;
  }
}
