package com.awsdemo.utility;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class AwsS3Utilities{
	
	private static final String SUFFIX = "/";
	
	public static void main(String[] args) {
		// credentials object identifying user for authentication
		// user must have AWSConnector and AmazonS3FullAccess for 
		// this example to work
		MyPropWithinClasspath properties = new MyPropWithinClasspath();
		
		
		 AWSCredentials credentials = new BasicAWSCredentials(properties.getPropertyValue("S3_Access_key_ID"), 
					properties.getPropertyValue("S3_Secret_access_key"));
		// create a client connection based on credentials
		AmazonS3 s3client = new AmazonS3Client(credentials);
		
		// create bucket - name must be unique for all S3 users
		String bucketName = "saravawsdemobucket";
		//s3client.createBucket(bucketName);
		
		// list buckets
		for (Bucket bucket : s3client.listBuckets()) {
			System.out.println(" - " + bucket.getName());
		}
		
		// create folder into bucket
		String folderName = "uploads";
		//createFolder(bucketName, folderName, s3client);
		
		// upload file to folder and set it to public
		String fileName = folderName + SUFFIX + "test.txt";
		String inputFileName="C://uploads//test.txt";
		File file = new File(inputFileName);
		uploadFile(bucketName,fileName,file,s3client);
		//deleteFile(bucketName,"uploads",s3client);
		
		//deleteFolder(bucketName, folderName, s3client);
		
		// deletes bucket
		//s3client.deleteBucket(bucketName);
	}
	
	
	public static void listBuckets(AmazonS3 s3client){
		for (Bucket bucket : s3client.listBuckets()) {
			System.out.println(" - " + bucket.getName());
		}
	}
	public static void uploadFile(String bucketName, String s3folderName, File inputFile,AmazonS3 s3client){
	/*	s3client.putObject(new PutObjectRequest(bucketName, s3folderName, 
							new File(inputFile))
						.withCannedAcl(CannedAccessControlList.PublicRead));*/
		
		
		s3client.putObject(new PutObjectRequest(bucketName, s3folderName, inputFile)
				.withCannedAcl(CannedAccessControlList.PublicRead));
		
		System.out.println( s3folderName+" File Uploaded successfully in " +bucketName +" "+s3folderName);
	}
	public static void createFolder(String bucketName, String folderName, AmazonS3 client) {
		// create meta-data for your folder and set content-length to 0
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0);

		// create empty content
		InputStream emptyContent = new ByteArrayInputStream(new byte[0]);

		// create a PutObjectRequest passing the folder name suffixed by /
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
				folderName + SUFFIX, emptyContent, metadata);

		// send request to S3 to create folder
		client.putObject(putObjectRequest);
	}

	/**
	 * This method first deletes all the files in given folder and than the
	 * folder itself
	 */
	public static void deleteFolder(String bucketName, String folderName, AmazonS3 client) {
		List<S3ObjectSummary> fileList = client.listObjects(bucketName, folderName).getObjectSummaries();
	
		for (S3ObjectSummary file : fileList) {
			client.deleteObject(bucketName, file.getKey());
		}
		client.deleteObject(bucketName, folderName);
	}
	public static boolean deleteFile(String bucketName, String folderName, AmazonS3 client) {
		client.deleteObject(bucketName, folderName);
		return true;
		
	}
	
}
