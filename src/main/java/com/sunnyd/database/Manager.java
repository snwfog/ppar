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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class Manager
{

    public static void main(String[] args)
    {
        final Logger logger = LoggerFactory.getLogger(Manager.class);
        logger.info("Hello world");

        // sample test for CRUD below:
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("firstName", "ppp");
        map.put("lastName", "pppp");

        // FIND:
        // System.out.println(find(1, "persons"));

        // SAVE:
        // System.out.println(save("persons", map));

        // DESTROY:
        // System.out.println(destroy(2, "persons"));

        // UPDATE:
        // System.out.println(update(3, "persons", map));

    }

    public static HashMap<String, Object> find(int id, String tableName)
    {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;

        HashMap<String, Object> result = new HashMap<String, Object>();
        try
        {
            connection = Connector.getConnection();
            stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT * FROM " + tableName
                    + " WHERE ID = " + id);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            // The column count starts from 1
            rs.next(); //
            for (int i = 1; i < columnCount + 1; i++)
            {
                String columnName = rsmd.getColumnName(i);
                Object value = rs.getObject(columnName);
                result.put(columnName, value);
            }
            return result;
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static HashMap<String, Object> findAll(String tableName)
    {
        return null;
    }

    public static int save(String tableName, HashMap<String, Object> hashmap)
    {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String columns = "";
        String values = "";

        int id = 0;

        try
        {
            connection = Connector.getConnection();

            // get column value pairs from hashmap as val,val,val...
            for (String key : hashmap.keySet())
            {
                columns += key + ",";
                Class<?> fieldType = hashmap.get(key).getClass();
                // type is string, add single quote
                if (fieldType.getName().indexOf("String") > 0)
                {
                    values += "'" + hashmap.get(key) + "',";
                } else
                {
                    values += hashmap.get(key) + ",";
                }
            }
            // remove trailing comma
            columns = columns.replaceAll(",$", "");
            values = values.replaceAll(",$", "");

            stmt = connection.prepareStatement("INSERT INTO " + tableName
                    + " (" + columns + ") VALUES (" + values + ")",
                    new String[] { "id" });
            stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();
            rs.next();
            id = rs.getInt(1);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return id;
    }

    public static boolean destroy(int id, String tableName)
    {
        Connection connection = null;
        Statement stmt = null;
        boolean isDestroyed = true;

        try
        {
            connection = Connector.getConnection();
            stmt = connection.createStatement();
            stmt.execute("DELETE FROM " + tableName + " WHERE ID = " + id);
        } catch (SQLException e)
        {
            e.printStackTrace();
            isDestroyed = false;
        }
        return isDestroyed;
    }

    // update 1 or more fields of a single row
    public static boolean update(int id, String tableName,
            HashMap<String, Object> hashmap)
    {
        Connection connection = null;
        Statement stmt = null;
        boolean isUpdated = true;

        try
        {
            connection = Connector.getConnection();
            stmt = connection.createStatement();

            for (Object key : hashmap.keySet())
            {
                String column = (String) key;
                Object newvalue = hashmap.get(key);
                Class<?> fieldType = hashmap.get(key).getClass();
                newvalue = fieldType.cast(hashmap.get(key));
                stmt.execute("UPDATE " + tableName + " SET " + column + " = '"
                        + newvalue + "' WHERE ID = " + id);
            }

        } catch (SQLException e)
        {
            e.printStackTrace();
            isUpdated = false;
        }
        return isUpdated;
    }

    public static void create()
    {
        // create obj
    }

}
