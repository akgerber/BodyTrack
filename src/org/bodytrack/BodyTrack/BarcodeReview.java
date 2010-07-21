package org.bodytrack.BodyTrack;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class BarcodeReview extends Activity {
	
	Button getBarcode;
	BTDbAdapter dbAdapter;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.barcode_review);
		
		//Set up button to go to camera activity
		getBarcode = (Button)findViewById(R.id.getBarcode);
		getBarcode.setOnClickListener(mGetBarcode);
		
        //connect to database
		dbAdapter = new BTDbAdapter(this).open();
	}
	
	
    /*Handles the barcode button: Requests the ZXing app scan a barcode*/
	private Button.OnClickListener mGetBarcode = new Button.OnClickListener(){
	    public void onClick(View v) {
	    	Intent intent = new Intent("com.google.zxing.client.android.SCAN");
	    	intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
	    	try {
	    		startActivityForResult(intent,0);
	    	} catch (ActivityNotFoundException e) {
   	
			}
	    	
	    }
    };
    
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == RESULT_OK) {
			long code = Long.parseLong((intent.getStringExtra("SCAN_RESULT")));
			
			dbAdapter.writeBarcode(code);

		} else {
		}
    }

}
