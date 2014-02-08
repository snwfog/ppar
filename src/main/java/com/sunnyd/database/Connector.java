package com.sunnyd.database;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector {

    static final Logger logger = LoggerFactory.getLogger( Connector.class );
    final static String driverClass = "com.mysql.jdbc.Driver";
    private static String database = "soen387::a2";
    private static String host = "127.0.0.1";
    private static String port = "3306";
    private static String username = "root";
    private static String password = "root";
    private static String url;
    //static reference to itself
    private static Connector instance;
    private static BoneCP connectionPool;

    public static void main( String[] args ) throws SQLException {
        Connector.getConnection();
    }

    //private constructor
    private Connector() {
        // Initiate the URL from properties files or fall back to default
        Properties prop = new Properties();
        try {
            // Load the files from the default path
            // Which is located at resources/config
            prop.load( this.getClass().getResourceAsStream( "/config/database.properties" ) );

            // Statically init the JDBC class
            Class.forName( driverClass );

            // Set the URL string for the JDBC connection
            database = prop.getProperty( "database", database );
            host = prop.getProperty( "host", host );
            port = prop.getProperty( "port", port );
            username = prop.getProperty( "username", username );
            password = prop.getProperty( "password", password );
            url = String.format( "jdbc:mysql://%s:%s/%s", host, port, database );
        } catch ( ClassNotFoundException e ) {
            logger.error( "Could not load driver class" );
        } catch ( IOException e ) {
            logger.error( "Could not load the database.properties file" );
        }
    }

    public static Connection getConnection() throws SQLException {
        logger.warn( "Total connection created: " + Connector.getTotalCreatedConnections() );
        return createConnection().getConnection();
    }

    public static int getTotalCreatedConnections() throws SQLException {
        return createConnection().getTotalCreatedConnections();
    }

    private static BoneCP createConnection() throws SQLException {
        if ( instance == null ) {
            instance = new Connector();
        }

        if ( connectionPool == null ) {
            BoneCPConfig config = new BoneCPConfig();
            config.setJdbcUrl( url );
            config.setUsername( username );
            config.setPassword( password );
            config.setMaxConnectionsPerPartition( 100 );
            connectionPool = new BoneCP( config );
        }

        return connectionPool;
    }
}
