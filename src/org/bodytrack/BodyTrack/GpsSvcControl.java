package org.bodytrack.BodyTrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GpsSvcControl extends Activity{
	private static final String TAG = "GpsSvcControl";
	private Button gpsSvcStartButton;
	private Button gpsSvcStopButton;
	private Button gpsShowButton;
	private Button gpsDumpButton;
	
	private TextView Outbox;
	private IGPSSvcRPC gpsBinder;
	protected BTDbAdapter dbAdapter;
	
	private String dumpAddress = "http://128.237.237.188/cgi-bin/test_api.py";

	
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
        gpsShowButton = (Button)findViewById(R.id.gpsShowButton);
        gpsShowButton.setOnClickListener(showData);
        gpsDumpButton = (Button)findViewById(R.id.gpsDumpButton);
        gpsDumpButton.setOnClickListener(dumpData);
        Outbox = (TextView)findViewById(R.id.Outbox);
        
		dbAdapter = new BTDbAdapter(this).open();

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
 
    private Button.OnClickListener showData = new Button.OnClickListener(){
	    public void onClick(View v) {
	    	Outbox.setText("");
	    	Cursor geodata = dbAdapter.fetchAllLocations();
	    	geodata.moveToFirst();
	    	while (geodata.isAfterLast() == false) {
	    		Outbox.append("\n" + geodata.getString(geodata.getColumnIndex("latitude")) + ", " 
	    			+ geodata.getString(geodata.getColumnIndex("longitude")) 
	    			+ "; acc:" + geodata.getString(geodata.getColumnIndex("accuracy")));
	    		geodata.moveToNext();
	    	}
	    	geodata.close();
	    }
    };
    
    private Button.OnClickListener dumpData = new Button.OnClickListener(){
	    public void onClick(View v) {
	    	Cursor geodata = dbAdapter.fetchAllLocations();
	    	List<String []> data = new ArrayList<String []>();
	    	
	    	geodata.moveToFirst();
	    	String [] namesArr = geodata.getColumnNames();
	    	data.add(namesArr);
	    	
	    	while (geodata.isAfterLast() == false) {
	    		ArrayList<String> fields= new ArrayList<String>();
		    	for (String name : namesArr) {
		    		fields.add(geodata.getString(geodata.getColumnIndex(name)));
		    		geodata.moveToNext();
		    	}
		    	data.add((String [])fields.toArray());
	    	}
	    	
	    	/*
	    	 *   5 fields = {}
  6 fields['device_class']='Google Nexus 1'
  7 fields['source_class']='location'
  8 fields['device_id']='00:26:4a:0e:ae:0a'
  9 fields['source_id']='00:26:4a:0e:ae:0a/location'
 10 fields['sensor_nickname']='phone location'
 11 fields['timezone']='UTC'
 12 fields['time_range']={"begin" : "2010-03-18T21:52:27.50", "end" : "2010-03-19T23:00:02.25"}
 13 fields['data']=[
 14     ["time", "latitude", "longitude", "altitude", "uncertainty in meters"],
 15     ["2010-03-18T21:52:20.00", 40.4459, -79.9763, 10],
 16     ["2010-03-18T21:52:30.00", 40.44591, -79.976305, 10],
 17     ["2010-03-18T21:52:40.00", 40.44592, -79.97631, 10]
 18     ]

	    	 */
	    	
	    	Map<String, Object> request = new HashMap<String, Object>();
	    	/*map.put("device_class","droid-phone");//TODO: get name
	    	map.put("source_class", "location");
	    	map.put("device_id", "00:00:00:00:00");//TODO: make real
	    	map.put("source_id", device_id + '/' + source_class);
	    	*/
	    	
	    	geodata.close();
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