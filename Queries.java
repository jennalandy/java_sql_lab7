import java.sql.*;
import java.util.Scanner;
import java.util.ArrayList;

public class Queries {

//-----------------------------------R1---------------------------------------------------------------//
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
	
//-----------------------------------R2---------------------------------------------------------------//
	public static void R2(String jdbcUrl, String dbUsername, String dbPassword) {
		try
		{
			Connection conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
			Statement stmt = conn.createStatement();

			String firstName, lastName, roomCode, beginDate, endDate;
			int numChildren, numAdults;
			Scanner input = new Scanner(System.in);

			System.out.println("Please Provide the Following Information \n");
			System.out.println("First Name: ");
			firstName = input.nextLine();

			System.out.println("Last Name: ");
			lastName = input.nextLine();

			System.out.println("Room code to indicate the specific room desired (or 'Any' to indicate no preference): ");
			roomCode = input.nextLine();

			System.out.println("Begin date of stay (YYYY-MM-DD): ");
			beginDate = input.nextLine();

			System.out.println("End date of stay (YYYY-MM-DD): ");
			endDate = input.nextLine();

			System.out.println("Number of children: ");
			numChildren = input.nextInt();
			System.out.println("Number of adults");
			numAdults = input.nextInt();

			String personCheckQuery = QueryLines.createR2PersonCountCheck(numChildren,numAdults);
			ResultSet countCheck = stmt.executeQuery(personCheckQuery);

			while(countCheck.next()){
				String code = countCheck.getString("RoomCode");
				int maxOcc = countCheck.getInt("maxOcc");
				String hasRoom = countCheck.getString("HasSpace");
				if(hasRoom.equals("NO")){
					System.out.println("Your requested person count exceeds the maximum capacity of room " + code);
				}
			}


			// System.out.println("Reservation details: FirstName" + firstName + "\nlastName" + lastName + "\nroomCode" + roomCode);
			// System.out.println("\nBeginDate: " + beginDate + "\nendDate" + endDate + "\nChildren:" + numChildren + "\nAdults: " + numAdults);

			//ResultSet rs = stmt.executeQuery(QueryLines.r1Query);
			/*System.out.println("\nRooms and Rates");
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
			}*/
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
			System.out.println("SQL Exception Caught");
			System.exit(1);
		}
	}
	
//-----------------------------------R3---------------------------------------------------------------//
	 public static java.sql.Date getNewDate(java.sql.Date oldDate, Scanner input, String inOrOut)
	{
		String label = null;
		if (inOrOut.equals("in")) { label = "Start Date (YYYY-MM-DD): ";}
		else { label = "End Date (YYYY-MM-DD): ";}
		System.out.print(label);
		String line = input.nextLine();
		java.sql.Date newDate = null;
		if (!line.equals("no change")) 
		{ 
			try {
				newDate = java.sql.Date.valueOf(line); 
			}
			catch(Exception e) {
				System.out.println("Must be a date in the form YYYY-MM-DD or 'no change'");
				newDate = getNewDate(oldDate, input, inOrOut);
			}
		}
		else { newDate = oldDate; }
		return newDate;
	}

	public static int getNewOccupancy(int oldOcc, Scanner input, String kidOrAdult){
		String label = null;
		if (kidOrAdult.equals("Adult")){
			label = "Number of Adults: ";
		}
		else { label = "Number of Kids: "; }
		System.out.print(label);
		String line = input.nextLine();
		int newOcc;
		if (!line.equals("no change")) 
		{ 
			try {
				newOcc = Integer.parseInt(line);
			}
			catch (Exception e) {
				System.out.println("Must be an integer or 'no change'");
				newOcc = getNewOccupancy(oldOcc, input, kidOrAdult);
			}
		}
		else { newOcc = oldOcc; }

		return newOcc;
	}

	public static boolean confirmAvailability(java.sql.Date newDate, java.sql.Date oldDate, int code, Connection conn, String inOrOut)
	{
		int available = 0;
		try 
		{
			String room = "";
			PreparedStatement getRoom = conn.prepareStatement("select Room from iguzmanl.lab7_reservations where code = ?");
			getRoom.setInt(1, code);
			ResultSet roomRs = getRoom.executeQuery();
			if (roomRs.next()) {
				room = roomRs.getString("Room");
			}

			PreparedStatement getAvailability = conn.prepareStatement(QueryLines.r3QueryAvail);
			

			if (inOrOut == "in") {
				getAvailability.setString(1, room);
				java.sql.Date newPlusOne = new Date(newDate.getYear(),newDate.getMonth(), newDate.getDate() + 1);
				java.sql.Date oldMinusOne = new Date(oldDate.getYear(),oldDate.getMonth(), oldDate.getDate() - 1);
				getAvailability.setDate(2, newPlusOne);
				getAvailability.setDate(3, oldMinusOne);
			}
			else {
				getAvailability.setString(1, room);
				java.sql.Date oldPlusOne = new Date(oldDate.getYear(),oldDate.getMonth(), oldDate.getDate() + 1);
				java.sql.Date newMinusOne = new Date(newDate.getYear(),newDate.getMonth(), newDate.getDate() - 1);
				getAvailability.setDate(2, oldPlusOne);
				getAvailability.setDate(3, newMinusOne);
			}

			ResultSet availabilityRs = getAvailability.executeQuery();
			if (availabilityRs.next()) {
				available = availabilityRs.getInt("available");
			}
		}
		catch(Exception e)
		{
			System.out.print("SQL Error");
		}
		if (available == 0) {return false;}
		else {return true;}
	}

	public static int confirmOccupancy(int newOccupancy, int code, Connection conn)
	{
		int limit = 0;
		try 
		{
			String room = "";
			PreparedStatement getRoom = conn.prepareStatement("select Room from iguzmanl.lab7_reservations where code = ?");
			getRoom.setInt(1, code);
			ResultSet roomRs = getRoom.executeQuery();
			if (roomRs.next()) {
				room = roomRs.getString("Room");
			}

			PreparedStatement getLimit = conn.prepareStatement("select maxOcc from iguzmanl.lab7_rooms where RoomCode = ?");
			getLimit.setString(1, room);
			ResultSet limitRs = getLimit.executeQuery();
			if (limitRs.next()) {
				limit = limitRs.getInt("maxOcc");
			}
			
		}
		catch(Exception e) 
		{
			System.out.println(e.getMessage());
		}
		
		return limit;
	}

	public static int getCode(Scanner input)
	{
		int code = 0;
		try
		{
			code = Integer.parseInt(input.nextLine().split(" ")[0]);
		}
		catch (NumberFormatException e)
		{
			System.out.println("Room code must be an integer");
			System.out.print("\nReservation Code: ");
			code = getCode(input);
		}
		return code;
	}

	public static void R3(String jdbcUrl, String dbUsername, String dbPassword, Scanner input) 
	{
		try
		{
			Connection conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);

			System.out.print("\nReservation Code: ");
			int code = getCode(input);

			PreparedStatement p = conn.prepareStatement("select * from iguzmanl.lab7_reservations where code = ?");
			p.setInt(1, code);
			ResultSet rs = p.executeQuery();

			if (rs.next()) {
				System.out.println("\nBelow is your current reservation information\n");
				System.out.printf("%-5s | %-9s | %-10s | %-10s | %-6s | %-20s | %-20s | %-6s | %-4s \n", "Room", "Room Name", "Check In", "Check Out", "Rate", "Last Name", "First Name", "Adults", "Kids");
				System.out.println("----------------------------------------------" +
							"----------------------------------------------" + 
							"----------------------");
				int Room = rs.getInt("Code");
				String RoomName = rs.getString("Room");
				String StrCheckIn = rs.getString("CheckIn");
				String StrCheckOut = rs.getString("CheckOut");
				float Rate = rs.getFloat("Rate");
				String LastName = rs.getString("LastName");
				String FirstName = rs.getString("FirstName");
				int Adults = rs.getInt("Adults");
				int Kids = rs.getInt("Kids");
				System.out.printf("%-5s | %-9s | %-10s | %-10s | %-6s | %-20s | %-20s | %-6s | %-4s \n\n", Room, RoomName, StrCheckIn, StrCheckOut, Rate, LastName, FirstName, Adults, Kids);

				System.out.println("The following prompts will allow you to update your Name, Dates, and Number of Guests");
				System.out.println("Type your desired update or 'no change' to keep the same\n");

				//update first name
				System.out.print("First Name: ");
				String line = input.nextLine();
				String newFirstName;
				if (!line.equals("no change")) 
				{ newFirstName = line; }
				else
				{ newFirstName = FirstName; }

				//update last name
				System.out.print("Last Name: ");
				line = input.nextLine();
				String newLastName;
				if (!line.equals("no change")) 
				{ newLastName = line; }
				else
				{ newLastName = LastName; }

				//update check in date
				Date CheckIn = java.sql.Date.valueOf(StrCheckIn);
				java.sql.Date newCheckIn = getNewDate(CheckIn, input, "in");
				boolean availableIn = confirmAvailability(newCheckIn, CheckIn, code, conn, "in");
				while (!availableIn) {
					System.out.println("\tThe room is not available for check in on " + newCheckIn);
					newCheckIn = getNewDate(CheckIn, input, "in");
					availableIn = confirmAvailability(newCheckIn, CheckIn, code, conn, "in");
				}

				//update check out date
				Date CheckOut = java.sql.Date.valueOf(StrCheckOut);
				java.sql.Date newCheckOut = getNewDate(CheckOut, input, "out");
				boolean availableOut = confirmAvailability(newCheckOut, CheckOut, code, conn, "out");
				while (!availableOut) {
					System.out.println("\tThe room is not available for check out on " + newCheckOut);
					newCheckOut = getNewDate(CheckOut, input, "out");
					availableOut = confirmAvailability(newCheckOut, CheckOut, code, conn, "out");
				}

				//make sure check out is after check in
				while (newCheckOut.getTime() - newCheckIn.getTime() <= 0) 
				{
					System.out.println("\tCheck out must be after check in");
					
					//update check in date
					newCheckIn = getNewDate(CheckIn, input, "in");
					availableIn = confirmAvailability(newCheckIn, CheckIn, code, conn, "in");
					while (!availableIn) {
						System.out.println("\tThe room is not available for check in on " + newCheckIn);
						newCheckIn = getNewDate(CheckIn, input, "in");
						availableIn = confirmAvailability(newCheckIn, CheckIn, code, conn, "in");
					}

					//update check out date
					newCheckOut = getNewDate(CheckOut, input, "out");
					availableOut = confirmAvailability(newCheckOut, CheckOut, code, conn, "out");
					while (!availableOut) {
						System.out.println("\tThe room is not available for check out on " + newCheckOut);
						newCheckOut = getNewDate(CheckOut, input, "out");
						availableOut = confirmAvailability(newCheckOut, CheckOut, code, conn, "out");
					}
				}

				//update number of adults
				int newAdults = getNewOccupancy(Adults, input, "Adult"); 
				while (newAdults == 0) 
				{
					System.out.println("\tThere must be at least one adult in this room");
					newAdults = getNewOccupancy(Adults, input, "Adult");
				}

				//update number of kids
				int newKids = getNewOccupancy(Kids, input, "Kid");

				//check new occupancy is under the room's limit
				int maxOcc = confirmOccupancy(newAdults + newKids, code, conn);
				while (maxOcc < (newAdults + newKids))
				{
					System.out.println("\tThis room has an occupancy limit of "+maxOcc);
					newAdults = getNewOccupancy(Adults, input, "Adult"); 
					newKids = getNewOccupancy(Kids, input, "Kid");
					maxOcc = confirmOccupancy(newAdults + newKids, code, conn);
				}

				////int newDuration = (int)(( newCheckOut.getTime() - newCheckIn.getTime() ) / MILLSECS_PER_DAY);
				////System.out.println(newDuration);

				System.out.println("\n"+newFirstName+", "+newLastName+", "+newCheckIn+", "+newCheckOut+", "+newAdults+", "+newKids);
			
				PreparedStatement update = conn.prepareStatement(QueryLines.r3Update);
				update.setDate(1, java.sql.Date.valueOf(newCheckIn.toString()));
				update.setDate(2, java.sql.Date.valueOf(newCheckOut.toString()));
				update.setString(3, newLastName.toUpperCase());
				update.setString(4, newFirstName.toUpperCase());
				update.setInt(5, newAdults);
				update.setInt(6, newKids);
				update.setInt(7, code);
				System.out.println(update);

				int rowCount = update.executeUpdate();
				System.out.println("\n"+rowCount+" Lines Affected");


				PreparedStatement p2 = conn.prepareStatement("select * from iguzmanl.lab7_reservations where code = ?");
				p2.setInt(1, code);
				ResultSet rs2 = p2.executeQuery();

				if (rs2.next())
				{
					System.out.println("\nBelow is your current reservation information\n");
					System.out.printf("%-5s | %-9s | %-10s | %-10s | %-6s | %-20s | %-20s | %-6s | %-4s \n", "Room", "Room Name", "Check In", "Check Out", "Rate", "Last Name", "First Name", "Adults", "Kids");
					System.out.println("----------------------------------------------" +
								"----------------------------------------------" + 
								"----------------------");
					Room = rs2.getInt("Code");
					RoomName = rs2.getString("Room");
					StrCheckIn = rs2.getString("CheckIn");
					StrCheckOut = rs2.getString("CheckOut");
					Rate = rs2.getFloat("Rate");
					LastName = rs2.getString("LastName");
					FirstName = rs2.getString("FirstName");
					Adults = rs2.getInt("Adults");
					Kids = rs2.getInt("Kids");
					System.out.printf("%-5s | %-9s | %-10s | %-10s | %-6s | %-20s | %-20s | %-6s | %-4s \n\n", Room, RoomName, StrCheckIn, StrCheckOut, Rate, LastName, FirstName, Adults, Kids);
				}
			}
			else {
				System.out.println("THIS RESERVATION CODE DOES NOT EXITS");
			}
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

//-----------------------------------R4---------------------------------------------------------------//
	public static void R4(String jdbcUrl, String dbUsername, String dbPassword) {
		try
		{
			String confirmationCode;
			Scanner input = new Scanner(System.in);

			Connection conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
			Statement stmt = conn.createStatement();

			System.out.println("Cancel an Existing Reservation\n");
			System.out.println("Please provide your confirmation code: ");
			confirmationCode = input.nextLine();

			// (1) Prepare/precompile SQL statement (parameterized with ? placeholders)
			PreparedStatement pstmt = conn.prepareStatement(
			 "Select * FROM iguzmanl.lab7_reservations WHERE CODE = ?");
			// (2) Set parameters (indexed starting at 1)
			pstmt.setString(1, confirmationCode);
			ResultSet rs = pstmt.executeQuery();

			if (!rs.isBeforeFirst() ) {  //the set is empty   
    		System.out.println("No Reservation matching this reservation code was found");
    		}
    		else{
    			System.out.printf("\n%-5s | %-4s | %-14s | %-14s | %-5s | %-20s | %-20s | %-6s | %-4s\n", "CODE","Room","CheckIn","CheckOut","Rate","LastName","FirstName","Adults","Kids");
				System.out.println("--------------------------------------------------" +
								"--------------------------------------------------" +
								"-------------------");
				while (rs.next()) {
					 String code = rs.getString("CODE");
					 String room = rs.getString("Room");
					 Date checkIn = rs.getDate("CheckIn");
					 Date checkOut = rs.getDate("CheckOut");
					 float rate = rs.getFloat("Rate");
					 String lastName = rs.getString("LastName");
					 String firstName = rs.getString("FirstName");
					 int adults = rs.getInt("Adults");
					 int kids = rs.getInt("Kids");
					 System.out.printf("%-5s | %-4s | %-14s | %-14s | %-5s | %-20s | %-20s | %-6s | %-4s\n", code,room,checkIn,checkOut,rate,lastName,firstName,adults,kids);
				} 
			}
			System.out.println("Would you like to cancel this reservation? (yes/no):");
			String ans = input.nextLine();
			if(ans.equals("yes") || ans.equals("Yes") ){
				pstmt = conn.prepareStatement(
			 	"DELETE FROM iguzmanl.lab7_reservations WHERE CODE = ?");
				pstmt.setString(1, confirmationCode);
				pstmt.executeUpdate();
				System.out.println("Reservation has been removed");
			} 
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
			System.out.println("SQL Exception Caught");
			System.exit(1);
		}
	}
	
//-----------------------------------R5---------------------------------------------------------------//
	public static java.sql.Date getDate(Scanner input, String startOrEnd)
	{
		String label = null;
		if (startOrEnd.equals("Start"))
		{
			label = "Start Date: ";
		}
		else
		{
			label = "End Date: ";
		}
		System.out.print(label);
		String line = input.nextLine();
		java.sql.Date date = null;
		
		if (line.equals("")) 
		{
			date = java.sql.Date.valueOf("0000-00-00");
		}
		else {
			try {
				date = java.sql.Date.valueOf(line); 
			}
			catch(Exception e) {
				System.out.println("Must be a date in the form YYYY-MM-DD or blank");
				date = getDate(input, startOrEnd);
			}
		}
		return date;
	}

	public static int getRes(Scanner input)
	{
		System.out.print("Reservation Number: ");
		String strRes = input.nextLine();
		int res = 0;
		if (!strRes.equals(""))
		{
			try
			{
				res = Integer.parseInt(strRes);
			}
			catch(Exception e)
			{
				System.out.println("Must be an integer or blank");
				res = getRes(input);
			}
		}
		else
		{
			res =  -1;
		}
		return res;
	}

	public static void R5(String jdbcUrl, String dbUsername, String dbPassword, Scanner input)
	{
		System.out.println("Here you can search for reservations based on first and \nlast name, a range of dates, room code, and/or reservation code.");
		System.out.println("Leave an area blank for 'Any', SQL LIKE wildcards are accepted.\n");

		System.out.print("First Name: ");
		String firstName = input.nextLine();

		System.out.print("Last Name: ");
		String lastName = input.nextLine();

		System.out.print("Start Date: ");
		String strStart = input.nextLine();
		java.sql.Date start = null;
			
		if (!strStart.equals(""))
		{
			try
			{
				start = java.sql.Date.valueOf(strStart);
			}
			catch(Exception e)
			{
				System.out.println("Must be a date in the form YYYY-MM-DD or blank");
				start = getDate(input,"Start");
			}
		}

		System.out.print("End Date: ");
		String strEnd = input.nextLine();
		java.sql.Date end = null;

		if (!strEnd.equals(""))
		{
			try
			{
				end = java.sql.Date.valueOf(strEnd);
			}
			catch(Exception e)
			{
				System.out.println("Must be a date in the form YYYY-MM-DD or blank");
				end = getDate(input,"End");
			}
		}

		System.out.print("Room Code: ");
		String room = input.nextLine();

		System.out.print("Reservation Number: ");
		String strRes = input.nextLine();
		int res = 0;
		if (!strRes.equals(""))
		{
			try
			{
				res = Integer.parseInt(strRes);
			}
			catch(Exception e)
			{
				System.out.println("Must be an integer or blank");
				res = getRes(input);
			}
		}

		ArrayList<String> toFillPrep = new ArrayList<>();

		String sqlStatement = "select * from iguzmanl.lab7_reservations ";
		if (!firstName.equals("") || !lastName.equals("") || !strStart.equals("") || 
			!strEnd.equals("") || !room.equals("") || !strRes.equals(""))
		{
			sqlStatement = sqlStatement + "where ";
			if (!firstName.equals(""))
			{
				toFillPrep.add("FirstName");
				if(firstName.contains("%") || firstName.contains("_") || firstName.contains("["))
				{
					sqlStatement = sqlStatement + "FirstName like ? ";
				}
				else
				{
					sqlStatement = sqlStatement + "FirstName = ? ";
				}
			}
			if (!lastName.equals(""))
			{
				if(!sqlStatement.endsWith("where "))
				{
					sqlStatement = sqlStatement + "and ";
				}
				toFillPrep.add("LastName");
				if(lastName.contains("%") || lastName.contains("_") || lastName.contains("["))
				{
					sqlStatement = sqlStatement + "LastName like ? ";
				}
				else
				{
					sqlStatement = sqlStatement + "LastName = ? ";
				}
			}
			if (!strStart.equals(""))
			{
				if(!sqlStatement.endsWith("where "))
				{
					sqlStatement = sqlStatement + "and ";
				}
				toFillPrep.add("Start");
				if(!strEnd.equals(""))
				{
					toFillPrep.add("End");
					sqlStatement = sqlStatement + "(CheckIn between ? and ? or CheckOut between ? and ?) ";
				}
				else
				{
					sqlStatement = sqlStatement + "CheckOut > ? ";
				}
			}
			if(!strEnd.equals(""))
			{
				if(strStart.equals(""))
				{
					if(!sqlStatement.endsWith("where "))
					{
						sqlStatement = sqlStatement + "and ";
					}
					toFillPrep.add("End");
					sqlStatement = sqlStatement + "CheckIn < ? ";
				}
			}
			if(!room.equals(""))
			{
				if(!sqlStatement.endsWith("where "))
				{
					sqlStatement = sqlStatement + "and ";
				}
				toFillPrep.add("Room");
				if(room.contains("%") || room.contains("_") || room.contains("["))
				{
					sqlStatement = sqlStatement + "Room like ? ";
				}
				else
				{
					sqlStatement = sqlStatement + "Room = ? ";
				}
			}
			if(!strRes.equals(""))
			{
				if(!sqlStatement.endsWith("where "))
				{
					sqlStatement = sqlStatement + "and ";
				}
				toFillPrep.add("Reservation");
				sqlStatement = sqlStatement + "CODE = ? ";
			}
		}
		sqlStatement = sqlStatement + ";";

		try
		{
			Connection conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
			PreparedStatement ps = conn.prepareStatement(sqlStatement);

			int i = 1;
			if(toFillPrep.contains("FirstName"))
			{
				ps.setString(i, firstName.toUpperCase());
				i++;
			}
			if(toFillPrep.contains("LastName"))
			{
				ps.setString(i, lastName.toUpperCase());
				i++;
			}
			if(toFillPrep.contains("Start"))
			{
				if(toFillPrep.contains("End"))
				{
					ps.setDate(i, start);
					i++;
					ps.setDate(i, end);
					i++;
					ps.setDate(i, start);
					i++;
					ps.setDate(i, end);
					i++;
				}
				else
				{
					ps.setDate(i, start);
					i++;
				}
			}
			if(toFillPrep.contains("End"))
			{
				if(!toFillPrep.contains("Start"))
				{
					ps.setDate(i, end);
					i++;
				}
			}
			if(toFillPrep.contains("Room"))
			{
				ps.setString(i, room.toUpperCase());
				i++;
			}
			if(toFillPrep.contains("Reservation"))
			{
				ps.setInt(i, res);
				i++;
			}

			ResultSet rs = ps.executeQuery();
			if(rs.next())
			{
				System.out.printf("%-5s | %-9s | %-10s | %-10s | %-6s | %-20s | %-20s | %-6s | %-4s \n", "Room", "Room Name", "Check In", "Check Out", "Rate", "Last Name", "First Name", "Adults", "Kids");
				System.out.println("----------------------------------------------" +
								"----------------------------------------------" + 
								"----------------------");
				int Room;
				String RoomName;
				String StrCheckIn;
				String StrCheckOut;
				float Rate;
				String LastName;
				String FirstName;
				int Adults;
				int Kids;

				Room = rs.getInt("Code");
				RoomName = rs.getString("Room");
				StrCheckIn = rs.getString("CheckIn");
				StrCheckOut = rs.getString("CheckOut");
				Rate = rs.getFloat("Rate");
				LastName = rs.getString("LastName");
				FirstName = rs.getString("FirstName");
				Adults = rs.getInt("Adults");
				Kids = rs.getInt("Kids");
				System.out.printf("%-5s | %-9s | %-10s | %-10s | %-6s | %-20s | %-20s | %-6s | %-4s \n", Room, RoomName, StrCheckIn, StrCheckOut, Rate, LastName, FirstName, Adults, Kids);
				

				while(rs.next())
				{
					Room = rs.getInt("Code");
					RoomName = rs.getString("Room");
					StrCheckIn = rs.getString("CheckIn");
					StrCheckOut = rs.getString("CheckOut");
					Rate = rs.getFloat("Rate");
					LastName = rs.getString("LastName");
					FirstName = rs.getString("FirstName");
					Adults = rs.getInt("Adults");
					Kids = rs.getInt("Kids");
					System.out.printf("%-5s | %-9s | %-10s | %-10s | %-6s | %-20s | %-20s | %-6s | %-4s \n", Room, RoomName, StrCheckIn, StrCheckOut, Rate, LastName, FirstName, Adults, Kids);
				}
			}
			else
			{
				System.out.println("NO RESULTS");
			}

		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
}
