package com.sunnyd.database.query;

import com.sunnyd.database.Manager;

import java.util.HashMap;

/**
 * This hook is for before the query (i.e. save, update) from the {@link Manager}
 * class, it will call the hook.
 */
public class QueryExecutorHook
{
  public static void beforeSave(HashMap<String, Object> hashMap)
  {


  }

  public static void afterSave(Manager manager)
  {

  }
}
