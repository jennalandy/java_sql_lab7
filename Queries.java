import java.sql.*;
import java.util.Scanner;

public class Queries {

	//R1
	public static void R1(String jdbcUrl, String dbUsername, String dbPassword) {
		try
		{
			Connection conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(QueryLines.r1Query);
			System.out.println("\nRooms and Rates");
			System.out.printf("\n%-4s | %-24s | %-4s | %-8s | %-8s | %-10s | %-10s | %-14s | %-13s\n", "Room","Room Name","Beds","Bed Type","Max Occu","Base Price","Popularity","Next Available","Last Occupied");
			System.out.println("--------------------------------------------------" + 
								"--------------------------------------------------" +
								"-------------------");
			while (rs.next()) {
				 String Room = rs.getString("RoomCode");
				 String RoomName = rs.getString("RoomName");
				 int beds = rs.getInt("Beds");
				 String bedType = rs.getString("bedType");
				 int maxOcc = rs.getInt("maxOcc");
				 int basePrice = rs.getInt("basePrice");
				 String decor = rs.getString("decor");
				 Date nextAvail = rs.getDate("nextAvailable");
				 float Pop = rs.getFloat("popularity");
				 int occupied = rs.getInt("occupied");
				 System.out.printf("%-4s | %-24s | %-4s | %-8s | %-1s | %-10s | %-10s | %-14s | %-8s\n", Room, RoomName, beds, bedType, maxOcc+" people", "$"+basePrice, Pop, nextAvail, occupied+" days");
			}
		}
		catch (SQLException e) 
		{
			System.out.println(e.getMessage());
			System.out.println("SQL Exception Caught");
			System.exit(1);
		}
	}

	//R3
	 public static void R3(String jdbcUrl, String dbUsername, String dbPassword, Scanner input) 
	 {
	 	try
		{
			Connection conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
			Statement stmt = conn.createStatement();

			System.out.print("\nReservation Code: ");
			int code = Integer.parseInt(input.nextLine().split(" ")[0]);
			PreparedStatement p = conn.prepareStatement("select * from iguzmanl.lab7_reservations where code = ?");
			p.setInt(1, code);
			ResultSet rs = p.executeQuery();

			System.out.println("\nBelow is your current reservation information\n");
			System.out.printf("%-5s | %-9s | %-10s | %-10s | %-6s | %-20s | %-20s | %-6s | %-4s \n", "Room", "Room Name", "Check In", "Check Out", "Rate", "Last Name", "First Name", "Adults", "Kids");
			System.out.println("----------------------------------------------" +
								"----------------------------------------------" + 
								"----------------------");

			String LastName = null;
			String FirstName = null;
			Date CheckIn = null;
			if (rs.next()) {
				int Room = rs.getInt("Code");
				String RoomName = rs.getString("Room");
				CheckIn = rs.getDate("CheckIn");
				Date CheckOut = rs.getDate("CheckOut");
				float Rate = rs.getFloat("Rate");
				LastName = rs.getString("LastName");
				FirstName = rs.getString("FirstName");
				int Adults = rs.getInt("Adults");
				int Kids = rs.getInt("Kids");
				System.out.printf("%-5s | %-9s | %-10s | %-10s | %-6s | %-20s | %-20s | %-6s | %-4s \n\n", Room, RoomName, CheckIn, CheckOut, Rate, LastName, FirstName, Adults, Kids);
			}

			System.out.println("The following prompts will allow you to update your Name, Dates, and Number of Guests");
			System.out.println("Type your desired update or 'no change' to keep the same\n");
			
			//update first name
			System.out.print("First Name: ");
			String nextThing = input.nextLine();
			String newFirstName;
			if (!nextThing.equals("no change")) 
			{ newFirstName = nextThing; }
			else
			{ newFirstName = FirstName; }

			//update last name
			System.out.print("Last Name: ");
			nextThing = input.nextLine();
			String newLastName;
			if (!nextThing.equals("no change")) 
			{ newLastName = nextThing; }
			else
			{ newLastName = LastName; }

			//update check in date
			System.out.print ("Start Date (YYYY-MM-DD): ");
			nextThing = input.nextLine();
			java.sql.Date newCheckIn;
			if (!nextThing.equals("no change")) 
			{ 
				newCheckIn = java.sql.Date.valueOf(nextThing); 
			}
			else
			{ newCheckIn = CheckIn; }

			//update check out date
			System.out.print ("End Date (YYYY-MM-DD): ");
			nextThing = input.nextLine();
			java.sql.Date newCheckOut;
			if (!nextThing.equals("no change")) 
			{ 
				nextLine = java.sql.Date.valueOf(nextThing); 
			}
			else
			{ nextLine = CheckOut; }

			//update number of adults

			//update number of kids
		}
		catch (SQLException e) 
		{
			System.out.println(e.getMessage());
			System.out.println("SQL Exception Caught");
			System.exit(1);
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
		}
	 }


}