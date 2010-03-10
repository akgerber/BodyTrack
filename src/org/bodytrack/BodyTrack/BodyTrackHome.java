package org.bodytrack.BodyTrack;

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
		builder.setMessage("DAAANG");
		d = builder.create();
		return d;
	}
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button barcodeButton = (Button)findViewById(R.id.barcodeButton);
        barcodeButton.setOnClickListener(mScanBarcode);
    }
    
    private Button.OnClickListener mScanBarcode = new Button.OnClickListener(){
	    public void onClick(View v) {
	    	Intent intent = new Intent("com.google.zxing.client.android.SCAN");
	    	intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
	    	startActivityForResult(intent,0);
	    	//showDialog(DAAANG);
	    }
    };
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	
    }
    }
}