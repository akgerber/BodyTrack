package org.bodytrack.BodyTrack.Activities;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.bodytrack.BodyTrack.DbAdapter;
import org.bodytrack.BodyTrack.R;
import org.json.JSONArray;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.Toast;

/**
 * This class defines a tabbed UI that allows the user to see the app's main
 * features. It is what is shown when the app is launched.
 */
public class HomeTabbed extends TabActivity {
	public DbAdapter dbAdapter;
	public static final String TAG = "HomeTabbed";
	String dumpAddress = "http://bodytrack.org/users/14/upload";

	private Menu mMenu;

	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.tabbed_home);
	    dbAdapter = new DbAdapter(this).open();
	    //Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, GpsSvcControl.class);
	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("gps").setIndicator("GPS")
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    // Do the same for the other tabs
	    intent = new Intent().setClass(this, CameraReview.class);
	    spec = tabHost.newTabSpec("camera").setIndicator("Camera")
	    	.setContent(intent);
	    tabHost.addTab(spec);
	    
	  /**  intent = new Intent().setClass(this, BarcodeReview.class);
	    spec = tabHost.newTabSpec("barcode").setIndicator("Barcodes")
	    	.setContent(intent);
	    tabHost.addTab(spec);**/
	    
	    intent = new Intent().setClass(this, Accelerometer.class);
	    spec = tabHost.newTabSpec("accelerometer").setIndicator("Accelerometer")
	    		.setContent(intent);
	    tabHost.addTab(spec);
	    Timer time = new Timer();
	    final Handler handler = new Handler();
	    //This is the timer that sends the data after every 15 seconds.
	    time.scheduleAtFixedRate(new TimerTask()
	    {
	   	public void run()
	    	{
	    	handler.post(new Runnable(){
	    	public void run()
	    	{
	    		sendData();
	    	}
	    	});
	    	}
	    }
	    ,15000,15000);
	    }

		
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		mMenu = menu;
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.prefs:
	    	Intent intent = new Intent(getApplicationContext(), BTPrefs.class);
	    	startActivity(intent);
			return true;
		}
		return false;
	}
	public void sendData() throws NumberFormatException
	{
		Cursor queData = dbAdapter.fetchAllQueries();
		JSONArray data = new JSONArray();
		JSONArray channel = new JSONArray();
		if(queData.moveToFirst())
		{
		for(String chan : queData.getString(queData.getColumnIndex(DbAdapter.STACK_KEY_CHANNEL)).split(","))
		{
			channel.put(chan);
		}
		ArrayList<String> fields = new ArrayList<String>();
		String[] infoData = queData.getString(queData.getColumnIndex(DbAdapter.STACK_KEY_DATA)).split(",");
		Log.i("LENGTH", Integer.toString(infoData.length));
		fields.add(infoData[0]);
		for(int i=1; i <= infoData.length; i++)
		{
			Log.i("I", Integer.toString(i));
			if(i==infoData.length)
			{
				JSONArray queryField = new JSONArray(fields);
				data.put(queryField);
				fields.clear();
			}
			else if(isCorrectTime(infoData[i]))
			{
				JSONArray queryField = new JSONArray(fields);
				data.put(queryField);
				fields.clear();
				fields.add(infoData[i]);
			}
			else
			{
				fields.add(infoData[i]);
			}
		}
    	queData.close();
    	fields.clear();
		HttpClient mHttpClient = new DefaultHttpClient();
    	HttpPost postToServer = new HttpPost(dumpAddress);
    	WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
    	WifiInfo address = wifiManager.getConnectionInfo();
    	try {
    		List<NameValuePair> postRequest = new ArrayList<NameValuePair>();
	    	postRequest.add(new BasicNameValuePair("device_id", address.getMacAddress()));
	    	postRequest.add(new BasicNameValuePair("channel_names", channel.toString())); 
	    	postRequest.add(new BasicNameValuePair("data", data.toString()));
	    	Log.i("DATA", data.toString());
    		postToServer.setEntity(new UrlEncodedFormEntity(postRequest));
    		HttpResponse response = mHttpClient.execute(postToServer);
    		Toast.makeText(HomeTabbed.this, "This is the response: " + EntityUtils.toString(response.getEntity()),
    				Toast.LENGTH_SHORT).show();	  
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
		}
		else
		{
			Toast.makeText(HomeTabbed.this, "NO DATA",Toast.LENGTH_SHORT).show();
			queData.close();
		}
	}
	public boolean isCorrectTime(String value)
	{
		try
		{
			if(Double.valueOf(value) > 10000000.0)
			{
				return true;
			}
			return false;
		}
		catch(NumberFormatException e)
		{
			return false;
		}
	}
}
