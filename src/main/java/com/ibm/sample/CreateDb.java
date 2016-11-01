package com.ibm.sample;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.org.lightcouch.PreconditionFailedException;
import com.google.gson.JsonObject;

@WebServlet("/cloudant/createdb")
public class CreateDb extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    	CloudantClient client = CloudantConnectionService.getConnection();
    	JsonObject output = new JsonObject();
    	
		try {
			String dbname = "student";
			client.createDB(dbname);
			output.addProperty("result", "Success created database " + dbname);
		} catch(PreconditionFailedException ex) {
			output.addProperty("err", ex.getReason());
		} 
		
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.println(output);
    }

}
