package org.bodytrack.BodyTrack;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class CameraActivity extends Activity implements SurfaceHolder.Callback{

	SurfaceView mSurfaceView;
	SurfaceHolder mSurfaceHolder;
	Camera mCamera;
	boolean previewRunning;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	getWindow().setFormat(PixelFormat.TRANSLUCENT);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
    	WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	
    	setContentView(R.layout.camera_layout);
    	mSurfaceView = (SurfaceView)findViewById(R.id.camera_disp);
    	mSurfaceHolder = mSurfaceView.getHolder();
    	mSurfaceHolder.addCallback(this);
    	mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    	mSurfaceView.setOnClickListener(takePicture);
    }

	private View.OnClickListener takePicture = new View.OnClickListener(){
		@Override
		public void onClick(View v) {
			Toast.makeText(CameraActivity.this, "HEY MAN", Toast.LENGTH_SHORT).show();
			mCamera.takePicture(null, null, mPictureCallback);
		}
	};
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = Camera.open();
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (Exception e){
			e.printStackTrace();
		}
		mCamera.startPreview();
	}	
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}


	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.stopPreview();
		previewRunning = false;
		mCamera.release();
		
	}
	
	Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
}
