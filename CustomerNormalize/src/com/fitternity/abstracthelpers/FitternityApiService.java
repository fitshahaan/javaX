package com.fitternity.abstracthelpers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;

import com.fitternity.enums.GoogleApis;
import com.google.gson.JsonObject;
public abstract class FitternityApiService
{

	protected String sendPost(final String url,JsonObject body) throws Exception {

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);

		System.out.println(" URL :: "+url);
		System.out.println(" body :: "+body.toString());
		
		post.addHeader("content-type", "application/json");
		
		// add header
//		post.setHeader("User-Agent", USER_AGENT);

		/*List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("sn", "C02G8416DRJM"));
		urlParameters.add(new BasicNameValuePair("cn", ""));
		urlParameters.add(new BasicNameValuePair("locale", ""));
		urlParameters.add(new BasicNameValuePair("caller", ""));
		urlParameters.add(new BasicNameValuePair("num", "12345"));*/

		post.setEntity(new StringEntity(body.toString()));
		
		HttpResponse response = client.execute(post);
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + post.getEntity());
		System.out.println("Response Code : " +response.getStatusLine().getStatusCode());

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) 
		{
			result.append(line);
		}

		System.out.println(result.toString());
		return result.toString();

	}
	
	protected String sendGet(String url1) throws Exception {

		String nullFragment = null;
		URL url = new URL(url1);
	    URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), nullFragment);
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(uri.toString());

		// add request header
		//	request.addHeader("User-Agent", "");

	
		HttpResponse response = client.execute(request);
		System.out.println("resp :: "+response);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
//		System.out.println(result.toString());
		return result.toString();

	}
	


protected String sendGet(String url1,String type1) throws Exception {

	String nullFragment = null;
	URL url = new URL(url1);
	System.out.println(" url  " +url.getProtocol());
	System.out.println(" url  " +url.toString());
	System.out.println(" url  " +url.toURI());
    
//	URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), nullFragment);
	URI uri = url.toURI();
    HttpClient client = new DefaultHttpClient();
	
	HttpGet request = new HttpGet(uri.toString());

	// add request header
	//	request.addHeader("User-Agent", "");

	System.out.println("url1 :: "+url1);
	System.out.println("request:: "+request);
	
	System.out.println("  uri.toString()  :: "+uri.toString());
	HttpResponse response = client.execute(request);
	System.out.println("resp :: "+response);
	BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	StringBuffer result = new StringBuffer();
	String line = "";
	while ((line = rd.readLine()) != null) {
		result.append(line);
	}
//	System.out.println(result.toString());
	return result.toString();

}


	
	protected abstract JSONObject processData(String data,GoogleApis api);

}

















