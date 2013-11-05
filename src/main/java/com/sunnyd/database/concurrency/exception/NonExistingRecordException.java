package com.sunnyd.database.concurrency.exception;

public class NonExistingRecordException extends RuntimeException
{
  public NonExistingRecordException(int id, String table)
  {
    super(String.format("Could not find the object from table %s[%s]", table, id));
  }
}
