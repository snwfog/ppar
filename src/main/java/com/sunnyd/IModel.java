package com.sunnyd;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;

public interface IModel
{
//  T map(int index, ResultSet r, StatementContext ctx);

  @Deprecated
  Integer getId();

//  void setId(Integer id);

  @Deprecated
  Boolean getUpdateFlag();

  @Deprecated
  void setUpdateFlag(Boolean flag);
}
