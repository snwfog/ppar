package com.sunnyd.database;

import com.sunnyd.database.query.QueryExecutorHook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Manager {

    public static void main(String[] args) {
//        final Logger logger = LoggerFactory.getLogger(Manager.class);
//        logger.info("Hello world");
//
         //sample test for CRUD below:
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("firstName", "asdsadsa");

//        // FIND:
//        // System.out.println(find(0, "persons"));
//
//        // FIND ALL:
//
//        // ArrayList<HashMap<String, Object>> r = findAll("persons", map); for
//        // (HashMap<String, Object> h : r){ for (String key : h.keySet()){
//        // System.out.println(key + ":" + h.get(key)); } }
//
//        // SAVE:
        System.out.println(save("peers", map));
//
//        // DESTROY:
//        // System.out.println(destroy(2, "peers"));
//
//        // UPDATE:
//        // System.out.println(update(0, "persons", map));
//
//        // converter Java to SQL:
//        HashMap<String, String> c = convertJavaSQL(map);
//        for (Object key : c.keySet()) { // System.out.println(key + " " +
//                                        // c.get(key));
//
//        }
        
        // System.out.println(toCamelCase("first_name_else"));
        // System.out.println(toUnderscoreCase("firstNameField"));

    }

    // find by id, return single row
    public static HashMap<String, Object> find(int id, String tableName) {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            connection = Connector.getConnection();
            stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT * FROM " + tableName + " WHERE ID = " + id);

            if (rs.next()) {
                return convertSQLJava(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // find by id, return single row
    public static Integer[] find(String tableName, String column, Object value) {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        value = convertJavaToSql(value);
        try {
            ArrayList<Integer> result = new ArrayList<Integer>();
            connection = Connector.getConnection();
            stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT * FROM " + tableName + " WHERE " + column + "=" + value);
            
            while(rs.next()){
                result.add(rs.getInt("id"));
            }
            return (Integer[]) result.toArray(new Integer[result.size()]);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // find multiple by criteria
    public static ArrayList<HashMap<String, Object>> findAll(String tableName, HashMap<String, Object> conditions) {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        ArrayList<HashMap<String, Object>> results = new ArrayList<HashMap<String, Object>>();

        String where = "";

        HashMap<String, String> SQLConditions = convertJavaSQL(conditions);

        for (String key : SQLConditions.keySet()) {
            where += key + " = " + SQLConditions.get(key) + " AND ";
        }
        // remove trailing comma
        where = where.replaceAll(" AND $", ""); // col1, col2, col3

        if (!where.equals("")) {
            where = " WHERE " + where;
        }

        try {
            connection = Connector.getConnection();
            stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT * FROM " + tableName + where);
            while (rs.next()) {
                HashMap<String, Object> row = new HashMap<String, Object>();
                row = convertSQLJava(rs);
                results.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;

    }

    public static int save(String tableName, HashMap<String, Object> hashmap) {
//        QueryExecutorHook.beforeSave(hashmap);
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;

        String columns = "";
        String values = "";

        int id = 0;

        try {
            connection = Connector.getConnection();
            HashMap<String, String> SQLHashmap = convertJavaSQL(hashmap);
            
            DatabaseMetaData md = connection.getMetaData();
            if (md.getColumns(null, null, tableName, "creation_date").next() ){
                SQLHashmap.put("creation_date", "NOW()");
                SQLHashmap.put("last_modified_date", "NOW()");
            }
            

            // get column value pairs from hashmap as val,val,val...
            for (String key : SQLHashmap.keySet()) {
                columns += key + ",";
                values += SQLHashmap.get(key) + ",";
            }
            // remove trailing comma
            columns = columns.replaceAll(",$", "");
            values = values.replaceAll(",$", "");

            stmt = connection.createStatement();

            // no id is provided (means auto-gen id)
            if (!hashmap.containsKey("id")) {
                stmt.executeUpdate("INSERT INTO " + tableName + " (" + columns + ") VALUES (" + values + ")",
                        Statement.RETURN_GENERATED_KEYS);
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    id = rs.getInt(1);
                }
            } else // id is provided (means
            {
                id = stmt.executeUpdate("INSERT INTO " + tableName + " (" + columns + ") VALUES (" + values + ")") != 0 ? (int) hashmap
                        .get("id") : 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public static boolean destroy(int id, String tableName) {
        Connection connection = null;
        Statement stmt = null;
        boolean isDestroyed = true;

        try {
            connection = Connector.getConnection();
            stmt = connection.createStatement();
            stmt.execute("DELETE FROM " + tableName + " WHERE ID = " + id);
        } catch (SQLException e) {
            e.printStackTrace();
            isDestroyed = false;
        }
        return isDestroyed;
    }

    // update 1 or more fields of a single row
    public static boolean update(int id, String tableName, HashMap<String, Object> hashmap) {
        Connection connection = null;
        Statement stmt = null;
        boolean isUpdated = true;

        try {
            connection = Connector.getConnection();
            stmt = connection.createStatement();

            HashMap<String, String> SQLHashmap = convertJavaSQL(hashmap);

            for (Object key : SQLHashmap.keySet()) {
                String column = (String) key;
                String newvalue = SQLHashmap.get(key);
                stmt.execute("UPDATE " + tableName + " SET " + column + " = " + newvalue + " WHERE ID = " + id);
            }
            
            DatabaseMetaData md = connection.getMetaData();
            if (md.getColumns(null, null, tableName, "creation_date").next() ){
                stmt.execute("UPDATE " + tableName + " SET last_modified_date = NOW() WHERE ID = " + id);
            }
            
            
            

        } catch (SQLException e) {
            e.printStackTrace();
            isUpdated = false;
        }
        return isUpdated;
    }

    // java (firstName:"bob") --> sql (first_name: "bob")
    public static HashMap<String, String> convertJavaSQL(HashMap<String, Object> original) {
        boolean DEBUG = true;
        HashMap<String, String> converted = new HashMap<String, String>();

        for (String key : original.keySet()) // field, value pair
        {
            Object value = original.get(key); // value could be null

            String type = "";
            if (value != null) {
                type = value.getClass().getSimpleName();
            }

            String key_underscore = toUnderscoreCase(key);
            switch (type) {
            case "": // null
                break;
            case "Boolean":
                converted.put(key_underscore, "'" + value.toString() + "'");
                break;
            case "Integer":
                converted.put(key_underscore, Integer.toString((int) value));
                break;
            case "Double":
                converted.put(key_underscore, Double.toString((double) value));
                break;
            case "String":
                converted.put(key_underscore, "'" + original.get(key) + "'");
                break;
            case "Date":
                Date dt = (Date) value;
                DateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                converted.put(key_underscore, "'" + parser.format(dt) + "'");
                break;
            default:
                System.out.println("Manager.java doesnt know this type: " + key + "=" + value);
                break;
            }
        }

        if (DEBUG) {
            for (String s : converted.keySet()) {
                System.out.println("key:" + s + " " + converted.get(s));
            }
        }
        return converted;
    }
    
    
    public static Object convertJavaToSql(Object value){
        String type = "";
        if (value != null) {
            type = value.getClass().getSimpleName();
        }
        switch (type) {
            case "": // null
                break;
            case "Boolean":
                return "'" + value.toString() + "'";
            case "Integer":
                return Integer.toString((int) value);
            case "Double":
                return Double.toString((double) value);
            case "String":
                return "'" + value + "'";
            case "Date":
                Date dt = (Date) value;
                DateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return "'" + parser.format(dt) + "'";
            default:
                System.out.println("Manager.java doesnt know this type:" + value);
        }
        return null;
    }

    // sql (first_name: "bob" varchar) --> java (firstName: "bob" as string)
    private static HashMap<String, Object> convertSQLJava(ResultSet resultset) throws SQLException {
        HashMap<String, Object> converted = new HashMap<String, Object>();
        ResultSetMetaData rsmd = resultset.getMetaData();
        int columnCount = rsmd.getColumnCount();
        for (int i = 1; i < columnCount + 1; i++) {
            String columnName = rsmd.getColumnName(i); // underscore_case
            String columnName_camel = toCamelCase(columnName); // columnName in
            // java var style

            String type = rsmd.getColumnTypeName(i);

            switch (type) {
            case "INT UNSIGNED":
                converted.put(columnName_camel, (int) resultset.getLong(columnName));
                break;
            case "INT":
                converted.put(columnName_camel, (Integer) resultset.getInt(columnName));
                break;
            case "TINYINT": // boolean
                converted.put(columnName_camel, resultset.getBoolean(columnName));
                break;
            case "VARCHAR":
                converted.put(columnName_camel, resultset.getString(columnName));
                break;
            case "DATETIME":
                converted.put(columnName_camel, (Date) resultset.getDate(columnName));
                break;
            case "TIMESTAMP":
                converted.put(columnName_camel, (Date) resultset.getTimestamp(columnName));
                break;
            default:
                System.out.println("Manager.java doesnt know this type: " + columnName + "=" + type + "="
                        + resultset.getObject(columnName));
                break;
            }

        }

        return converted;
    }

    // first_name_field --> firstNameField
    public static String toCamelCase(String underscore_case) {
        String[] parts = underscore_case.split("_");
        String camel = "";
        // convert the 1st character of all part (separated by '_') to upper
        // case
        for (String part : parts) {
            camel = camel + part.substring(0, 1).toUpperCase() + part.substring(1).toLowerCase();
        }
        // except the 1st char
        return camel.substring(0, 1).toLowerCase() + camel.substring(1);
    }

    // firstNameField --> first_name_field
    public static String toUnderscoreCase(String camel) {
        return camel.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

}
