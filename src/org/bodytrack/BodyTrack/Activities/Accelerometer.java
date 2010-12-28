package org.bodytrack.BodyTrack.Activities;


import org.bodytrack.BodyTrack.R;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class Accelerometer extends Activity implements SensorEventListener{
	//This is the manager that gets instantiated inside the on create method.
	SensorManager manager = null;
	TextView title;
	TextView accelX;
	TextView accelY;
	TextView accelZ;
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
		accelX.setText("X: " + Float.toString(event.values[0]));
		accelY.setText("Y: " +  Float.toString(event.values[1]));
		accelZ.setText("Z: " + Float.toString(event.values[2]));
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
}
