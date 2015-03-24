
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

// reset database
public class dropTables {

	public dropTables(Connection con) {
		String tableNames[] = { "member_1", "member_2", "member_3",
				"TimePeriod", "exhibit", "event_hold_for", "object_has_1",
				"object_has_2", "object_has_3", "exhibits", "RSVPs", "creates",
				"artist" };
		Statement stmt;
		
		for (int i = 0; i < 13; i++) {
			try {
				stmt = con.createStatement();
				stmt.executeQuery("DROP TABLE " + tableNames[i]
						+ " CASCADE CONSTRAINTS");
				stmt.close();
			} catch (SQLException ex) {
				System.out.println("Couldn't drop tables. Message: "
						+ ex.getMessage());
			}
		}
	}
}

