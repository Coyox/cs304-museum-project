// ora_k8w8
// a20713137


import java.sql.*;

public class Query {
	
	public ResultSet query(Connection con, String attr, String from){
		Statement stmt;
		ResultSet rs = null;
		String statement = "SELECT " + attr + " FROM " + from;
		try {

			stmt = con.createStatement();
			rs = stmt.executeQuery(statement);
			
			//stmt.close();
		} catch (SQLException ex) {
			System.out.println("query Message: " + ex.getMessage());
		}
		
		return rs;
	}
	
	public ResultSet queryWhere(Connection con, String select, String from, String where) {
		Statement stmt;
		ResultSet rs = null;
		String statement = "SELECT " + select + " FROM " + from + " WHERE " + where;
		System.out.println(statement);
		try {

			stmt = con.createStatement();
			rs = stmt.executeQuery(statement);
			
			//stmt.close();
		} catch (SQLException ex) {
			System.out.println("queryWhere Message: " + ex.getMessage());
		}
		
		return rs;
	}
}
