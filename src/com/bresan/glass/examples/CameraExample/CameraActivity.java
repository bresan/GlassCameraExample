package com.bresan.glass.examples.CameraExample;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

//Special thanks to Nortom Lam for camera example source

public class CameraActivity extends Activity {
	private SurfaceHolder surfaceHolder;
	private Camera camera;
	private boolean previewOn;
	private final static int CAMERA_FPS = 5000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.camera_preview);

		// Set up the camera preview user interface
		getWindow().setFormat(PixelFormat.UNKNOWN);
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.camerapreview);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(new SurfaceHolderCallback());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_CAMERA: { // camera button (hardware)

			camera.stopPreview(); // stop the preview
			camera.release(); // release the camera
			previewOn = false;

			// Return false to allow the camera button to do its default action
			return false;
		}
		case KeyEvent.KEYCODE_DPAD_CENTER: // touchpad tap
		case KeyEvent.KEYCODE_ENTER: {

			camera.stopPreview();
			camera.release();
			previewOn = false; // Don't release the camera in surfaceDestroyed()

			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // capture image
			startActivityForResult(intent, 0);

			return true;
		}
		default: {
			return super.onKeyDown(keyCode, event);
		}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK == resultCode) {
			// want to do something after taking the picture?
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	// camera preview stuff
	class SurfaceHolderCallback implements SurfaceHolder.Callback {
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			if (null != camera) {

				try {
					Camera.Parameters params = camera.getParameters(); // must change the camera parameters to fix a bug in XE1
					params.setPreviewFpsRange(CAMERA_FPS, CAMERA_FPS);
					camera.setParameters(params);

					camera.setPreviewDisplay(surfaceHolder);
					camera.startPreview();
					previewOn = true;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			camera = Camera.open();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if (previewOn) {
				camera.stopPreview(); //stop the preview
				camera.release();  //release the camera for using it later (or if another app want to use)
			}
		}
	}

}
