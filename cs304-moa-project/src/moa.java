
// We need to import the java.sql package to use JDBC
import java.sql.*;
import java.util.Calendar;
import java.util.Date;
// for reading from the command line
import java.io.*;

// for the login window
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class moa {

	/*
	 * This class implements a graphical login window and a simple text
	 * interface for interacting with the branch table
	 */
	// command line reader
	private BufferedReader in = new BufferedReader(new InputStreamReader(
			System.in));

	private Connection con;

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
		if (connect("ora_b6m8", "a52417128")) {
			// if the username and password are valid,
			// remove the login window and display a text menu
			resetDB();
			showMenu();
		}
	}

	private void resetDB() {
		new dropTables(con);
		new createTables(con);
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
				System.out.print("7.  Query\n>>");

				choice = Integer.parseInt(in.readLine());

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
				default:
					System.out.println("Please enter a valid choice.");
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

	private void browseArtists() {
		Statement stmt;
		String artistName;
		ResultSet rs;
		;
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
			String wait = in.readLine();
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
		System.out.println("This method will list events.");

	}

	private void browseArtifacts() {
		// TODO Auto-generated method stub
		System.out.println("This method will list artifacts.");
	}

	private void updateMember() {
		// TODO Auto-generated method stub
		System.out.println("This method will update member ");

	}

	private void showMember() {
		// TODO Auto-generated method stub
		System.out.println("This method will show member.");
	}

	private void deleteMember() {
		String mname;
		int phoneNumber;
		PreparedStatement ps;

		try {
			ps = con.prepareStatement("DELETE FROM member WHERE mname='?' AND phoneNumber='?''");

			System.out.print("\nMember Name: ");
			mname = in.readLine();
			ps.setString(1, mname);

			System.out.print("\nMember Phone Number: ");
			phoneNumber = Integer.parseInt(in.readLine());
			ps.setInt(2, phoneNumber);

			ps.executeUpdate();
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
		// wait for RETURN before displaying menu again
		try {
			String wait = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
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
			String wait = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void enterQuery() {
		Statement stmt;
		String query = "";
		String[] colNames;
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
			System.out.println(" ");
			// display column names;
			for (int i = 0; i < numCols; i++) {
				// get column name and print it
				colNames[i] = rsmd.getColumnName(i+1);
				System.out.printf("%-18s", colNames[i]);
			}
			System.out.println(" ");

			while (rs.next()) {
				// for display purposes get everything from Oracle
				// as a string

				// simplified output formatting; truncation may occur
				for (int i = 0; i <numCols; i++) {
					result = rs.getString(colNames[i]);
					System.out.printf("%-18s", result);
				}
				System.out.println(" ");
			}
			stmt.close();
		} catch (SQLException ex) {
			System.out.println("Message: " + ex.getMessage());
		}
		// wait for RETURN before displaying menu again
		try {
			String wait = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private int calculateFee(int age) {
		if (age < 19 && age < 65) {
			return 45;
		} else {
			return 50;
		}
	}

	public static void main(String args[]) {
		moa m = new moa();

	}
}
