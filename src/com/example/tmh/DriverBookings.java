package com.example.tmh;

import android.app.ActionBar;
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
public class DriverBookings extends ListActivity
{
  private static final String TAG_CAB = "cab_number";
  private static final String TAG_NAME = "name";
  private static final String TAG_NOTIFY = "";
  private static final String TAG_PID = "pid";
  private static final String TAG_PRODUCTS = "products";
  private static final String TAG_STATUS = "status";
  private static final String TAG_SUCCESS = "success";
  private static final int NOTIFY_ME_ID=1337;
  private static final String TAG_TEL = "tel";
  private static String getbookings = "http://tmh.bugs3.com/android_connect/driverbookings.php";
  AppLocationService appLocationService;
  SQLiteDatabase db = null;
  JSONParser jParser = new JSONParser();
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
  AlertDialogManager alert = new AlertDialogManager();
  ConnectionDetector cd;
  
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
    	  new LoadAllProducts().execute(new String[0]);
    	    
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
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(R.layout.all_products);
    this.productsList = new ArrayList();
    setActionBar();
    this.tabStrip1 = findViewById(2131099671);
    this.tabStrip2 = findViewById(2131099673);
    this.tabStrip3 = findViewById(2131099675);
    Button localButton = (Button)findViewById(2131099661);
    this.tab1 = ((Button)findViewById(2131099670));
    this.tab2 = ((Button)findViewById(2131099672));
    this.tab3 = ((Button)findViewById(2131099674));
    this.tabStrip3.setBackgroundColor(Color.parseColor("#FFFFFF"));
    this.tab1.setTextColor(Color.parseColor("#FFC9C3"));
    this.tab2.setTextColor(Color.parseColor("#FFC9C3"));
    this.db = openOrCreateDatabase("mydb", 0, null);
    Cursor localCursor = this.db.rawQuery("select * from login", null);
    localCursor.moveToFirst();
    this.tel = localCursor.getString(localCursor.getColumnIndex("mobile_no"));
    localButton.setText("Logged in as " + this.tel);
    cd = new ConnectionDetector(getApplicationContext());

  isOnline();
    getListView();
    this.tab2.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        Intent localIntent = new Intent(DriverBookings.this.getApplicationContext(), AllCabs.class);
        DriverBookings.this.startActivity(localIntent);
      }
    });
    this.tab1.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        Intent localIntent = new Intent(DriverBookings.this.getApplicationContext(), Bookings.class);
        DriverBookings.this.startActivity(localIntent);
      }
    });
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
   // isOnline();
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
        DriverBookings.this.startActivity(localIntent);
      }
      
      public void onClick(View paramAnonymousView)
      {
        Intent localIntent = new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
        DriverBookings.this.startActivity(localIntent);
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
    
    protected String doInBackground(String... paramVarArgs)
    {
      ArrayList localArrayList = new ArrayList();
      localArrayList.add(new BasicNameValuePair("tel",tel ));
      JSONObject localJSONObject1 = DriverBookings.this.jParser.makeHttpRequest(DriverBookings.getbookings, "GET", localArrayList);
      Log.d("All Products: ", localJSONObject1.toString());
      try
      {
        if (localJSONObject1.getInt("success") == 1)
        {
          DriverBookings.this.products = localJSONObject1.getJSONArray("products");
          int i = 0;
          for (  i = 0; i < products.length(); i++) {
            JSONObject localJSONObject2 = DriverBookings.this.products.getJSONObject(i);
            String str1 = localJSONObject2.getString("pid");
            String str2 = localJSONObject2.getString("name");
            String str3 = localJSONObject2.getString("cab_number");
            String str4 = localJSONObject2.getString("status");
            String str5 = localJSONObject2.getString("tel");
            String str6 = "";
            if ("pending".equals(str4)) {
              str6 = "NEW";
              //notification();
            }
            
              HashMap localHashMap = new HashMap();
              localHashMap.put("pid", str1);
              localHashMap.put("name", str2);
              localHashMap.put("cab_number", str3);
              localHashMap.put("status", str4);
              localHashMap.put("", str6);
              localHashMap.put("tel", str5);
              DriverBookings.this.productsList.add(localHashMap);
              
              if ("waiting".equals(str4)) {
                str6 = "";
              } else if ("confirmed".equals(str4)) {
                str6 = "NEW";
             //   notification();
            }
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
      DriverBookings.this.pDialog.dismiss();
      DriverBookings.this.runOnUiThread(new Runnable()
      {
        public void run()
        {
          SimpleAdapter localSimpleAdapter = new SimpleAdapter(DriverBookings.this, DriverBookings.this.productsList, 2130903049, new String[] { "name", "tel", "status", "" }, new int[] { 2131099665, 2131099658, 2131099661, 2131099666 });
          DriverBookings.this.setListAdapter(localSimpleAdapter);
        }
      });
      ListView list = getListView();
      list.setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
        public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          try
          {
            JSONObject localJSONObject = DriverBookings.this.products.getJSONObject(paramAnonymousInt);
            String str1 = ((JSONObject)localJSONObject).getString("name");
            String str2 = ((JSONObject)localJSONObject).getString("tel");
            String str3 = ((JSONObject)localJSONObject).getString("userfrom");
            String str4 = ((JSONObject)localJSONObject).getString("userto");
            String str5 = ((JSONObject)localJSONObject).getString("status");
            String str6 = ((JSONObject)localJSONObject).getString("fare");
            String bid = ((JSONObject)localJSONObject).getString("bid");
            String gcm = ((JSONObject)localJSONObject).getString("gcm_reg_id");
            Intent localIntent = new Intent(DriverBookings.this.getApplicationContext(), SendQuote.class);
            localIntent.putExtra("status", str5);
            localIntent.putExtra("bid", bid);
            localIntent.putExtra("fare", str6);
            localIntent.putExtra("name", str1);
            localIntent.putExtra("tel", str2);
            localIntent.putExtra("userfrom", str3);
            localIntent.putExtra("userto", str4);
            localIntent.putExtra("gcm_id", gcm);
            startActivity(localIntent);
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
      DriverBookings.this.pDialog = new ProgressDialog(DriverBookings.this);
      DriverBookings.this.pDialog.setMessage("Loading bookings...");
      DriverBookings.this.pDialog.setIndeterminate(false);
      DriverBookings.this.pDialog.setCancelable(false);
      DriverBookings.this.pDialog.show();
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
									"New Message from client", ip);
			
			//After uncomment this line you will see number of notification arrived
			//note.number=2;
			mgr.notify(NOTIFY_ME_ID, note);
		//	 Vibrator v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
			 // Vibrate for 500 milliseconds
			 Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			 v.vibrate(500);
  }
}
