package com.sunnyd;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;

import java.util.Map;

public class JDBITest
{
  public static void main(String[] args)
  {
    DBI dbi = new DBI("jdbc:mysql://localhost:3306/ppardb", "root", "root");
    Handle h = dbi.open();
    Query<Map<String, Object>> rs = h.createQuery("select * from peers");


  }
}
