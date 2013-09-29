package db;

//Step 1: Use interfaces from java.sql package
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    //static reference to itself
    private static DBConnection instance = new DBConnection();
    public static final String URL = "jdbc:mysql://localhost/harrydb";
    public static final String USER = "harry";
    public static final String PASSWORD = "harry";
    public static final String DRIVER_CLASS = "com.mysql.jdbc.Driver";

    //private constructor
    private DBConnection() {
        try {
            //Step 2: Load MySQL Java driver
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Connection createConnection() {

        Connection connection = null;
        try {
            //Step 3: Establish Java MySQL connection
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("ERROR: Unable to Connect to Database.");
        }
        return connection;
    }

    public static Connection getConnection() {
        return instance.createConnection();
    }
}