package com.ibm.sample.student.cloudant;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CloudantConnectionService {
	
	protected CloudantClient getConnection() {
		JsonObject credentials   = getCredentials();
	
		String username = credentials.get("username").getAsString();
		String password = credentials.get("password").getAsString();
		
		try {
			CloudantClient client = ClientBuilder.url(new URL("https://" + username + ".cloudant.com"))
			        .username(username)
			        .password(password)
			        .build();
			return client;
		} catch(MalformedURLException ex) {
			System.err.println(ex.getMessage());
		}
		
		return null;
	}
	
	protected JsonObject getCredentials() {
	    //for local deployment
	    if(System.getenv("VCAP_SERVICES") == null || System.getenv("VCAP_SERVICES").isEmpty()) {
	    	return readPropertiesFile();
	    }

	    //for bluemix deployment
	    else {
			JsonParser parser = new JsonParser();
		    JsonObject allServices = parser.parse(System.getenv("VCAP_SERVICES")).getAsJsonObject();
			return ((JsonObject)allServices.getAsJsonArray("cloudantNoSQLDB").get(0)).getAsJsonObject("credentials");
	    }
	
	}
	
	/**
	 * For local deployment. To retrieve cloudant username and password from credential.properties
	 * @return credential JsonObject
	 */
	private JsonObject readPropertiesFile() {
		Properties prop = new Properties();
		InputStream input = null;
		JsonObject credentialsJson = new JsonObject();
		try {
			//read from current directory
			input = new FileInputStream("credential.properties");
			// load a properties file
			prop.load(input);

			// get the username and pasword from properties file
			credentialsJson.addProperty("username", prop.getProperty("username"));
			credentialsJson.addProperty("password", prop.getProperty("password"));

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return credentialsJson;
	}
}