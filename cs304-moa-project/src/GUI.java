import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GUI {
	JFrame mainFrame;
	Boolean isAdmin;
	private String login_name;
	private String login_phone;
	Connection con;

	public GUI(Connection conn) {

		isAdmin = true;
		con = conn;
		
		//start();
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
					login_name = usernameField.getText();
					login_phone = String.valueOf(passwordField
							.getPassword());
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

	// Check if member exists table
	private boolean memberExists(String username, String password) {
		PreparedStatement ps;
		ResultSet rs;
		try {
			ps = con.prepareStatement("SELECT * FROM member_1 WHERE mname=? AND phone=?");

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
		final moa m = new moa(con);
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
						m.insert(mname, age, addr, email, phone);
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
		// JPanel p3 = createTab("Search for Artist");
		JPanel p3 = new JPanel(new BorderLayout());
		p3.setOpaque(false);
		JPanel p4 = createTab("Search for Member");
		//JPanel p4 = new JPanel(new BorderLayout());
		p4.setOpaque(false);

		JPanel aLabelPanel = new JPanel(new GridLayout(2, 1));
		JPanel aFieldPanel = new JPanel(new GridLayout(2, 1));
		JPanel aSearchPanel = new JPanel(new GridLayout(2,1));

		JLabel aNameLabel = new JLabel("Search by artist name: ",
				JLabel.RIGHT);
		JLabel aNatLabel = new JLabel("Search by artist nationality: ",
				JLabel.RIGHT);

		final JTextField artistName = new JTextField(20);
		final JTextField artistNat = new JTextField(20);
		
		JButton aNameButton = new JButton("Search");
		JButton aNatButton = new JButton("Search");

		aLabelPanel.add(aNameLabel);
		aFieldPanel.add(artistName);
		aLabelPanel.add(aNatLabel);
		aFieldPanel.add(artistNat);
		aSearchPanel.add(aNameButton);
		aSearchPanel.add(aNatButton);

		artistName.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Search Data Base for "
						+ artistName.getText());
			}

		});

		artistName.requestFocus();
		aLabelPanel.setOpaque(false);
		p3.add(aLabelPanel, BorderLayout.WEST);
		p3.add(aFieldPanel, BorderLayout.CENTER);
		p3.add(aSearchPanel, BorderLayout.EAST);
		p3.setBorder(BorderFactory.createEmptyBorder(10, 10, 360, 50));

		searchPanel(p4);

		if (isAdmin) {
			// Trophy button
			// //////////////////////////////////////////////////
			JButton award = new JButton("Award Oldest Member!");

			award.addActionListener(new ActionListener() {
				Query q = new Query();
				ResultSet rs;

				@Override
				public void actionPerformed(ActionEvent e) {
					rs = q.queryWhere(con, "m.mname, m.phone, m.age",
							"member_1 m",
							"(m.age=(select max(m2.age) from member_1 m2))");
					ImageIcon icon = new ImageIcon("lib/crash.png");
					tablePopUp(rs, "Winner(s)!", icon);
				}

			});
			Image image = Toolkit.getDefaultToolkit().getImage(
					"lib/trophy.png");
			JLabel imageLabel = new JLabel();
			imageLabel.setIcon(new ImageIcon(image));
			final JPanel awardPanel = new JPanel(new BorderLayout());
			awardPanel.add(imageLabel, BorderLayout.NORTH);
			awardPanel.setOpaque(false);
			awardPanel.add(award);
			award.addActionListener(new ActionListener() {
				Statement stmt;
				String query ="SELECT m.mname, m.phone,m.age " +
						"FROM member_1 m " +
						"WHERE m.age=(SELECT MAX(m2.age) FROM member_1 m2)";
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					// will display profile for searched member
					// createProfileTabe blablabla
					

					// names of columns

					ResultSetMetaData rsmd;
					try {
						stmt = con.createStatement();
						ResultSet rs = stmt.executeQuery(query);
						rsmd = rs.getMetaData();

						Vector<String> columnNames = new Vector<String>();
						int columnCount = rsmd.getColumnCount();
						for (int i = 1; i <= columnCount; i++) {
							columnNames.add(rsmd.getColumnName(i));
						}

						// data of the table
						Vector<Vector<Object>> data = new Vector<Vector<Object>>();
						while (rs.next()) {
							Vector<Object> vector = new Vector<Object>();
							for (int j = 1; j <= columnCount; j++) {
								vector.add(rs.getObject(j));
							}
							data.add(vector);
						}
						ImageIcon icon = new ImageIcon("lib/blank-profile.png");
						DefaultTableModel defTable = new DefaultTableModel(
								data, columnNames);
						JTable table = new JTable(defTable);
						JOptionPane.showMessageDialog(mainFrame,
								new JScrollPane(table),
								"Oldest Member!" , 0, icon);

					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(mainFrame,
								e1.getMessage());
					}
				}
			});
			p4.add(awardPanel);
			awardPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0,
					0));
			// //////////////////////////////////////////////////////////////////
			tabs.addTab("Members", p4);

		} else {
			tabs.addTab("Profile", p);
		}
		tabs.setMnemonicAt(0, KeyEvent.VK_1);
		tabs.addTab("Exhibits", p1);
		tabs.setMnemonicAt(1, KeyEvent.VK_2);
		tabs.addTab("Events", p2);
		tabs.setMnemonicAt(2, KeyEvent.VK_3);
		tabs.addTab("Artists", p3);
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
	 * @param p4
	 */
	private void searchPanel(JPanel p4) {
		JPanel membPanel = new JPanel(new BorderLayout());
		JPanel labPanel = new JPanel(new GridLayout(2, 1));
		JPanel textPanel = new JPanel(new GridLayout(2, 1));
		final JPanel editPanel = new JPanel(new BorderLayout());
		JPanel footer = new JPanel(new BorderLayout());

		membPanel.add(labPanel, BorderLayout.WEST);
		membPanel.add(textPanel, BorderLayout.CENTER);
		footer.add(editPanel, BorderLayout.CENTER);
		footer.setBorder(BorderFactory.createEmptyBorder(0, 180, 0, 180));
		footer.setOpaque(false);
		p4.add(membPanel, BorderLayout.CENTER);
		p4.add(footer, BorderLayout.SOUTH);

		JLabel name = new JLabel("Member Name: ");
		labPanel.add(name);
		final JTextField nameField = new JTextField(20);
		nameField.requestFocus();
		textPanel.add(nameField);

		JLabel phone = new JLabel("Phone Number: ");
		labPanel.add(phone);
		final JTextField phoneField = new JTextField(20);
		textPanel.add(phoneField);

		final JButton search = new JButton("search");

		search.setForeground(Color.BLUE);
		search.setFont(new Font("Helvetica", Font.PLAIN, 14));
		search.addActionListener(new ActionListener() {
			Query q = new Query();

			@Override
			public void actionPerformed(ActionEvent e) {
				ResultSet rs;
				if (nameField.getText().equals("")){
					 rs = q.queryWhere(con, "*", "member_1",
							"(phone='"+ phoneField.getText() + "')");
				}
				else if (nameField.getText().contains("%")||nameField.getText().contains("_")){
					rs = q.queryWhere(con, "*", "member_1",
							"(mname like '" + nameField.getText() + "')");
				}
				else if (phoneField.getText().equals("")){
					rs = q.queryWhere(con, "*", "member_1",
							"(mname='" + nameField.getText() + "')");
				}
				
				else{
					 rs = q.queryWhere(con, "*", "member_1",
						"(mname='" + nameField.getText() + "' and phone='"
								+ phoneField.getText() + "')");
				}
			

				// names of columns
				String title = "User: " + nameField.getText();
				ImageIcon icon = new ImageIcon("blank-profile.png");
				tablePopUp(rs, title, icon);
			}
		});
		editPanel.add(search);

		p4.setOpaque(false);
		membPanel.setOpaque(false);
		labPanel.setOpaque(false);
		textPanel.setOpaque(false);
		editPanel.setOpaque(false);
	}

	/**
	 * @param rs
	 * @param title
	 * @param icon2 
	 */
	private void tablePopUp(ResultSet rs, String title, ImageIcon icon) {
		ResultSetMetaData rsmd;
		try {
			rsmd = rs.getMetaData();

			Vector<String> columnNames = new Vector<String>();
			int columnCount = rsmd.getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				columnNames.add(rsmd.getColumnName(i));
			}

			// data of the table
			Vector<Vector<Object>> data = new Vector<Vector<Object>>();
			while (rs.next()) {
				Vector<Object> vector = new Vector<Object>();
				for (int j = 1; j <= columnCount; j++) {
					vector.add(rs.getObject(j));
				}
				data.add(vector);
			}
			DefaultTableModel defTable = new DefaultTableModel(
					data, columnNames);
			JTable table = new JTable(defTable);
			JOptionPane.showMessageDialog(mainFrame,
					new JScrollPane(table), title, 0, icon);

		} catch (SQLException e1) {
			JOptionPane.showMessageDialog(mainFrame,
					e1.getMessage());
		}
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

		Query q = new Query();
		final Member m = new Member();

		// SELECT * FROM member_1 WHERE mname = login_name AND phone =
		// login_phone
		ResultSet rs = q.queryWhere(con, "*", "member_1", "mname = '"
				+ login_name + "' AND phone ='" + login_phone + "'");

		String db_name = null;
		int db_age = -1;
		String db_addr = null;
		String db_email = null;
		String db_phone = null;
		// String db_sign = null;

		try {
			rs.next();

			db_name = rs.getString("mname");
			db_age = rs.getInt("age");
			db_addr = rs.getString("addr");
			db_email = rs.getString("email");
			db_phone = rs.getString("phone");
			rs.close();

			// db_sign = rs.getString("signUpDate");
		} catch (SQLException ex) {
			System.out.println("createProfile Message: " + ex.getMessage());
		}
		
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
		nameField.setText(db_name);
		nameField.setEditable(false);
		textPanel.add(nameField);

		JLabel age = new JLabel("Age: ");
		labPanel.add(age);
		JTextField ageField = new JTextField(20);
		ageField.setText(Integer.toString(db_age));
		ageField.setEditable(false);
		textPanel.add(ageField);

		JLabel address = new JLabel("Address: ");
		labPanel.add(address);
		JTextField addressField = new JTextField(20);
		addressField.setText(db_addr);
		addressField.setEditable(false);
		textPanel.add(addressField);

		JLabel email = new JLabel("E-Mail: ");
		labPanel.add(email);
		JTextField emailField = new JTextField(20);
		emailField.setText(db_email);
		emailField.setEditable(false);
		textPanel.add(emailField);

		JLabel digits = new JLabel("Phone Number: ");
		labPanel.add(digits);
		JTextField digitsField = new JTextField(20);
		digitsField.setText(db_phone);
		digitsField.setEditable(false);
		textPanel.add(digitsField);

		final JTextField[] fields = { nameField, ageField, addressField,
				emailField, digitsField };

		// Name Edit
		final JButton edit = new JButton("Edit");
		final JButton save = new JButton("Save Changes");

		final ActionListener saveListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String mname_new = fields[0].getText();
				fields[0].setEditable(false);
				int age_new = Integer.parseInt(fields[1].getText());
				fields[1].setEditable(false);
				String addr_new = fields[2].getText();
				fields[2].setEditable(false);
				String email_new = fields[3].getText();
				fields[3].setEditable(false);
				String phone_new = fields[4].getText();
				fields[4].setEditable(false);

				int err = m.updateMember(con, login_name, login_phone,
						mname_new, phone_new, addr_new, age_new, email_new);
				if (err < 1) {
					System.out.println("Error updating member");
				}
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