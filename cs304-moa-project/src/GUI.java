import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public class GUI {
	JFrame mainFrame;
	Boolean isAdmin = false;
	private String login_name;
	private String login_phone;
	Connection con;

	public GUI(Connection conn) {
		// isAdmin = true;
		con = conn;
		// start();
		signIn();
	}

	public void signIn() {
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
				if (admin.isSelected()) {

					if (usernameField.getText().equals("admin")
							&& String.valueOf(passwordField.getPassword())
									.equals("")) {
						isAdmin = true;
						mainFrame.dispose();
						start();
					} else {
						passwordField.setText("");
						passwordField.requestFocus();
					}
				} else {
					if ((memberExists(usernameField.getText(),
							String.valueOf(passwordField.getPassword())))) {
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
		labelPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));
		fieldPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));

		footer.add(login);
		footer.add(signup);
		footer.add(admin);
		mainFrame.add(labelPanel, BorderLayout.WEST);
		mainFrame.add(fieldPanel, BorderLayout.CENTER);
		mainFrame.add(footer, BorderLayout.SOUTH);
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
				JOptionPane.showMessageDialog(mainFrame,
						"Incorrect Login Info");
				return false;
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(mainFrame,
					ex.getMessage());
			try {
				con.rollback();
			} catch (SQLException ex2) {
				JOptionPane.showMessageDialog(mainFrame,
						ex2.getMessage());
				System.exit(-1);
			}
			return false;
		}
	}

	// insert new member into member table
	private void signUp() {
		final Member m = new Member();
		mainFrame.dispose();
		setUpFrame("Sign Up", 400, 500);

		String[] labels = { "Name: ", "Age: ", "Address: ", "E-Mail: ",
				"Phone Number: " };
		int numPairs = labels.length;
		final JTextField[] fields = { new JTextField(20), new JTextField(20),
				new JTextField(20), new JTextField(20), new JTextField(20) };

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

					String mname = fields[0].getText();
					int age = Integer.parseInt(fields[1].getText());
					String addr = fields[2].getText();
					String email = fields[3].getText();
					String phone = fields[4].getText();
					try {
						m.insert(con, mname, age, addr, email, phone);
						JOptionPane.showMessageDialog(mainFrame, "Success");
						signIn();
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(mainFrame,
								e1.getMessage());
						try {
							// undo the insert
							con.rollback();
						} catch (SQLException ex2) {
							JOptionPane.showMessageDialog(mainFrame,
									ex2.getMessage());
							System.exit(-1);
						}
					}
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
		labelPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		mainFrame.add(fieldPanel, BorderLayout.CENTER);
		mainFrame.add(footer, BorderLayout.SOUTH);
		// visibility
		mainFrame.pack();
		mainFrame.setVisible(true);
	}

	private void start() {
		setUpFrame("Welcome", 500, 500);

		JPanel tabbedPanel = createTabs();

		Container pane = mainFrame.getContentPane();
		pane.setLayout(new BorderLayout());

		JLabel adminLabel = new JLabel("Signed in as admin.");
		adminLabel.setOpaque(false);
		adminLabel.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 5));
		Font font = new Font("Helvetica", Font.ITALIC, 12);
		adminLabel.setFont(font);
		if (isAdmin) {
			pane.add(adminLabel, BorderLayout.NORTH);
		}

		mainFrame.add(tabbedPanel);
		mainFrame.pack();
		mainFrame.setVisible(true);
	}

	private JPanel createTabs() {
		JPanel tabbedPanel = new JPanel(new GridLayout());
		JTabbedPane tabs = new JTabbedPane();

		if (!isAdmin) {
			JPanel profile = createProfileTab();
			tabs.addTab("Profile", profile);
		} else {
			JPanel admin = createAdminTab();
			JPanel award = createAwardPanel();
			admin.add(award);
			tabs.addTab("Members", admin);
		}
		tabs.setMnemonicAt(0, KeyEvent.VK_1);

		JPanel exhibit = createExhibitSearch();
		JPanel RSVP = createRSVPTab();
		JPanel RSVPmem = RSVPTab();
		JPanel Artist = createArtistTab();

		tabs.addTab("Exhibit", exhibit);
		tabs.setMnemonicAt(1, KeyEvent.VK_2);

		if (!isAdmin) {
			tabs.addTab("Events", RSVPmem);
		} else {

			tabs.addTab("Events", RSVP);
		}
		tabs.setMnemonicAt(2, KeyEvent.VK_3);
		tabs.addTab("Artists", Artist);
		tabs.setMnemonicAt(3, KeyEvent.VK_4);
		tabbedPanel.add(tabs);

		return tabbedPanel;
	}

	private JPanel createAdminTab() {
		// JPanel admin = createTab("Search for Member");
		JPanel main = new JPanel();
		main.setOpaque(false);
		JPanel admin = new JPanel(new BorderLayout());
		admin.setOpaque(false);

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
		admin.add(membPanel, BorderLayout.CENTER);
		admin.add(footer, BorderLayout.SOUTH);

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
				// names of columns
				String title = "User Searching";
				ImageIcon icon = new ImageIcon("lib/blank-profile.png");

				// Read inputs
				String mName = nameField.getText();
				String mPhone = phoneField.getText();
				boolean emptyName = mName.equals("");
				boolean emptyPhone = mPhone.equals("");

				// Check conditions for search

				// If both fields empty get all members
				if (emptyName && emptyPhone) {
					rs = q.query(con, "*", "member_1");
				} else if (emptyName && !emptyPhone) {
					if (!mPhone.matches("\\d+") || mPhone.length() > 10) {
						JOptionPane
								.showMessageDialog(
										new JFrame(),
										"Phone number must be a sequence of numbers up to 10 characters.",
										"Phone Number Error",
										JOptionPane.ERROR_MESSAGE);
						return;
					}
					rs = q.queryWhere(con, "*", "member_1", "(phone LIKE '%"
							+ mPhone + "%')");
				} else if (!emptyName && emptyPhone) {
					if (!Pattern.matches("[a-zA-Z\\s]*", mName)) {
						JOptionPane.showMessageDialog(new JFrame(),
								"Name must be a sequence of letters or words.",
								"Name Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					// Because db is not caps agnostic
					String capFirst = mName.substring(0, 1).toUpperCase()
							+ mName.substring(1);
					rs = q.queryWhere(con, "*", "member_1", "(mname LIKE '"
							+ capFirst + "%')");
				} else {
					if (!Pattern.matches("[a-zA-Z\\s]*", mName)) {
						JOptionPane.showMessageDialog(new JFrame(),
								"Name must be a sequence of letters or words.",
								"Name Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if (!mPhone.matches("\\d+") || mPhone.length() > 10) {
						JOptionPane
								.showMessageDialog(
										new JFrame(),
										"Phone number must be a sequence of numbers up to 10 characters.",
										"Phone Number Error",
										JOptionPane.ERROR_MESSAGE);
						return;
					}
					String capFirst = mName.substring(0, 1).toUpperCase()
							+ mName.substring(1);
					rs = q.queryWhere(con, "*", "member_1", "(mname LIKE '%"
							+ capFirst + "%' and phone LIKE '%" + mPhone
							+ "%')");

				}

				tablePopUp(rs, title, icon);
			}
		});
		editPanel.add(search);

		admin.setOpaque(false);
		membPanel.setOpaque(false);
		labPanel.setOpaque(false);
		textPanel.setOpaque(false);
		editPanel.setOpaque(false);

		main.add(admin);

		return main;
	}

	private JPanel createAwardPanel() {
		JButton award = new JButton("Award Oldest Member!");

		Image image = Toolkit.getDefaultToolkit().getImage("lib/trophy.png");
		JLabel imageLabel = new JLabel();
		imageLabel.setIcon(new ImageIcon(image));

		final JPanel awardPanel = new JPanel(new BorderLayout());
		awardPanel.add(imageLabel,BorderLayout.NORTH);
		awardPanel.setOpaque(false);
		awardPanel.add(award);
		award.addActionListener(new ActionListener() {
			Statement stmt;
			String query = "SELECT m.mname, m.phone,m.age "
					+ "FROM member_1 m "
					+ "WHERE m.age=(SELECT MAX(m2.age) FROM member_1 m2)";

			@Override
			public void actionPerformed(ActionEvent e) {
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
					DefaultTableModel defTable = new DefaultTableModel(data,
							columnNames);
					JTable table = new JTable(defTable);
					JOptionPane.showMessageDialog(mainFrame, new JScrollPane(
							table), "Oldest Member!", 0, icon);

				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(mainFrame, e1.getMessage());
				}
			}
		});
		//awardPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));

		return awardPanel;
	}

	private JPanel createExhibitSearch() {
		Query q = new Query();
		JPanel main = new JPanel(new GridLayout(2, 1));
		main.setOpaque(false);
		// JPanel main = new JPanel(new BorderLayout());
		JPanel eSearch = new JPanel(new GridLayout(2, 1));

		JPanel top = new JPanel(new GridLayout(1, 2));
		JLabel label = new JLabel("Show Objects in: ",SwingConstants.RIGHT);
		label.setOpaque(false);
		final Vector<Object> names = q.querySelectOne(con, "ename", "exhibit");
		final DefaultComboBoxModel model = new DefaultComboBoxModel(names);
		final JComboBox comboBox = new JComboBox(model);
		comboBox.setOpaque(false);
		comboBox.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));
		top.add(label);
		top.add(comboBox);
		top.setOpaque(false);
//		top.setBorder(BorderFactory.createEmptyBorder(0,100,0,200));

		JPanel buttons = new JPanel(false);
		buttons.setOpaque(false);
		JButton go = new JButton("Go");
		buttons.add(go);
		// go.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		go.addActionListener(new ActionListener() {
			Query q = new Query();

			@Override
			public void actionPerformed(ActionEvent e) {
				String exhibit = (String) comboBox.getSelectedItem();
				//System.out.println(exhibit);

				String statement = "SELECT  o1.objectID, o1.location, e.specialist "
						+ "FROM (object_has_1 o1 INNER JOIN object_has_3 o3 "
						+ "ON o1.location = o3.location "
						+ "INNER JOIN exhibit e "
						+ "on e.ename = o3.ename) "
						+ "WHERE e.ename = '" + exhibit + "'";

				ResultSet rs = q.stockQuery(con, statement);
				if (rs != null) {
					tablePopUp(rs, exhibit, null);
				}
			}
		});

		eSearch.add(top);
		eSearch.add(buttons);
		eSearch.setOpaque(false);

		if (isAdmin) {
			JPanel eDelete = new JPanel(new GridLayout(2, 1));
			JPanel top2 = new JPanel(new GridLayout(1, 2));
			JLabel label2 = new JLabel(
					"Delete Exhibit and Associated Objects: ",
					SwingConstants.RIGHT);
			label2.setOpaque(false);
			final DefaultComboBoxModel model2 = new DefaultComboBoxModel(names);
			final JComboBox comboBox2 = new JComboBox(model2);
			comboBox2.setOpaque(false);
			comboBox2.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));
			top2.add(label2);
			top2.add(comboBox2);
			top2.setOpaque(false);
			// top.setBorder(BorderFactory.createEmptyBorder(100,0,300,0));

			JPanel buttons2 = new JPanel(false);
			buttons2.setOpaque(false);
			JButton go2 = new JButton("Go");
			buttons2.add(go2);
			// go2.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

			go2.addActionListener(new ActionListener() {
				Query q = new Query();

				@Override
				public void actionPerformed(ActionEvent e) {
					String exhibit = (String) comboBox2.getSelectedItem();
					//System.out.println(exhibit);

					String statement = "DELETE FROM exhibit WHERE ename= '"
							+ exhibit + "'";

					int count = q.stockUpdate(con, statement);
					if (count < 0) {
						JOptionPane.showMessageDialog(new JFrame(),
								"Error Occured", "Delete Exhibit Error",
								JOptionPane.ERROR_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(new JFrame(), "Deleted "
								+ count + " rows!", "Delete Exhibit",
								JOptionPane.INFORMATION_MESSAGE);
						names.remove(exhibit);
						comboBox.setSelectedIndex(0);
						comboBox2.setSelectedIndex(0);
					}
				}
			});

			eDelete.add(top2);
			eDelete.add(buttons2);
			eDelete.setOpaque(false);

			main.add(eSearch);
			main.add(eDelete);
		} else {
			main = eSearch;
		}

		return main;
	}

	private JPanel createRSVPTab() {
		JPanel p2 = new JPanel(new BorderLayout());
		p2.setOpaque(false);

		JButton RSVP_by_everyone = new JButton(
				"Find the events RSVPed by everyone!");

		RSVP_by_everyone.addActionListener(new ActionListener() {

			Query q = new Query();
			ResultSet rs;

			@Override
			public void actionPerformed(ActionEvent e) {
				rs = q.queryWhere(
						con,
						"e.title, e.fee",
						"event e",
						"NOT EXISTS "
								+ "( SELECT * FROM member_1 m WHERE NOT EXISTS"
								+ "(SELECT * FROM RSVPs r "
								+ "WHERE e.title=r.title AND m.mname=r.mname AND m.phone=r.phone))");
				ImageIcon icon = new ImageIcon("lib/crash.png");
				tablePopUp(rs, "The events RSVPed by everyone!", icon);
			}
		});

		JButton RSVP_avg = new JButton(
				"Find the average age of members RSVPing each event!");

		RSVP_avg.addActionListener(new ActionListener() {

			Query q = new Query();
			ResultSet rs;

			@Override
			public void actionPerformed(ActionEvent e) {
				rs = q.queryWhere(con, "e.title, avg(m.age) as avg_age",
						"event e, RSVPs r, member_1 m",
						"e.title =r.title and m.mname=r.mname and m.phone = r.phone GROUP BY e.title");
				ImageIcon icon = new ImageIcon("lib/crash.png");
				tablePopUp(rs, "The most popular events!", icon);
			}
		});

		JButton RSVP_amount = new JButton(
				"Find the RSVP amount for each event!");

		RSVP_amount.addActionListener(new ActionListener() {

			Query q = new Query();
			ResultSet rs;

			@Override
			public void actionPerformed(ActionEvent e) {

				rs = q.queryWhere(con, "e.title, count(r.mname) as AMOUNT",
						"event e, RSVPs r", "e.title =r.title GROUP BY e.title");
				ImageIcon icon = new ImageIcon("lib/crash.png");
				tablePopUp(rs, "The events RSVP amout!", icon);
			}
		});

		final JPanel RSVPPanel = new JPanel(new GridLayout(3, 1));
		RSVPPanel.setOpaque(false);
		RSVPPanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 0, 100));

		RSVPPanel.add(RSVP_by_everyone);
		RSVPPanel.add(RSVP_avg);
		RSVPPanel.add(RSVP_amount);

		p2.add(RSVPPanel, BorderLayout.NORTH);

		return p2;
	}

	private JPanel RSVPTab() {
		Query q = new Query();
		String query = "SELECT e.title FROM event e WHERE e.title NOT IN"
				+ " (SELECT e1.title FROM event e1, RSVPs r WHERE r.mname ='"
				+ login_name + "' AND r.phone ='" + login_phone
				+ "' AND e1.title = r.title)";

		String query2 = "SELECT e1.title FROM event e1, RSVPs r WHERE r.mname ='"
				+ login_name
				+ "' AND r.phone ='"
				+ login_phone
				+ "' AND e1.title = r.title";

		final Vector<Object> names = q.querySelectOne(con, query);
		DefaultComboBoxModel model = new DefaultComboBoxModel(names);
		final JComboBox comboBox = new JComboBox(model);

		final Vector<Object> events = q.querySelectOne(con, query2);
		DefaultComboBoxModel model2 = new DefaultComboBoxModel(events);
		final JComboBox comboBox2 = new JComboBox(model2);

		JPanel p = new JPanel(new BorderLayout());
		JPanel p2 = new JPanel(new BorderLayout());
		JPanel boxPanel = new JPanel(new GridLayout(2, 1));
		JPanel buttonPanel = new JPanel(new GridLayout(2, 1));
		final JPanel editPanel = new JPanel(new BorderLayout());
		JPanel footer = new JPanel(new BorderLayout());

		p.add(boxPanel, BorderLayout.WEST);
		p.add(buttonPanel, BorderLayout.CENTER);

		boxPanel.setBorder(BorderFactory.createEmptyBorder(200, 10, 200, 10));
		buttonPanel
				.setBorder(BorderFactory.createEmptyBorder(200, 10, 200, 10));
		p.setBorder(BorderFactory.createEmptyBorder(0, 100, 0, 100));

		p2.add(p, BorderLayout.CENTER);

		p2.add(p, BorderLayout.CENTER);

		JButton RSVP = new JButton("RSVP to Event");
		JButton unattend = new JButton("Unattend this Event");

		unattend.addActionListener(new ActionListener() {
			Query q = new Query();

			@Override
			public void actionPerformed(ActionEvent e) {
				String event = (String) comboBox.getSelectedItem();
				int count = q.stockUpdate(con,
						"DELETE from RSVPs WHERE mname='" + login_name
								+ "' AND phone='" + login_phone
								+ "' AND title='" + comboBox2.getSelectedItem()
								+ "'");
				events.remove(event);
				names.add(event);
				mainFrame.validate();
				mainFrame.repaint();
				if (!events.isEmpty()) {
					comboBox.setSelectedIndex(0);
				} else {
					comboBox.setSelectedIndex(-1);
				}
				if (count != 1) {
					JOptionPane.showMessageDialog(mainFrame, "error unRSVPing!");
				}
			}
		});

		RSVP.addActionListener(new ActionListener() {
			Query q = new Query();

			@Override
			public void actionPerformed(ActionEvent e) {
				String event = (String) comboBox.getSelectedItem();
				int count = q.stockUpdate(con, "INSERT into RSVPs values ('" +event+
						"', '" + login_name + "', '" + login_phone + "')");
				names.remove(event);
				events.add(event);
				mainFrame.validate();
				mainFrame.repaint();
				if (!names.isEmpty()) {
					comboBox.setSelectedIndex(0);
				} else {
					comboBox.setSelectedIndex(-1);
				}
				if (count != 1) {
					JOptionPane.showMessageDialog(mainFrame, "error RSVPing!");
				}
			}
		});
		boxPanel.add(comboBox);
		boxPanel.add(comboBox2);

		buttonPanel.add(RSVP);
		buttonPanel.add(unattend);
p2.setOpaque(false);
p.setOpaque(false);
boxPanel.setOpaque(false);
buttonPanel.setOpaque(false);
		return p2;
	}

	private JPanel createArtistTab() {
		// JPanel p3 = createTab("Search for Artist");
		JPanel p3 = new JPanel(new BorderLayout());
		p3.setOpaque(false);

		JPanel aLabelPanel = new JPanel(new GridLayout(2, 1));
		JPanel aFieldPanel = new JPanel(new GridLayout(2, 1));
		JPanel aSearchPanel = new JPanel(new GridLayout(2, 1));

		final JLabel aNameLabel = new JLabel("Search by artist name: ",
				JLabel.RIGHT);
		JLabel aNatLabel = new JLabel("Search by artist nationality: ",
				JLabel.RIGHT);

		final JTextField artistName = new JTextField(20);
		final JTextField artistNat = new JTextField(20);
		Font font = new Font("Helvetica", Font.PLAIN, 14);
		artistName.setFont(font);
		artistNat.setFont(font);

		JButton aNameButton = new JButton("Search");
		JButton aNatButton = new JButton("Search");

		JPanel checkSearch = new JPanel(new GridLayout(1, 3));
		final JCheckBox date = new JCheckBox("Birthdate");
		date.setOpaque(false);
		final JCheckBox nat = new JCheckBox("Nationality");
		nat.setOpaque(false);
		JLabel include = new JLabel("Include in search: ");
		include.setOpaque(false);
		checkSearch.add(include);
		checkSearch.add(date);
		checkSearch.add(nat);

		aLabelPanel.add(aNameLabel);
		aFieldPanel.add(artistName);
		aLabelPanel.add(aNatLabel);
		aFieldPanel.add(artistNat);
		aSearchPanel.add(aNameButton);
		aSearchPanel.add(aNatButton);

		aNameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				artistName.requestFocus();
				ResultSet rs;
				Query q = new Query();
				String select = "aname as name";
				String name = artistName.getText();
				if (date.isSelected()) {
					select = select + ", dateOfBirth";
				}
				if (nat.isSelected()) {
					select = select + ", nationality";
				}
				if(name.isEmpty()){
					rs = q.query(con, select, "artist");
				} else{
					rs = q.queryWhere(con, select, "artist", "aname = '"
							+ artistName.getText() + "'");
					//System.out.println("aname = '" + artistName.getText() + "'");
				}
				tablePopUp(rs, "Artists", null);
				artistName.setText("");
			}
		});

		aNatButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ResultSet rs;
				Query q = new Query();
				String select = "aname as name";
				String nationality = artistNat.getText();
				if (date.isSelected()) {
					select = select + ", dateOfBirth";
				}
				if (nat.isSelected()) {
					select = select + ", nationality";
				}
				
				if (nationality.isEmpty()){
					rs = q.query(con, select, "artist");
				} else {
				rs = q.queryWhere(con, select, "artist",
						" nationality = '" + nationality + "'");
				//System.out.println("nationality = '" + nationality
				//		+ "'");
				}
				tablePopUp(rs, "Artists", null);
				artistNat.setText("");
				artistNat.requestFocus();
			}
		});

		artistName.requestFocus();
		aLabelPanel.setOpaque(false);
		checkSearch.setOpaque(false);
		p3.add(aLabelPanel, BorderLayout.WEST);
		p3.add(aFieldPanel, BorderLayout.CENTER);
		p3.add(aSearchPanel, BorderLayout.EAST);
		p3.add(checkSearch, BorderLayout.SOUTH);
		p3.setBorder(BorderFactory.createEmptyBorder(10, 10, 360, 50));

		return p3;
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
			DefaultTableModel defTable = new DefaultTableModel(data,
					columnNames);
			JTable table = new JTable(defTable);

			JOptionPane.showMessageDialog(mainFrame, new JScrollPane(table),
					title, 0, icon);
		} catch (SQLException e1) {
			JOptionPane.showMessageDialog(mainFrame, e1.getMessage());
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
			//System.out.println("createProfile Message: " + ex.getMessage());
			JOptionPane.showMessageDialog(mainFrame,
					ex.getMessage());
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
		final JButton save = new JButton("Save Changes");
		final JButton edit = new JButton("Edit");
		JButton delete = new JButton("Delete Account");

		final ActionListener saveListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String mname_new = fields[0].getText();
				// fields[0].setEditable(false);
				int age_new = Integer.parseInt(fields[1].getText());
				fields[1].setEditable(false);
				String addr_new = fields[2].getText();
				fields[2].setEditable(false);
				String email_new = fields[3].getText();
				fields[3].setEditable(false);
				String phone_new = fields[4].getText();
				// fields[4].setEditable(false);

				int err = m.updateMember(con, login_name, login_phone,
						mname_new, phone_new, addr_new, age_new, email_new);
				if (err == -1) {
					JOptionPane.showMessageDialog(mainFrame,
							"Oops! Couldn't update", "Error",
							JOptionPane.ERROR_MESSAGE);
					// System.out.println("Error updating member");
				} else if (err == -2) {
					Query q2 = new Query();
					// MEMBER WITH SAME NAME AND PHONE ALREADT EXIST
					//System.out
					//		.println("MEMBER WITH SAME NAME AND PHONE ALREADT EXIST");

					JOptionPane.showMessageDialog(mainFrame,
							"Member with same Name and Phone already exist.",
							"Constraint Error", JOptionPane.ERROR_MESSAGE);

					ResultSet rs = q2.queryWhere(con, "*", "member_1",
							"mname = '" + login_name + "' AND phone ='"
									+ login_phone + "'");

					String db_name = null;
					int db_age = -1;
					String db_addr = null;
					String db_email = null;
					String db_phone = null;

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
						//System.out.println("createProfile Message: "
						//		+ ex.getMessage());
					}

					fields[0].setText(db_name);
					fields[1].setText(Integer.toString(db_age));
					fields[2].setText(db_addr);
					fields[3].setText(db_email);
					fields[4].setText(db_phone);

				}
				editPanel.remove(save);
				editPanel.add(edit, BorderLayout.CENTER);
				edit.requestFocus();
			}

		};

		ActionListener editListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// fields[0].setEditable(true);
				fields[0].setToolTipText("Cannot Update Name");
				fields[1].setEditable(true);
				fields[2].setEditable(true);
				fields[3].setEditable(true);
				fields[4].setToolTipText("Cannot Update Phone Number");
				// fields[4].setEditable(true);

				editPanel.remove(edit);
				editPanel.add(save, BorderLayout.CENTER);
				save.requestFocus();
				fields[0].requestFocus();

			}

		};

		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Member m = new Member();
				int err = m.deleteMember(con, login_name, login_phone);
				if (err == -1) {
					//System.out.println("Error deleting member");
				} else {
					mainFrame.dispose();
					System.exit(0);
				}
			}
		});

		// edit.setForeground(Color.BLUE);
		// edit.setFont(new Font("Helvetica", Font.PLAIN, 14));
		edit.addActionListener(editListener);
		editPanel.add(edit, BorderLayout.CENTER);

		// save.setForeground(Color.BLUE);
		// save.setFont(new Font("Helvetica", Font.PLAIN, 14));
		save.addActionListener(saveListener);

		delete.setForeground(Color.RED);
		editPanel.add(delete, BorderLayout.EAST);

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
		profilePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		JTextArea text = new JTextArea();
		text.setText("select member and display info");
		profilePanel.add(text);

		mainFrame.add(profilePanel);
		mainFrame.validate();
		mainFrame.repaint();
	}

}
