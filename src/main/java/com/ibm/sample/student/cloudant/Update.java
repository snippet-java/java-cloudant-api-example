package com.ibm.sample.student.cloudant;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.org.lightcouch.DocumentConflictException;
import com.cloudant.client.org.lightcouch.NoDocumentException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@WebServlet("/cloudant/update")
public class Update extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
    	
    	CloudantClient client = new CloudantConnectionService().getConnection();	
		JsonObject output = new JsonObject();

		String docId = request.getParameter("id");
		String firstName = request.getParameter("firstname");
		String lastName = request.getParameter("lastname");

		if(docId == null || docId.isEmpty()) {
			output.addProperty("err", "Please specify valid Doc ID");
		}
		else {
			try {
		    	String dbName = "student";
		    	Database db = client.database(dbName, false);
		
		    	db.find(docId);
		    	InputStream is = db.find(docId);
				int i;
				char c;
				String doc = "";
				while((i=is.read())!=-1)
		         {
		            c=(char)i;
		            doc += c;
		         }
				JsonParser parser = new JsonParser();
				JsonObject docJson = parser.parse(doc).getAsJsonObject();
				
				if(!(firstName == null || firstName.isEmpty()))
					docJson.addProperty("firstname", firstName);
				if(!(lastName == null || lastName.isEmpty()))
					docJson.addProperty("lastname", lastName);
				db.update(docJson);
				
				output.addProperty("result", "Success update document");
		    	
	    	} catch(NoDocumentException ex) {
	    		output.addProperty("err", ex.getReason());
	    	} catch(DocumentConflictException ex) {
	    		output.addProperty("err", ex.getReason());
	    	}
		}
		out.println(output);
    }

}