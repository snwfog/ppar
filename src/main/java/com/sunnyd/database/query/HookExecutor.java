package com.sunnyd.database.query;

/**
 * Created with IntelliJ IDEA.
 * User: snw
 * Date: 11/5/2013
 * Time: 2:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class HookExecutor<T>
{
  QueryHook<T> hook;

  public HookExecutor(QueryHook<T> hook)
  {
    this.hook = hook;
  }

  public T exec(QueryHook<T> hook)
  {
    return hook.exec();
  }
}
