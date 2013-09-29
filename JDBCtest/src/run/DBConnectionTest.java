package run; /**
 * Created with IntelliJ IDEA.
 * User: momoking
 * Date: 2013-09-28
 * Time: 11:46 PM
 * To change this template use File | Settings | File Templates.
 */
import java.sql.*;
import db.DBConnection;

public class DBConnectionTest {
    public static void main(String[] args){
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;


        try{
            connection = DBConnection.getConnection();
            stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT  from test data where id =" + 1);

            if(rs.next()){
                System.out.println(rs.getString("foo"));
            }

        }catch(SQLException e){
            e.printStackTrace();
        }

    }
}
