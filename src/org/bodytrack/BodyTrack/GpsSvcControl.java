package org.bodytrack.BodyTrack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GpsSvcControl extends Activity{
	private static final String TAG = "GpsSvcControl";
	private Button gpsSvcStartButton;
	private Button gpsSvcStopButton;
	private Button gpsShowButton;
	private Button gpsDumpButton;
	
	private TextView Outbox;
	private IGPSSvcRPC gpsBinder;
	protected BTDbAdapter dbAdapter;
	
	protected SharedPreferences prefs;
	protected String dumpAddress;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.v(TAG, "Starting GpsSvcControl activity");
        setContentView(R.layout.gpscontrol);
        //Set up buttons
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
        
        //connect to database
		dbAdapter = new BTDbAdapter(this).open();
		
		//Load preferences
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		dumpAddress = prefs.getString("upload_address", "FAIL");
		Log.v(TAG, "loaded submission address " + dumpAddress + 
				" from preferences");
		
    }
    
    public void onResume() {
		//Load preferences
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		//reset upload address if changed
		dumpAddress = prefs.getString("upload_address", "FAIL");
		Log.v(TAG, "loaded submission address " + dumpAddress + 
				" from preferences");
		
		super.onResume();
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
 
    //TODO: replace with a listview in bottom half of view (maybe move svc
    //buttons to menu?)
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
    
    //TODO: moved all uploading to DB adapter? sure shouldn't be here
    private Button.OnClickListener dumpData = new Button.OnClickListener(){
	    public void onClick(View v) {
	    	//grab location data cursor from database
	    	Cursor geodata = dbAdapter.fetchAllLocations();
	    	
	    	//create a json array to put it in
	    	JSONArray data = new JSONArray();
	    	
	    	//iterate through location data in db
	    	geodata.moveToFirst();
	    	String [] namesArr = geodata.getColumnNames();
	    	List <String> columns = Arrays.asList(namesArr);
	    	JSONArray jsonColumns = new JSONArray(columns);
	    	data.put(jsonColumns);

	    	while (geodata.isAfterLast() == false) {
	    		ArrayList<String> fields= new ArrayList<String>();
		    	for (String name : namesArr) {
		    		fields.add(geodata.getString(geodata.getColumnIndex(name)));
		    	}
		    	JSONArray jsonFields = new JSONArray(fields);
		    	data.put(jsonFields);
	    		geodata.moveToNext();
	    	}

	    	

	    	//make an http request
	    	HttpClient mHttpClient = new DefaultHttpClient();
	    	HttpPost postToServer = new HttpPost(dumpAddress);
	    	try {
	    		List<NameValuePair> postRequest = new ArrayList<NameValuePair>();
		    	postRequest.add(new BasicNameValuePair("device_class","droid-phone"));//TODO: get name
		    	postRequest.add(new BasicNameValuePair("source_class", "location"));
		    	postRequest.add(new BasicNameValuePair("device_id", "00:00:00:00:00"));//TODO: make real
		    	postRequest.add(new BasicNameValuePair("source_id", "00:00:00:00:00/location"));
		    	postRequest.add(new BasicNameValuePair("sensor_nickname", "phone location"));
		    	postRequest.add(new BasicNameValuePair("timezone", "utc"));//TODO:: make real
		    	postRequest.add(new BasicNameValuePair("data", data.toString()));
		    	
	    		postToServer.setEntity(new UrlEncodedFormEntity(postRequest));
	    		HttpResponse response = mHttpClient.execute(postToServer);
				Toast.makeText(GpsSvcControl.this, response.toString(),
						Toast.LENGTH_SHORT).show();	  
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    	
	    	
	    	
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