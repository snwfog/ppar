package com.sunnyd.database;

import java.sql.PreparedStatement;

/**
 * Created by snw on 1/6/2014.
 */
public class SQLUtils {

    /**
     * Trivival way of getting only the SQL statement from a PreparedStatement for testing.
     *
     * @return
     */
    public static String prepareStatementToString(PreparedStatement stmt)
    {
        int indexOfFirstWhiteSpace = stmt.toString().indexOf( ' ' );
        return stmt.toString().substring( indexOfFirstWhiteSpace+1, stmt.toString().length() - 1);
    }

}
