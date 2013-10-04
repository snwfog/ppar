/*
 * Copyright 2013 8D Technologies, Inc. All Rights Reserved.
 *
 * This software is the proprietary information of 8D Technologies, Inc.
 * Use is subject to license terms.
 */

package com.sunnyd.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class Manager
{

  public static void main(String[] args)
  {
    final Logger logger = LoggerFactory.getLogger(Manager.class);
    logger.info("Hello world");
  }

  public static HashMap<Object, Object> find(int id, String tableName){
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		HashMap<Object, Object> result = new HashMap<Object, Object>();
		try {
			connection = Connector.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT * from "+tableName+" where id ="+id);
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();

			// The column count starts from 1
			rs.next(); //
			for (int i = 1; i < columnCount + 1; i++ ) {
			  String name = rsmd.getColumnName(i);
			  rs.getString(name);
			  result.put(name, rs.getString(name));
			}
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
  }
}
