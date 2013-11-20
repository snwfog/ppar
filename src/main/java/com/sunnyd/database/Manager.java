package com.sunnyd.database;

import com.sunnyd.database.query.QueryExecutorHook;
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
        map.put("firstName", "newguy");

        // FIND:
         System.out.println(find(1, "persons"));

        // FIND ALL:

//         ArrayList<HashMap<String, Object>> r = findAll("persons", map); for
//         (HashMap<String, Object> h : r){ for (String key : h.keySet()){
//         System.out.println(key + ":" + h.get(key)); } }

        // SAVE:
        // System.out.println(save("persons", map));

        // DESTROY:
        // System.out.println(destroy(2, "persons"));

        // UPDATE:
        // System.out.println(update(3, "persons", map));

        // converter Java to SQL:
        /**
         * HashMap<String,String> c = convertJavaSQL(map); for (Object key :
         * c.keySet()) { System.out.println(key + " " + c.get(key));
         * 
         * }
         */
        


    }

    // find by id, return single row
    public static HashMap<String, Object> find(int id, String tableName)
    {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;

        try
        {
            connection = Connector.getConnection();
            stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT * FROM " + tableName
                    + " WHERE ID = " + id);
            
            if (rs.next()){
                return convertSQLJava(rs);
            }
            
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    // find multiple by criteria
    public static ArrayList<HashMap<String, Object>> findAll(String tableName,
            HashMap<String, Object> conditions)
    {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        ArrayList<HashMap<String, Object>> results = new ArrayList<HashMap<String, Object>>();

        String where = "";

        HashMap<String, String> SQLConditions = convertJavaSQL(conditions);

        for (String key : SQLConditions.keySet())
        {

            where += key + " = " + SQLConditions.get(key) + " AND ";

        }

        // remove trailing comma
        where = where.replaceAll(" AND $", ""); // col1, col2, col3

        if (!where.equals(""))
        {
            where = " WHERE " + where;
        }

        try
        {
            connection = Connector.getConnection();
            stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT * FROM " + tableName + where);
            ResultSetMetaData rsmd = rs.getMetaData();
            
            //int columnCount = rsmd.getColumnCount();


            while (rs.next())
            {
                
                HashMap<String, Object> row = new HashMap<String, Object>();
                row = convertSQLJava(rs);
                /**
                for (int i = 1; i < columnCount + 1; i++)
                {
                    String columnName = rsmd.getColumnName(i);
                    Object value = rs.getObject(columnName);
                    row.put(columnName, value);
                }*/
                
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
        QueryExecutorHook.beforeSave(hashmap);
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;

        String columns = "";
        String values = "";

        int id = 0;

        try
        {
            connection = Connector.getConnection();

            HashMap<String, String> SQLHashmap = convertJavaSQL(hashmap);

            // get column value pairs from hashmap as val,val,val...
            for (String key : SQLHashmap.keySet())
            {
                columns += key + ",";
                values += SQLHashmap.get(key) + ",";
                Class<?> fieldType = hashmap.get(key).getClass();
                // type is string, add single quote

                // REVIEW (@harry): This should be >= 0?
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

            HashMap<String, String> SQLHashmap = convertJavaSQL(hashmap);

            for (Object key : SQLHashmap.keySet())
            {
                String column = (String) key;
                String newvalue = SQLHashmap.get(key);
                stmt.execute("UPDATE " + tableName + " SET " + column + " = "
                        + newvalue + " WHERE ID = " + id);
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

    // helper method to convert java type (string, int, date) to mysql type
    // written in str
    private static HashMap<String, String> convertJavaSQL(
            HashMap<String, Object> original)
    {
        HashMap<String, String> converted = new HashMap<String, String>();

        for (String key : original.keySet())
        {
            Class<?> fieldType = original.get(key).getClass();
            String type = fieldType.getSimpleName();

            switch (type)
            {
                case "Boolean":
                    converted
                            .put(key, "'" + original.get(key).toString() + "'");
                    break;
                case "Integer":
                    converted.put(key,
                            Integer.toString((int) original.get(key)));
                    break;
                case "Double":
                    converted.put(key,
                            Double.toString((double) original.get(key)));
                    break;
                case "String":
                    converted.put(key, "'" + original.get(key) + "'");
                    break;
                case "Date":
                    Date dt = (Date) original.get(key);
                    DateFormat parser = new SimpleDateFormat(
                            "yyyy-mm-dd hh:mm:ss");
                    converted.put(key, "'" + parser.format(dt) + "'");
                    break;
                default:
                    System.out.println("i dunno this type yet, tell harry");
                    break;
            }
        }
        return converted;
    }

    // convert result got from sql to java type
    private static HashMap<String, Object> convertSQLJava(ResultSet resultset) throws SQLException
    {
        HashMap<String, Object> converted = new HashMap<String, Object>();
        ResultSetMetaData rsmd = resultset.getMetaData();
        int columnCount = rsmd.getColumnCount();
        for (int i = 1; i < columnCount + 1; i++)
        {
            String columnName = rsmd.getColumnName(i);
            String type = rsmd.getColumnTypeName(i);
            // System.out.println(columnName + " -> " + type);
            switch(type){
                case "INT UNSIGNED":
                    converted.put(columnName, (int)((long) resultset.getObject(columnName)));
                    break;
                case "TINYINT": //boolean
                    converted.put(columnName, (Boolean) resultset.getObject(columnName));
                    break;
                case "VARCHAR":
                    converted.put(columnName, resultset.getObject(columnName).toString());
                    break;
                case "DATETIME":
                    converted.put(columnName, (Date) resultset.getObject(columnName));
                    break;
                default:
                    System.out.println("i dunno this type yet, tell harry about it");
                    break;
            }
            
        }
        
        return converted;
    }

}
