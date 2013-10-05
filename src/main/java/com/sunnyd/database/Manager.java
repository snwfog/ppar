package com.sunnyd.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Manager
{

    public static void main(String[] args)
    {
        final Logger logger = LoggerFactory.getLogger(Manager.class);
        logger.info("Hello world");

        // sample test for CRUD below:
        HashMap<String, Object> map = new HashMap<String, Object>();
        
        map.put("firstName", new Date());
        map.put("asd", true);
        map.put("lastName", 99.88);

        // FIND:
        // System.out.println(find(1, "persons"));
        
        // FIND ALL:
        /**ArrayList<HashMap<String, Object>> r = findAll("persons", map);
        for (HashMap<String, Object> h : r){
            for (String key : h.keySet()){
                System.out.println(key + ":" + h.get(key));
            }
        }*/
        

        // SAVE:
        // System.out.println(save("persons", map));

        // DESTROY:
        // System.out.println(destroy(2, "persons"));

        // UPDATE:
        // System.out.println(update(3, "persons", map));
        
        
        
        
        // converter Java to SQL:

        
        
        HashMap<String,String> c = convertJavaSQL(map);
        for (Object key : c.keySet())
        {    
            System.out.println(key + " " + c.get(key));
            
        }

    }

    // find by id, return single row
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

    
    // find multiple by criteria
    public static ArrayList<HashMap<String, Object>> findAll(String tableName, HashMap<String, Object> conditions)
    {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        ArrayList<HashMap<String, Object>> results = new ArrayList<HashMap<String, Object>>();
        
  
        String where = "";
        
        for (String key : conditions.keySet())
        {
            Class<?> fieldType = conditions.get(key).getClass();

            if (fieldType.getName().indexOf("String") > 0)
            {
                where += key + " = '" + conditions.get(key) + "' AND ";
            } else
            {
                where += key + " = " + conditions.get(key) + " AND ";
            }
        }
        
        // remove trailing comma
        where = where.replaceAll(" AND $", ""); // col1, col2, col3
       
        if (!where.equals("")){
            where = " WHERE " + where;
        }
        

        try
        {
            connection = Connector.getConnection();
            stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT * FROM " + tableName + where);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            rs.beforeFirst(); //go to beginning
            while (rs.next())
            {
                HashMap<String, Object> row = new HashMap<String, Object>();
                for (int i = 1; i < columnCount + 1; i++)
                {
                    String columnName = rsmd.getColumnName(i);
                    Object value = rs.getObject(columnName);
                    row.put(columnName, value);
                }
                results.add(row);
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return results;
        
    }

    public static int save(String tableName, HashMap<String, Object> hashmap)
    {
        Connection connection = null;
        Statement stmt = null;
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

                // REVIEW (@harry): This should be >= 0
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

            stmt = connection.createStatement();
            stmt.executeUpdate("INSERT INTO " + tableName + " (" + columns
                    + ") VALUES (" + values + ")",
                    Statement.RETURN_GENERATED_KEYS);

            /**
             * stmt = connection.prepareStatement("INSERT INTO " + tableName +
             * " (" + columns + ") VALUES (" + values + ")", new String[] { "id"
             * });
             */

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
    
    
    // helper method to convert java type (string, int, date) to mysql type written in str
    private static HashMap<String, String> convertJavaSQL(HashMap<String, Object> original){
        HashMap<String, String> converted = new HashMap<String, String>();
        
        for (String key : original.keySet())
        {
            Class<?> fieldType = original.get(key).getClass();
            String type = fieldType.getSimpleName();
            
            switch (type){
                case "Boolean":
                    converted.put(key, "'" + original.get(key).toString() + "'");
                    break;
                case "Integer":
                    converted.put(key, Integer.toString((int) original.get(key)));
                    break;
                case "Double":
                    converted.put(key, Double.toString((double) original.get(key)));
                    break;
                case "String":
                    converted.put(key, "'" + original.get(key) + "'");
                    break;
                case "Date":
                    Date dt = (Date) original.get(key);   
                    DateFormat parser = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                    converted.put(key, "'" + parser.format(dt) +"'");
                    break;
                default:
                    System.out.println("i dunno this type yet, tell harry");
                    break;
            }
        }
        return converted;
    }

}
