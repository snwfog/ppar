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
import com.sunnyd.database.query.SQLTableMetaData;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Manager {
    static final Logger logger = LoggerFactory.getLogger( Manager.class );

    private static Connection connection;

    static {
        try {
            connection = Connector.getConnection();
        } catch ( SQLException e ) {
            logger.error( "Failed statically initiate database connection." );
        }

    }

  // find by id, return single row
  public static Map<String, Object> find(int id, String tableName)
  {
      return find("SELECT * FROM " + tableName + " WHERE ID = " + id);
  }
  
  
  public static Map<String, Object> find(String sqlQuery){
      Statement stmt = null;
      ResultSet rs = null;
      try
      {
            if(connection.isClosed()){
                connection = Connector.getConnection();
            }
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sqlQuery);
            
            if (rs.next())
            {
              return convertSQLToJava(rs);
            }

      }
      catch (SQLException e)
      {
        e.printStackTrace();
      }
      
      return null;
  }
  
  public static ResultSet rawSQLfind(String queryString)
  {
    //Connection remains open
    Statement stmt = null;
    ResultSet rs = null;

    try
    {
        if(connection.isClosed()){
            connection = Connector.getConnection();
        }
        stmt = connection.createStatement();
        rs = stmt.executeQuery(queryString);
        return rs;
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  // find by id, return single row
  public static Integer[] find(String tableName, String column, Object value)
  {
    Statement stmt = null;
    ResultSet rs = null;
    value = convertJavaToSql(value);
    try
    {
      if(connection.isClosed()){
        connection = Connector.getConnection();
      }  
        
      ArrayList<Integer> result = new ArrayList<Integer>();
      stmt = connection.createStatement();
      rs = stmt.executeQuery("SELECT * FROM " + tableName + " WHERE " + column + "=" + value);

      while (rs.next())
      {
        result.add(rs.getInt("id"));
      }
      return (Integer[]) result.toArray(new Integer[result.size()]);

    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  // find multiple by criteria
  public static ArrayList<Map<String, Object>> findAll(String tableName, Map<String, Object> conditions)
  {
    ArrayList<Map<String, Object>> results = new ArrayList<Map<String, Object>>();

    String where = "";
    Map<String, String> SQLConditions = convertJavaToSQL(conditions);

    if (conditions != null)
    {
      //Return all if condition is null
      for (String key : SQLConditions.keySet())
      {
//        if (conditions.get(toCamelCase(key)) instanceof String
//            && !SQLTableMetaData.hasUniqueKeyConstraint(tableName, key))
//          where += key + " == " + SQLConditions.get(key) + "%' AND ";
//        else
          where += key + " = '" + SQLConditions.get(key) + "' AND ";
      }

      // remove trailing comma
      where = where.replaceAll(" AND $", ""); // col1, col2, col3

      if (!where.equals(""))
      {
        where = " WHERE " + where;
      }
    }


    return findAll("SELECT * FROM " + tableName + where);
  }
  
  public static ArrayList<Map<String,Object>> findAll(String sqlQuery){
      Statement stmt = null;
      ResultSet rs = null;
      ArrayList<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
      
      try
      {
        if(connection.isClosed()){
            connection = Connector.getConnection();
        }
        stmt = connection.createStatement();
        logger.info(sqlQuery);
        rs = stmt.executeQuery(sqlQuery);
        while (rs.next())
        {
          Map<String, Object> row = new HashMap<String, Object>();
          row = convertSQLToJava(rs);
          results.add(row);
        }
      }
      catch (SQLException e)
      {
        e.printStackTrace();
      }
      return results;
  }


    public static <T extends Base> int save( Class<T> klazz, Map<String, Object> hashMap ) {
        try {
            if(connection.isClosed()){
                connection = Connector.getConnection();
            }        
            Funnel<T> funnel = FunnelFactory.getInstance( klazz );
            return Manager.save( klazz, hashMap, funnel );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static <T extends Base> int save( Class<T> klazz, Map<String, Object> hashMap, Funnel<T> funnel ) {
        ResultSet rs = null;
        String tableName = BaseHelper.getClassTableName( klazz );

        int id = 0;

        try {
            // no id is provided (means auto-gen id)
            if ( !hashMap.containsKey( "id" ) ) {
                PreparedStatement stm = prepareInsertStatementSQL( hashMap, tableName, true );
                stm.executeUpdate();
                rs = stm.getGeneratedKeys();
                if ( rs.next() ) {
                    id = rs.getInt( 1 );
                }
            } else // id is provided (means
            {
                PreparedStatement stm = prepareInsertStatementSQL( hashMap, tableName, false );
                id = stm.executeUpdate() != 0 ? (int) hashMap.get( "id" ) : 0;
            }
        } catch ( SQLException e ) {
            e.printStackTrace();
        }

        try {
            T model = klazz.getConstructor( Map.class ).newInstance( hashMap );
            String thisModelSha = Manager.getSha( model, funnel );
            Manager.updateSha( id, tableName, thisModelSha );
        } catch ( InvocationTargetException e ) {
            e.printStackTrace();
        } catch ( NoSuchMethodException e ) {
            e.printStackTrace();
        } catch ( InstantiationException e ) {
            e.printStackTrace();
        } catch ( IllegalAccessException e ) {
            e.printStackTrace();
        }

        return id;
    }

    private static PreparedStatement prepareInsertStatementSQL( Map<String, Object> original, String tableName,
            boolean generateKey ) {
        if ( original == null ) {
            return null;
        }

        PreparedStatement stmt = null;

        try {
            DatabaseMetaData md = connection.getMetaData();
            if ( md.getColumns( null, null, tableName, "creation_date" ).next() ) {
                original.put( "creationDate", new Date() );
                original.put( "lastModifiedDate", new Date() );
            }

            String columns = "(";
            String values = "(";
            for ( String key : original.keySet() ) // field, value pair
            {
                columns += toUnderscoreCase( key ) + ",";
                values += "?,";
            }
            columns = columns.substring( 0, columns.length() - 1 ) + ")";
            values = values.substring( 0, values.length() - 1 ) + ")";

            logger.info( "INSERT INTO " + tableName + " " + columns + " VALUES " + values );

            if ( generateKey ) {
                stmt = connection.prepareStatement( "INSERT INTO " + tableName + " " + columns + " VALUES " + values,
                        Statement.RETURN_GENERATED_KEYS );
            } else {
                stmt = connection.prepareStatement( "INSERT INTO " + tableName + " " + columns + " VALUES " + values );
            }

            setJavaToSQL( stmt, original, tableName );

        } catch ( SQLException e ) {
            return null;
        }

        return stmt;
    }



    private static PreparedStatement prepareUpdateStatementSQL( Integer id, Map<String, Object> original,
            String tableName) {

        if ( original == null ) {
            return null;
        }

        PreparedStatement stmt = null;
        try {
                assert !connection.isClosed(); 
                
                DatabaseMetaData md = connection.getMetaData();
                if ( md.getColumns( null, null, tableName, "last_modified_date" ).next() ) {
                    original.put( "lastModifiedDate", new Date() );
                }
        
        
                String sets = "";
                for ( String key : original.keySet() ) // field, value pair
                {
                    sets += toUnderscoreCase( key ) + " = ?,";
                }
                sets = sets.substring( 0, sets.length() - 1 );
                System.out.println( "UPDATE " + tableName + " SET " + sets + " WHERE ID =" + id );
                stmt = connection.prepareStatement( "UPDATE " + tableName + " SET " + sets + " WHERE ID =" + id );
                setJavaToSQL( stmt, original, tableName );

        } catch ( SQLException e ) {
            return null;
        }

        return stmt;
    }

    private static void setJavaToSQL( PreparedStatement stm, Map<String, Object> original, String tableName) {
        try {
            
            assert !connection.isClosed();
            
            int counter = 1;
            for ( String key : original.keySet() ) // field, value pair
            {
                Object value = original.get( key ); // value could be null
                String type = "";
                if ( value != null ) {
                    type = value.getClass().getSimpleName();
                    switch ( type ) {
                        case "Boolean":
                            stm.setBoolean( counter, (Boolean) value );
                            break;
                        case "Integer":
                            stm.setInt( counter, (Integer) value );
                            break;
                        case "Double":
                            stm.setDouble( counter, (Double) value );
                            break;
                        case "String":
                            stm.setString( counter, (String) value );
                            break;
                        case "Date":
                        case "Timestamp":
                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String currentTime = sdf.format(value);
                            stm.setTimestamp(counter, Timestamp.valueOf(currentTime) );   
                            break;
                        default:
                            logger.error(
                                    "Manager.java doesn't know how to convert this type: " + key + "(" + type + ") " +
                                            original.get( key ) );
                            break;
                    }
                } else {
                    DatabaseMetaData md = connection.getMetaData();
                    ResultSet column = md.getColumns( null, null, tableName, toUnderscoreCase( key ) );
                    if ( column.next() ) {
                        stm.setNull( counter, column.getType() );
                    }
                }
                counter += 1;
            }
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }

    public static int save( String tableName, Map<String, Object> hashMap )
            throws MySQLIntegrityConstraintViolationException {
        //for table not related to any model (i.e relation table)
        Statement stmt = null;
        ResultSet rs = null;

        String columns = "";
        String values = "";

        int id = 0;

        try {          
            if(connection.isClosed()){
                connection = Connector.getConnection();
            }
            Map<String, String> SQLHashmap = convertJavaToSQL( hashMap );

            DatabaseMetaData md = connection.getMetaData();
            if ( md.getColumns( null, null, tableName, "creation_date" ).next() ) {
                String date = (new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" )).format( new Date() );
                SQLHashmap.put( "creation_date", date );
                SQLHashmap.put( "last_modified_date", date );
            }


            // get column value pairs from hashmap as val,val,val...
            for ( String key : SQLHashmap.keySet() ) {
                columns += key + ",";
                values += "" + SQLHashmap.get( key ) + ",";
            }
            // remove trailing comma
            columns = columns.replaceAll( ",$", "" );
            values = values.replaceAll( ",$", "" );

            stmt = connection.createStatement();

            // no id is provided (means auto-gen id)
            if ( !hashMap.containsKey( "id" ) ) {
                System.out.println( "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + values + ")" );
                stmt.executeUpdate( "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + values + ")",
                        Statement.RETURN_GENERATED_KEYS );
                rs = stmt.getGeneratedKeys();
                if ( rs.next() ) {
                    id = rs.getInt( 1 );
                }
            } else // id is provided (means
            {
                id = stmt.executeUpdate( "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + values + ")" ) !=
                        0 ? (int) hashMap.get( "id" ) : 0;
            }
        } catch (MySQLIntegrityConstraintViolationException e){
            throw new com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException();
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
        return id;
    }

    public static boolean destroy( int id, String tableName ) {
        Statement stmt = null;
        boolean isDestroyed = true;

        try {
            if(connection.isClosed()){
                connection = Connector.getConnection();
            }
            stmt = connection.createStatement();
            System.out.println("DELETE FROM " + tableName + " WHERE ID = " + id);
            stmt.execute( "DELETE FROM " + tableName + " WHERE ID = " + id );
        } catch ( SQLException e ) {

            isDestroyed = false;
        }
        return isDestroyed;
    }

    public static ArrayList<HashMap<String, Object>> destroy( String tableName, Map<String, Object> conditions ) {
        Statement stmt = null;
        ResultSet rs = null;
        ArrayList<HashMap<String, Object>> results = new ArrayList<HashMap<String, Object>>();

        String where = "";

        Map<String, String> SQLConditions = convertJavaToSQL( conditions );

        for ( String key : SQLConditions.keySet() ) {
            where += key + " = '" + SQLConditions.get( key ) + "' AND ";
        }
        // remove trailing comma
        where = where.replaceAll( " AND $", "" ); // col1, col2, col3

        if ( !where.equals( "" ) ) {
            where = " WHERE " + where;
        }

        //TODO: Should you return all result when variable "where" is ""(empty)?
        try {
            if(connection.isClosed()){
                connection = Connector.getConnection();
            }
            stmt = connection.createStatement();
            stmt.execute( "DELETE FROM " + tableName + where );
        } catch ( SQLException e ) {

        }
        return results;

    }

    // update 1 or more fields of a single row
    public static <T extends Base> boolean update( int id, Class<T> klazz, Map<String, Object> hashMap ) {
        String tableName = BaseHelper.getClassTableName( klazz );
        boolean isUpdated = false;

        try {            
            if(connection.isClosed()){
                connection = Connector.getConnection();
            }
            
            assert !connection.isClosed();
            
            // Acquire mutex lock
            Manager.acquireLock( id, tableName );

            // Check for optimistic lock
            if ( !Manager.checkIntegrity( id, klazz, hashMap ) ) {
                return false;
            }
            
            PreparedStatement stm = prepareUpdateStatementSQL( id, hashMap, tableName );
            stm.executeUpdate();

            Funnel<T> funnel = FunnelFactory.getInstance( klazz );
            T model = klazz.getConstructor( Map.class ).newInstance( hashMap );
            String thisModelSha = Manager.getSha( model, funnel );
            Manager.updateSha( id, tableName, thisModelSha );

            // Set is updated true
            isUpdated = true;
            

        } catch ( SQLException | VersionChangedException e ) {
            logger.error("Could not update the model, semaphore is locked ", e);
        } catch ( InvocationTargetException | NoSuchMethodException
                | InstantiationException | IllegalAccessException e ) {
            logger.error("Problem with model serialization and hash id generation, possible unstable model ", e);
        }
        
        try {
            if(connection.isClosed()){
                connection = Connector.getConnection();
            }
            
            Manager.releaseLock( id, tableName );          
        } catch ( SQLException e ) {
            logger.error( "Could not release lock on model active record model, possible locked forever ", e );
            throw Throwables.propagate( e );
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

            String keyUnderscore = toUnderscoreCase( key );
            switch ( type ) {
                case "": // null
                    converted.put( keyUnderscore, null );
                    break;
                case "Boolean":
                    converted.put( keyUnderscore, value.toString() );
                    break;
                case "Integer":
                    converted.put( keyUnderscore, Integer.toString( (Integer) value ) );
                    break;
                case "Double":
                    converted.put( keyUnderscore, Double.toString( (double) value ) );
                    break;
                case "String":
                    converted.put( keyUnderscore, (String) original.get( key ) );
                    break;
                case "Date":
                    Date dt = (Date) value;
                    DateFormat parser = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
                    converted.put( keyUnderscore, parser.format( dt ) );
                    break;
                default:
                    logger.error(
                            "Manager.java doesn't know how to convert this type: " + keyUnderscore + "(" + type + ") " +
                                    original.get( key ) );
                    break;
            }

            logger.info( "Key: " + keyUnderscore + " Value:" + converted.get( keyUnderscore ) );
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
            String columnNameCamelCase = toCamelCase( columnName ); // columnName in
            // java var style
            String type = rsmd.getColumnTypeName( i );

            switch ( type ) {
                case "INT UNSIGNED":
                    converted.put( columnNameCamelCase, resultset.getObject( columnName ) );
                    break;
                case "INT":
                    converted.put( columnNameCamelCase, resultset.getObject( columnName ) );
                    break;

                case "DOUBLE":
                    converted.put( columnNameCamelCase, resultset.getDouble( columnName ) );
                    break;

                case "FLOAT"://TODO: replace second method with first
                    converted.put( columnNameCamelCase, resultset.getObject( columnName ) == null ? null :
                            Double.parseDouble( resultset.getObject( columnName ).toString() ) );
                    //converted.put( columnName_camel, resultset.getFloat( columnName ) );
                    break;

                case "TINYINT": // boolean
                    converted.put( columnNameCamelCase, resultset.getBoolean( columnName ) );
                    break;
                case "VARCHAR":
                case "CHAR":
                    converted.put( columnNameCamelCase, resultset.getString( columnName ) );
                    break;
                case "DATE":
                case "DATETIME":
                    converted.put( columnNameCamelCase, resultset.getDate( columnName ) );
                    break;
                case "TIMESTAMP":
                    converted.put( columnNameCamelCase, resultset.getTimestamp( columnName ) );
                    break;
                default:
                    logger.error(
                            "Manager.java doesn't know how to convert this type: " + columnName + "(" + type + ") " +
                                    resultset );
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

    private static synchronized void acquireLock( int id, String tableName ) throws SQLException {
        if(connection.isClosed()){
            connection = Connector.getConnection();
        }
        
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery( "SELECT semaphore FROM " + tableName + " WHERE id = " + id );
        if ( !rs.next() ) {
            throw new NonExistingRecordException( id, tableName );
        } else if ( rs.getInt( 1 ) != 0 ) {
            throw new CannotAcquireSemaphoreException( id, tableName );
        } else {
            stmt.executeUpdate( "UPDATE " + tableName + " SET semaphore = 1 WHERE id = " + id );
        }     
    }

    private static synchronized void releaseLock( int id, String tableName ) throws SQLException {
        if(connection.isClosed()){
            connection = Connector.getConnection();
        }
        
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery( "SELECT semaphore FROM " + tableName + " WHERE id = " + id );
        if ( !rs.next() ) {
            throw new NonExistingRecordException( id, tableName );
        } else if ( rs.getInt( 1 ) != 1 ) {
            throw new CannotReleaseSemaphoreException( id, tableName );
        } else {
            stmt.executeUpdate( "UPDATE " + tableName + " SET semaphore = 0 WHERE id = " + id );
        }
    }

    private static <T extends Base> String getSha( T model, Funnel<T> funnelKlazz ) {
        Hasher hasher = Hashing.sha256().newHasher();
        String newHashCode = "";
        newHashCode = hasher.putObject( model, funnelKlazz ).hash().toString();

        return newHashCode;
    }

    private static <T extends Base> boolean checkIntegrity( int id, Class<T> klazz, Map<String, Object> map ) {
        Constructor<T> cons = null;
        String tableName = BaseHelper.getClassTableName( klazz );
        try {
            cons = klazz.getConstructor( Map.class );
            Funnel<T> funnel = FunnelFactory.getInstance( klazz );
            T latest = cons.newInstance( Manager.find( id, tableName ) );
            String newHashCode = Manager.getSha( latest, funnel );
            T old = cons.newInstance( Manager.find( id, tableName ) );
            String oldHashCode = Manager.getSha( old, funnel );
            if ( !oldHashCode.equalsIgnoreCase( newHashCode ) ) {
                throw new VersionChangedException( id, tableName, newHashCode );
            }
        } catch ( NoSuchMethodException e ) {

        } catch ( InvocationTargetException e ) {

        } catch ( InstantiationException e ) {

        } catch ( IllegalAccessException e ) {

        }

        return true;
    }

    private static boolean updateSha( int id, String tableName, String sha ) {
        Statement stmt = null;
        try {
            if(connection.isClosed()){
                connection = Connector.getConnection();
            }
            stmt = connection.createStatement();
            stmt.executeUpdate( "UPDATE " + tableName + " SET etag = '" + sha + "' WHERE ID = " + id );
        } catch ( SQLException e ) {

            return false;
        }

        return true;
    }

}
