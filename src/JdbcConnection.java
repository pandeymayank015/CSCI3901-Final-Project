import java.sql.*;

public class JdbcConnection {
    Connection createConnection(){
         Connection connect = null;
         Statement statement = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception ex) {
            System.out.println("Unable to connect");
        }
        try
        {
            connect = DriverManager.getConnection("jdbc:mysql://db.cs.dal.ca:3306?serverTimezone=UTC&useSSL=false" , "mpandey", "B00917801");
            statement = connect.createStatement();
            statement.execute("use mpandey;");
        }
        catch (SQLException e)
        {
            System.out.println("Connection failed!");
        }
        return connect;
    }
}

