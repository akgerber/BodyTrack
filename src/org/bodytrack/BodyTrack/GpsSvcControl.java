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
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.v(TAG, "Starting GpsSvcControl activity");
        setContentView(R.layout.gpscontrol);
        Button gpsSvcStartButton = (Button)findViewById(R.id.gpsSvcStartButton);
        Button gpsSvcStopButton = (Button)findViewById(R.id.gpsSvcStopButton);
        gpsSvcStartButton.setOnClickListener(mStartSvc);
        gpsSvcStopButton.setOnClickListener(mStopSvc);
    }
    
    private void startGps() {
    	Intent intent = new Intent(this, GpsService.class);
    	this.startService(intent);
    	Boolean bindSuccess = this.bindService(intent, sc, 0);
        Log.v(TAG, "Telling GPS service to start. Success? " + bindSuccess);
    }
    
    private void stopGps() {
        Log.v(TAG, "Telling GPS service to stop");

    	try {
    		this.unbindService(sc);
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
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			Utilities.showDialog("connected", GpsSvcControl.this);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Utilities.showDialog("disconnected", GpsSvcControl.this);
			
		}
    };
    
    private Button.OnClickListener mStopSvc = new Button.OnClickListener(){
	    public void onClick(View v) {
	    	stopGps();
	    }
    };
   
    
}