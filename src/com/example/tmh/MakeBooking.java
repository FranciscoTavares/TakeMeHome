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
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MakeBooking extends Activity  {
	 
	private LocationManager locationManager;
 	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
    TextView DriverName;
	EditText inputFrom;
	EditText inputTo;
	EditText inputPassword;
	TextView inputCabnumber;
	EditText inputUsertype;
	EditText inputTel;
	Double lat;
	Double lng;
	 String tel;
	  SQLiteDatabase db = null;
 private String provider;
 private View tabStrip1;
 private View tabStrip2;
 GPSTracker gps;
 AppLocationService appLocationService;

	//private static String url_create_product = "http://10.0.2.2/android_connect/create_product.php";
	private static String url_sendRequest = "http://tmh.bugs3.com/android_connect/send_booking.php";

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	
	private   String TAG_driverID = "";
	private   String TAG_TEL = "";
	private   String GCM_ID = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.booking);

		// Edit Text findViewById
		DriverName = (TextView)findViewById(R.id.fname);
		inputCabnumber = (TextView)findViewById(R.id.nplate);
		inputFrom = (EditText)findViewById(R.id.gofrom);
		inputTo = (EditText)findViewById(R.id.go_to);
		setActionBar();
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		 
	    this.db = openOrCreateDatabase("mydb", 0, null);
	      Cursor localCursor = this.db.rawQuery("select * from login", null);
	      localCursor.moveToFirst();
	      tel = localCursor.getString(localCursor.getColumnIndex("mobile_no"));
	    // default
	    Criteria criteria = new Criteria();
	    gps = new GPSTracker(MakeBooking.this);

        // check if GPS enabled      
        if(gps.canGetLocation()){
          
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
          
         
  		  inputFrom.setText(latitude+","+longitude);
         //   Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();  
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

	   
// Create button
		Button btn = (Button) findViewById(R.id.btnCreateProducts);
		Intent obj = getIntent();
	       
	        String drivername = obj.getStringExtra("driverName");
	        String Cab = obj.getStringExtra("NumberPlate");
	        // TAG_driverID = obj.getStringExtra("driverTel");
	        TAG_driverID  = obj.getStringExtra("tel");
	        GCM_ID = obj.getStringExtra("gcm_id");
	        DriverName.setText(drivername);
	        inputCabnumber.setText(Cab);
	      
		// button click event
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// creating new product in background thread
				new CreateUser().execute();
				
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


		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MakeBooking.this);
			pDialog.setMessage("Please Wait..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}


		protected String doInBackground(String... args) {
			String userFrom = inputFrom.getText().toString();
			String userTo = inputTo.getText().toString();
		
			

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("departure", userFrom));
			params.add(new BasicNameValuePair("destination", userTo));
			params.add(new BasicNameValuePair("userID", tel));
			params.add(new BasicNameValuePair("driverID", TAG_driverID));
			params.add(new BasicNameValuePair("message", "please hurry up"));
			params.add(new BasicNameValuePair("gcm_id", GCM_ID));
			
			

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
					 
					finish();
				} else {
			 
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
 
}