// ora_k8w8
// a20713137


import java.sql.*;
import java.util.Vector;

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
			//System.out.println("query Message: " + ex.getMessage());
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
			//System.out.println("queryWhere Message: " + ex.getMessage());
		}
		
		return rs;
	}
	
	public Vector<Object> querySelectOne(Connection con, String select, String from) {
		Statement stmt;
		ResultSet rs = null;
		String statement = "SELECT " + select + " FROM " + from;
		Vector<Object> names = new Vector<Object>();
		//System.out.println(statement);
		try {

			stmt = con.createStatement();
			rs = stmt.executeQuery(statement);
			
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			if (columnCount != 1) {
				return null;
			}
			while(rs.next()) {
				names.add(rs.getObject(1));
			}
			
			stmt.close();
		} catch (SQLException ex) {
			//System.out.println("queryWhere Message: " + ex.getMessage());
		}
		
		return names;
	}
	
	public Vector<Object> querySelectOne(Connection con, String query) {
		Statement stmt;
		ResultSet rs = null;
		Vector<Object> names = new Vector<Object>();
		//System.out.println(query);
		try {

			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			if (columnCount != 1) {
				return null;
			}
			while(rs.next()) {
				names.add(rs.getObject(1));
			}
			
			stmt.close();
		} catch (SQLException ex) {
			//System.out.println("queryWhere Message: " + ex.getMessage());
		}
		
		return names;
	}
	
	public ResultSet stockQuery(Connection con, String query) {
		Statement stmt;
		ResultSet rs = null;
		//System.out.println(query);
		try {

			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			
			//stmt.close();
		} catch (SQLException ex) {
			//System.out.println("stockQuery Message: " + ex.getMessage());
		}
		
		return rs;
	}
	
	public int stockUpdate(Connection con, String query) {
		Statement stmt;
		int count = -1;
		//System.out.println(query);
		try {

			stmt = con.createStatement();
			count = stmt.executeUpdate(query);
			con.commit();
			stmt.close();
		} catch (SQLException ex) {
			//System.out.println("stockUpdate Message: " + ex.getMessage());
		}
		
		return count;
	}
	
}
