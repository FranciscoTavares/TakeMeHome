package com.example.tmh;

 
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
 

public class SendQuote
  extends Activity
{
  private static final String TAG_MESSAGE = "message";
  private static final String TAG_SUCCESS = "success";
  private static String url_sendRequest = "http://tmh.bugs3.com/android_connect/send_quote.php";
  
  private static String url_Booking_cancel = "http://tmh.bugs3.com/android_connect/cancel.php";
  
  TextView DriverName;
  EditText Fare;
  private String TAG_ID = "";
  EditText inputFrom;
  TextView inputTo;
  Button btn,cancel;
  JSONParser jsonParser = new JSONParser();
  private ProgressDialog pDialog;
	private   String GCM_ID = "";
  private void setActionBar()
  {
    ActionBar localActionBar = getActionBar();
    localActionBar.setNavigationMode(0);
    localActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0099cc")));
    localActionBar.setDisplayShowHomeEnabled(false);
    localActionBar.setDisplayHomeAsUpEnabled(true);
  
    localActionBar.setDisplayUseLogoEnabled(false);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(R.layout.confirm_booking);
    this.DriverName = ((TextView)findViewById(R.id.cname));
    this.Fare = ((EditText)findViewById(R.id.fare));
    this.inputFrom = ((EditText)findViewById(R.id.departfrom));
    btn = (Button)findViewById(R.id.tv4);
    cancel = (Button)findViewById(R.id.cancel);
    this.inputTo = ((TextView)findViewById(R.id.destination));
    setActionBar();
    Button localButton = (Button)findViewById(R.id.send);
    Intent localIntent1 = getIntent();
    String str1 = localIntent1.getStringExtra("name");
    String str2 = localIntent1.getStringExtra("userfrom");
    String str3 = localIntent1.getStringExtra("userto");
    String str4 = localIntent1.getStringExtra("status");
    String str5 = localIntent1.getStringExtra("bid");
    String str6 = localIntent1.getStringExtra("fare");
    String str7 = localIntent1.getStringExtra("tel");
    GCM_ID = localIntent1.getStringExtra("gcm_id");
    TAG_ID = localIntent1.getStringExtra("bid");
    
    if ("confirmed".equals(str4))
    {
      Intent localIntent2 = new Intent(getApplicationContext(), ConfirmBook.class);
      localIntent2.putExtra("name", str1);
      localIntent2.putExtra("bid", str5);
      localIntent2.putExtra("userfrom", str2);
      localIntent2.putExtra("userto", str3);
      localIntent2.putExtra("fare", str6);
      localIntent2.putExtra("status", str4);
      localIntent2.putExtra("driverTel", str7);
      startActivity(localIntent2);
    }
    DriverName.setText(str1);
    inputFrom.setText(str2);
    inputTo.setText(str3);
    Fare.setText(str6);
    
    btn.setOnClickListener(new android.view.View.OnClickListener() {
   	 
        public void onClick(View view)
        {
        String url = "http://maps.google.com/maps?q=";
        Intent viw = new Intent("android.intent.action.VIEW", Uri.parse(url+inputFrom.getText().toString()));
        startActivity(viw);
        }

    });
    
    localButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        new CreateUser().execute();
      }
    });
    cancel.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        new RemoveBooking().execute();
      }
    });
  }
  
  class CreateUser
    extends AsyncTask<String, String, String>
  {
    CreateUser() {}
    
    protected String doInBackground(String... paramVarArgs)
    {
      String fare = Fare.getText().toString();
     
      ArrayList localArrayList = new ArrayList();
      localArrayList.add(new BasicNameValuePair("bid", TAG_ID));
      localArrayList.add(new BasicNameValuePair("fare", fare));
      localArrayList.add(new BasicNameValuePair("gcm_id", GCM_ID));
		
      JSONObject localJSONObject = SendQuote.this.jsonParser.makeHttpRequest(SendQuote.url_sendRequest, "POST", localArrayList);
      Log.d("Create Response", localJSONObject.toString());
      try
      {
        if (localJSONObject.getInt("success") == 1)
        {
          Intent localIntent = new Intent(SendQuote.this.getApplicationContext(), DriverBookings.class);
         startActivity(localIntent);
          finish();
          
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
      SendQuote.this.pDialog.dismiss();
    }
    
    protected void onPreExecute()
    {
      super.onPreExecute();
      SendQuote.this.pDialog = new ProgressDialog(SendQuote.this);
      SendQuote.this.pDialog.setMessage("Please Wait..");
      SendQuote.this.pDialog.setIndeterminate(false);
      SendQuote.this.pDialog.setCancelable(true);
      SendQuote.this.pDialog.show();
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
 
    JSONObject localJSONObject = SendQuote.this.jsonParser.makeHttpRequest(SendQuote.url_Booking_cancel, "POST", localArrayList);
    Log.d("Create Response", localJSONObject.toString());
    try
    {
      if (localJSONObject.getInt("success") == 1)
      {
        Intent localIntent = new Intent(SendQuote.this.getApplicationContext(), DriverBookings.class);
        SendQuote.this.startActivity(localIntent);
        SendQuote.this.finish();
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
    SendQuote.this.pDialog.dismiss();
  }
  
  protected void onPreExecute()
  {
    super.onPreExecute();
    SendQuote.this.pDialog = new ProgressDialog(SendQuote.this);
    SendQuote.this.pDialog.setMessage("Cancelling booking..");
    SendQuote.this.pDialog.setIndeterminate(false);
    SendQuote.this.pDialog.setCancelable(true);
    SendQuote.this.pDialog.show();
  }
}
}

