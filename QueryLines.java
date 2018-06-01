public class QueryLines {
	//R1
	public static String r1Query = 
		"select * from " +
		"(select G.Room, nextAvailable, popularity, occupied from "+
		    "(select C.Room, nextAvailable, popularity from " +
		       
		        "(select Room, min(CheckOut) as nextAvailable from " +
		            "(select *, "+
	                    "(case "+
	                        "when exists "+ 
	                            "(select * from iguzmanl.lab7_reservations as r1 " +
	                            "where r1.CheckIn = r2.CheckOut " +
	                            "and r1.Room = r2.Room) " + 
	                            "then 'No' " +
	                        "else 'Yes' " +
	                    "end) as AvailableTonight " +
	                    "from iguzmanl.lab7_reservations as r2 " +
	                    "where CheckOut > curdate() "+
	                    "order by CheckIn) as A " +
	                "where AvailableTonight = 'Yes' " +
		            "group by Room) as C " +
		        
		        "inner join " + 
		                
		        "(select room, round(sum(occupied)/180, 2) as popularity from " +
		            "(select *, datediff(CheckOut, CheckIn) as occupied from " +
		                "(select *, datediff(curdate(), checkOut) as lastXDays, " +
		                    "(case " +
		                        "when exists " + 
		                            "(select * from iguzmanl.lab7_reservations as r1  " +
		                            "where r1.CheckIn = r2.CheckOut " +
		                            "and r1.Room = r2.Room)  " +
		                            "then 'No' " +
		                        "else 'Yes' " +
		                    "end) as AvailableTonight " +
		                "from iguzmanl.lab7_reservations as r2 " +
		                "order by CheckIn) as D " +
		                "where D.lastXDays between 0 and 180) as E " +
		            "group by room " +
		            "order by popularity desc) as F " +
		            
		        "on C.Room = F.Room) as G " +
		    
		    "inner join " +

		    "(select Room, CheckOut, occupied from " +
		        "(select *, datediff(CheckOut, CheckIn) as occupied " +
		            "from iguzmanl.lab7_reservations) as H " +
		        "where " +
		        "(Room, CheckOut) in " +
		            "(select Room, min(CheckOut) from " +
		                "iguzmanl.lab7_reservations " +
		                "where CheckOut < curdate() " +
		                "group by Room)) as I " +
		                
		    "on G.room = I.room) as J " +
		    "inner join iguzmanl.lab7_rooms as rooms "+
		    "on rooms.RoomCode = J.room " + 
		    "order by popularity desc;";
}