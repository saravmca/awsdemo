package com.awsdemo.servlets;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.awsdemo.dao.ImageDAO;
import com.awsdemo.utility.AwsS3Utilities;
import com.awsdemo.utility.MyPropWithinClasspath;

@SuppressWarnings("serial")
public class FileDeleteHandler extends HttpServlet{
	
	 private ImageDAO imageDAO = new ImageDAO();
	 private MyPropWithinClasspath properties = new MyPropWithinClasspath();
	   private static final String SUFFIX = "/";
	
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
	  String imageId=request.getParameter("imageId");
	  String fileName=request.getParameter("fileName");
	 
	  try {
		  //Code for local Drive
		 /* if(deleteFileFromDrive(fileName)){
			  imageDAO.deleteFile(fileName);
			  request.setAttribute("imageList", imageDAO.getFileList());
		  	}*/
		  if(deleteFileFromAWS(fileName)){
			  imageDAO.deleteFile(fileName);
			  request.setAttribute("imageList", imageDAO.getFileList());
		  }
	  } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	  }
	  request.getRequestDispatcher("/result.jsp").forward(request, response);
  }
 
 private boolean deleteFileFromDrive(String fileName){
	 boolean isDeleted=false;
	 MyPropWithinClasspath properties = new MyPropWithinClasspath();
	
	 try{
		 isDeleted=Files.deleteIfExists(Paths.get( properties.getPropertyValue("UPLOAD_DIRECTORY")+"//"+fileName));
        }
        catch(NoSuchFileException e)
        {
            System.out.println("No such file/directory exists");
        }
        catch(DirectoryNotEmptyException e)
        {
            System.out.println("Directory is not empty.");
        }
        catch(IOException e)
        {
            System.out.println("Invalid permissions.");
        }
         
	  
	 return isDeleted;
 }
 
 private boolean deleteFileFromAWS(String fileName){
	 AWSCredentials credentials = new BasicAWSCredentials(properties.getPropertyValue("S3_Access_key_ID"), 
														properties.getPropertyValue("S3_Secret_access_key"));
	 // create a client connection based on credentials
	 AmazonS3 s3client = new AmazonS3Client(credentials);
	 AwsS3Utilities.deleteFile(properties.getPropertyValue("S3_BucketName"), 
			 					properties.getPropertyValue("S3_BucketFolder")+SUFFIX+fileName, 
			 					s3client);
	 System.out.println("FileDeleteHandler.deleteFileFromAWS():: fileName " +fileName+"deleted in AWS Bucket");
	 return true;
	 
	 

 }
}
