package com.sunnyd.database;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class DatabaseTestSetup {

    final Logger logger = LoggerFactory.getLogger( DatabaseTestSetup.class );
    protected Connection conn;
    protected Statement stmt;

    @BeforeClass
    public void setUp() throws SQLException {
        conn = Connector.getConnection();
        stmt = conn.createStatement();
    }

    @AfterClass
    public void tearDown() throws SQLException {
        logger.info( " Closing JDBC active connection. Good Bye!" );
        conn.close();
        stmt.close();
    }

}
