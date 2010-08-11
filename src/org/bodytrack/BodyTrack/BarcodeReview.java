package org.bodytrack.BodyTrack;

import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;


/*
 * This class defines an activity which allows the user to review
 *  previously captured  barcodes and has a button to allow the
 *   capture of new ones.
 */
public class BarcodeReview extends ListActivity {
	public static final String TAG = "BarcodeReview";
	
	Button getBarcode;
	BTDbAdapter dbAdapter;
	Cursor bccursor;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.barcode_review);
		
		//Set up button to go to camera activity
		getBarcode = (Button)findViewById(R.id.getBarcode);
		getBarcode.setOnClickListener(mGetBarcode);
		
        //connect to database
		dbAdapter = new BTDbAdapter(this).open();
		bccursor = dbAdapter.fetchAllBarcodes();
		
		//set list contents
		ListAdapter bcAdapter = new SimpleCursorAdapter(
				this, //context
				android.R.layout.simple_list_item_1,
				bccursor,
				new String[] {BTDbAdapter.BC_KEY_BARCODE},
				new int[] {android.R.id.text1}
				);
		setListAdapter(bcAdapter);
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
