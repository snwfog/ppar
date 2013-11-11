package com.sunnyd.database;

import java.sql.Connection;

import com.jcraft.jsch.Session;

public class SSHjdbcSession {

    private Session session;
    private String server;
    private int port;
    private Connection connection;
    
    
    public SSHjdbcSession(Session session, String server, int port) {
        super();
        this.session = session;
        this.server = server;
        this.port = port;
    }
    
    public Session getSession() {
        return session;
    }
    public void setSession(Session session) {
        this.session = session;
    }
    public String getServer() {
        return server;
    }
    public void setServer(String server) {
        this.server = server;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    
    
    
}