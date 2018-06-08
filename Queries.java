import java.sql.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.text.SimpleDateFormat;  
import java.util.Date;  
import java.util.Calendar;

public class Queries {
	static ArrayList<resInfo> resArray = new ArrayList<resInfo>();


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
				 String nextAvail = rs.getString("nextAvailable");
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
	// will keep arraylist of potential reservations if no identical match
	private static class resInfo{
		private String roomCode;
		private String roomName;
		private String checkIn;
		private String checkOut;
		private String bedType;
		private int basePrice;
	
		public resInfo(String rCode, String rName, String cIn, String cOut, String bType, int bPrice){
			this.roomCode = rCode;
			this.roomName = rName;
			this.checkIn = cIn;
			this.checkOut = cOut;
			this.bedType = bType;
			this.basePrice = bPrice;

		}
		public void setStartDate(String s){
			this.checkIn = s;
		}
		public void setEndDate(String s){
			this.checkOut = s;
		}
		public String getRoomCode(){
			return this.roomCode;
		}
		public String getRoomName(){
			return this.roomName;
		}
		public String getCheckIn(){
			return this.checkIn;
		}
		public String getCheckOut(){
			return this.checkOut;
		}
		public String getBedType(){
			return this.bedType;
		}
		public int returnBasePrice(){
			return this.basePrice;
		}
	}

	public static int getDateDifference(String checkIn, String checkOut, Connection conn){
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int diff = 0; 
		try{
			pstmt = conn.prepareStatement("select datediff(?, ?) as diff;");
			pstmt.setString(1,checkOut);
			pstmt.setString(2,checkIn);
			rs = pstmt.executeQuery();
			rs.next();
			diff = rs.getInt("diff");
		}
		catch (SQLException e){
			System.out.println(e.getMessage());
			System.out.println("SQL Exception Caught");
			System.exit(1);
		}
		return diff;

	}
	public static void addToArray(ResultSet rs, String beginDate, String endDate){
		resInfo r;
		try{	
			while (rs.next()) { 
				String rCode = rs.getString("RoomCode");
				String rName = rs.getString("RoomName");
				String bType = rs.getString("bedType");
				int bPrice = rs.getInt("basePrice");
				r = new resInfo(rCode,rName,beginDate,endDate,bType,bPrice);
				resArray.add(r);
			} 
		}
		catch (SQLException e){
			System.out.println(e.getMessage());
			System.out.println("SQL Exception Caught");
			System.exit(1);
		}
	}
	public static void displayOptions(ResultSet rs, String beginDate, String endDate){
		resInfo r;
		int i = 0;
		System.out.printf("\n%-14s | %-24s | %-14s | %-14s | %-14s | %-9s | %-3s\n", "RoomCode","RoomName","CheckIn","CheckOut", "BedType","BasePrice","OptionNumber");
		System.out.println("--------------------------------------------------" +
						"--------------------------------------------------" +
					"-------------------");
		try{	
			while (rs.next()) { 
				i++;
				String rCode = rs.getString("RoomCode");
				String rName = rs.getString("RoomName");
				String bType = rs.getString("bedType");
				int bPrice = rs.getInt("basePrice");
				r = new resInfo(rCode,rName,beginDate,endDate,bType,bPrice);
				resArray.add(r);

				System.out.printf("%-14s | %-24s | %-14s | %-14s | %-14s | %-9s | %-3s\n", rCode,rName,beginDate,endDate,bType,bPrice,i);
			} 
		}
		catch (SQLException e){
			System.out.println(e.getMessage());
			System.out.println("SQL Exception Caught");
			System.exit(1);
		}

	}
	public static void displayAlternatives(){
		int i = 0;
		System.out.printf("\n%-14s | %-24s | %-14s | %-14s | %-14s | %-9s | %-3s\n", "RoomCode","RoomName","CheckIn","CheckOut", "BedType","BasePrice","OptionNumber");
		System.out.println("--------------------------------------------------" +
						"--------------------------------------------------" +
					"-------------------");
		while(i < resArray.size()){
			String rCode = resArray.get(i).getRoomCode();
			String rName = resArray.get(i).getRoomName();
			String bType = resArray.get(i).getBedType();
			int bPrice = resArray.get(i).returnBasePrice();
			String beginDate = resArray.get(i).getCheckIn();
			String endDate = resArray.get(i).getCheckOut();
			System.out.printf("%-14s | %-24s | %-14s | %-14s | %-14s | %-9s | %-3s\n", rCode,rName,beginDate,endDate,bType,bPrice,i);
			i++;
		}

	}
	public static int returnCode(Connection conn){
		Random rand = new Random();

		PreparedStatement p =  null;
		int code = 0;
		try{
			while(true){
				code = rand.nextInt(100000);
				p = conn.prepareStatement(QueryLines.r2DoesCodeExist);
				p.setInt(1,code);
				ResultSet codeExists = p.executeQuery();
				if(!codeExists.isBeforeFirst()){
					return code;
				}
			}			
		}
		catch (SQLException e){
			System.out.println(e.getMessage());
			System.out.println("SQL Exception Caught");
			System.exit(1);
		}
		return code;
	}
	public static double generateRate(String checkIn, String checkOut, int baseRate){
		int weekDays = 0;
		int weekEnds = 0;
		double finalRate = 0;
		double daysTotalRate = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date checkInDate = null;
		Date checkOutDate = null;
		try{
			checkInDate = sdf.parse(checkIn);
			checkOutDate = sdf.parse(checkOut);
		}
		catch (Exception e){
			System.out.println(e.getMessage());
			System.out.println("Parse Exception Caught");
			System.exit(1);
		}
		Calendar startCal = Calendar.getInstance();
		Calendar endCal = Calendar.getInstance();

		startCal.setTime(checkInDate);
		endCal.setTime(checkOutDate);
		for(Date date = startCal.getTime() ; startCal.before(endCal); startCal.add(Calendar.DATE,1), date = startCal.getTime()){
			Calendar day = Calendar.getInstance();
			day.setTime(date);

			if(day.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || day.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ){
				weekEnds++;
			}
			else{
				weekDays++;
			} //weekend
		}
		daysTotalRate += (weekDays * baseRate) + (weekEnds * (1.10 * baseRate));
		finalRate = (daysTotalRate * .18) + daysTotalRate;
		System.out.println(weekDays + " weekdays and " + weekEnds + " weekends");
		return finalRate;

	}
	public static void makeReservation(Connection conn, String firstName, String lastName, int numAdults, int numChildren){
		Scanner input = new Scanner(System.in);
		boolean askForInput = true;
		PreparedStatement pstmt = null;
		int selectedReservation;
		double rate;
		int code = returnCode(conn);
		while(askForInput){
			System.out.println("\nPlease provide the reservation option number to confirm your reservation or 'cancel' to return to the main menu: ");
			String ret = input.nextLine();
			if(ret.equals("Cancel") || ret.equals("cancel")){
				System.out.println("Returning to main menu...");
				return;
			}
			try {
				selectedReservation = Integer.parseInt(ret);
			} 
			catch (NumberFormatException e) {
				System.out.println("Invalid Input");
				continue;
			}
			if(selectedReservation >= 1 && selectedReservation <= resArray.size()){
				resInfo reservation = resArray.get(selectedReservation - 1);
				rate = generateRate(reservation.getCheckIn(), reservation.getCheckOut(), reservation.returnBasePrice());

				try{
					pstmt = conn.prepareStatement(QueryLines.r2Insert);
					pstmt.setInt(1,code);
					pstmt.setString(2,reservation.getRoomCode());
					pstmt.setString(3,reservation.getCheckIn());
					pstmt.setString(4,reservation.getCheckOut());
					pstmt.setDouble(5, rate); //TODO: generate rate
					pstmt.setString(6,lastName);
					pstmt.setString(7,firstName);
					pstmt.setInt(8,numAdults);
					pstmt.setInt(9,numChildren);
					pstmt.executeUpdate();
				}
				catch (SQLException e){
					System.out.println(e.getMessage());
					System.out.println("SQL Exception Caught");
					System.exit(1);
				}


				System.out.println("Reservation Confirmation");
				System.out.printf("\n%-14s | %-14s | %-14s | %-24s | %-14s | %-14s | %-14s | %-14s | %-14s | %-14s\n", "FirstName", "LastName", "RoomCode","RoomName","CheckIn","CheckOut", "BedType", "Adults", "Children", "Cost of Stay");
				System.out.println("--------------------------------------------------" +
						"--------------------------------------------------" +
					"-------------------");
				System.out.printf("\n%-14s | %-14s | %-14s | %-24 | %-14s | %-14s | %-14s | %-14s | %-14s | %-14s\n", firstName,lastName,reservation.getRoomCode(),reservation.getRoomName(),reservation.getCheckIn(),reservation.getCheckOut(),reservation.getBedType(),numAdults, numChildren, rate);

				return;
			}
			else{
				System.out.println("Please choose a valid reservation option that is listed");
				continue;
			}
		}

	}
	public static void R2(String jdbcUrl, String dbUsername, String dbPassword) {
		try
		{
			resArray.clear();
			int status = 0; // 0 = Any, 1 = with preference
			Connection conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
			Statement stmt = conn.createStatement();

			String firstName, lastName, roomCode, beginDate, bedType, endDate;
			int numChildren, numAdults ,daysApart;
			Scanner input = new Scanner(System.in);

			System.out.println("Reservations");
			System.out.println("\nPlease Provide the Following Information to Create a Reservation");
			System.out.print("First Name: ");
			firstName = input.nextLine();

			System.out.print("Last Name: ");
			lastName = input.nextLine();

			System.out.print("Room code to indicate the specific room desired (or 'Any' to indicate no preference): ");
			roomCode = input.nextLine();

			System.out.print("Preferred Bed Type (or 'Any' to indicate no preference): ");
			bedType = input.nextLine();

			System.out.print("Begin date of stay (YYYY-MM-DD): ");
			beginDate = input.nextLine();

			System.out.print("End date of stay (YYYY-MM-DD): ");
			endDate = input.nextLine();

			System.out.print("Number of children: ");
			numChildren = input.nextInt();
			System.out.print("Number of adults: ");
			numAdults = input.nextInt();

			daysApart = getDateDifference( beginDate,  endDate,  conn);
			//generateRate(beginDate,endDate);

			//check if any rooms have the capacity
			ResultSet maxCheck = stmt.executeQuery(QueryLines.r2MaxOccCheck);
			maxCheck.next();
			if((numChildren+numAdults) > maxCheck.getInt("maximum")){
				System.out.println("Your requested person count exceeds the maximum capacity of all our rooms");
				return;
			}
			String personCheckQuery = QueryLines.createR2PersonCountCheck(numChildren,numAdults);
			ResultSet countCheck = stmt.executeQuery(personCheckQuery);
			PreparedStatement pstmt = null;
			ResultSet rs;
			//notify users of rooms that don't meet the capacity
			while(countCheck.next()){
				String code = countCheck.getString("RoomCode");
				int maxOcc = countCheck.getInt("maxOcc");
				String hasRoom = countCheck.getString("HasSpace");
				if(hasRoom.equals("NO")){
					System.out.println("Your requested person count exceeds the maximum capacity of room " + code);
				}
			}
			//select which query to run
			if(roomCode.equals("Any") && bedType.equals("Any")){
				pstmt = conn.prepareStatement(QueryLines.r2Any);
				pstmt.setString(1,beginDate);
				pstmt.setString(2,endDate);
				pstmt.setInt(3,(numChildren + numAdults));
				status = 0;
				rs = pstmt.executeQuery();

			}
			else if(roomCode.equals("Any")){
				pstmt = conn.prepareStatement(QueryLines.r2Bed);
				pstmt.setString(1,beginDate);
				pstmt.setString(2,endDate);
				pstmt.setInt(3,(numChildren + numAdults));
				pstmt.setString(4,bedType);
				status = 1;
				rs = pstmt.executeQuery();

			}
			else if(bedType.equals("Any")){
				pstmt = conn.prepareStatement(QueryLines.r2Room);
				pstmt.setString(1,beginDate);
				pstmt.setString(2,endDate);
				pstmt.setInt(3,(numChildren + numAdults));
				pstmt.setString(4,roomCode);
				status = 1;
				rs = pstmt.executeQuery();

			}
			else{
				pstmt = conn.prepareStatement(QueryLines.r2RoomAndBed);
				pstmt.setString(1,beginDate);
				pstmt.setString(2,endDate);
				pstmt.setInt(3,(numChildren + numAdults));
				pstmt.setString(4,bedType);
				pstmt.setString(5,roomCode);
				status = 1;
				rs = pstmt.executeQuery();
			}
			PreparedStatement newEndStmt = null;
			PreparedStatement getRoomInfo = null;
			ResultSet roomSet = null;
			ResultSet nextAvailableDates = null;
			ResultSet newEndDate =  null;
			resInfo r;
			//if empty set, change parameters
			if(!rs.isBeforeFirst()) {  //the set is empty
				//if room or bed, change to any
				//if already any, change date
				while(resArray.size() < 5){
					if(status == 1){
						status = 0;
						pstmt = conn.prepareStatement(QueryLines.r2Bed);
						pstmt.setString(1,beginDate);
						pstmt.setString(2,endDate);
						pstmt.setInt(3,(numChildren + numAdults));
						pstmt.setString(4,bedType);
						status = 1;
						rs = pstmt.executeQuery();
						addToArray(rs, beginDate, endDate);
					}
					else{
						String newCheckOut = null;
						//get next available reservation date
						pstmt = conn.prepareStatement(QueryLines.r2GetNextAvilable);
						nextAvailableDates = pstmt.executeQuery();
						//go through each room and its respective reservation date
						while(nextAvailableDates.next()){
							String newCheckIn = nextAvailableDates.getString("nextAvailable");
							String newroomCode = nextAvailableDates.getString("Room");
							//get an appropriate end date
							newEndStmt = conn.prepareStatement(QueryLines.r2CalculateEndDate);
							newEndStmt.setString(1,newCheckIn);
							newEndStmt.setInt(2,daysApart);
							newEndDate = newEndStmt.executeQuery();
							while(newEndDate.next()){
								newCheckOut = newEndDate.getString("new");
							}
							//get the rest of info about the room to add to arraylist
							getRoomInfo = conn.prepareStatement("select * from iguzmanl.lab7_rooms where RoomCode = ?");
							getRoomInfo.setString(1,newroomCode);
							roomSet = getRoomInfo.executeQuery();
							while(roomSet.next()){
								String rName = roomSet.getString("RoomName");
								String bType = roomSet.getString("bedType");
								int bPrice = roomSet.getInt("basePrice");
								if( roomSet.getInt("maxOcc") >= (numAdults + numChildren)){
									r = new resInfo(newroomCode,rName,newCheckIn,newCheckOut,bType,bPrice);
									r.setStartDate(newCheckIn);
									r.setEndDate(newCheckOut);
									resArray.add(r);
								}
							}

						}
					}
				}
				System.out.println("No exact matches were found, below are other suggestions");
				displayAlternatives();
				makeReservation(conn,firstName,lastName,numAdults,numChildren);
    		}
    		else{
    			displayOptions(rs,beginDate, endDate);
    			makeReservation(conn, firstName, lastName,numAdults, numChildren);
    		}
		}
		catch (SQLException e){
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
		else { label = "Number of Children: "; }
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

	public static boolean confirmAvailability(java.sql.Date startDate, java.sql.Date endDate, int code, Connection conn)
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
			getAvailability.setString(1, room);
			getAvailability.setInt(2, code);
			getAvailability.setDate(3, startDate);
			getAvailability.setDate(4, endDate);
			getAvailability.setDate(5, startDate);

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
			System.out.println("\nReservation Change");
			
			Connection conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);

			System.out.print("\nReservation Code: ");
			int code = getCode(input);

			PreparedStatement p = conn.prepareStatement("select rooms.RoomName, A.* from "+
							"(select * from iguzmanl.lab7_reservations where code = ?) as A " +
							"inner join iguzmanl.lab7_rooms as rooms on A.room = rooms.RoomCode;");
			p.setInt(1, code);
			ResultSet rs = p.executeQuery();

			if (rs.next()) {
				System.out.println("\nBelow is your current reservation information\n");
				System.out.printf("%-11s | %-22s | %-10s | %-10s | %-6s | %-20s | %-20s | %-6s | %-4s \n", "Reservation", "Room Name", "Check In", "Check Out", "Rate", "Last Name", "First Name", "Adults", "Kids");
				System.out.println("----------------------------------------------" +
							"----------------------------------------------" + 
							"-----------------------------------------");
				String RoomName = rs.getString("RoomName");
				int Res = rs.getInt("Code");
				String RoomCode = rs.getString("Room");
				String StrCheckIn = rs.getString("CheckIn");
				String StrCheckOut = rs.getString("CheckOut");
				float Rate = rs.getFloat("Rate");
				String LastName = rs.getString("LastName");
				String FirstName = rs.getString("FirstName");
				int Adults = rs.getInt("Adults");
				int Kids = rs.getInt("Kids");
				System.out.printf("%-11s | %-22s | %-10s | %-10s | %-6s | %-20s | %-20s | %-6s | %-4s \n\n", Res, RoomName, StrCheckIn, StrCheckOut, Rate, LastName, FirstName, Adults, Kids);

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

				while(newFirstName.equals("")) {
					System.out.println("\tYou must have a first name or 'no change'");
					System.out.print("First Name: ");
					line = input.nextLine();
					if (!line.equals("no change")) 
					{ newFirstName = line; }
					else
					{ newFirstName = FirstName; }
				}

				//update last name
				System.out.print("Last Name: ");
				line = input.nextLine();
				String newLastName;
				if (!line.equals("no change")) 
				{ newLastName = line; }
				else
				{ newLastName = LastName; }

				while(newLastName.equals("")) {
					System.out.println("\tYou must have a last name or 'no change'");
					System.out.print("Last Name: ");
					line = input.nextLine();
					if (!line.equals("no change")) 
					{ newLastName = line; }
					else
					{ newLastName = LastName; }
				}

				//update check in date
				java.sql.Date CheckIn = java.sql.Date.valueOf(StrCheckIn);
				java.sql.Date newCheckIn = getNewDate(CheckIn, input, "in");

				//update check out date
				java.sql.Date CheckOut = java.sql.Date.valueOf(StrCheckOut);
				java.sql.Date newCheckOut = getNewDate(CheckOut, input, "out");

				//confirm availability in new range
				while (!confirmAvailability(newCheckIn, newCheckOut, code, conn)) {
					System.out.println("\tThe room is not available between " + newCheckIn + " and " + newCheckOut);
					newCheckIn = getNewDate(CheckIn, input, "in");
					newCheckOut = getNewDate(CheckOut, input, "out");
				}

				//make sure check out is after check in
				while (newCheckOut.getTime() - newCheckIn.getTime() <= 0) 
				{
					System.out.println("\tCheck out must be after check in");
					
					//update check in date
					newCheckIn = getNewDate(CheckIn, input, "in");

					//update check out date
					newCheckOut = getNewDate(CheckOut, input, "out");

					//confirm availability in new range
					while (!confirmAvailability(newCheckIn, newCheckOut, code, conn)) {
						System.out.println("\tThe room is not available between " + newCheckIn + " and " + newCheckOut);
						newCheckIn = getNewDate(CheckIn, input, "in");
						newCheckOut = getNewDate(CheckOut, input, "out");
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

				
				PreparedStatement update = conn.prepareStatement(QueryLines.r3Update);
				update.setDate(1, java.sql.Date.valueOf(newCheckIn.toString()));
				update.setDate(2, java.sql.Date.valueOf(newCheckOut.toString()));
				update.setString(3, newLastName.toUpperCase());
				update.setString(4, newFirstName.toUpperCase());
				update.setInt(5, newAdults);
				update.setInt(6, newKids);
				update.setInt(7, code);

				int rowCount = update.executeUpdate();

				PreparedStatement p2 = conn.prepareStatement("select rooms.RoomName, A.* from "+
							"(select * from iguzmanl.lab7_reservations where code = ?) as A " +
							"inner join iguzmanl.lab7_rooms as rooms on A.room = rooms.RoomCode;");
				p2.setInt(1, code);
				ResultSet rs2 = p2.executeQuery();

				if (rs2.next())
				{
					System.out.println("\nBelow is your updated reservation information\n");
					System.out.printf("%-11s | %-22s | %-10s | %-10s | %-6s | %-20s | %-20s | %-6s | %-4s \n", "Reservation", "Room Name", "Check In", "Check Out", "Rate", "Last Name", "First Name", "Adults", "Kids");
				System.out.println("----------------------------------------------" +
							"----------------------------------------------" + 
							"-----------------------------------------");
					RoomName = rs2.getString("RoomName");
					Res = rs2.getInt("Code");
					RoomCode = rs2.getString("Room");
					StrCheckIn = rs2.getString("CheckIn");
					StrCheckOut = rs2.getString("CheckOut");
					Rate = rs2.getFloat("Rate");
					LastName = rs2.getString("LastName");
					FirstName = rs2.getString("FirstName");
					Adults = rs2.getInt("Adults");
					Kids = rs2.getInt("Kids");
					System.out.printf("%-11s | %-22s | %-10s | %-10s | %-6s | %-20s | %-20s | %-6s | %-4s \n\n", Res, RoomName, StrCheckIn, StrCheckOut, Rate, LastName, FirstName, Adults, Kids);
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

			System.out.println("Reservation Cancellation\n");
			System.out.print("Please provide your reservation number: ");
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
			System.out.print("\nWould you like to cancel this reservation? (yes/no): ");
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
		System.out.println("\nReservation Details");
		
		System.out.println("\nHere you can search for reservations based on first and \nlast name, a range of dates, room code, and/or reservation code.");
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

		String sqlStatement = "select rooms.RoomName, A.* from (select * from iguzmanl.lab7_reservations ";
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
		sqlStatement = sqlStatement + ") as A inner join iguzmanl.lab7_rooms as rooms on A.Room = rooms.RoomCode;";

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
				System.out.printf("%-11s | %-22s | %-10s | %-10s | %-6s | %-20s | %-20s | %-6s | %-4s \n", "Reservation", "Room Name", "Check In", "Check Out", "Rate", "Last Name", "First Name", "Adults", "Kids");
				System.out.println("----------------------------------------------" +
								"----------------------------------------------" + 
								"-----------------------------------------");
				String RoomName;
				int Res;
				String RoomCode;
				String StrCheckIn;
				String StrCheckOut;
				float Rate;
				String LastName;
				String FirstName;
				int Adults;
				int Kids;

				RoomName = rs.getString("RoomName");
				Res = rs.getInt("Code");
				RoomCode = rs.getString("Room");
				StrCheckIn = rs.getString("CheckIn");
				StrCheckOut = rs.getString("CheckOut");
				Rate = rs.getFloat("Rate");
				LastName = rs.getString("LastName");
				FirstName = rs.getString("FirstName");
				Adults = rs.getInt("Adults");
				Kids = rs.getInt("Kids");
				System.out.printf("%-11s | %-23s | %-10s | %-10s | %-6s | %-20s | %-20s | %-6s | %-4s \n", Res, RoomName, StrCheckIn, StrCheckOut, Rate, LastName, FirstName, Adults, Kids);
				

				while(rs.next())
				{
					RoomName = rs.getString("RoomName");
					Res = rs.getInt("Code");
					RoomCode = rs.getString("Room");
					StrCheckIn = rs.getString("CheckIn");
					StrCheckOut = rs.getString("CheckOut");
					Rate = rs.getFloat("Rate");
					LastName = rs.getString("LastName");
					FirstName = rs.getString("FirstName");
					Adults = rs.getInt("Adults");
					Kids = rs.getInt("Kids");
					System.out.printf("%-11s | %-23s | %-10s | %-10s | %-6s | %-20s | %-20s | %-6s | %-4s \n", Res, RoomName, StrCheckIn, StrCheckOut, Rate, LastName, FirstName, Adults, Kids);
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
	
	//-----------------------------------R6---------------------------------------------------------------//
	public static void R6(String jdbcUrl, String dbUsername, String dbPassword) {
		try
		{
			int janTotal ,febTotal, marTotal, aprTotal, mayTotal, junTotal, julyTotal, augTotal, sepTotal, octTotal, novTotal, decTotal, yearTotal;
			yearTotal  = janTotal = febTotal = marTotal = aprTotal = mayTotal = junTotal = julyTotal = augTotal = sepTotal = octTotal = novTotal = decTotal = 0;
			Connection conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
			Statement stmt = conn.createStatement();
			PreparedStatement p = conn.prepareStatement(QueryLines.r6Select);

			ResultSet roomList = stmt.executeQuery(QueryLines.r6GetRooms);

			System.out.println("\nMonthly Revenue Report");
			System.out.printf("\n%-5s | %-5s | %-5s | %-5s | %-5s | %-5s | %-5s | %-5s | %-5s | %-5s | %-5s | %-5s | %-5s | %-5s |\n", "Room","Jan","Feb","Mar","Apr","May","Jun","July","Aug","Sep","Oct","Nov","Dec","Total");
			System.out.println("--------------------------------------------------" +
								"--------------------------------------------------" +
								"-----------");
			while (roomList.next()) {
				yearTotal = 0;
				 String room = roomList.getString("RoomCode");
				 for(int i = 1; i <= 12; i++){
				 	p.setString(i,room);
				 }
				 ResultSet totals = p.executeQuery();
				 while(totals.next()){
				 	janTotal = totals.getInt("Jan");
				 	febTotal = totals.getInt("Feb");
				 	marTotal = totals.getInt("Mar");
				 	aprTotal = totals.getInt("Apr");
				 	mayTotal = totals.getInt("May");
				 	junTotal = totals.getInt("Jun");
				 	julyTotal = totals.getInt("July");
				 	augTotal = totals.getInt("Aug");
				 	sepTotal = totals.getInt("Sep");
				 	octTotal = totals.getInt("Oct");
				 	novTotal = totals.getInt("Nov");
				 	decTotal = totals.getInt("Dec");
				 	yearTotal += janTotal + febTotal + marTotal + aprTotal + mayTotal + junTotal + julyTotal + sepTotal + novTotal + decTotal;
				 }
				 System.out.printf("%-5s | %-5s | %-5s | %-5s | %-5s | %-5s | %-5s | %-5s | %-5s | %-5s | %-5s | %-5s | %-5s | %-5s |\n",room,janTotal,febTotal,marTotal,aprTotal,mayTotal,junTotal,julyTotal,augTotal,sepTotal,octTotal,novTotal,decTotal,yearTotal);
			}
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
			System.out.println("SQL Exception Caught");
			System.exit(1);
		}
	}
}
