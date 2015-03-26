
import java.sql.*;

public class Query {
	
	public ResultSet queryWhere(Connection con, String select, String from, String where) {
		Statement stmt;
		ResultSet rs = null;
		String statement = "SELECT " + select + " FROM " + from + " WHERE " + where;
		try {

			stmt = con.createStatement();
			rs = stmt.executeQuery(statement);
			
			stmt.close();
		} catch (SQLException ex) {
			System.out.println("queryWhere Message: " + ex.getMessage());
		}
		
		return rs;
	}
}