package com.sunnyd.database.concurrency.exception;

public class CannotAcquireSemaphoreException extends RuntimeException
{
  public CannotAcquireSemaphoreException(int id, String tableName)
  {
    super(String.format("Cannot acquire semaphore lock on table %s and object %s", tableName, id));
  }
}
