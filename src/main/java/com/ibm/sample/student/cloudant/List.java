package com.ibm.sample.student.cloudant;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.org.lightcouch.NoDocumentException;
import com.cloudant.http.Http;
import com.cloudant.http.HttpConnection;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@WebServlet("/cloudant/list")
public class List extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	CloudantConnectionService cloudantService = new CloudantConnectionService();
    	CloudantClient client = cloudantService.getConnection();	
		JsonObject output = new JsonObject();
	
    	try {
	    	String dbName = "student";
	    	Database db = client.database(dbName, false);
	
	    	JsonObject credentials = cloudantService.getCredentials();
	    	HttpConnection httpResponse = client.executeRequest(
	    			Http.GET(new URL("https://" + credentials.get("username").getAsString() + ".cloudant.com/" + db.info().getDbName() + "/_all_docs")));
	
	    	JsonParser parser = new JsonParser(); 
			output = parser.parse(httpResponse.responseAsString()).getAsJsonObject();
	    	
    	} catch(NoDocumentException ex) {
    		output.addProperty("err", "No Database/Document found");
    	}
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.println(output);
    }

}
