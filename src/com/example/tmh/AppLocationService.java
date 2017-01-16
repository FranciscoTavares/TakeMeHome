 
package com.example.tmh;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class AppLocationService extends Service
    implements LocationListener
{

    private static final long MIN_DISTANCE_FOR_UPDATE = 2000;
    private static final long MIN_TIME_FOR_UPDATE = 1;
    Location location;
    protected LocationManager locationManager;

    public AppLocationService(Context context)
    {
        locationManager = (LocationManager)context.getSystemService("location");
    }

    public Location getLocation(String s)
    {
        if (locationManager.isProviderEnabled(s))
        {
            locationManager.requestLocationUpdates(s, 0x1d4c0L, 10F, this);
            if (locationManager != null)
            {
                location = locationManager.getLastKnownLocation(s);
                return location;
            }
        }
        return null;
    }

    public IBinder onBind(Intent intent)
    {
        return null;
    }

    public void onLocationChanged(Location location1)
    {
    }

    public void onProviderDisabled(String s)
    {
    }

    public void onProviderEnabled(String s)
    {
    }

    public void onStatusChanged(String s, int i, Bundle bundle)
    {
    }
}
