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
