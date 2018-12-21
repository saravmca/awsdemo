<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>File Upload Example in JSP and Servlet - Java web application</title>
    </head>
 
    <body> 
    	<div>
            <h3> Choose File to Upload in Server </h3>
            <form action="upload" method="post" enctype="multipart/form-data">
            	<input type="file" name="file" />
                <input type="submit" value="upload" />
            </form>          
        </div>
        
        <div id="result">
            <h3>${requestScope["message"]}</h3>
            <table border="1" style="border-collapse: collapse">
            	<th>S.No</th>
            	<th>FileName</th>
            	
            	<th>Modifide Date/Time</th>
            	<th>Remove</th>
            	<c:forEach var="image" items="${imageList}">
            	<tr>
            		<td>${image.imageId}</td>
            		<td>${image.imageName}</td>
            		<td>${image.updatedDate}</td>
            		<td><a href="delete?actionName=DELETE&imageId=${image.imageId}&fileName=${image.imageName}">Remove</a></td>
            	</tr>
            	</c:forEach>  
            	
            </table>
        </div>
      
    </body>
</html>