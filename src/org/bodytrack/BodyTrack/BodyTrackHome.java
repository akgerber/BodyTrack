package org.bodytrack.BodyTrack;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BodyTrackHome extends Activity{
	static final int DAAANG = 0;
	
	protected Dialog onCreateDialog(int id) {
		Dialog d;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("SCANT.");
		d = builder.create();
		return d;
	}
	
	private void showDialog(String message) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage(message);
	    builder.setPositiveButton("OK", null);
	    builder.show();
	}
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button barcodeButton = (Button)findViewById(R.id.barcodeButton);
        Button dataButton = (Button)findViewById(R.id.dataButton);
        barcodeButton.setOnClickListener(mScanBarcode);
        dataButton.setOnClickListener(mShowData);

    }
    
    private Button.OnClickListener mScanBarcode = new Button.OnClickListener(){
	    public void onClick(View v) {
	    	Intent intent = new Intent("com.google.zxing.client.android.SCAN");
	    	intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
	    	startActivityForResult(intent,0);
	    	//showDialog(DAAANG);
	    }
    };
    
    private Button.OnClickListener mShowData = new Button.OnClickListener(){
	    public void onClick(View v) {
	    	showData();
	    }
    };
    
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
    		showDialog(data.toString());
    	} catch(Exception e) {showDialog(e.toString());}
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == RESULT_OK) {
			try {
	    		FileOutputStream bcFile = this.openFileOutput("barcodes.csv", MODE_APPEND);
	    		OutputStreamWriter writer = new OutputStreamWriter(bcFile);
    			String code = intent.getStringExtra("SCAN_RESULT");
        		//showDialog("hey" + code);
        		try {
        			writer.write(code);
        		} catch(Exception e){ showDialog("FILE FAIL");}	
        		finally {
        			try {
        				writer.close();
        				bcFile.close();
        			} catch (Exception e) { showDialog("File did not close out");}
        		}
			}
    		catch (FileNotFoundException e) {/*TODO*/}
		} else {
			showDialog("aww snap");
		}
    }
    
}