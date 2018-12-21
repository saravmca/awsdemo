package com.awsdemo.servlets;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.awsdemo.dao.ImageDAO;

public class PageLoader extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	private ImageDAO imageDAO = new ImageDAO();
	  protected void doGet(HttpServletRequest request, HttpServletResponse response)
	           throws ServletException, IOException {
		  String eventName=request.getParameter("event");
		  switch(eventName){
		  case "GETFILELIST" :
			  getFileList(request,response);
			  	break;
		   default:
			   System.out.println("PageLoader.doGet() Event Not Matched");
		  }
		  
	  }
	  private void getFileList(HttpServletRequest request, HttpServletResponse response)  {
		  
		  try {
			  request.setAttribute("imageList", imageDAO.getFileList());
			  request.getRequestDispatcher("/result.jsp").forward(request, response);
			
		} catch (ServletException | IOException | SQLException e) {
				e.printStackTrace();
		}
	  }
}
