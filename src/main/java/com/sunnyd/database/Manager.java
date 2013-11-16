package com.sunnyd.database;

import com.google.common.base.Throwables;
import com.google.common.hash.Funnel;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.sunnyd.Base;
import com.sunnyd.BaseHelper;
import com.sunnyd.database.concurrency.exception.CannotAcquireSemaphoreException;
import com.sunnyd.database.concurrency.exception.CannotReleaseSemaphoreException;
import com.sunnyd.database.concurrency.exception.NonExistingRecordException;
import com.sunnyd.database.concurrency.exception.VersionChangedException;
import com.sunnyd.database.hash.FunnelFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Manager {

    //public static void main( String[] args ) {
    //    Manager.checkIntegrity( 1, "peers", new HashMap<String, Object>() );
    //}

    // find by id, return single row
    public static Map<String, Object> find( int id, String tableName ) {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            connection = Connector.getConnection();
            stmt = connection.createStatement();
            rs = stmt.executeQuery( "SELECT * FROM " + tableName + " WHERE ID = " + id );

            if ( rs.next() ) {
                return convertSQLToJava( rs );
            }

        } catch ( SQLException e ) {
            e.printStackTrace();
        }
        return null;
    }

    // find by id, return single row
    public static Integer[] find( String tableName, String column, Object value ) {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        value = convertJavaToSql( value );
        try {
            ArrayList<Integer> result = new ArrayList<Integer>();
            connection = Connector.getConnection();
            stmt = connection.createStatement();
            rs = stmt.executeQuery( "SELECT * FROM " + tableName + " WHERE " + column + "=" + value );

            while ( rs.next() ) {
                result.add( rs.getInt( "id" ) );
            }
            return (Integer[]) result.toArray( new Integer[result.size()] );

        } catch ( SQLException e ) {
            e.printStackTrace();
        }
        return null;
    }

    // find multiple by criteria
    public static ArrayList<Map<String, Object>> findAll( String tableName, Map<String, Object> conditions ) {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        ArrayList<Map<String, Object>> results = new ArrayList<Map<String, Object>>();

        String where = "";
        Map<String, String> SQLConditions = convertJavaToSQL( conditions );
        
        if(conditions != null){
            //Return all if condition is null  
            for ( String key : SQLConditions.keySet() ) {
                where += key + " = " + SQLConditions.get( key ) + " AND ";
            }
            // remove trailing comma
            where = where.replaceAll( " AND $", "" ); // col1, col2, col3
    
            if ( !where.equals( "" ) ) {
                where = " WHERE " + where;
            }
        }

        //TODO: Should you return all result when variable "where" is ""(empty)?
        try {
            connection = Connector.getConnection();
            stmt = connection.createStatement();
            rs = stmt.executeQuery( "SELECT * FROM " + tableName + where );
            while ( rs.next() ) {
                try {
                    connection = Connector.getConnection();
                    stmt = connection.createStatement();
                    rs = stmt.executeQuery( "SELECT * FROM " + tableName + where );
                    while ( rs.next() ) {
                        Map<String, Object> row = new HashMap<String, Object>();
                        row = convertSQLToJava( rs );
                        results.add( row );
                    }
                } catch ( SQLException e ) {
                    e.printStackTrace();
                }
                return results;

            }
        } catch ( SQLException e ) {
            e.printStackTrace();
        }

        return results;
    }

    public static <T extends Base> int save( Class<T> klazz, Map<String, Object> hashMap ) {
        String klazzName = klazz.getSimpleName();
        String funnelName = "com.sunnyd.database.hash."+ klazzName + "Funnel";

        Class<?> funnelClass = null;
        Funnel<T> funnel = null;
        try {
            funnelClass = Class.forName( funnelName );
            funnel = (Funnel<T>) funnelClass.newInstance();

        } catch ( ClassNotFoundException e ) {
            e.printStackTrace();
        } catch ( InstantiationException e ) {
            e.printStackTrace();
        } catch ( IllegalAccessException e ) {
            e.printStackTrace();
        }


        return Manager.save( klazz, hashMap, funnel );
    }

    private static <T extends Base> int save( Class<T> klazz, Map<String, Object> hashMap, Funnel<T> funnel ) {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;

        String columns = "";
        String values = "";
        String tableName = BaseHelper.getClassTableName( klazz );

        int id = 0;

        try {
            connection = Connector.getConnection();
            Map<String, String> SQLHashmap = convertJavaToSQL( hashMap );

            DatabaseMetaData md = connection.getMetaData();
            if ( md.getColumns( null, null, tableName, "creation_date" ).next() ) {
                SQLHashmap.put( "creation_date", "NOW()" );
                SQLHashmap.put( "last_modified_date", "NOW()" );
            }


            // get column value pairs from hashmap as val,val,val...
            for ( String key : SQLHashmap.keySet() ) {
                columns += key + ",";
                values += SQLHashmap.get( key ) + ",";
            }
            // remove trailing comma
            columns = columns.replaceAll( ",$", "" );
            values = values.replaceAll( ",$", "" );

            stmt = connection.createStatement();

            // no id is provided (means auto-gen id)
            if ( !hashMap.containsKey( "id" ) ) {
                System.out.println( "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + values + ")" );
                stmt.executeUpdate( "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + values + ")", Statement.RETURN_GENERATED_KEYS );
                rs = stmt.getGeneratedKeys();
                if ( rs.next() ) {
                    id = rs.getInt( 1 );
                }
            } else // id is provided (means
            {
                id = stmt.executeUpdate( "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + values + ")" ) != 0 ? (int) hashMap.get( "id" ) : 0;
            }
        } catch ( SQLException e ) {
            e.printStackTrace();
        }

        try
        {
            T model = klazz.getConstructor( Map.class ).newInstance( hashMap );
            String thisModelSha = Manager.getSha( model, funnel );
            Manager.updateSha( id, tableName, thisModelSha );


        } catch ( NoSuchMethodException e ) {
            e.printStackTrace();
        } catch ( InvocationTargetException e ) {
            e.printStackTrace();
        } catch ( InstantiationException e ) {
            e.printStackTrace();
        } catch ( IllegalAccessException e ) {
            e.printStackTrace();
        }
        return id;
    }
    
    
    public static int save( String tableName, Map<String, Object> hashMap) throws MySQLIntegrityConstraintViolationException {
    //for table not related to any model (i.e relation table)
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;

        String columns = "";
        String values = "";

        int id = 0;

        try {
            connection = Connector.getConnection();
            Map<String, String> SQLHashmap = convertJavaToSQL( hashMap );

            DatabaseMetaData md = connection.getMetaData();
            if ( md.getColumns( null, null, tableName, "creation_date" ).next() ) {
                SQLHashmap.put( "creation_date", "NOW()" );
                SQLHashmap.put( "last_modified_date", "NOW()" );
            }


            // get column value pairs from hashmap as val,val,val...
            for ( String key : SQLHashmap.keySet() ) {
                columns += key + ",";
                values += SQLHashmap.get( key ) + ",";
            }
            // remove trailing comma
            columns = columns.replaceAll( ",$", "" );
            values = values.replaceAll( ",$", "" );

            stmt = connection.createStatement();

            // no id is provided (means auto-gen id)
            if ( !hashMap.containsKey( "id" ) ) {
                System.out.println( "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + values + ")" );
                stmt.executeUpdate( "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + values + ")", Statement.RETURN_GENERATED_KEYS );
                rs = stmt.getGeneratedKeys();
                if ( rs.next() ) {
                    id = rs.getInt( 1 );
                }
            } else // id is provided (means
            {
                id = stmt.executeUpdate( "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + values + ")" ) != 0 ? (int) hashMap.get( "id" ) : 0;
            }
        } catch ( SQLException e ) {
            if(e.getClass().getCanonicalName() == "com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException"){
                    throw new com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException();
            }
            e.printStackTrace();
        }
        return id;
    }

    public static boolean destroy( int id, String tableName ) {
        Connection connection = null;
        Statement stmt = null;
        boolean isDestroyed = true;

        try {
            connection = Connector.getConnection();
            stmt = connection.createStatement();
            stmt.execute( "DELETE FROM " + tableName + " WHERE ID = " + id );
        } catch ( SQLException e ) {
            e.printStackTrace();
            isDestroyed = false;
        }
        return isDestroyed;
    }

    public static ArrayList<HashMap<String, Object>> destroy( String tableName, Map<String, Object> conditions ) {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        ArrayList<HashMap<String, Object>> results = new ArrayList<HashMap<String, Object>>();

        String where = "";

        Map<String, String> SQLConditions = convertJavaToSQL( conditions );

        for ( String key : SQLConditions.keySet() ) {
            where += key + " = " + SQLConditions.get( key ) + " AND ";
        }
        // remove trailing comma
        where = where.replaceAll( " AND $", "" ); // col1, col2, col3

        if ( !where.equals( "" ) ) {
            where = " WHERE " + where;
        }

        //TODO: Should you return all result when variable "where" is ""(empty)?
        try {
            connection = Connector.getConnection();
            stmt = connection.createStatement();
            stmt.execute( "DELETE FROM " + tableName + where );
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
        return results;

    }

    // update 1 or more fields of a single row
    public static <T extends Base> boolean update( int id, Class<T> klazz, Map<String, Object> hashMap ) {
        Connection connection = null;
        Statement stmt = null;
        String tableName = BaseHelper.getClassTableName( klazz );
        boolean isUpdated = true;

        try {
            // Acquire mutex lock
            Manager.acquireLock( id, tableName );

            // Check for optimistic lock
            if ( !Manager.checkIntegrity( id, klazz, hashMap ) ) {
                return false;
            }

            connection = Connector.getConnection();
            stmt = connection.createStatement();

            Map<String, String> SQLHashMap = convertJavaToSQL( hashMap );

            for ( Object key : SQLHashMap.keySet() ) {
                String column = (String) key;
                String newValue = SQLHashMap.get( key );
                stmt.execute( "UPDATE " + tableName + " SET " + column + " = " + newValue + " WHERE ID = " + id );
            }

            DatabaseMetaData md = connection.getMetaData();
            if ( md.getColumns( null, null, tableName, "creation_date" ).next() ) {
                stmt.execute( "UPDATE " + tableName + " SET last_modified_date = NOW() WHERE ID = " + id );
            }

            String klazzName = klazz.getSimpleName();
            String funnelName = "com.sunnyd.database.hash."+ klazzName + "Funnel";

            Funnel<T> funnel = FunnelFactory.getInstance( klazz );
            T model = klazz.getConstructor( Map.class ).newInstance( hashMap );
            String thisModelSha = Manager.getSha( model, funnel );
            Manager.updateSha( id, tableName, thisModelSha );

        } catch ( SQLException e ) {
            e.printStackTrace();
            isUpdated = false;
        } catch ( VersionChangedException e ) {
            isUpdated = false;
            Throwables.propagate( e );
        } catch ( InvocationTargetException e ) {
            e.printStackTrace();
        } catch ( NoSuchMethodException e ) {
            e.printStackTrace();
        } catch ( InstantiationException e ) {
            e.printStackTrace();
        } catch ( IllegalAccessException e ) {
            e.printStackTrace();
        } finally {
            // Release mutex lock
            Manager.releaseLock( id, tableName );
        }

        return isUpdated;
    }

    // java (firstName:"bob") --> sql (first_name: "bob")
    public static Map<String, String> convertJavaToSQL( Map<String, Object> original ) {
        boolean DEBUG = true;
        HashMap<String, String> converted = new HashMap<String, String>();
        if ( original == null ) {
            return converted;
        }
        for ( String key : original.keySet() ) // field, value pair
        {
            Object value = original.get( key ); // value could be null

            String type = "";
            if ( value != null ) {
                type = value.getClass().getSimpleName();
            }

            String key_underscore = toUnderscoreCase( key );
            switch ( type ) {
                case "": // null
                    converted.put( key_underscore, null );
                    break;
                case "Boolean":
                    converted.put( key_underscore, "'" + value.toString() + "'" );
                    break;
                case "Integer":
                    converted.put( key_underscore, Integer.toString( (Integer) value ) );
                    break;
                case "Double":
                    converted.put( key_underscore, Double.toString( (double) value ) );
                    break;
                case "String":
                    converted.put( key_underscore, "'" + original.get( key ) + "'" );
                    break;
                case "Date":
                    Date dt = (Date) value;
                    DateFormat parser = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
                    converted.put( key_underscore, "'" + parser.format( dt ) + "'" );
                    break;
                default:
                    System.out.println( "Manager.java doesnt know this type: " + key + "=" + value );
                    break;
            }
        }

        if ( DEBUG ) {
            for ( String s : converted.keySet() ) {
                System.out.println( "key:" + s + " " + converted.get( s ) );
            }
        }
        return converted;
    }

    public static Object convertJavaToSql( Object value ) {
        String type = "";
        if ( value != null ) {
            type = value.getClass().getSimpleName();
        }
        switch ( type ) {
            case "": // null
                return null;
            case "Boolean":
                return "'" + value.toString() + "'";
            case "Integer":
                return Integer.toString( (int) value );
            case "Double":
                return Double.toString( (double) value );
            case "String":
                return "'" + value + "'";
            case "Date":
                Date dt = (Date) value;
                DateFormat parser = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
                return "'" + parser.format( dt ) + "'";
            default:
                System.out.println( "Manager.java doesnt know this type:" + value );
        }
        return null;
    }

    // sql (first_name: "bob" varchar) --> java (firstName: "bob" as string)
    public static HashMap<String, Object> convertSQLToJava( ResultSet resultset ) throws SQLException {
        HashMap<String, Object> converted = new HashMap<String, Object>();
        ResultSetMetaData rsmd = resultset.getMetaData();
        int columnCount = rsmd.getColumnCount();

        for ( int i = 1; i < columnCount + 1; i++ ) {
            String columnName = rsmd.getColumnName( i ); // underscore_case
            String columnName_camel = toCamelCase( columnName ); // columnName in
            // java var style
            String type = rsmd.getColumnTypeName( i );

            switch ( type ) {
                case "INT UNSIGNED":
                    converted.put( columnName_camel, (int) resultset.getLong( columnName ) );
                    break;
                case "INT":
                    converted.put( columnName_camel, (Integer) resultset.getInt( columnName ) );
                    break;
                case "TINYINT": // boolean
                    converted.put( columnName_camel, resultset.getBoolean( columnName ) );
                    break;
                case "VARCHAR":
                case "CHAR":
                    converted.put( columnName_camel, resultset.getString( columnName ) );
                    break;
                case "DATETIME":
                    converted.put( columnName_camel, (Date) resultset.getDate( columnName ) );
                    break;
                case "TIMESTAMP":
                    converted.put( columnName_camel, (Date) resultset.getTimestamp( columnName ) );
                    break;
                default:
                    System.out.println( "Manager.java doesnt know this type: " + columnName + "=" + type + "=" + resultset.getObject( columnName ) );
                    break;
            }

        }

        return converted;
    }

    // first_name_field --> firstNameField
    public static String toCamelCase( String underscore_case ) {
        String[] parts = underscore_case.split( "_" );
        String camel = "";
        // convert the 1st character of all part (separated by '_') to upper
        // case
        for ( String part : parts ) {
            camel = camel + part.substring( 0, 1 ).toUpperCase() + part.substring( 1 ).toLowerCase();
        }
        // except the 1st char
        return camel.substring( 0, 1 ).toLowerCase() + camel.substring( 1 );
    }

    // firstNameField --> first_name_field
    public static String toUnderscoreCase( String camel ) {
        return camel.replaceAll( "([a-z])([A-Z])", "$1_$2" ).toLowerCase();
    }

    private static synchronized void acquireLock( int id, String tableName ) {
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = Connector.getConnection();
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT semaphore FROM " + tableName + " WHERE id = " + id );
            if ( !rs.next() ) {
                throw new NonExistingRecordException( id, tableName );
            } else if ( rs.getInt( 1 ) != 0 ) {
                throw new CannotAcquireSemaphoreException( id, tableName );
            } else {
                stmt.executeUpdate( "UPDATE " + tableName + " SET semaphore = 1 WHERE id = " + id );
            }
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }

    private static synchronized void releaseLock( int id, String tableName ) {
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = Connector.getConnection();
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT semaphore FROM " + tableName + " WHERE id = " + id );
            if ( !rs.next() ) {
                throw new NonExistingRecordException( id, tableName );
            } else if ( rs.getInt( 1 ) != 1 ) {
                throw new CannotReleaseSemaphoreException( id, tableName );
            } else {
                stmt.executeUpdate( "UPDATE " + tableName + " SET semaphore = 0 WHERE id = " + id );
            }
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }

    private static <T extends Base> String getSha( T model, Funnel<T> funnelKlazz ) {
        Hasher hasher = Hashing.sha256().newHasher();
        String newHashCode = "";
        newHashCode = hasher.putObject( model, funnelKlazz ).hash().toString();

        return newHashCode;
    }

    private static <T extends Base> boolean checkIntegrity( int id, Class<T> klazz, Map<String, Object> map ) {
        Connection conn = null;
        Statement stmt = null;


        Constructor<T> cons = null;
        String tableName = BaseHelper.getClassTableName( klazz );
        try {
            conn = Connector.getConnection();
            stmt = conn.createStatement();
            cons = klazz.getConstructor(Map.class);
            String funnelClass = "com.sunnyd.database.hash."+klazz.getSimpleName() + "Funnel";
            Funnel<T> funnel = FunnelFactory.getInstance( klazz );
            T latest = cons.newInstance( Manager.find(id, tableName) );
            String newHashCode = Manager.getSha( latest, funnel );
            T old = cons.newInstance( Manager.find( id, tableName ));
            String oldHashCode = Manager.getSha( old, funnel );
            if ( !oldHashCode.equalsIgnoreCase( newHashCode ) ) {
                throw new VersionChangedException( id, tableName, newHashCode );
            }
        } catch ( SQLException e) {
            e.printStackTrace();
        } catch ( NoSuchMethodException e ) {
            e.printStackTrace();
        } catch ( InvocationTargetException e ) {
            e.printStackTrace();
        } catch ( InstantiationException e ) {
            e.printStackTrace();
        } catch ( IllegalAccessException e ) {
            e.printStackTrace();
        }

        return true;
    }

    private static boolean updateSha( int id, String tableName, String sha ) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = Connector.getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate( "UPDATE " + tableName + " SET etag = '" + sha + "' WHERE ID = " + id );
        } catch ( SQLException e ) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
