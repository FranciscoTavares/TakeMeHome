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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class Login extends Activity{
	
	Button btnViewProducts;
	Button btnNewProduct;
	JSONParser jsonParser = new JSONParser();
	  private Button signup;
	  GPSTracker gps;
	  private Button account;
	  SQLiteDatabase db=null;
	  private static final String TAG = "BroadcastTest";
		private Intent intent;
		EditText inputEmail;
		EditText inputPassword;
		AlertDialogManager alert = new AlertDialogManager();
		
		// Connection detector
		ConnectionDetector cd;
		Intent i=null;
		private ProgressDialog pDialog;
		private static String url_login = "http://tmh.bugs3.com/android_connect/login.php";

		// JSON Node names
		private static final String TAG_SUCCESS = "success";
		private static final String TAG_MESSAGE = "message";
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		
		cd = new ConnectionDetector(getApplicationContext());

		// Check if Internet present
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			//return;
		}
		 gps = new GPSTracker(this);

	        // check if GPS enabled      
	        if(gps.canGetLocation()){


	         }else{
	           
	            gps.showSettingsAlert();
	        }
		
		db=openOrCreateDatabase("mydb", MODE_PRIVATE, null);
		db.execSQL("create table if not exists login(name varchar,mobile_no varchar,email_id varchar,password varchar, user_type varchar)");
			
		Cursor c=db.rawQuery("select * from login", null);	
		c.moveToFirst();
		if(c.getCount()>0)
		{
		String tel = c.getString(c.getColumnIndex("mobile_no"));
		i=new Intent(this,AllCabs.class);
		
		
		i.putExtra("tel", tel);
		startActivity(i);
		db.close();
		finish();
		// intent = new Intent(this, BroadcastServices.class); 
		
		}
		
		inputEmail = (EditText) findViewById(R.id.etemail);
		inputPassword = (EditText) findViewById(R.id.etpassword);
		
	
		// Buttons
		//btnViewProducts = (Button) findViewById(R.id.btnViewProducts);
		//btnNewProduct = (Button) findViewById(R.id.btnCreateProduct);
		signup = (Button) findViewById(R.id.btnCreateProducts);
		account = (Button) findViewById(R.id.signup);
		
		setActionBar();
		// view products click event
		signup.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// Launching All products Activity
				//Intent i = new Intent(getApplicationContext(), AllCabs.class);
				//startActivity(i);
				new CreateUser().execute();
				
			}
		});
		account.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// Launching All products Activity
				Intent ii = new Intent(getApplicationContext(), Register.class);
				startActivity(ii);
				
			}
		});
		// view products click event
		
	}
	private void setActionBar() {
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0099cc")));
		
		
		actionBar.setDisplayShowHomeEnabled(false);
		 
		actionBar.setDisplayUseLogoEnabled(false);
		
	}
	class CreateUser extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Login.this);
			pDialog.setMessage("Logging in..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Creating product
		 * */
		protected String doInBackground(String... args) {
		
			String tel = inputEmail.getText().toString();
			String pwd = inputPassword.getText().toString();
		

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
		
			params.add(new BasicNameValuePair("pwd", pwd));
			params.add(new BasicNameValuePair("tel", tel));
			
			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_login,
					"POST", params);
			
			// check log cat fro response
			Log.d("Create Response", json.toString());

			// check for success tag
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// successfully created product
					
					
						
				
                   db.execSQL("insert into login values('','"+tel+"','','"+pwd+"','')");
					 
					Intent i = new Intent(getApplicationContext(), AllCabs.class);
					startActivity(i);
					i.putExtra("tel", tel);
					startActivity(i);
					db.close();
					finish();
					
				} else {
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
	 private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
	      @Override
	      public void onReceive(Context context, Intent intent) {
	      //	updateUI(intent);       
	      }
	  };    
	  
		@Override
		public void onResume() {
			super.onResume();
			 
			
		}
	  
	
}
