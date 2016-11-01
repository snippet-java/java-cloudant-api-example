package com.ibm.sample;

import java.net.MalformedURLException;
import java.net.URL;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CloudantConnectionService {
	
	protected static CloudantClient getConnection() {
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
	
	protected static JsonObject getCredentials() {
		JsonParser parser = new JsonParser();
	    JsonObject allServices = parser.parse(System.getenv("VCAP_SERVICES")).getAsJsonObject();
		JsonObject credentials   = ((JsonObject)allServices.getAsJsonArray("cloudantNoSQLDB").get(0)).getAsJsonObject("credentials");
		
		return credentials;
	
	}
}
