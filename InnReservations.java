import java.sql.*;
import java.util.Scanner;


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


		Scanner input = new Scanner(System.in);
		String prompt = "\nPlease choose one of the following options\n" +
						"1: Rooms and Rates\n" + 
						"2: Reservations\n" +
						"3: Reservation Change\n" +
						"4: Reservaton Cancellation\n" +
						"5: Reservation Details\n" + 
						"6: Revenue\n" +
						"Q: Quit\n";

		System.out.println("Welcome to the Inn! Here you can query information\n" + 
							"about the inn's rooms and reservation as well as\n" +
							"create, change, or cancel reservations.\n");

		System.out.println(prompt);

		String nextLine = input.nextLine();
		String[] inputLine = nextLine.split(" ");

		while( !(inputLine[0].charAt(0) == 'Q' || inputLine[0].charAt(0)=='q') ) 
		{
			if (inputLine[0].charAt(0) == '1') 
			{
				Queries.R1(jdbcUrl, dbUsername, dbPassword);
			}

			if (inputLine[0].charAt(0) == '3') 
			{
				Queries.R3(jdbcUrl, dbUsername, dbPassword, input);
			}

			System.out.println(prompt);
			nextLine = input.nextLine();
			inputLine = nextLine.split(" ");
		}

		System.out.println("Goodbye!");
		System.exit(0);
	}
}