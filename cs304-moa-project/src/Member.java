
import java.sql.*;

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
	
	public int deleteMember(Connection con, String mname, String phone) {
		PreparedStatement ps;
		int count = -1;

		try {
			// cant be just member? either member_1, member_2, member_3?
			ps = con.prepareStatement("DELETE FROM member_1 WHERE mname=? AND phone=?");
			ps.setString(1, mname);
			ps.setString(2, phone);

			count = ps.executeUpdate();
			con.commit();
			ps.close();
		} catch (SQLException ex) {
			System.out.println("deleteMember: " + ex.getMessage());
			try {
				con.rollback();
			} catch (SQLException ex2) {
				System.out.println("deleteMember2: " + ex2.getMessage());
				System.exit(-1);
			}
		}
		System.out.println("Deleted " + count + " row(s).");
		// wait for RETURN before displaying menu again
		
		return count;
	}
	
}