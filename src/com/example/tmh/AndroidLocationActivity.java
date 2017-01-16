
package com.example.tmh;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
 

public class AndroidLocationActivity extends Activity
{

    AppLocationService appLocationService;
    private Button btn;
    private Button btn2;
    private Button btn3;
    private Button btn4;
    Button btnGPSShowLocation;
    Button btnNWShowLocation;
    String f;
    String one;
    String t;
    String two;

    public AndroidLocationActivity()
    {
        one = "one";
        two = "two";
        t = "three";
        f = "four";
    }

    public void locate(String s)
    {
    	  Location obj = appLocationService.getLocation("gps");
        if (obj != null)
        {
            double d = obj.getLatitude();
            double d1 = obj.getLongitude();
           
            String obj1 = Double.toString(d1);
            
            Toast.makeText(getApplicationContext(), (new StringBuilder()).append("Mobile Location (NW): \nLatitude: ").append(d).append("\nLongitude: ").append(d1).toString(), 1).show();
            Intent intent = new Intent(getApplicationContext(),AllCabs.class);
            intent.putExtra("loc",obj1);
           intent.putExtra("one", s);
            startActivity(intent);
            return;
        } else
        {
            Intent intent = new Intent(getApplicationContext(),AllCabs.class);
            intent.putExtra("loc", "0.3");
            intent.putExtra("one", s);
            startActivity(intent);
            showSettingsAlert("GPS");
            return;
        }
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(0x7f040000);
        btn = (Button)findViewById(0x7f090002);
        btn2 = (Button)findViewById(0x7f090003);
        btn3 = (Button)findViewById(0x7f090004);
        btn4 = (Button)findViewById(0x7f090005);
        appLocationService = new AppLocationService(this);
        btnGPSShowLocation = (Button)findViewById(0x7f090007);
        btn.setOnClickListener(new android.view.View.OnClickListener() {

            
            public void onClick(View view)
            {
                locate("one");
            }

            
        });
        btn2.setOnClickListener(new android.view.View.OnClickListener() {

            
            public void onClick(View view)
            {
                locate("two");
            }

            
        });
        btn3.setOnClickListener(new android.view.View.OnClickListener() {
 

            public void onClick(View view)
            {
                locate("three");
            }
 
        });
        btn4.setOnClickListener(new android.view.View.OnClickListener() {
 
            public void onClick(View view)
            {
                locate("four");
            }

             
        });
        btnNWShowLocation = (Button)findViewById(0x7f090006);
        btnNWShowLocation.setOnClickListener(new android.view.View.OnClickListener() {
 
            public void onClick(View view)
            {
            	
           Location locate      = appLocationService.getLocation("network");
                if (locate != null)
                {
                    double d = locate.getLatitude();
                    double d1 = locate.getLongitude();
                    Toast.makeText(getApplicationContext(), (new StringBuilder()).append("Mobile Location (NW): \nLatitude: ").append(d).append("\nLongitude: ").append(d1).toString(), 1).show();
                    return;
                } else
                {
                    showSettingsAlert("NETWORK");
                    return;
                }
            }

            
        
        });
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(0x7f080002, menu);
        return true;
    }

    public void showSettingsAlert(String s)
    {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle((new StringBuilder()).append(s).append(" SETTINGS").toString());
        builder.setMessage((new StringBuilder()).append(s).append(" is not enabled! Want to go to settings menu?").toString());
        builder.setPositiveButton("Settings", new android.content.DialogInterface.OnClickListener() {
 

            public void onClick(View view)
            {
            	Intent    dialoginterface =  new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
                startActivity(dialoginterface);
            }
 
 

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub

            	Intent    dialoginterface =  new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
                startActivity(dialoginterface);
				
			}
        });
        builder.setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {
 
            public void onClick(DialogInterface dialoginterface, int i)
            {
                dialoginterface.cancel();
            }

             
        });
        builder.show();
    }
}
