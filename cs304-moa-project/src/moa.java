// We need to import the java.sql package to use JDBC
import java.sql.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Vector;
// for reading from the command line
import java.io.*;

// for the login window
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;

import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableModel;

public class moa {

	/*
	 * This class implements a graphical login window and a simple text
	 * interface for interacting with the branch table
	 */
	// command line reader
	private BufferedReader in = new BufferedReader(new InputStreamReader(
			System.in));

	private Connection con;
	private Boolean isAdmin = false;
	private GUI gui;

	private String login_name;
	private String login_phone;

	@SuppressWarnings("unused")
	private String wait;

	/*
	 * constructs login window and loads JDBC driver
	 */
	public moa() {
		try {
			// Load the Oracle JDBC driver
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		} catch (SQLException ex) {
			System.out.println("Message: " + ex.getMessage());
			System.exit(-1);
		}
		if (connect("ora_e7u9a", "a71498133")) {
			// if the username and password are valid,
			// remove the login window and display a text menu
			// resetDB();

			// /////////////UNCOMMENT HERE FOR LOGIN ETC///////////////
			gui = new GUI(con);
			showMenu();
		}
	}
	
	public moa(Connection conn){
		con = conn;
	}

	private void resetDB() {
		new dropTables(con);
		createTables create = new createTables(con);
		create.exampleUser();
	}

	/*
	 * connects to Oracle database named ug using user supplied username and
	 * password
	 */
	private boolean connect(String username, String password) {
		String connectURL = "jdbc:oracle:thin:@dbhost.ugrad.cs.ubc.ca:1522:ug";

		try {
			con = DriverManager.getConnection(connectURL, username, password);

			System.out.println("\nConnected to Oracle!");
			return true;
		} catch (SQLException ex) {
			System.out.println("Message: " + ex.getMessage());
			return false;
		}
	}

	private void showMenu() {
		// gui.start();
		int choice;
		boolean quit;

		quit = false;

		try {
			// disable auto commit mode
			con.setAutoCommit(false);

			while (!quit) {
				System.out
						.print("\n\nHey there! What would you like to do?: \n");
				System.out.print("1.  Browse Artifacts\n");
				System.out.print("2.  Browse Events\n");
				System.out.print("3.  Browse Exhibits\n");
				System.out.print("4.  Browse Artists\n");
				System.out.print("5.  Quit\n");
				System.out.print("6.  Insert Member\n");
				System.out.print("7.  Query\n");
				System.out.print("8.  Delete Member\n");
				System.out.print("9.  Update Member\n");
				System.out.print("10. Test division\n>>");
				try {
					choice = Integer.parseInt(in.readLine());
				} catch (Exception e) {
					choice = 0;
				}
				System.out.println(" ");

				switch (choice) {
				case 1:
					browseArtifacts();
					break;
				case 2:
					browseEvents();
					break;
				case 3:
					browseExhibits();
					break;
				case 4:
					browseArtists();
					break;
				case 5:
					quit = true;
					break;
				case 6:
					insertMember();
					break;
				case 7:
					enterQuery();
					break;
				case 8:
					deleteMember();
					break;
				case 9:
					updateMember();
					break;
				case 10:
					excuteQuery();
					break;
				default:
					System.out.println("Please enter a valid choice.");
					// wait for RETURN before displaying menu again
					wait = in.readLine();

				}
			}

			con.close();
			in.close();
			System.out.println("\nGood Bye!\n\n");
			System.exit(0);
		} catch (IOException e) {
			System.out.println("IOException!");

			try {
				con.close();
				System.exit(-1);
			} catch (SQLException ex) {
				System.out.println("Message: " + ex.getMessage());
			}
		} catch (SQLException ex) {
			System.out.println("Message: " + ex.getMessage());
		}
	}

	private void excuteQuery() {
		Statement stmt;
		//Division
		//Find the events RSVPed by every member.
		String query1 = "SELECT e.title, e.startDate, e.fee FROM event e " +
				"WHERE NOT EXISTS ( SELECT * FROM member_1 m WHERE NOT EXISTS" +
				"(SELECT * FROM RSVPs r WHERE e.title=r.title AND m.mname=r.mname AND m.phone=r.phone))";
		//Aggregation
		//Find the name, phone and age of the oldest member.
		String query2 ="SELECT m.mname, m.phone, m.age " +
				"FROM member_1 m " +
				"WHERE m.age=(SELECT MAX(m2.age) FROM member_1 m2)";
		String[] colNames;
		int[] colType;
		String result;
		ResultSet rs;

		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(query2);

			// get info on ResultSet
			ResultSetMetaData rsmd = rs.getMetaData();
			// get number of columns
			int numCols = rsmd.getColumnCount();
			colNames = new String[numCols];
			colType = new int[numCols];
			System.out.println(" ");
			// display column names;
			for (int i = 0; i < numCols; i++) {
				// get column name and print it

				colNames[i] = rsmd.getColumnName(i + 1);
				colType[i] = rsmd.getColumnType(i + 1);
				System.out.printf("%-25s", colNames[i]);
			}
			System.out.println(" ");

			while (rs.next()) {
				for (int i = 0; i < numCols; i++) {
					if (colType[i] == 91) {
						System.out.print(rs.getDate(colNames[i]) + "        ");
					} else {
						result = rs.getString(colNames[i]);
						System.out.printf("%-25s", result);
					}
				}
				System.out.println(" ");
			}
			stmt.close();
		} catch (SQLException ex) {
			System.out.println("Message: " + ex.getMessage());
		}
		

	}

	private void browseArtists() {
		Statement stmt;
		String artistName;
		ResultSet rs;

		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT aname FROM artist");

			// get info on ResultSet
			ResultSetMetaData rsmd = rs.getMetaData();

			// get number of columns
			int numCols = rsmd.getColumnCount();

			System.out.println(" ");

			// display column names;
			for (int i = 0; i < numCols; i++) {
				// get column name and print it

				System.out.printf("%-15s", rsmd.getColumnName(i + 1));
			}

			System.out.println(" ");

			while (rs.next()) {
				// for display purposes get everything from Oracle
				// as a string

				// simplified output formatting; truncation may occur
				artistName = rs.getString("aname");
				System.out.println(artistName);
			}
			stmt.close();
		} catch (SQLException ex) {
			System.out.println("Message: " + ex.getMessage());
		}
		// wait for RETURN before displaying menu again
		try {
			wait = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void browseExhibits() {
		// TODO Auto-generated method stub
		System.out.println("This method will list exhibits.");

	}

	private void browseEvents() {
		// TODO Auto-generated method stub
		// select to_char(startTime, 'dd/mm/yyyy hh24:mi:ss') as startTime from
		// events
		System.out.println("This method will list events.");

	}

	private void browseArtifacts() {
		// TODO Auto-generated method stub
		System.out.println("This method will list artifacts.");
	}

	private void updateMember() {
		String mname;
		String phoneNumber;
		String new_entry;
		String column = null;
		PreparedStatement ps;
		int count = 0;
		int choice;
		boolean pass = false;

		try {
			System.out.print("\nMember Name: ");
			mname = in.readLine();

			System.out.print("\nMember Phone Number: ");
			phoneNumber = in.readLine();

			String temp_statement = "UPDATE member_1 " + "SET $column =? "
					+ "WHERE mname=? AND phone=?";

			while (!pass) {
				System.out.print("Update: \n");
				System.out.print("1.  Address\n");
				System.out.print("2.  Email\n>>");

				try {
					choice = Integer.parseInt(in.readLine());
				} catch (Exception e) {
					choice = 0;
				}

				switch (choice) {
				case 1:
					column = "addr";
					pass = true;
					break;
				case 2:
					column = "email";
					pass = true;
					break;
				default:
					System.out
							.println("Please choose either Address or Email\n");
					break;
				}
			}

			String statement = temp_statement.replace("$column", column);
			ps = con.prepareStatement(statement);

			ps.setString(2, mname);
			ps.setString(3, phoneNumber);

			System.out.print("New entry: ");
			new_entry = in.readLine();
			ps.setString(1, new_entry);

			count = ps.executeUpdate();
			con.commit();
			ps.close();
		} catch (IOException e) {
			System.out.println("IOException!");
		} catch (SQLException ex) {
			System.out.println("Message1: " + ex.getMessage());
			try {
				con.rollback();
			} catch (SQLException ex2) {
				System.out.println("Message2: " + ex2.getMessage());
				System.exit(-1);
			}
		}
		System.out.println("Changed " + count + " row(s).");
		// wait for RETURN before displaying menu again
		try {
			wait = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showMember() {
		// TODO Auto-generated method stub
		System.out.println("This method will show member.");
	}

	private void deleteMember() {
		String mname;
		String phoneNumber;
		PreparedStatement ps;
		int count = 0;

		try {
			// cant be just member? either member_1, member_2, member_3?
			ps = con.prepareStatement("DELETE FROM member_1 WHERE mname=? AND phone=?");

			System.out.print("\nMember Name: ");
			mname = in.readLine();
			ps.setString(1, mname);

			System.out.print("\nMember Phone Number: ");
			phoneNumber = in.readLine();
			ps.setString(2, phoneNumber);

			count = ps.executeUpdate();
			con.commit();
			ps.close();
		} catch (IOException e) {
			System.out.println("IOException!");
		} catch (SQLException ex) {
			System.out.println("Message1: " + ex.getMessage());
			try {
				// undo the insert
				con.rollback();
			} catch (SQLException ex2) {
				System.out.println("Message2: " + ex2.getMessage());
				System.exit(-1);
			}
		}
		System.out.println("Deleted " + count + " row(s).");
		// wait for RETURN before displaying menu again
		try {
			wait = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void insert(String mname, int age, String addr, String email,
			String phoneNumber) throws SQLException {
		int memberFee;
		Date signUpDate;

		PreparedStatement ps;
		PreparedStatement ps2;
		PreparedStatement ps3;

		try {
			ps = con.prepareStatement("INSERT INTO member_1 VALUES (?,?,?,?,?)");
			ps2 = con.prepareStatement("INSERT INTO member_2 VALUES (?,?)");
			ps3 = con.prepareStatement("INSERT INTO member_3 VALUES (?,?)");

			ps.setString(1, mname);
			ps.setInt(2, age);
			if (addr.length() == 0) {
				ps.setString(3, null);
			} else {
				ps.setString(3, addr);
			}
			ps.setString(4, email);
			ps.setString(5, phoneNumber);

			memberFee = calculateFee(age);
			ps2.setInt(2, memberFee);
			ps2.setInt(1, age);

			signUpDate = Calendar.getInstance().getTime();
			java.sql.Date sqlDate = new java.sql.Date(signUpDate.getTime());
			ps3.setString(1, email);
			ps3.setDate(2, sqlDate);

			ps.executeUpdate();
			ps2.executeUpdate();
			ps3.executeUpdate();

			// commit work
			con.commit();
			ps.close();
			ps2.close();
			ps3.close();
		} catch (SQLException ex) {
			System.out.println("Message: " + ex.getMessage());
			throw ex;
		}
	}

	private void insertMember() {
		String mname;
		int age;
		int memberFee;
		Date signUpDate;
		String addr;
		String email;
		String phoneNumber;
		PreparedStatement ps;
		PreparedStatement ps2;
		PreparedStatement ps3;

		try {
			ps = con.prepareStatement("INSERT INTO member_1 VALUES (?,?,?,?,?)");
			ps2 = con.prepareStatement("INSERT INTO member_2 VALUES (?,?)");
			ps3 = con.prepareStatement("INSERT INTO member_3 VALUES (?,?)");

			System.out.print("\nMember Name: ");
			mname = in.readLine();
			ps.setString(1, mname);

			System.out.print("\nMember Age: ");
			age = Integer.parseInt(in.readLine());
			ps.setInt(2, age);

			System.out.print("\nAddress: ");
			addr = in.readLine();

			if (addr.length() == 0) {
				ps.setString(3, null);
			} else {
				ps.setString(3, addr);
			}

			System.out.print("\nE-Mail: ");
			email = in.readLine();
			ps.setString(4, email);

			System.out.print("\nMember Phone Number: ");
			phoneNumber = in.readLine();
			ps.setString(5, phoneNumber);

			memberFee = calculateFee(age);
			ps2.setInt(2, memberFee);
			ps2.setInt(1, age);

			signUpDate = Calendar.getInstance().getTime();
			java.sql.Date sqlDate = new java.sql.Date(signUpDate.getTime());
			ps3.setString(1, email);
			ps3.setDate(2, sqlDate);

			ps.executeUpdate();
			ps2.executeUpdate();
			ps3.executeUpdate();

			// commit work
			con.commit();

			ps.close();
		} catch (IOException e) {
			System.out.println("IOException!");
		} catch (SQLException ex) {
			System.out.println("Message: " + ex.getMessage());
			try {
				// undo the insert
				con.rollback();
			} catch (SQLException ex2) {
				System.out.println("Message: " + ex2.getMessage());
				System.exit(-1);
			}
		}
		// wait for RETURN before displaying menu again
		try {
			wait = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void enterQuery() {
		Statement stmt;
		String query = "";
		String[] colNames;
		int[] colType;
		String result;
		ResultSet rs;

		try {
			System.out.print("\nEnter Query: ");
			try {
				query = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

			stmt = con.createStatement();
			rs = stmt.executeQuery(query);

			// get info on ResultSet
			ResultSetMetaData rsmd = rs.getMetaData();
			// get number of columns
			int numCols = rsmd.getColumnCount();
			colNames = new String[numCols];
			colType = new int[numCols];
			System.out.println(" ");
			// display column names;
			for (int i = 0; i < numCols; i++) {
				// get column name and print it

				colNames[i] = rsmd.getColumnName(i + 1);
				colType[i] = rsmd.getColumnType(i + 1);
				System.out.printf("%-25s", colNames[i]);
			}
			System.out.println(" ");

			while (rs.next()) {
				for (int i = 0; i < numCols; i++) {
					if (colType[i] == 91) {
						System.out.print(rs.getDate(colNames[i]) + "        ");
					} else {
						result = rs.getString(colNames[i]);
						System.out.printf("%-25s", result);
					}
				}
				System.out.println(" ");
			}
			stmt.close();
		} catch (SQLException ex) {
			System.out.println("Message: " + ex.getMessage());
		}
		// wait for RETURN before displaying menu again
		try {
			wait = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private int calculateFee(int age) {
		if (age < 19 || age > 65) {
			return 45;
		} else {
			return 50;
		}
	}


	public static void main(String args[]) {
		// String fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment()
		// .getAvailableFontFamilyNames();
		//
		// for (int i = 0; i < fonts.length; i++) {
		// System.out.println(fonts[i]);
		// }
		// }

		moa m = new moa();
	}
}
