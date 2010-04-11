package org.bodytrack.BodyTrack;

import java.io.FileOutputStream;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GpsService extends Service{
	/*constants*/
	private static final String TAG = "GpsService";
	private final long minTime = 5;
	private final long minDistance = 10;
	
	private LocationListener locListen;
	private LocationManager locMan;
	private boolean isLogging;
	
	FileOutputStream gpsFile;
	
	@Override
	public void onCreate() {
		super.onCreate();
    	Log.v(TAG, "Starting GPS service");
				
		/*Get an instance of the location manager*/
		locMan = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
		locListen = new myLocListen();
		
		try{
			gpsFile = openFileOutput("barcodes.csv", MODE_APPEND);
		} catch(Exception e) {
	    	Log.e(TAG, "Failed to open file; exception: " + e.toString());	
		}

	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return rpcBinder;
	}
	
	/*TODO: check for races*/
	private void startLogging() {
    	Log.v(TAG, "Starting GPS logging");

		/*Register the location listener with the location manager*/
		if (!isLogging) {
			locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locListen);
		}
		isLogging = true;
	}
	
	private void stopLogging() {
    	Log.v(TAG, "Stopping GPS logging");

		/*Stop getting location updates*/
		if (isLogging) {
			locMan.removeUpdates(locListen);
		}
		isLogging = false;
	}

	/*
	 * Private classes:
	 * These classes implement interfaces necessary to implement the GPS service:
	 * -The LocationListener class gives the  
	 */
	
	/*
	 * Implement a location listener
	 */
	private class myLocListen implements LocationListener{

		public void onLocationChanged(Location arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
	}
		
	/*
	 * Implement the RPC binding interface
	 */
	private IBinder rpcBinder = new IGPSSvcRPC.Stub(){
		public void startLogging() {
			GpsService.this.startLogging();
		}
		
		public void stopLogging() {
			GpsService.this.stopLogging();
		}
		
		public boolean isLogging() {
			return GpsService.this.isLogging;
		}
	};
		
}
