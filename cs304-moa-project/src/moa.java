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
	
	@SuppressWarnings("unused")
	String wait;

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
		if (connect("ora_k8w8", "a20713137")) {
			// if the username and password are valid,
			// remove the login window and display a text menu
			// resetDB();

			// /////////////UNCOMMENT HERE FOR LOGIN ETC///////////////
			gui = new moaGUI();
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
				System.out.print("7.  Query\n");
				System.out.print("8.  Delete Member\n");
				System.out.print("9.  Update Member\n>>");
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
		// select to_char(startTime, 'dd/mm/yyyy hh24:mi:ss') as startTime from events
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
			
			String temp_statement = "UPDATE member_1 "
					  + "SET $column =? " 
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
					System.out.println("Please choose either Address or Email\n");
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
		System.out.println("Changed "+ count +" row(s).");
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
		System.out.println("Deleted "+ count +" row(s).");
		// wait for RETURN before displaying menu again
		try {
			wait = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void insert(String mname, int age, String addr, String email,
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
							System.out.print(rs.getDate(colNames[i])
									+ "        ");
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

	private class moaGUI {
		JFrame mainFrame;

		public moaGUI() {
			// start();
			signIn();

			// Toolkit tk = Toolkit.getDefaultToolkit();
			// Dimension dim = tk.getScreenSize();
			// int xPos = (dim.width / 2) - (mainFrame.getWidth() / 2);
			// int yPos = (dim.height / 2) - (mainFrame.height() / 2);
			//
			// mainFrame.setLocation(xPos, yPos);

		}

		private void signIn() {
			// Frame Stuff
			if (mainFrame != null) {
				mainFrame.dispose();
			}
			setUpFrame("Login", 300, 150);

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
			setUpFrame("Sign Up", 400, 500);

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

						String mname = fields[0].getText() + " "
								+ fields[1].getText();
						int age = Integer.parseInt(fields[2].getText());
						String addr = fields[3].getText();
						String email = fields[4].getText();
						String phone = fields[5].getText();
						try {
							insert(mname, age, addr, email, phone);
						} catch (SQLException e1) {
							JOptionPane.showMessageDialog(mainFrame,
									e1.getMessage());
							try {
								// undo the insert
								con.rollback();
							} catch (SQLException ex2) {
								System.out.println("Message2: "
										+ ex2.getMessage());
								System.exit(-1);
							}
						}
						JOptionPane.showMessageDialog(mainFrame, "Success");
						signIn();
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
			mainFrame.setVisible(true);
		}

		private void start() {
			setUpFrame("Welcome", 500, 500);

			JPanel tabbedPanel = new JPanel(new GridLayout());
			JTabbedPane tabs = new JTabbedPane();

			// probably make this a method of its own
			JPanel p = createProfileTab();
			JPanel p1 = createTab("Browse exhibits and artifacts");
			p1.setOpaque(false);
			JPanel p2 = createTab("RSVP to an event");
			p2.setOpaque(false);
			JPanel p3 = createTab("Search for Artist");
			p3.setOpaque(false);
			JPanel p4 = createTab("Search for Member");
			p4.setOpaque(false);

			final JTextField searchField = new JTextField(20);
			searchField.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("Search Data Base for "
							+ searchField.getText());
				}

			});
			searchField.requestFocus();
			p3.add(searchField);

			final JTextField membSearchField = new JTextField(20);
			membSearchField.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("Search Data Base for "
							+ membSearchField.getText());
				}

			});
			membSearchField.requestFocus();
			p4.add(membSearchField);

			if (isAdmin) {
				tabs.addTab("Members", p4);

			} else {
				tabs.addTab("Profile", p);
			}
			tabs.setMnemonicAt(0, KeyEvent.VK_1);
			tabs.addTab("Exhibits", p1);
			tabs.setMnemonicAt(1, KeyEvent.VK_2);
			tabs.addTab("Events", p2);
			tabs.setMnemonicAt(2, KeyEvent.VK_3);
			tabs.addTab("Search", p3);
			tabs.setMnemonicAt(3, KeyEvent.VK_4);

			tabbedPanel.add(tabs);

			Container pane = mainFrame.getContentPane();
			pane.setLayout(new BorderLayout());

			JLabel adminLabel = new JLabel("signed in as admin");
			adminLabel.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 5));
			Font font = new Font("Helvetica", Font.ITALIC, 12);
			adminLabel.setFont(font);
			if (isAdmin) {
				pane.add(adminLabel, BorderLayout.NORTH);
			}

			mainFrame.add(tabbedPanel);

			mainFrame.setVisible(true);
		}

		/**
		 * @param y
		 * @param x
		 * 
		 */
		private void setUpFrame(String title, int width, int height) {
			mainFrame = new JFrame(title);
			mainFrame.setSize(width, height);
			mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			mainFrame.setResizable(false);
			mainFrame.setLocationRelativeTo(null);
		}

		/**
		 * @param title
		 * @return
		 */
		private JPanel createTab(String title) {
			JPanel p = new JPanel(false);
			JLabel filler = new JLabel(title);
			filler.setHorizontalAlignment(JLabel.CENTER);
			// p.setLayout(new GridLayout(2, 1));
			p.add(filler);
			return p;
		}

		private JPanel createProfileTab() {
			JPanel p = new JPanel(new BorderLayout());
			JPanel p2 = new JPanel(new BorderLayout());
			JPanel labPanel = new JPanel(new GridLayout(5, 1));
			JPanel textPanel = new JPanel(new GridLayout(5, 1));
			final JPanel editPanel = new JPanel(new BorderLayout());
			JPanel footer = new JPanel(new BorderLayout());

			p.add(labPanel, BorderLayout.WEST);
			p.add(textPanel, BorderLayout.CENTER);
			footer.add(editPanel, BorderLayout.CENTER);
			footer.setBorder(BorderFactory.createEmptyBorder(0, 180, 0, 180));
			footer.setOpaque(false);
			p2.add(p, BorderLayout.CENTER);
			p2.add(footer, BorderLayout.SOUTH);

			JLabel mname = new JLabel("Full Name: ");
			labPanel.add(mname);
			JTextField nameField = new JTextField(20);
			nameField.setText("Select Name from Database.");
			nameField.setEditable(false);
			textPanel.add(nameField);

			JLabel age = new JLabel("Age: ");
			labPanel.add(age);
			JTextField ageField = new JTextField(20);
			ageField.setText("Select Age from Database.");
			ageField.setEditable(false);
			textPanel.add(ageField);

			JLabel address = new JLabel("Address: ");
			labPanel.add(address);
			JTextField addressField = new JTextField(20);
			addressField.setText("Select Address from Database.");
			addressField.setEditable(false);
			textPanel.add(addressField);

			JLabel email = new JLabel("E-Mail: ");
			labPanel.add(email);
			JTextField emailField = new JTextField(20);
			emailField.setText("Select E-Mail from Database.");
			emailField.setEditable(false);
			textPanel.add(emailField);

			JLabel digits = new JLabel("Phone Number: ");
			labPanel.add(digits);
			JTextField digitsField = new JTextField(20);
			digitsField.setText("Select Phone Number from Database.");
			digitsField.setEditable(false);
			textPanel.add(digitsField);

			final JTextField[] fields = { nameField, ageField, addressField,
					emailField, digitsField };

			// Name Edit
			final JButton edit = new JButton("edit");
			final JButton save = new JButton("save changes");

			final ActionListener saveListener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					fields[0].setEditable(false);
					fields[1].setEditable(false);
					fields[2].setEditable(false);
					fields[3].setEditable(false);
					fields[4].setEditable(false);

					editPanel.remove(save);
					editPanel.add(edit);
					edit.requestFocus();
				}

			};

			ActionListener editListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					fields[0].setEditable(true);
					fields[1].setEditable(true);
					fields[2].setEditable(true);
					fields[3].setEditable(true);
					fields[4].setEditable(true);

					editPanel.remove(edit);
					editPanel.add(save);
					save.requestFocus();
					fields[0].requestFocus();

				}

			};

			edit.setForeground(Color.BLUE);
			edit.setFont(new Font("Helvetica", Font.PLAIN, 14));
			edit.addActionListener(editListener);
			editPanel.add(edit);

			save.setForeground(Color.BLUE);
			save.setFont(new Font("Helvetica", Font.PLAIN, 14));
			save.addActionListener(saveListener);

			p.setBorder(BorderFactory.createEmptyBorder(120, 10, 120, 50));
			p.setOpaque(false);
			p2.setOpaque(false);
			labPanel.setOpaque(false);
			textPanel.setOpaque(false);
			editPanel.setOpaque(false);
			return p2;
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
