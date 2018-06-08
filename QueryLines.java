import java.nio.file.Files;
public class QueryLines {
	//R1

	public static String r1Query = "select rooms.*, nextAvailable, popularity, occupied from "+
	    "(select G.Room, nextAvailable, popularity, occupied from "+
	        "(select C.Room, nextAvailable, popularity from "+
	           
	            "(select Room, min(CheckOut) as nextAvailable from "+
	                "(select *, "+
	                    "(case "+
	                        "when exists "+ 
	                            "(select * from iguzmanl.lab7_reservations as r1 "+ 
	                            "where r1.CheckIn = r2.CheckOut "+
	                            "and r1.Room = r2.Room) "+ 
	                            "then 'No' "+
	                        "else 'Yes' "+
	                    "end) as AvailableTonight "+
	                    "from iguzmanl.lab7_reservations as r2 "+
	                    "where CheckOut >= curdate() "+
	                    "order by CheckIn) as A "+
	                "where AvailableTonight = 'Yes' "+
	                "group by Room) as C "+
	            
	            "inner join "+ 
	                    
	            "(select room, round(sum(datediff(ending, start))/180,2) as popularity from "+
	                "(select *, "+
	                    "case "+
	                        "when datediff(curdate(), checkIn) > 180  "+
	                            "then date_sub(curdate(), interval 180 day) "+
	                        "else checkin "+
	                    "end as start, "+
	                    "case "+
	                        "when datediff(curdate(), checkOut) < 0 "+
	                            "then curdate() "+
	                        "else checkout "+
	                    "end as ending "+
	                "from iguzmanl.lab7_reservations "+
	                    "where datediff(curdate(), checkOut) between 0 and 180 "+
	                        "or datediff(curdate(), checkIn) between 0 and 180 "+
	                    "order by checkout) as A "+
	                "group by room "+
	                "order by popularity desc) as F "+
	                
	            "on C.Room = F.Room) as G "+
	        
	        "inner join "+
	    
	        "(select Room, CheckOut, occupied from "+
	            "(select *, datediff(CheckOut, CheckIn) as occupied "+
	                "from iguzmanl.lab7_reservations) as H "+
	            "where "+
	            "(Room, CheckOut) in "+
	                "(select Room, max(CheckOut) from "+
	                    "iguzmanl.lab7_reservations "+
	                    "where CheckOut <= curdate() "+
	                    "group by Room)) as I "+
	                    
	        "on G.room = I.room) as J "+
	    
	    "inner join iguzmanl.lab7_rooms as rooms "+
	    "on rooms.RoomCode = J.Room "+
	    "order by popularity desc, RoomCode; ";
		

	// R3
	public static String r3QueryAvail = "select " +
		    "case " +
		        "when exists (select * from iguzmanl.lab7_reservations " +
		                        "where room = ? "+
		                        "and code <> ? " +
		                        "and Checkout between ? and ? " +
		                        "and Checkout <> ?) " +
		        	"then 0 " +
		    	"else 1 "+
		    "end as available;";

	public static String r3Update = "update iguzmanl.lab7_reservations " +
		"set CheckIn = ?, CheckOut = ?, LastName = ?, FirstName = ?, Adults = ?, Kids = ? "+
		"where CODE = ?;";
	
	//R2
	public static String r2MaxOccCheck = "SELECT max(maxOcc) as maximum FROM iguzmanl.lab7_rooms;";
	public static String r2RoomAndBed = "SELECT * from " + 
								"iguzmanl.lab7_rooms " +
								"WHERE RoomCode NOT IN( " +
								"SELECT Distinct Room " +
								"FROM iguzmanl.lab7_reservations " +
								"WHERE (? BETWEEN CheckIn AND CheckOut) OR " +
								"(? BETWEEN CheckIn AND CheckOut) ) " +
								"HAVING maxOcc >= ? AND bedType = ? AND RoomCode = ?;";
	
	public static String r2Room = "SELECT * from " + 
								"iguzmanl.lab7_rooms " +
								"WHERE RoomCode NOT IN( " +
								"SELECT Distinct Room " +
								"FROM iguzmanl.lab7_reservations " +
								"WHERE (? BETWEEN CheckIn AND CheckOut) OR " +
								"(? BETWEEN CheckIn AND CheckOut) ) " +
								"HAVING maxOcc >= ? AND RoomCode = ?;";

	public static String r2Bed = "SELECT * from " + 
								"iguzmanl.lab7_rooms " +
								"WHERE RoomCode NOT IN( " +
								"SELECT Distinct Room " +
								"FROM iguzmanl.lab7_reservations " +
								"WHERE (? BETWEEN CheckIn AND CheckOut) OR " +
								"(? BETWEEN CheckIn AND CheckOut) ) " +
								"HAVING maxOcc >= ? AND bedType = ?;";

	public static String r2Any = "SELECT * from " + 
								"iguzmanl.lab7_rooms " +
								"WHERE RoomCode NOT IN( " +
								 "SELECT Distinct Room " +
								  "FROM iguzmanl.lab7_reservations " +
								    "WHERE (? BETWEEN CheckIn AND CheckOut) OR " +
								    "(? BETWEEN CheckIn AND CheckOut) ) " +
								"HAVING maxOcc >= ?;";
	public static String r2GetNextAvilable = "(select Room, min(CheckOut) as nextAvailable from " +
		            "(select *, "+
	                    "(case "+ 
	                        "when exists "+ 
	                            "(select * from iguzmanl.lab7_reservations as r1 "+
	                            "where r1.CheckIn = r2.CheckOut "+
	                            "and r1.Room = r2.Room) "+
	                            "then 'No' "+
	                        "else 'Yes' "+ 
	                    "end) as AvailableTonight "+ 
	                    "from iguzmanl.lab7_reservations as r2 "+
	                    "where CheckOut > curdate() "+
	                    "order by CheckIn) as A "+
	                "where AvailableTonight = 'Yes' "+
		            "group by Room);";
    public static String r2Insert = "INSERT INTO iguzmanl.lab7_reservations (CODE,Room,Checkin,CheckOut,Rate,LastName,FirstName,Adults,Kids) VALUES (?,?,?,?,?,?,?,?,?);";
    public static String r2DoesCodeExist = "SELECT CODE FROM iguzmanl.lab7_reservations WHERE CODE = ?;";
    public static String r2CalculateEndDate = "SELECT DATE_ADD(?, INTERVAL ? DAY) as new;";

/* ------------ R 6 -------------------------------------------------------------------*/
    public static String r6GetRooms = "select RoomCode from iguzmanl.lab7_rooms;";
    public static String r6Select = "SELECT Jan.rev as Jan , Feb.rev as Feb , Mar.rev as Mar, Apr.rev as Apr, May.rev as May , Jun.rev as Jun , July.rev as July, Aug.rev as Aug,Sep.rev as Sep ,Oct.rev as Oct,Nov.rev as Nov ,December.rev as 'Dec' " +
	"FROM ( SELECT sum(Rate) as rev FROM iguzmanl.lab7_reservations  WHERE Room = ? AND DATE_FORMAT(CheckOut, '%Y-%M') = DATE_FORMAT('2018-01-00', \"%Y-%M\") ) as Jan "+
	"join (SELECT sum(Rate) as rev FROM iguzmanl.lab7_reservations  WHERE Room = ? AND DATE_FORMAT(CheckOut, '%Y-%M') = DATE_FORMAT('2018-02-00', \"%Y-%M\") ) as Feb  "+
	"join (SELECT sum(Rate) as rev FROM iguzmanl.lab7_reservations  WHERE Room = ? AND DATE_FORMAT(CheckOut, '%Y-%M') = DATE_FORMAT('2018-03-00', \"%Y-%M\") ) as Mar  "+
	"join (SELECT sum(Rate) as rev FROM iguzmanl.lab7_reservations  WHERE Room = ? AND DATE_FORMAT(CheckOut, '%Y-%M') = DATE_FORMAT('2018-04-00', \"%Y-%M\") ) as Apr  "+
	"join (SELECT sum(Rate) as rev FROM iguzmanl.lab7_reservations  WHERE Room = ? AND DATE_FORMAT(CheckOut, '%Y-%M') = DATE_FORMAT('2018-05-00',\"%Y-%M\") ) as May  "+
	"join (SELECT sum(Rate) as rev FROM iguzmanl.lab7_reservations  WHERE Room = ? AND DATE_FORMAT(CheckOut, '%Y-%M') = DATE_FORMAT('2018-06-00', \"%Y-%M\") ) as Jun  "+
	"join (SELECT sum(Rate) as rev FROM iguzmanl.lab7_reservations  WHERE Room = ? AND DATE_FORMAT(CheckOut, '%Y-%M') = DATE_FORMAT('2018-07-00', \"%Y-%M\") ) as July  "+
	"join (SELECT sum(Rate) as rev FROM iguzmanl.lab7_reservations  WHERE Room = ? AND DATE_FORMAT(CheckOut, '%Y-%M') = DATE_FORMAT('2018-08-00', \"%Y-%M\") ) as Aug  "+
	"join (SELECT sum(Rate) as rev FROM iguzmanl.lab7_reservations  WHERE Room = ? AND DATE_FORMAT(CheckOut, '%Y-%M') = DATE_FORMAT('2018-09-00', \"%Y-%M\") ) as Sep  "+
	"join (SELECT sum(Rate) as rev FROM iguzmanl.lab7_reservations  WHERE Room = ? AND DATE_FORMAT(CheckOut, '%Y-%M') = DATE_FORMAT('2018-10-00', \"%Y-%M\") ) as Oct  "+
	"join (SELECT sum(Rate) as rev FROM iguzmanl.lab7_reservations  WHERE Room = ? AND DATE_FORMAT(CheckOut, '%Y-%M') = DATE_FORMAT('2018-11-00', \"%Y-%M\") ) as Nov  "+
	"join (SELECT sum(Rate) as rev FROM iguzmanl.lab7_reservations  WHERE Room = ? AND DATE_FORMAT(CheckOut, '%Y-%M') = DATE_FORMAT('2018-12-00', \"%Y-%M\") ) as December;";

   	public static String createR2PersonCountCheck(int children, int adults){
		int sum = children + adults;
		String total = Integer.toString(sum);
		String query = "select RoomCode, maxOcc, (case " + 
								   "WHEN maxOcc >= " + total + " THEN 'YES' " +
								   "WHEN maxOcc < " + total + " THEN 'NO' " +
								   "END) AS HasSpace " +
								   "FROM iguzmanl.lab7_rooms;";
		return query;
	}
}
