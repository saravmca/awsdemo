/**
 * 
 */
package com.awsdemo.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.awsdemo.connection.JDBCConnection_JDK7;



/**
 * @author sagadevs
 *
 */
public class ImageDAO {
	public int updateDB(String fileName)throws SQLException{
		   int executed = 0;
		   try{
			   JDBCConnection_JDK7  jdbcConn= new JDBCConnection_JDK7();
			   Connection con = jdbcConn.getConnection();
			 
			   String sqlQuery="INSERT INTO image (image_name,updated_date) VALUES(?,?)";
			   PreparedStatement preparedStatement=con.prepareStatement(sqlQuery);
			   preparedStatement.setString(1,fileName);
			   preparedStatement.setString(2, getCurrentTimeStamp());
			   executed= preparedStatement.executeUpdate();
			   preparedStatement.close();
			   System.out.println("image "+fileName+ " inserted in DB successfully");
			   con.close();
			   
		   }catch(SQLException exe){
			   exe.printStackTrace();
		   }finally{
			   
		   }
		   
		   return executed;
	   }
	public int deleteFile(String filename)throws SQLException{
		   
		   int deleted=0;
		   JDBCConnection_JDK7  jdbcConn= new JDBCConnection_JDK7();
		   Connection con = jdbcConn.getConnection();
		   
		   String sqlQuery="DELETE from image where image_name=?";
		   PreparedStatement preparedStatement=con.prepareStatement(sqlQuery);
		   preparedStatement.setString(1,filename);
		   deleted=preparedStatement.executeUpdate();
		   preparedStatement.close();
		   con.close();
		   System.out.println("ImageDAO.deleteFile():: "+filename+"-->deleted "+deleted);
		   return deleted;
	 }
	public static String getCurrentTimeStamp() {
	    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
	    Date now = new Date(System.currentTimeMillis());
	    String strDate = sdfDate.format(now);
	    return strDate;
	}
	
	public List<Image> getFileList() throws SQLException{
		   List<Image> list = new ArrayList<Image>();
		   String strQuery="SELECT * from image ORDER BY Updated_date desc";
		   JDBCConnection_JDK7  jdbcConn= new JDBCConnection_JDK7();
		   Connection con = jdbcConn.getConnection();
			// create the java statement
		      Statement st = con.createStatement();
		      
		      // execute the query, and get a java resultset
		      ResultSet rs = st.executeQuery(strQuery);
		      
		      // iterate through the java resultset
		      while (rs.next())
		      {
		    	  Image image= new Image();
		       
		        image.setImageId(rs.getInt("image_id"));
		        image.setImageName(rs.getString("image_name"));
		        image.setUpdatedDate(rs.getString("updated_date"));
		        list.add(image);
		       
		      }
		      st.close();
		      
		   return list;
	   }
}
