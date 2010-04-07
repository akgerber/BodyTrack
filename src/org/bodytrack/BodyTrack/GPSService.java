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

public class GPSService extends Service{
	/*constants*/
	private final long minTime = 5;
	private final long minDistance = 10;
	
	private LocationListener locListen;
	private LocationManager locMan;
	private boolean isLogging;
	
	FileOutputStream gpsFile;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		Context myContext = getApplicationContext();
		
		/*Get an instance of the location manager*/
		locMan = (LocationManager)myContext.getSystemService(Context.LOCATION_SERVICE);
		locListen = new myLocListen();
		
		try{
		gpsFile = openFileOutput("barcodes.csv", MODE_APPEND);
		} catch(Exception e) {
			
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
		/*Register the location listener with the location manager*/
		if (!isLogging) {
			locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locListen);
		}
		isLogging = true;
	}
	
	private void stopLogging() {
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
			GPSService.this.startLogging();
		}
		
		public void stopLogging() {
			GPSService.this.stopLogging();
		}
		
		public boolean isLogging() {
			return GPSService.this.isLogging;
		}
	};
		
}
