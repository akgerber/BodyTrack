package org.bodytrack.BodyTrack;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class BodyTrackHome extends Activity{
	private static final String TAG = "BodyTrackHome";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "Starting BodyTrackHome activity");
        setContentView(R.layout.main);
        
        /*Set button handlers*/
        Button gpsButton = (Button)findViewById(R.id.gpsButton);
        Button barcodeButton = (Button)findViewById(R.id.barcodeButton);
        Button pixButton = (Button)findViewById(R.id.pixButton);
        Button dataButton = (Button)findViewById(R.id.dataButton);

        gpsButton.setOnClickListener(mGotoGps);
        pixButton.setOnClickListener(mGotoCam);
        barcodeButton.setOnClickListener(mScanBarcode);
        dataButton.setOnClickListener(mShowData);

    }

    /*Handles the GPS button: goes to the GPS service control activity*/
    private Button.OnClickListener mGotoGps = new Button.OnClickListener(){
	    public void onClick(View v) {
	    	Log.v(TAG, "BodyTrackHome starting GPS service control");
	    	Intent intent = new Intent(getApplicationContext(), GpsSvcControl.class);
	    	startActivity(intent);
	    }
    };
    
    /*Handles the pix button: goes to the camera control activity*/
    private Button.OnClickListener mGotoCam = new Button.OnClickListener(){
	    public void onClick(View v) {
	    	Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
	    	startActivity(intent);
	    }
    }; 
    
    /*Handles the barcode button: Requests the ZXing app scan a barcode*/
    private Button.OnClickListener mScanBarcode = new Button.OnClickListener(){
	    public void onClick(View v) {
	    	Log.v(TAG, "BodyTrackHome starting ZXing for barcode scan");	    	
	    	Intent intent = new Intent("com.google.zxing.client.android.SCAN");
	    	intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
	    	startActivityForResult(intent,0);
	    }
    };
    
    /*Handles the show data button: */
    private Button.OnClickListener mShowData = new Button.OnClickListener(){
	    public void onClick(View v) {
	    	showData();
	    }
    };
    
    /*Shows logged barcodes*/
    private void showData(){
    	try {
    		FileInputStream bcFile = this.openFileInput("barcodes.csv");
    		InputStreamReader reader = new InputStreamReader(bcFile);
    		char[] buf = new char[256];
    		StringBuilder data = new StringBuilder();
    		while (reader.read(buf) != -1)
    		{
    			data.append(buf.toString() + "\n");
    		}
    		Utilities.showDialog(data.toString(), this);
    	} catch(Exception e) {Utilities.showDialog(e.toString(), this);}
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	Log.v(TAG, "BodyTrackHome receiving activity result");	    	
		if (resultCode == RESULT_OK) {
			try {
	    		FileOutputStream bcFile = this.openFileOutput("barcodes.csv", MODE_APPEND);
	    		OutputStreamWriter writer = new OutputStreamWriter(bcFile);
    			String code = intent.getStringExtra("SCAN_RESULT");
        		try {
        			writer.write(code);
        		} catch(Exception e) { 
        	    	Log.e(TAG, "Failed to write to file; exception:" + e.toString());
        			Utilities.showDialog("FILE FAIL", this);
        			}	
        		finally {
        			try {
        				writer.close();
        				bcFile.close();
        			} catch (Exception e) {
            	    	Log.e(TAG, "Failed to close files:" + e.toString());
        				Utilities.showDialog("File did not close out", this);
        			}
        		}
			}
    		catch (FileNotFoundException e) {
    	    	Log.e(TAG, "File not found; exception:" + e.toString());
    		}
		} else {
	    	Log.v(TAG, "Unexpected activity result");
			Utilities.showDialog("aww snap", this);
		}
    }
    
}