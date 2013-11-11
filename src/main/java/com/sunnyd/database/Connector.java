package com.sunnyd.database;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Properties;

public class Connector {

    private static SSHjdbcSession sshInstance;
    private static Connection connInstance;
    private static Session sessionInstance;

    static {
        String driverName = "com.mysql.jdbc.Driver";
        try {
            Class.forName( driverName );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public static SSHjdbcSession getConnection() {
        if ( sshInstance == null ) {
            sshInstance = doSshTunnel();
        }
        String databaseName = "soen387l";
        String username = "soen387l";
        String password = "h82j76";
        String url = "jdbc:mysql://" + sshInstance.getServer() + ":" + sshInstance.getPort() + "/" + databaseName; // for Mysql

        try {
            if ( connInstance == null ) {
                connInstance = DriverManager.getConnection( url, username, password );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        sshInstance.setConnection( connInstance );

        return sshInstance;

    }

    public static void close( ResultSet rs, Statement stmt, SSHjdbcSession ssHsession ) {
        try {
            if ( rs != null ) {
                rs.close();
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        try {
            if ( stmt != null ) {
                stmt.close();
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        try {
            if ( ssHsession != null ) {
                ssHsession.getConnection().close();
            }
            ssHsession.getSession().disconnect();
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }

    public static void printRs( ResultSet rs ) {
        try {
            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            StringBuffer sb = new StringBuffer();
            while ( rs.next() ) {
                for ( int i = 1; i <= cols; i++ ) {
                    String columnName = meta.getColumnName( i );
                    sb.append( columnName + "=" );
                    sb.append( rs.getString( columnName ) + "  " );
                }
                sb.append( "\n" );
            }
            System.out.print( sb.toString() );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private static SSHjdbcSession doSshTunnel() {
        String strSshUser = "w_khed"; // SSH loging username
        String strSshPassword = "Ks%7/${v"; // SSH login password
        String strSshHost = "login.encs.concordia.ca";
        int nSshPort = 22; // remote SSH host port number
        String strRemoteHost = "clipper.encs.concordia.ca";
        int nLocalPort = findUnusedPort(); // local port number use to bind SSH
        // tunnel
        String localServer = "127.0.0.1";
        int nRemotePort = 3306; // remote port number of your database
        JSch jsch = new JSch();

        try {
            if ( sessionInstance == null ) {

                sessionInstance = jsch.getSession( strSshUser, strSshHost, nSshPort );
                sessionInstance.setPassword( strSshPassword );

                Properties config = new Properties();
                config.put( "StrictHostKeyChecking", "no" );
                sessionInstance.setConfig( config );
                sessionInstance.connect();
                sessionInstance.setPortForwardingL( nLocalPort, strRemoteHost, nRemotePort );
            }

        } catch ( JSchException e ) {
            e.printStackTrace();
        }

        if (sshInstance == null)
             sshInstance = new SSHjdbcSession( sessionInstance, localServer, nLocalPort );
        return sshInstance;
    }

    private static int findUnusedPort() {
        final int startingPort = 1025;
        final int endingPort = 1200;
        for ( int port = 1025; port < 1200; port++ ) {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket( port );
                return port;
            } catch ( IOException e ) {
                System.out.println( "Port " + port + "is currently in use, retrying port " + port + 1 );
            } finally {
                // Clean up
                if ( serverSocket != null ) {
                    try {
                        serverSocket.close();
                    } catch ( IOException e ) {
                        throw new RuntimeException( "Unable to close socket on port" + port, e );
                    }
                }
            }
        }
        throw new RuntimeException( "Unable to find open port between " + startingPort + " and " + endingPort );
    }



}
