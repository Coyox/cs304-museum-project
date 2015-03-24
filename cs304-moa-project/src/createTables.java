
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class createTables {

	String timePeriod = "CREATE TABLE TimePeriod(startDate DATE NOT NULL, endDate DATE NOT NULL, "
			+ "startTime DATE NOT NULL, endTime DATE NOT NULL, "
			+ "PRIMARY KEY (startDate, endDate, startTime, endTime))";
	String event = "CREATE TABLE event_hold_for(title VARCHAR(100), "
			+ "startDate DATE NOT NULL, endDate DATE NOT NULL, startTime DATE NOT NULL, endTime DATE NOT NULL, fee INTEGER,"
			+ "PRIMARY KEY (title), FOREIGN KEY (startDate, endDate, startTime, endTime) REFERENCES TimePeriod)";
	String exhibit = "CREATE TABLE exhibit(ename VARCHAR(100), "
			+ "startDate DATE NOT NULL, endDate DATE NOT NULL, startTime DATE NOT NULL, endTime DATE NOT NULL, specialist VARCHAR(50),"
			+ "PRIMARY KEY (ename), FOREIGN KEY (startDate, endDate, startTime, endTime) REFERENCES TimePeriod)";
	String member_1 = "CREATE TABLE member_1(" + "mname VARCHAR(50) NOT NULL, "
			+ "age INTEGER NOT NULL," + "addr VARCHAR(50),"
			+ "email VARCHAR(50)," + "phone VARCHAR(12) NOT NULL,"
			+ "PRIMARY KEY (mname, phone), " + "UNIQUE(mname, email)" + ")";
	String member_2 = "CREATE TABLE member_2(" + "age INTEGER NOT NULL, "
			+ "fee INTEGER NOT NULL, " + "PRIMARY KEY (age)" + ")";
	String member_3 = "CREATE TABLE member_3(" + "email VARCHAR(50) NOT NULL, "
			+ "signUpDate DATE, " + "PRIMARY KEY (email)" + ")";
	String artist = "CREATE TABLE artist(aname VARCHAR(20), dateOfBirth DATE, nationality VARCHAR(20), "
			+ "PRIMARY KEY (aname, dateOfBirth))";
	String object_has_1 = "CREATE TABLE object_has_1(objectID INTEGER, type VARCHAR(20), location VARCHAR(100), "
			+ "placeMade VARCHAR(50), dateMade DATE, "
			+ "PRIMARY KEY (objectID))";
	String object_has_2 = "CREATE TABLE object_has_2(placeMade VARCHAR(50), dateMade DATE, "
			+ "culture VARCHAR(50), PRIMARY KEY (placeMade, dateMade))";
	String object_has_3 = "CREATE TABLE object_has_3(location VARCHAR(100), ename VARCHAR(100), "
			+ "PRIMARY KEY (location), FOREIGN KEY (ename) REFERENCES exhibit)";
	String creates = "CREATE TABLE creates(aname VARCHAR(50), dateOfBirth DATE, objectID INTEGER, "
			+ "PRIMARY KEY (aname, dateOfBirth, objectID), "
			+ "FOREIGN KEY (aname, dateOfBirth) REFERENCES artist, "
			+ "FOREIGN KEY (objectID) REFERENCES object_has_1)";
	String exhibits = "CREATE TABLE exhibits(title VARCHAR(100), objectID INTEGER, "
			+ "PRIMARY KEY (title, objectID), "
			+ "FOREIGN KEY (title) REFERENCES event_hold_for, "
			+ "FOREIGN KEY (objectID) REFERENCES object_has_1)";
	String RSVPs = "CREATE TABLE RSVPs(title VARCHAR(100), mname VARCHAR(50), phone VARCHAR(12), "
			+ "PRIMARY KEY (title, mname, phone), "
			+ "FOREIGN KEY (title) REFERENCES event_hold_for, "
			+ "FOREIGN KEY (mname, phone) REFERENCES member_1)";
	String queries[] = { timePeriod, event, exhibit, member_1, member_2,
			member_3, artist, object_has_1, object_has_2, object_has_3,
			creates, exhibits, RSVPs };

	public createTables(Connection con) {
		Statement stmt;
		
		for (int i = 0; i < 13; i++) {
			try {
				stmt = con.createStatement();
				stmt.executeQuery(queries[i]);
				stmt.close();
			} catch (SQLException ex) {
				System.out.println("Couldn't create creates table. Message: "
						+ ex.getMessage());
			}
		}
	}

}