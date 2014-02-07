package com.sunnyd.database;

import com.google.common.collect.BiMap;

import java.util.Date;

import org.testng.annotations.BeforeClass;

public class SQLClassMapTest {

    static BiMap<Class, String> classMap;

    @BeforeClass
    public void setUp() {
        classMap.put( Integer.class, "INT" );
        classMap.put( Integer.class, "TINYINT" );
        classMap.put( Long.class, "INT UNSIGNED" ); // Long hold larger int value
        classMap.put( String.class, "VARCHAR" );
        classMap.put( Date.class, "DATETIME" );
        classMap.put( Date.class, "TIMESTMP" );
    }
}
