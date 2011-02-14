package org.bodytrack.BodyTrack.Activities;


import java.util.ArrayList;
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
import org.bodytrack.BodyTrack.DbAdapter;
import org.bodytrack.BodyTrack.R;
import org.json.JSONArray;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Accelerometer extends Activity implements SensorEventListener{
	//This is the manager that gets instantiated inside the on create method.
	SensorManager manager = null;
	TextView title;
	TextView accelX;
	TextView accelY;
	TextView accelZ;
	Timer time;
	Handler handler = new Handler();
	Button sendData; 
	Float[] accelValue = new Float[3];
	String dumpAddress = "http://bodytrack.org/users/14/upload";
	protected DbAdapter dbAdapter; 
	/**
	 * This method gets called when the program is first started. The method initializes the text views and the System manager.
	 * This System manager is used in order to activate the sensors of the Android phone. 
	 * The text views are only there temporarily to test. 
	 **/
	//TODO delete the text views once the accelerometer is working.
	public void onCreate(Bundle savedInstanceState)
	{
	super.onCreate(savedInstanceState);
	setContentView(R.layout.accel);
    manager = (SensorManager) getSystemService(SENSOR_SERVICE);
    title = (TextView) findViewById(R.id.title);
    title.setText("Accelerometer");
    accelX = (TextView) findViewById(R.id.accelX);
    accelY = (TextView) findViewById(R.id.accelY);
    accelZ = (TextView) findViewById(R.id.accelZ);
    sendData = (Button) findViewById(R.id.upload);
    sendData.setText("Upload Data");
    sendData.setOnClickListener(upload);
    time = new Timer();
    //This is the timer that sends the data after every 15 seconds.
    time.schedule(new TimerTask()
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
    ,15000, 15000);
    }
	
	@Override
	/**
	 * Please read inside comment.
	 **/
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Nothing needs to be in this method. Just here in order to make the program compile correctly.
		
	}
	
	@Override
	/**
	 * This method is invoked when the sensor values have changed. This method is used for all sensors when the programmer
	 * wants to gain access to the values. Currently the event.values array contains all the values the sensor gives out. 
	 **/
	public void onSensorChanged(SensorEvent event) {
		/**accelX.setText("X: " + Float.toString(event.values[0]));
		accelY.setText("Y: " +  Float.toString(event.values[1]));
		accelZ.setText("Z: " + Float.toString(event.values[2]));**/
		accelValue[0] = event.values[0];
		accelValue[1] = event.values[1];
		accelValue[2] = event.values[2];
	}

	@Override
	/**
	 * This method is invoked when the user returns back to the program. This is needed when it corresponds to the 
	 * onPause method. The method registers the sensor that is needed for the program to continue. The SensorManager.SENSOR_DELAY_GAME
	 * tells the phone at what speed the sensor should gather the data. The fastest mode is SENSOR_DELAY_FASTEST.
	 **/
	protected void onResume()
	{
		manager.registerListener(this, manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) , SensorManager.SENSOR_DELAY_GAME);
		super.onResume();
	}
	
	@Override
	/**
	 * This method is invoked when the user switches to another program or closes this running program.
	 * It unregisters the listener because it is not needed during the moment when the user has switched the program.
	 **/
	protected void onPause()
	{
		manager.unregisterListener(this, manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
		super.onPause();
	}
	/**
	 * This is for testing to see if the data is actually sent.
	 */
	private Button.OnClickListener upload = new Button.OnClickListener(){
		public void onClick(View v)
		{
			sendData();
		}
	};
	/**
	 * This sends the data to the database.
	 */
	public void sendData()
	{
	WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
	WifiInfo info =  wifi.getConnectionInfo();
	JSONArray data = new JSONArray();
	for(int i=0; i < accelValue.length; i++)
	{
	data.put(accelValue[i]);
	}
	HttpClient mHttpClient = new DefaultHttpClient();
	HttpPost postToServer = new HttpPost(dumpAddress);
	try {
		List<NameValuePair> postRequest = new ArrayList<NameValuePair>();
		Toast.makeText(Accelerometer.this, info.getMacAddress(), Toast.LENGTH_SHORT).show();
    	postRequest.add(new BasicNameValuePair("device_id",info.getMacAddress()));
    	postRequest.add(new BasicNameValuePair("timezone", Long.toString(System.currentTimeMillis())));
    	postRequest.add(new BasicNameValuePair("data", data.toString()));
    	
		postToServer.setEntity(new UrlEncodedFormEntity(postRequest));
		HttpResponse response = mHttpClient.execute(postToServer);
		Toast.makeText(Accelerometer.this, response.toString(),
				Toast.LENGTH_SHORT).show();	  
	} catch (Exception e) {
		e.printStackTrace();
	}
	}
}
