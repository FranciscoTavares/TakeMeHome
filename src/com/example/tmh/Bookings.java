package com.example.tmh;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.os.Vibrator;

public class Bookings
  extends ListActivity
{
  private static final String TAG_CAB = "cab_number";
  private static final String TAG_FARE = "fare";
  private static final String TAG_NAME = "name";
  private static final String TAG_NOTIFY = "";
  private static final String TAG_PID = "pid";
  private static final String TAG_PRODUCTS = "products";
  private static final String TAG_STATUS = "status";
  private static final String TAG_SUCCESS = "success";
	private static final int NOTIFY_ME_ID=1337;
  private static final String TAG_TEL = "tel";
  private static String getbookings = "http://tmh.bugs3.com/android_connect/getbookings.php";
  AppLocationService appLocationService;
  SQLiteDatabase db = null;
  JSONParser jParser = new JSONParser();
  String notify = "";
  private ProgressDialog pDialog;
  JSONArray products = null;
  ArrayList<HashMap<String, String>> productsList;
  private Button tab1;
  private Button tab2;
  private Button tab3;
  private View tabStrip1;
  private View tabStrip2;
  private View tabStrip3;
  String tel;
private Activity context;
ConnectionDetector cd;
AlertDialogManager alert = new AlertDialogManager();

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
    Button localButton = (Button)findViewById(2131099661);
    this.tab3 = ((Button)findViewById(2131099674));
    this.tabStrip1.setBackgroundColor(Color.parseColor("#FFFFFF"));
    this.tab2.setTextColor(Color.parseColor("#FFC9C3"));
    this.tab3.setTextColor(Color.parseColor("#FFC9C3"));
    this.db = openOrCreateDatabase("mydb", 0, null);
    Cursor localCursor = this.db.rawQuery("select * from login", null);
    localCursor.moveToFirst();
    tel = localCursor.getString(localCursor.getColumnIndex("mobile_no"));
    localButton.setText("Logged in as " + this.tel);
	cd = new ConnectionDetector(getApplicationContext());

	 isOnline();
   
    getListView();
    this.tab2.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        Intent localIntent = new Intent(Bookings.this.getApplicationContext(), AllCabs.class);
        Bookings.this.startActivity(localIntent);
      }
    });
    this.tab3.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        Intent localIntent = new Intent(Bookings.this.getApplicationContext(), DriverBookings.class);
        Bookings.this.startActivity(localIntent);
      }
    });
  }
  private void setActionBar()
  {
    ActionBar localActionBar = getActionBar();
    localActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0099cc")));
    localActionBar.setDisplayUseLogoEnabled(false);
    localActionBar.setTitle(Html.fromHtml("<h4'>TakeMeHome</h4>"));
  }
  
  public boolean isOnline()
  {
    NetworkInfo localNetworkInfo = ((ConnectivityManager)getSystemService("connectivity")).getActiveNetworkInfo();
    if ((localNetworkInfo != null) && (localNetworkInfo.isConnected()))
    {
    
      new LoadAllProduct().execute(new String[0]);
      return true;
    }else{
    alert.showAlertDialog(this,
			"Internet Connection Error",
			"Please connect to working Internet connection", false);
return false;
    }
  }
  
  public void locate()
  {
    Location localLocation = this.appLocationService.getLocation("gps");
    if (localLocation != null)
    {
      localLocation.getLatitude();
      Double.toString(localLocation.getLongitude());
      return;
    }
    showSettingsAlert("GPS");
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

  
  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    getMenuInflater().inflate(2131296256, paramMenu);
    return true;
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (paramMenuItem.getItemId() == 2131099684) {
      return true;
    }
    return super.onOptionsItemSelected(paramMenuItem);
  }
  
  protected void onResume()
  {
    super.onResume();
    //isOnline();
    //new LoadAllProduct().execute(new String[0]);
  }
  
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
        Bookings.this.startActivity(localIntent);
      }
      
      public void onClick(View paramAnonymousView)
      {
        Intent localIntent = new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
        Bookings.this.startActivity(localIntent);
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
  
  class LoadAllProduct
    extends AsyncTask<String, String, String>
  {
    LoadAllProduct() {}
    
    protected String doInBackground(String... paramVarArgs)
    {
      ArrayList paramString = new ArrayList();
      paramString.add(new BasicNameValuePair("tel",tel));
      JSONObject localJSONObject1 = Bookings.this.jParser.makeHttpRequest(Bookings.getbookings, "GET", paramString);
      Log.d("All Products: ", localJSONObject1.toString());
      try
      {
        if (localJSONObject1.getInt("success") == 1)
        {
          Bookings.this.products = localJSONObject1.getJSONArray("products");
          int i = 0;
          for ( i = 0; i < products.length(); i++) {
        	  
            JSONObject c = products.getJSONObject(i);
            JSONObject localJSONObject2 = Bookings.this.products.getJSONObject(i);
            String str1 = localJSONObject2.getString("pid");
            String str2 = localJSONObject2.getString("name");
            String str3 = localJSONObject2.getString("cab_number");
            String str4 = localJSONObject2.getString("status");
            localJSONObject2.getString("fare");
            String str5 = "";
         
           
              HashMap localHashMap = new HashMap();
              localHashMap.put("pid", str1);
              localHashMap.put("name", str2);
              localHashMap.put("cab_number", str3);
              localHashMap.put("status", str4);
              localHashMap.put("", str5);
              Bookings.this.productsList.add(localHashMap);
              
            }
          }
    
         
      }
      catch (JSONException localJSONException)
      {
        localJSONException.printStackTrace();
      }
      return null;
    }
    
    
    
    protected void onPostExecute(String paramString)
    {
      Bookings.this.pDialog.dismiss();
      Bookings.this.runOnUiThread(new Runnable()
      {
        public void run()
        {
          SimpleAdapter localSimpleAdapter = new SimpleAdapter(Bookings.this, Bookings.this.productsList, 2130903049, new String[] { "name", "cab_number", "status", "" }, new int[] { 2131099665, 2131099658, 2131099661, 2131099666 });
          Bookings.this.setListAdapter(localSimpleAdapter);
        }
      });
      ListView list = getListView();
      list.setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
        public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          try
          {
            JSONObject localJSONObject = Bookings.this.products.getJSONObject(paramAnonymousInt);
            String str1 = ((JSONObject)localJSONObject).getString("name");
            String str2 = ((JSONObject)localJSONObject).getString("bid");
            String str3 = ((JSONObject)localJSONObject).getString("userfrom");
            String str4 = ((JSONObject)localJSONObject).getString("userto");
            String str5 = ((JSONObject)localJSONObject).getString("fare");
            String str6 = ((JSONObject)localJSONObject).getString("status");
            String tel = ((JSONObject)localJSONObject).getString("tel");
            String gcm = ((JSONObject)localJSONObject).getString("gcm_reg_id");
            Intent localIntent = new Intent(Bookings.this.getApplicationContext(), ConfirmBook.class);
            localIntent.putExtra("name", str1);
            localIntent.putExtra("bid", str2);
            localIntent.putExtra("userfrom", str3);
            localIntent.putExtra("userto", str4);
            localIntent.putExtra("fare", str5);
            localIntent.putExtra("status", str6);
            localIntent.putExtra("gcm_id", gcm);
            localIntent.putExtra("driverTel", tel);
            Bookings.this.startActivity(localIntent);
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
      Bookings.this.pDialog = new ProgressDialog(Bookings.this);
      Bookings.this.pDialog.setMessage("Loading bookings...");
      Bookings.this.pDialog.setIndeterminate(false);
      Bookings.this.pDialog.setCancelable(false);
      Bookings.this.pDialog.show();
    }
  }
  public void notification(){
	  final NotificationManager mgr=
				(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
			
			Notification note=new Notification(R.drawable.ic_launcher,
															"Take me home Message!",
															System.currentTimeMillis());
			
			// This pending intent will open after notification click
			PendingIntent ip = PendingIntent.getActivity(getBaseContext(), 0, 
					                         new Intent(getBaseContext(), Bookings.class),
													0);
			
			note.setLatestEventInfo(getBaseContext(), "Take me home",
									"New Take me home message", ip);
			
			//After uncomment this line you will see number of notification arrived
			//note.number=2;
			mgr.notify(NOTIFY_ME_ID, note);
		//	 Vibrator v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
			 // Vibrate for 500 milliseconds
			 Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			 v.vibrate(500);
  }
}


