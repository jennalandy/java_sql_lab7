import java.sql.*;

public class InnReservations{

	public static void main(String args[]) {
		try 
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
		}
		catch (ClassNotFoundException e) 
		{
			System.out.println(e.getMessage());
			System.out.println("Class Not Found");
			System.exit(1);
		}

		String jdbcUrl = System.getenv("APP_JDBC_URL");
		String dbUsername = System.getenv("APP_JDBC_USER");
		String dbPassword = System.getenv("APP_JDBC_PW");

		try
		{
			Connection conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
		}
		catch (SQLException e) 
		{
			System.out.println(e.getMessage());
			System.out.println("SQL Exception Caught");
			System.exit(1);
		}

		/*Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("select * from lab7_rooms");
		while (rs.next()) {
			 String roomCode = rs.getString("RoomCode");
			 String roomName = rs.getString("RoomName");
			 System.out.println(roomCode + " " + roomName);
		}*/

	}
}