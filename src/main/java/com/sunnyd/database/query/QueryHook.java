package com.sunnyd.database.query;

/**
 * Created with IntelliJ IDEA.
 * User: snw
 * Date: 11/5/2013
 * Time: 2:54 PM
 * To change this template use File | Settings | File Templates.
 */
public interface QueryHook<T>
{
  T exec();
}
