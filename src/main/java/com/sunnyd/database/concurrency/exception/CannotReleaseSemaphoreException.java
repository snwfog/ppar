package com.sunnyd.database.concurrency.exception;

/**
 * Created with IntelliJ IDEA.
 * User: snw
 * Date: 11/5/2013
 * Time: 6:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class CannotReleaseSemaphoreException extends RuntimeException
{
  public CannotReleaseSemaphoreException(int id, String tableName)
  {
    super(String.format("Cannot release semaphore lock on table %s and object %s", tableName, id));
  }
}
