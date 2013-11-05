package com.sunnyd.database.concurrency.exception;

public class VersionChangedException extends RuntimeException
{
  public VersionChangedException(int id, String tableName, String databaseTimestamp, String yourTimestamp)
  {
    super(String.format("Object %s[%s] version has moved forward since last access %s on %s", tableName, id, yourTimestamp, databaseTimestamp));
  }
}
