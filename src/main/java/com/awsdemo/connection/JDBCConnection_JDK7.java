package com.awsdemo.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.awsdemo.utility.MyPropWithinClasspath;

public class JDBCConnection_JDK7 {
	Connection con =null;
	@SuppressWarnings("unused")
	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		Connection testConn= null;
		try{
			JDBCConnection_JDK7  connObj= new JDBCConnection_JDK7();
			testConn=connObj.getConnection();
			String strQuery="SELECT * from image";
			
			// create the java statement
		      Statement st = testConn.createStatement();
		      
		      // execute the query, and get a java resultset
		      ResultSet rs = st.executeQuery(strQuery);
		      
		      // iterate through the java resultset
		      while (rs.next())
		      {
		        int id = rs.getInt("image_id");
		       
		        System.out.println("ID:" +id);
		       
		      }
		      st.close();
		    
			
			System.out.println("JDBCConnection_JDK7.main() Connection Created...."+testConn);
		}catch(SQLException sqe){
			sqe.printStackTrace();
			if(null!=testConn){
				testConn.close();
				System.out.println("JDBCConnection_JDK7.main() Connection closed...."+testConn);
			}
		}finally{
			if(null!=testConn){
				//testConn.close();
				System.out.println("finally Connection Close...."+testConn);
			}
		}

	}
	
	public Connection getConnection() throws SQLException{
		DriverManager.registerDriver(new com.mysql.jdbc.Driver ());
		// con = DriverManager.getConnection( "jdbc:mysql://localhost:3306/awsdemo-local","awsdemo","awsdemo123");
		MyPropWithinClasspath properties = new MyPropWithinClasspath();
		 con = DriverManager.getConnection( properties.getPropertyValue("JDBC_DB_URL"),
				 							properties.getPropertyValue("JDBC_DB_USERNAME"),
				 							properties.getPropertyValue("JDBC.DB_PASSWORD"));
		 return con;
	}
	public void closeConnection() throws SQLException{
		if(null!=con){
			con.close();
		}
	}

}
