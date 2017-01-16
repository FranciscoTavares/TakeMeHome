package com.example.tmh;

import static com.example.tmh.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.example.tmh.CommonUtilities.EXTRA_MESSAGE;
import static com.example.tmh.CommonUtilities.SENDER_ID;
import android.R.layout;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.google.android.gcm.GCMRegistrar;
 

public class AllCabs
  extends ListActivity
  implements LocationListener
{
  private static final String TAG_CAB = "cab_number";
  Bookings b;
  private static final String TAG_DIST = "distance";
  private static final String TAG_NAME = "name";
  private static final String TAG_PID = "pid";
  private static final String TAG_PRODUCTS = "products";
  private static final String TAG_STATUS = "status";
  private static final String TAG_SUCCESS = "success";
  private static final String TAG_TEL = "tel";
  private static String url_all_products = "http://tmh.bugs3.com/android_connect/get_all_products.php";
  private static String url_sendRequest = "http://tmh.bugs3.com/android_connect/update_location.php";
  private Button Footer;
  AppLocationService appLocationService;
  SQLiteDatabase db = null;
  JSONParser jParser = new JSONParser();
  Double lat;
  Double lng;
  private LocationManager locationManager;
  private ProgressDialog pDialog;
  JSONArray products = null;
  ArrayList<HashMap<String, String>> productsList;
  private String provider;
  private Button tab1;
  private Button tab2;
  private Button tab3;
  private View tabStrip1;
  private View tabStrip2;
  private ImageView image;
  private View tabStrip3;

  
  String tel;
  GPSTracker gps;
  private EditText txt;
  UpdateLocation updates;
  private static final String TAG = "BroadcastTest";
	private Intent intent;
	private static final int NOTIFY_ME_ID=1337;
	
	// Asyntask
	AsyncTask<Void, Void, Void> mRegisterTask;
	
	// Alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();
	
	// Connection detector
	ConnectionDetector cd;
	
	public static String name;
	public static String email;
	
	  public void onCreate(Bundle paramBundle)
	  {
	    super.onCreate(paramBundle);
	    setContentView(R.layout.all_products);
	    this.productsList = new ArrayList();
	    setActionBar();
	    this.tabStrip1 = findViewById(2131099671);
	    this.tabStrip2 = findViewById(2131099673);
	    this.tabStrip3 = findViewById(2131099675);
	    this.tab1 = ((Button)findViewById(2131099670));
	    this.tab2 = ((Button)findViewById(2131099672));
	    this.tab3 = ((Button)findViewById(2131099674));
	    this.Footer = ((Button)findViewById(2131099661));
	    this.tabStrip2.setBackgroundColor(Color.parseColor("#FFFFFF"));
	    this.tab1.setTextColor(Color.parseColor("#FFC9C3"));
	    this.tab3.setTextColor(Color.parseColor("#FFC9C3"));
	    this.txt = ((EditText)findViewById(2131099665));
	    image = (ImageView)findViewById(R.id.item_img);
	   
		cd = new ConnectionDetector(getApplicationContext());

		
		 gps = new GPSTracker(AllCabs.this);

	        // check if GPS enabled      
	        if(gps.canGetLocation()){
	          
	            lat = gps.getLatitude();
	            lng  = gps.getLongitude();
	            
	           isOnline();
	         
	         }else{
	           
	            gps.showSettingsAlert();
	        }
	        
	      this.db = openOrCreateDatabase("mydb", 0, null);
	      Cursor localCursor = this.db.rawQuery("select * from login", null);
	      localCursor.moveToFirst();
	      this.tel = localCursor.getString(localCursor.getColumnIndex("mobile_no"));
	      this.Footer.setText("Logged in as " + this.tel);
	      getListView();
	      email = tel;
	      name="TMH";
	      // Make sure the device has the proper dependencies.
			GCMRegistrar.checkDevice(this);

			// Make sure the manifest was properly set - comment out this line
			// while developing the app, then uncomment it when it's ready.
			GCMRegistrar.checkManifest(this);

		 
			
			registerReceiver(mHandleMessageReceiver, new IntentFilter(
					DISPLAY_MESSAGE_ACTION));
			
			// Get GCM registration id
			final String regId = GCMRegistrar.getRegistrationId(this);

			// Check if regid already presents
			if (regId.equals("")) {
				// Registration is not present, register now with GCM			
				GCMRegistrar.register(this, SENDER_ID);
			} else {
				// Device is already registered on GCM
				if (GCMRegistrar.isRegisteredOnServer(this)) {
					// Skips registration.				
				//	Toast.makeText(getApplicationContext(), "Already registered with GCM"+regId, Toast.LENGTH_LONG).show();
				} else {
					// Try to register again, but not in the UI thread.
					// It's also necessary to cancel the thread onDestroy(),
					// hence the use of AsyncTask instead of a raw thread.
					final Context context = this;
					mRegisterTask = new AsyncTask<Void, Void, Void>() {

						@Override
						protected Void doInBackground(Void... params) {
							// Register on our server
							// On server creates a new user
							ServerUtilities.register(context, name, tel, regId);
							return null;
						}

						@Override
						protected void onPostExecute(Void result) {
							mRegisterTask = null;
						}

					};
					mRegisterTask.execute(null, null, null);
				}
				
			}
	     
	      
	     
	        
	        
	      this.tab1.setOnClickListener(new View.OnClickListener()
	      {
	        public void onClick(View paramAnonymousView)
	        {
	          Intent localIntent = new Intent(AllCabs.this.getApplicationContext(), Bookings.class);
	          AllCabs.this.startActivity(localIntent);
	        }
	      });
	      this.tab3.setOnClickListener(new View.OnClickListener()
	      {
	        public void onClick(View paramAnonymousView)
	        {
	          Intent localIntent = new Intent(AllCabs.this.getApplicationContext(), DriverBookings.class);
	          AllCabs.this.startActivity(localIntent);
	        }
	      });
	      
	      
	      
	  }
	  
	  private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
				// Waking up mobile if it is sleeping
				WakeLocker.acquire(getApplicationContext());
				Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();
				
				// Releasing wake lock
				WakeLocker.release();
			}
		};
		
		@Override
		protected void onDestroy() {
			if (mRegisterTask != null) {
				mRegisterTask.cancel(true);
			}
			try {
				unregisterReceiver(mHandleMessageReceiver);
				GCMRegistrar.onDestroy(this);
			} catch (Exception e) {
				Log.e("UnRegister Receiver Error", "> " + e.getMessage());
			}
			super.onDestroy();
		}

  private void setActionBar()
  {
    ActionBar localActionBar = getActionBar();
    localActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0099cc")));
    localActionBar.setDisplayUseLogoEnabled(false);
    localActionBar.setTitle(Html.fromHtml("<h6'>TakeMeHome</h6>"));
  }
  
  public boolean isOnline()
  {
    NetworkInfo localNetworkInfo = ((ConnectivityManager)getSystemService("connectivity")).getActiveNetworkInfo();
    if ((localNetworkInfo != null) && (localNetworkInfo.isConnected()))
    {
      //new AllCabs();
        new LoadAllProducts().execute(new String[0]);
    	 
      return true;
    }else{
    	alert.showAlertDialog(AllCabs.this,
				"Internet Connection Error",
				"Please connect to working Internet connection", false);
    return false;
    }
  }
  
  public void locate()
  {
    Location localLocation = gps.getLocation();
    if (localLocation != null)
    {
     // localLocation.getLatitude();
    //  Double.toString(localLocation.getLongitude());
      new LoadAllProducts().execute(new String[0]);
      return;
    }else{
    	 showSettingsAlert("GPS");
    }
   
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    if (paramInt2 == 100)
    {
      Intent localIntent = getIntent();
      finish();
      startActivity(localIntent);
    }
  }
  

  
 
  
  public void onLocationChanged(Location paramLocation)
  {
    new StringBuilder("New Latitude: ").append(paramLocation.getLatitude()).append("New Longitude: ").append(paramLocation.getLongitude()).toString();
    lat = Double.valueOf(paramLocation.getLatitude());
    lng = Double.valueOf(paramLocation.getLongitude());
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
  getMenuInflater().inflate(R.menu.main, menu);
  return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

  // Handle action bar actions click
  switch (item.getItemId()) {
  case R.id.action_settings:
  	return true;
  case R.id.refresh:
	  Intent i = new Intent(this, AllCabs.class);
	    startActivity(i);
 // new AllCabs();
  return true;
  default:
  	return super.onOptionsItemSelected(item);
  }
  }
  
  protected void onPause()
  {
    super.onPause();
 //   isOnline();
  }
  
   
	@Override
	public void onResume() {
		super.onResume();
	//	isOnline();
	//new AllCabs();
	 }
 
  public void onProviderDisabled(String paramString)
  {
    startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
   // Toast.makeText(getBaseContext(), "Gps is turned off!! ", 0).show();
  }
  
  public void onProviderEnabled(String paramString)
  {
    Toast.makeText(getBaseContext(), "Gps is turned on!! ", 0).show();
  }
  
 
  
  public void onStatusChanged(String paramString, int paramInt, Bundle paramBundle) {}
  
  public void showSettingsAlert(String paramString)
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
    localBuilder.setTitle(paramString + " SETTINGS");
    localBuilder.setMessage(paramString + " is not enabled! Want to go to settings menu?");
    localBuilder.setPositiveButton("Settings", new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        Intent localIntent = new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
        AllCabs.this.startActivity(localIntent);
      }
      
      public void onClick(View paramAnonymousView)
      {
        Intent localIntent = new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
        AllCabs.this.startActivity(localIntent);
      }
    });
    localBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        paramAnonymousDialogInterface.cancel();
      }
    });
    localBuilder.show();
  }
  
  class LoadAllProducts
    extends AsyncTask<String, String, String>
  {
    LoadAllProducts() {}
    
    protected String doInBackground(String... args) {
        // Building Parameters
    	String latitude =lat.toString();
        String longtude = lng.toString();
        ArrayList localArrayList2 = new ArrayList();
        BasicNameValuePair localBasicNameValuePair1 = new BasicNameValuePair("lat", latitude);
        localArrayList2.add(localBasicNameValuePair1);
        BasicNameValuePair localBasicNameValuePair2 = new BasicNameValuePair("lng", longtude);
        localArrayList2.add(localBasicNameValuePair2);
        localArrayList2.add(new BasicNameValuePair("tel", AllCabs.this.tel));
        if (!"".equals(latitude)) {
          AllCabs.this.jParser.makeHttpRequest(AllCabs.url_sendRequest, "POST", localArrayList2);
        }
        String miniLat = latitude.substring(0, 3);
        String minLong = longtude.substring(0, 2);
        
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userlat", latitude));
        params.add(new BasicNameValuePair("userlng", longtude));
        JSONObject json = jParser.makeHttpRequest(url_all_products, "GET", params);

        // Check your log cat for JSON reponse
        Log.d("All Products: ", json.toString());

        try {
            // Checking for SUCCESS TAG
            int success = json.getInt(TAG_SUCCESS);

            if (success == 1) {
                // products found
                // Getting Array of Products
                products = json.getJSONArray("products");

                // looping through All Products
                for (int i = 0; i < products.length(); i++) {
                    JSONObject c = products.getJSONObject(i);

                    // Storing each json item in variable
                   
                    String phone = c.getString(TAG_TEL);
                    if(tel.equals(phone)){
                    	
                    }else{
                    	
                    	 String name = c.getString(TAG_NAME);
                         String id = c.getString(TAG_PID);
                      
                         String status = c.getString(TAG_STATUS);
                         String dist = c.getString(TAG_DIST);
                         String cab = c.getString(TAG_CAB);	   // creating new HashMap
                         
                         
                         
                         
                         HashMap<String, String> map = new HashMap<String, String>();

                         // adding each child node to HashMap key => value
                         map.put(TAG_NAME, name);
                         map.put(TAG_DIST, dist);
                         map.put(TAG_CAB, cab);
                         map.put(TAG_STATUS, status);
                         
                         // adding HashList to ArrayList
                         productsList.add(map);
                         
                    }
                  
                 
                }
            }  
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
    
  
    protected void onPostExecute(String paramString)
    {
    	
    	  // dismiss the dialog after getting all products
        pDialog.dismiss();
        // updating UI from Background Thread
        runOnUiThread(new Runnable() {
            public void run() {
                /**
                 * Updating parsed JSON data into ListView
                 * */
                ListAdapter adapter = new SimpleAdapter(
                        AllCabs.this, productsList,
                        R.layout.mypuchase_item, new String[] { TAG_NAME,
                                TAG_CAB,TAG_DIST},
                        new int[] { R.id.txt1, R.id.tv2,R.id.txt5,});
                // updating listview
              // image.setImageDrawable(getResources().getDrawable(R.drawable.two));
                setListAdapter(adapter);
            }
            
        });
        ListView list = getListView();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
        public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          try
          {
            JSONObject localJSONObject = AllCabs.this.products.getJSONObject(paramAnonymousInt);
            ((JSONObject)localJSONObject).getString("pid");
            String str1 = ((JSONObject)localJSONObject).getString("name");
            String str2 = ((JSONObject)localJSONObject).getString("cab_number");
            String str3 = ((JSONObject)localJSONObject).getString("tel");
            String gcm = ((JSONObject)localJSONObject).getString("gcm_reg_id");
            Intent localIntent = new Intent(AllCabs.this.getApplicationContext(), MakeBooking.class);
            localIntent.putExtra("driverName", str1);
            localIntent.putExtra("NumberPlate", str2);
            localIntent.putExtra("pid", str3);
            localIntent.putExtra("gcm_id", gcm);
            localIntent.putExtra("tel", str3);
            AllCabs.this.startActivity(localIntent);
            return;
          }
          catch (JSONException localJSONException)
          {
            localJSONException.printStackTrace();
          }
        }
      });
      
    
    }
    
    protected void onPreExecute()
    {
      super.onPreExecute();
      AllCabs.this.pDialog = new ProgressDialog(AllCabs.this);
      AllCabs.this.pDialog.setMessage("Loading content...");
      AllCabs.this.pDialog.setIndeterminate(false);
      AllCabs.this.pDialog.setCancelable(false);
      AllCabs.this.pDialog.show();
    }
  }
  
}

