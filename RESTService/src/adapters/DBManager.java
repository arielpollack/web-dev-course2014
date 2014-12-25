package adapters;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {
	
	static Connection conn;
	static private final String DB_USER = "root";
	static private final String DB_PWD = "";
	static public final String DB_NAME = "aptmanage";
	
	static 
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("------ JDBC Driver loaded ------");
		}
		catch (ClassNotFoundException ex)
		{
			System.err.println("*** ERR *** Can't load driver");
		}
		
		try 
		{
			conn = DriverManager.getConnection("jdbc:mysql://localhost/" + DB_NAME, DB_USER, DB_PWD);
			System.out.println("------ Connected to DB ------");
		}
		catch (SQLException ex)
		{
			System.out.println("*** ERR *** Couldn't connect to DB.");
			System.out.println(ex.getMessage());
		}
	}
	
	public static Connection getConnection()
	{
		return conn;
	}
}
