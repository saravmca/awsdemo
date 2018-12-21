/**
 * 
 */
package com.awsdemo.servlets;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.awsdemo.dao.ImageDAO;
import com.awsdemo.utility.AwsS3Utilities;
import com.awsdemo.utility.MyPropWithinClasspath;

/**
* Servlet to handle File upload request from Client
* @author saravanan
*/
@SuppressWarnings("serial")
@MultipartConfig
public class FileUploadHandler extends HttpServlet {
	
   private ImageDAO imageDAO = new ImageDAO();
   private MyPropWithinClasspath properties = new MyPropWithinClasspath();
   private static final String SUFFIX = "/";
   private  String realPath=null;
   private String HOME_DIR=System.getProperty( "user.home" );
   
   public void init(){
	   realPath = getServletContext().getRealPath("");
	   System.out.println("Application Path = "+realPath + "OS Home Directory"+HOME_DIR);
	  }   

@SuppressWarnings("unchecked")
@Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
	   try {
		uploadFile(request,response);
	} catch (AmazonClientException | InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   }
   

   private void uploadFile(HttpServletRequest request, HttpServletResponse response)throws ServletException, 
   																					IOException, AmazonServiceException,
   																					AmazonClientException, 
   																					InterruptedException{
	 
	  
	 if(ServletFileUpload.isMultipartContent(request)){
		
           try {
        	  
        	   List<FileItem> multiparts = new ServletFileUpload(
                                           new DiskFileItemFactory()).parseRequest(request);
        	   for(FileItem item : multiparts){
            	  
            	   if(!item.isFormField()){
                	   File file=new File(item.getName());
                	   System.out.println("Input File from Multiparts:"+file);
                	   
                	   String name = new File(item.getName()).getName();
                	   System.out.println("Multiparts FileName:"+name);
                	   if(null!=name &&!"".equals(name) ){
                		   	//item.write( new File(UPLOAD_DIRECTORY + File.separator + name));
                		   //item.write( file); // uploads locally 
                		   System.out.println("Multiparts FileName:Before call AWS : " +name +" "+file);
                		   item.write(new File("/var/lib/tomcat8/webapps/ROOT/"+name));
                		   //item.write(new File(HOME_DIR+File.separator+name));
                		   System.out.println("Multipart Written the file into home path..");
                		   uploadToAWSBucket(name,file); //Uploads S3 Location
                		   imageDAO.updateDB(name);
                		   request.setAttribute("message", "File Uploaded Successfully");
                	   }
            	   }
               }	
          
             
              request.setAttribute("imageList", imageDAO.getFileList());
              
	           } catch (Exception ex) {
	              request.setAttribute("message", "File Upload Failed due to " + ex);
	           	}          
           }else{
        	   request.setAttribute("message",
                                "Sorry this Servlet only handles file upload request");
           }
   
	   
	 request.getRequestDispatcher("/result.jsp").forward(request, response);
      
   }
   
   private void uploadFile2(HttpServletRequest request, HttpServletResponse response)throws ServletException, 
		IOException, AmazonServiceException,
		AmazonClientException, 
		InterruptedException{
	 
	// gets absolute path of the web application
       String appPath = request.getServletContext().getRealPath("");
       // constructs path of the directory to save uploaded file
       String savePath = appPath + File.separator + "uploads";

       // creates the save directory if it does not exists
       File fileSaveDir = new File(savePath);
       if (!fileSaveDir.exists()) {
           fileSaveDir.mkdir();
       }

       for (Part part : request.getParts()) {
           String fileName = extractFileName(part);
           part.write(savePath + File.separator + fileName);
       }

       request.setAttribute("message", "Upload has been done successfully!");
       getServletContext().getRequestDispatcher("/result.jsp").forward(
               request, response);
	   
   }
  
   private String extractFileName(Part part) {
       String contentDisp = part.getHeader("content-disposition");
       String[] items = contentDisp.split(";");
       for (String s : items) {
           if (s.trim().startsWith("filename")) {
               return s.substring(s.indexOf("=") + 2, s.length() - 1);
           }
       }
       return "";
   }
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
	   doPost(request, response);
	   
   }
   
  @SuppressWarnings("deprecation")
private void uploadToAWSBucket(String inputFileName, File inputFile){
	  AWSCredentials credentials = new BasicAWSCredentials(properties.getPropertyValue("S3_Access_key_ID"), 
			  												properties.getPropertyValue("S3_Secret_access_key"));
	  System.out.println("FileUploadHandler.uploadToAWSBucket():: inputFile1.4 "+ inputFile);
	 File tempFile = new File("/var/lib/tomcat8/webapps/ROOT/"+inputFileName);
	  //File tempFile = new File(HOME_DIR+File.separator+inputFileName);
	 if(tempFile.exists()){
		 System.out.println("file exists in "+tempFile.getAbsolutePath());
	 }
	
	 System.out.println("File Path"+tempFile.getPath());
	  	  
	// create a client connection based on credentials
	 AmazonS3 s3client = new AmazonS3Client(credentials);
	
	 //s3client.setEndpoint("http://localhost:8080/awsdemo/upload");
	 //s3client.setEndpoint("http://www.saravbook.tk");
	 //s3client.setS3ClientOptions(S3ClientOptions.builder().setPathStyleAccess(true).disableChunkedEncoding().build());
	 
	 //String inputFilePath= tempFilePath+File.separator+inputFileName;
	 String s3FileName = properties.getPropertyValue("S3_BucketFolder") + SUFFIX + inputFileName;
	 System.out.println("s3FileName "+s3FileName);
	 AwsS3Utilities.uploadFile( properties.getPropertyValue("S3_BucketName"), 
			 					s3FileName, 
			 					tempFile, 
			 					s3client);
	 
	 //Delete temp file after uplaoded in S3
	 
	 if(tempFile.exists()){
		 tempFile.deleteOnExit();
		 System.out.println(tempFile+"is file deleted ");
	 }
	
  }
  
  
 
}