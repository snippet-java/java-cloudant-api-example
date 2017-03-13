package com.ibm.sample.student.cloudant;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;
import com.google.gson.JsonObject;

@WebServlet("/cloudant/create")
public class Create extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	CloudantClient client = new CloudantConnectionService().getConnection();
    	
    	//Create a dummy json document
    	JsonObject studentJson = new JsonObject();
    	studentJson.addProperty("firstname", "Joe");
    	studentJson.addProperty("lastname", "Doe");
    	studentJson.addProperty("studentId", generateStudentId());

    	String dbname = "student";
    	Database db = client.database(dbname, false);
    	Response dbResponse = db.save(studentJson);
    		
		JsonObject output = new JsonObject();
		//for success insertion
		if(dbResponse.getStatusCode() < 400) {
			output.add("doc", studentJson);	
	    
			//dbResponse json data
	    	JsonObject dbResponseJson = new JsonObject();
	    	dbResponseJson.addProperty("status", dbResponse.getStatusCode() + " - " + dbResponse.getReason());
	    	dbResponseJson.addProperty("id", dbResponse.getId());
	    	dbResponseJson.addProperty("rev", dbResponse.getRev());
	    
			output.add("data", dbResponseJson);
		}
		else {
			output.addProperty("err", dbResponse.getStatusCode() + " - " + dbResponse.getReason());
		}
		
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.println(output);
    }
    
    private String generateStudentId() {
    	return "ID#" + new Double(Math.floor(Math.random()*10000)).intValue();
    }

    private static final long serialVersionUID = 1L;
}