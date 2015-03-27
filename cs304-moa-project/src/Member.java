
import java.sql.*;
// for reading from the command line
import java.io.*;

public class Member {
	
	public int updateMember(Connection con, String mname, String phone, 
			String mname_new, String phone_new, String addr, int age, String email) {
		
		PreparedStatement ps;
		int count = -1;

		String statement = "UPDATE member_1 SET mname ='" + mname_new + 
							"', phone = '" + phone_new +
							"', addr = '" + addr +
							"', age = " + age +
							", email = '" + email +
							"' WHERE mname = '" + mname +
							"' AND phone = '" + phone + "'";
		
		try {
			ps = con.prepareStatement(statement);
			
			count = ps.executeUpdate();
			con.commit();
			ps.close();
		} catch (SQLException ex) {
			System.out.println("updateMember: " + ex.getMessage());
			try {
				con.rollback();
				if (ex.getMessage().contains("ORA-00001")) {
					return -2;
				}
			} catch (SQLException ex2) {
				System.out.println("updateMember2: " + ex2.getMessage());
				System.exit(-1);
			}
		}

		return count;
	}
}