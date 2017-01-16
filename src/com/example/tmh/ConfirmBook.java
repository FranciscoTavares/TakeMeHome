package com.example.tmh;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ConfirmBook extends Activity {

	// Progress Dialog
	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
    TextView DriverName;
    TextView inputFrom;
    TextView inputTo;
    TextView Fare;
    Button btn,cancel;
	
    String userfrom;
	// url to create new product
	//private static String url_create_product = "http://10.0.2.2/android_connect/create_product.php";
	private static String url_sendRequest = "http://tmh.bugs3.com/android_connect/confirm_book.php";
	  
	  private static String url_Booking_cancel = "http://tmh.bugs3.com/android_connect/cancel.php";
	  
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	private   String GCM_ID = "";
	private   String TAG_ID, driverTel = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewbooking);

		// Edit Text findViewById
		DriverName = (TextView)findViewById(R.id.cname);
		Fare = (TextView)findViewById(R.id.fare);
		inputFrom = (TextView)findViewById(R.id.departfrom);
		inputTo = (TextView)findViewById(R.id.destination);
		btn = (Button)findViewById(R.id.tv4);
		cancel = (Button)findViewById(R.id.cancel);
		setActionBar();
		
// Create button
		Button btnCreateProduct = (Button) findViewById(R.id.send);
		 Intent obj = getIntent();
	       
	       String name = obj.getStringExtra("name");
	      
	       userfrom = obj.getStringExtra("userfrom");
	       String userto = obj.getStringExtra("userto");
	       String myfare = obj.getStringExtra("fare");
	       String status = obj.getStringExtra("status");
	       GCM_ID = obj.getStringExtra("gcm_id");
	        driverTel = obj.getStringExtra("driverTel");
	       
	        TAG_ID = obj.getStringExtra("bid");
	        
	        
	        DriverName.setText(name);
	        inputFrom.setText(userfrom);
	        inputTo.setText(userto);
	        Fare.setText(myfare);
	        
	        
	        if("confirmed".equals(status)){
	        	btnCreateProduct.setVisibility(View.GONE);
	        }
	        btn.setOnClickListener(new android.view.View.OnClickListener() {
	        	 
	            public void onClick(View view)
	            {
	            	String url = "http://maps.google.com/maps?q=";
	           
	               Intent viw = new Intent("android.intent.action.VIEW", Uri.parse(url+userfrom));
	                startActivity(viw);
	            }
	  
	        });
	         
		// button click event
		btnCreateProduct.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// creating new product in background thread
				new CreateUser().execute();
				
			}
		});
		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// creating new product in background thread
				new RemoveBooking().execute();
				
			}
		});
	}
	private void setActionBar() {
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0099cc")));
		
		
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
	
		actionBar.setDisplayUseLogoEnabled(false);
		
	}
	/**
	 * Background Async Task to Create new product
	 * */
	class CreateUser extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ConfirmBook.this);
			pDialog.setMessage("Please Wait..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Creating product
		 * */
		protected String doInBackground(String... args) {
			  String fare = Fare.getText().toString();
			  String bid = TAG_ID.toString();
			 
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("bid", bid));
			params.add(new BasicNameValuePair("tel", driverTel));
			params.add(new BasicNameValuePair("gcm_id", GCM_ID));
			
			
			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_sendRequest,"POST", params);
			
			// check log cat fro response
			Log.d("Create Response", json.toString());

			// check for success tag
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// successfully created product
					Intent i = new Intent(getApplicationContext(), Bookings.class);
					startActivity(i);
					
					// closing this screen
					finish();
				} else {
				//	Toast.makeText(getApplicationContext(), , 1);
					//Intent i = new Intent(getApplicationContext(), MakeBooking.class);
					//startActivity(i);
					// failed to create product
					//Toast.makeText(getApplicationContext(), "", 1);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
			pDialog.dismiss();
		}

	}
	

	  class RemoveBooking extends AsyncTask<String, String, String>
	{
		  RemoveBooking() {}
	  
	  protected String doInBackground(String... paramVarArgs)
	  {
	    String fare = Fare.getText().toString();
	   
	    ArrayList localArrayList = new ArrayList();
	    localArrayList.add(new BasicNameValuePair("bid", TAG_ID));
	 
	    JSONObject localJSONObject = ConfirmBook.this.jsonParser.makeHttpRequest(url_Booking_cancel, "POST", localArrayList);
	    Log.d("Create Response", localJSONObject.toString());
	    try
	    {
	      if (localJSONObject.getInt("success") == 1)
	      {
	        Intent localIntent = new Intent(ConfirmBook.this.getApplicationContext(), AllCabs.class);
	         startActivity(localIntent);
	       finish();
	      }
	      return null;
	    }
	    catch (JSONException localJSONException)
	    {
	      for (;;)
	      {
	        localJSONException.printStackTrace();
	      }
	    }
	  }
	  
	  protected void onPostExecute(String paramString)
	  {
	    pDialog.dismiss();
	  }
	  
	  protected void onPreExecute()
	  {
	    super.onPreExecute();
	   pDialog = new ProgressDialog(ConfirmBook.this);
	    pDialog.setMessage("Cancelling booking..");
	   pDialog.setIndeterminate(false);

	   pDialog.show();
	  }
	}
}
