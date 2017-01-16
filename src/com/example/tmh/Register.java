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
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Register extends Activity {

	// Progress Dialog
	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
	EditText inputfName;
	EditText inputlName;
	EditText inputEmail;
	EditText inputPassword;
	EditText inputCabnumber;
	EditText inputUsertype;
	EditText inputTel;
	Spinner dropdown;
	SQLiteDatabase db=null;

	// url to create new product
	//private static String url_create_product = "http://10.0.2.2/android_connect/create_product.php";
	private static String url_create_product = "http://tmh.bugs3.com/android_connect/create_product.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		// Edit Text
		inputfName = (EditText) findViewById(R.id.fname);
		inputlName = (EditText) findViewById(R.id.lname);
		inputEmail = (EditText) findViewById(R.id.etemail);
		inputPassword = (EditText) findViewById(R.id.etpassword);
	
		inputTel = (EditText) findViewById(R.id.tel);
	    dropdown = (Spinner)findViewById(R.id.spiner);
	   // String yy="Select One";
	    
	    db=openOrCreateDatabase("mydb", MODE_PRIVATE, null);
		db.execSQL("create table if not exists login(name varchar,mobile_no varchar,email_id varchar,password varchar, user_type varchar)");
		
		
	   
		String[] items = new String[]{"user","driver","select one"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
			dropdown.setAdapter(adapter);
		    dropdown.setSelection(adapter.getPosition("2"));
		 	dropdown.setOnItemSelectedListener(new OnItemSelectedListener(){
		 		@Override
		 		public void  onItemSelected (AdapterView<?> parent, View v, int position, long id){
		 			
		 			switch(position){
		 			case 0:
		 				//inputCabnumber.setVisibility(View.GONE);
		 				break;
		 			
		 			case 1:
		 			//	inputCabnumber.setVisibility(View.VISIBLE);
		 				break;
		 			}
		 			
		 		}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
		 		
		 	});
			
			
		setActionBar();
// Create button
		Button btnCreateProduct = (Button) findViewById(R.id.btnCreateProducts);

		// button click event
		btnCreateProduct.setOnClickListener(new View.OnClickListener() {

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

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Register.this);
			pDialog.setMessage("Creating User..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Creating product
		 * */
		protected String doInBackground(String... args) {
			String fname = inputfName.getText().toString();
			String lname = inputlName.getText().toString();
			String email = inputEmail.getText().toString();
			String pwd = inputPassword.getText().toString();
			String tel = inputTel.getText().toString();
			//String cab = inputCabnumber.getText().toString();
			//String usertype = inputUsertype.getText().toString();

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("fname", fname));
			params.add(new BasicNameValuePair("lname", lname));
			params.add(new BasicNameValuePair("email", email));
			params.add(new BasicNameValuePair("pwd", pwd));
			params.add(new BasicNameValuePair("tel", tel));
			//params.add(new BasicNameValuePair("cab", cab));
			params.add(new BasicNameValuePair("usertype", "user"));
		

			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_create_product,
					"POST", params);
			
			// check log cat fro response
			Log.d("Create Response", json.toString());

			// check for success tag
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// successfully created product
					Intent i = new Intent(getApplicationContext(), AllCabs.class);
					startActivity(i);
				
                   db.execSQL("insert into login values('"+fname+"','"+tel+"','"+email+"','"+pwd+"','user')");
					
					i.putExtra("tel", tel);
					startActivity(i);
					db.close();
					finish();
					


				} else {
					Toast.makeText(getBaseContext(), "User not created ",
							Toast.LENGTH_SHORT).show();
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
