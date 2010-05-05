package org.bodytrack.BodyTrack;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class GpsSvcControl extends Activity{
	private static final String TAG = "GpsSvcControl";
	private Button gpsSvcStartButton;
	private Button gpsSvcStopButton;
	private IGPSSvcRPC gpsBinder;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.v(TAG, "Starting GpsSvcControl activity");
        setContentView(R.layout.gpscontrol);
        gpsSvcStartButton = (Button)findViewById(R.id.gpsSvcStartButton);
        gpsSvcStopButton = (Button)findViewById(R.id.gpsSvcStopButton);
        gpsSvcStartButton.setOnClickListener(mStartSvc);
        gpsSvcStopButton.setOnClickListener(mStopSvc);
        gpsSvcStopButton.setEnabled(false);
    }
    
    private void startGps() {
    	Intent intent = new Intent(this, GpsService.class);
    	this.startService(intent);
    	Boolean bindSuccess = this.bindService(intent, sc, 0);
        Log.v(TAG, "Telling GPS service to start. Success? " + bindSuccess);
        gpsSvcStartButton.setEnabled(false);
        gpsSvcStopButton.setEnabled(true);


    }
    
    private void stopGps() {
        Log.v(TAG, "Telling GPS service to stop");

    	try {
        	Intent intent = new Intent(this, GpsService.class);
    		this.unbindService(sc);
    		this.stopService(intent);
            gpsSvcStartButton.setEnabled(true);
            gpsSvcStopButton.setEnabled(false);
    	} catch (Exception e) {
            Log.e(TAG, "Attempting to stop GPS service which wasn't running; exception:" + e.toString());
    	}
    }
    
    private Button.OnClickListener mStartSvc = new Button.OnClickListener(){
	    public void onClick(View v) {
	    	startGps();
	    }
    };
    
    private ServiceConnection sc = new ServiceConnection(){
		@Override
		public void onServiceConnected(ComponentName svc, IBinder binder) {
			Log.v(TAG, "Service connected");
			gpsBinder = IGPSSvcRPC.Stub.asInterface(binder);
	        try {
	        	gpsBinder.startLogging();
	        } catch(Exception e) {
	        	Log.e(TAG, "Failed to start logging in GPS Service; exception: " + e);
	        }
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.v(TAG, "Service disconnected");
		}
    };
    
    private Button.OnClickListener mStopSvc = new Button.OnClickListener(){
	    public void onClick(View v) {
	    	stopGps();
	    }
    };
   
    
}