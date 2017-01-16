package com.example.tmh;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

 
 

public class BackgroundBookings  {
	
	BackgroundBookings() {    }
private static String getbookings = "http://tmh.bugs3.com/android_connect/getbookings.php";
 
JSONParser jParser = new JSONParser();
String notify = "";

JSONArray products = null;
ArrayList<HashMap<String, String>> productsList;




public String Status(String tel){
	String st="";
      ArrayList paramString = new ArrayList();
      paramString.add(new BasicNameValuePair("tel",tel));
      JSONObject localJSONObject1 =  jParser.makeHttpRequest(getbookings, "GET", paramString);
    
      try
      {
        if (localJSONObject1.getInt("success") == 1)
        {
         products = localJSONObject1.getJSONArray("products");
          int i = 0;
          for ( i = 0; i < products.length(); i++) {
        	  
            JSONObject c = products.getJSONObject(i);
            JSONObject localJSONObject2 = products.getJSONObject(i);
            String str1 = localJSONObject2.getString("pid");
            String str2 = localJSONObject2.getString("name");
            String str3 = localJSONObject2.getString("cab_number");
            String str4 = localJSONObject2.getString("status");
            localJSONObject2.getString("fare");
            
            if ("pending".equals(str4)) {
            	st = "";
            }
            if ("waiting".equals(str4)) {
            	st = "NEW";
            	
            
              }
            if ("confirmed".equals(str4)) {
            	st = "NEW";
               
              }
           
              
            }
          }
    
         
      }
      catch (Exception localJSONException)
      {
        localJSONException.printStackTrace();
      }
      return st;
    }
    
    
}