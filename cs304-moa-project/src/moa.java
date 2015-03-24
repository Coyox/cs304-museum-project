// We need to import the java.sql package to use JDBC
import java.sql.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
// for reading from the command line
import java.io.*;

// for the login window
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;

import javax.swing.SpringLayout;

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
	private moaGUI gui;

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
			//resetDB();
			
///////////////UNCOMMENT HERE FOR LOGIN ETC///////////////
			//gui = new moaGUI();
			showMenu();
		}
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
				System.out.print("7.  Query\n>>");
				
				String cmd = in.readLine();
				try {
					choice = Integer.parseInt(cmd);
				} catch (Exception e) {
					System.out.println("Please enter a valid choice.");
					choice = 0;
				}
				//choice = Integer.parseInt(in.readLine());

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
			// cant be just member? either member_1, member_2, member_3?
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

				colNames[i] = rsmd.getColumnName(i+1);
				colType[i] = rsmd.getColumnType(i+1);
				System.out.printf("%-25s", colNames[i]);
			}
			System.out.println(" ");

			while (rs.next()) {
				for (int i = 0; i <numCols; i++) {
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
			String wait = in.readLine();
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

	private class moaGUI {
		JFrame mainFrame;

		public moaGUI() {
			start();
			// signIn();

			// Toolkit tk = Toolkit.getDefaultToolkit();
			// Dimension dim = tk.getScreenSize();
			// int xPos = (dim.width / 2) - (mainFrame.getWidth() / 2);
			// int yPos = (dim.height / 2) - (mainFrame.height() / 2);
			//
			// mainFrame.setLocation(xPos, yPos);

		}

		private void signIn() {
			// Frame Stuff
			mainFrame = new JFrame("Login");
			mainFrame.setSize(300, 150);
			mainFrame.setLocationRelativeTo(null);
			mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			// Login Panels
			JPanel labelPanel = new JPanel(new GridLayout(2, 1));
			JPanel fieldPanel = new JPanel(new GridLayout(2, 1));
			JPanel footer = new JPanel();
			JLabel username = new JLabel("Username: ", JLabel.RIGHT);
			username.setToolTipText("Your username is your E-Mail address");
			JLabel password = new JLabel("Password: ", JLabel.RIGHT);

			// Text Field
			final JTextField usernameField = new JTextField(15);
			usernameField.requestFocus();
			final JPasswordField passwordField = new JPasswordField(15);
			passwordField.setEchoChar('*');

			// Buttons
			final JCheckBox admin = new JCheckBox("Admin");
			JButton login = new JButton("Login");
			mainFrame.getRootPane().setDefaultButton(login);
			login.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (memberExists(usernameField.getText(),
							String.valueOf(passwordField.getPassword()))) {
						if (admin.isSelected()) {
							isAdmin = true;
						}
						mainFrame.dispose();
						start();
					} else {
						passwordField.setText("");
						passwordField.requestFocus();
					}
				}
			});

			JButton signup = new JButton("Sign Up");
			signup.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					signUp();
				}
			});

			// add to main frame
			labelPanel.add(username);
			fieldPanel.add(usernameField);
			labelPanel.add(password);
			fieldPanel.add(passwordField);
			labelPanel
					.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));
			fieldPanel
					.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));

			footer.add(login);
			footer.add(signup);
			footer.add(admin);
			mainFrame.add(labelPanel, BorderLayout.WEST);
			mainFrame.add(fieldPanel, BorderLayout.CENTER);
			mainFrame.add(footer, BorderLayout.SOUTH);

			// anonymous inner class for closing the window
			// mainFrame.addWindowListener(new WindowAdapter() {
			// public void windowClosing(WindowEvent e) {
			// System.exit(0);
			// }
			// });

			// visibility
			mainFrame.pack();
			mainFrame.setResizable(false);
			mainFrame.setVisible(true);
		}

		// Check login table
		private boolean memberExists(String username, String password) {
			PreparedStatement ps;
			ResultSet rs;
			try {
				ps = con.prepareStatement("SELECT * FROM login WHERE email=? AND password=?");

				ps.setString(1, username);
				ps.setString(2, password);

				rs = ps.executeQuery();

				if (rs.next()) {
					con.commit();
					ps.close();
					return true;
				} else {
					System.out.println("user does not exist");
					return false;
				}
			} catch (SQLException ex) {
				System.out.println("Message: " + ex.getMessage());
				try {
					con.rollback();
				} catch (SQLException ex2) {
					System.out.println("Message: " + ex2.getMessage());
					System.exit(-1);
				}
				return false;
			}
		}

		// insert new member into member table
		private void signUp() {
			mainFrame.dispose();
			mainFrame = new JFrame("Sign Up");
			mainFrame.setSize(400, 500);
			mainFrame.setLocationRelativeTo(null);
			mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			final JPanel panel = new JPanel();
			String[] labels = { "First Name: ", "Last Name: ", "Age: ",
					"Address: ", "E-Mail: ", "Phone Number: " };
			int numPairs = labels.length;
			final JTextField[] fields = { new JTextField(20),
					new JTextField(20), new JTextField(20), new JTextField(20),
					new JTextField(20), new JTextField(20) };

			JPanel labelPanel = new JPanel(new GridLayout(labels.length, 1));
			JPanel fieldPanel = new JPanel(new GridLayout(labels.length, 1));
			JPanel footer = new JPanel();

			for (int i = 0; i < numPairs; i++) {
				JLabel label = new JLabel(labels[i], JLabel.RIGHT);
				label.setLabelFor(fields[i]);
				labelPanel.add(label);

				JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
				p.add(fields[i]);
				fieldPanel.add(p);
			}

			JButton signupButton = new JButton("Sign Up");
			signupButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
			JButton backButton = new JButton("Cancel");
			signupButton.setAlignmentX(JButton.CENTER_ALIGNMENT);

			footer.add(backButton);
			footer.add(signupButton);

			signupButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					PreparedStatement ps;
					Boolean incomplete = false;
					for (int i = 0; i < fields.length; i++) {
						if (fields[i].getText().isEmpty()) {
							incomplete = true;
							fields[i].setBackground(Color.pink);
						}
					}
					if (incomplete) {
						JOptionPane.showMessageDialog(mainFrame,
								"Please fill in all fields");
					} else {
						try {
							ps = con.prepareStatement("INSERT INTO member_1 VALUES (?, ?, ?, ?, ?)");

							ps.setString(1, fields[0].getText() + " "
									+ fields[1].getText());

							ps.setInt(2, Integer.parseInt(fields[2].getText()));

							ps.setString(3, fields[3].getText());

							ps.setString(4, fields[4].getText());

							ps.setInt(5, Integer.parseInt(fields[5].getText()));

							ps.executeQuery();
							con.commit();
							ps.close();

						} catch (SQLException ex) {
							System.out.println("Message: " + ex.getMessage());
							try {
								// undo the insert
								con.rollback();
							} catch (SQLException ex2) {
								System.out.println("Message: "
										+ ex2.getMessage());
								System.exit(-1);
							}
						}
						JOptionPane.showMessageDialog(mainFrame, "Success");
						mainFrame.dispose();
					}
				}
			});

			backButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					mainFrame.dispose();
					signIn();
				}
			});

			mainFrame.add(labelPanel, BorderLayout.WEST);
			labelPanel.setBorder(BorderFactory
					.createEmptyBorder(10, 10, 10, 10));
			// labelPanel.setBackground(Color.darkGray);
			mainFrame.add(fieldPanel, BorderLayout.CENTER);
			mainFrame.add(footer, BorderLayout.SOUTH);
			// visibility
			mainFrame.pack();
			mainFrame.setResizable(false);
			mainFrame.setVisible(true);
		}

		private void start() {
			// showMenu();
			mainFrame = new JFrame("Welcome");
			mainFrame.setSize(500, 500);
			mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			mainFrame.setLocationRelativeTo(null);
			
			mainFrame.setLayout(new GridLayout(2,1));
			
			JPanel header = new JPanel();
			header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			
			JLabel welcome = new JLabel("Welcome! Let's get started :)");
			final JLabel profile = new JLabel("My Profile");
			
			
			profile.setHorizontalAlignment(JLabel.LEFT);
			
			header.add(profile, BorderLayout.NORTH);
			header.add(welcome, BorderLayout.NORTH);
			
			//profile.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 100));
			profile.setForeground(Color.BLUE);

			profile.addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
					viewProfile();
					Font font = profile.getFont();
					Map attributes = font.getAttributes();
					attributes.put(TextAttribute.UNDERLINE,
							TextAttribute.UNDERLINE);
					profile.setFont(font.deriveFont(attributes));
					profile.removeMouseListener(this);
				}

				@Override
				public void mousePressed(MouseEvent e) {
					profile.setForeground(Color.darkGray);
				}

				@Override
				public void mouseReleased(MouseEvent e) {

				}

				@Override
				public void mouseEntered(MouseEvent e) {
					Font font = profile.getFont();
					Map attributes = font.getAttributes();
					attributes.put(TextAttribute.UNDERLINE,
							TextAttribute.UNDERLINE_ON);
					profile.setFont(font.deriveFont(attributes));
				}

				@Override
				public void mouseExited(MouseEvent e) {
					Font font = profile.getFont();
					Map attributes = font.getAttributes();
					attributes.put(TextAttribute.UNDERLINE,
							TextAttribute.UNDERLINE);
					profile.setFont(font.deriveFont(attributes));
				}

			});
			profile.setToolTipText("Review your info");

			//header.add(profile, BorderLayout.WEST);
			//header.add(welcome);
			mainFrame.add(header);

			mainFrame.setVisible(true);
		}

		private void viewProfile() {
			JPanel profilePanel = new JPanel();
			profilePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20,
					20));

			JTextArea text = new JTextArea();
			text.setText("select member and display info");
			profilePanel.add(text);

			mainFrame.add(profilePanel);
			mainFrame.validate();
			mainFrame.repaint();
		}

	}

	public static void main(String args[]) {
		moa m = new moa();
	}
}
