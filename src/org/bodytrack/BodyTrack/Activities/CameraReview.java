package org.bodytrack.BodyTrack.Activities;

import org.bodytrack.BodyTrack.R;
import org.bodytrack.BodyTrack.R.id;
import org.bodytrack.BodyTrack.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


/**
 * This class defines an activity that allows the user to review
 * previously taken (meal) photos and has a button to take new
 * ones.
 */
public class CameraReview extends Activity {
	public static final String TAG = "cameraReview";
	
	Button takePic;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_review);
		
		//Set up button to go to camera activity
		takePic = (Button)findViewById(R.id.takePic);
		takePic.setOnClickListener(mTakePic);
	}
	
	private Button.OnClickListener mTakePic = new Button.OnClickListener(){
		public void onClick(View v) {
			Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
			startActivity(intent);
		}
	};

}
