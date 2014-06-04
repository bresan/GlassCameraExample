package com.bresan.glass.examples.CameraExample;

//Special thanks to Nortom Lam for Camera Example on Android

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraActivity extends Activity {
	private SurfaceHolder surfaceHolder;
	private Camera camera;
	private boolean previewOn;

	Context context = this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.camera_preview);

		getWindow().setFormat(PixelFormat.UNKNOWN);
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.camerapreview);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(new SurfaceHolderCallback());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_CAMERA: {
			camera.stopPreview();
			camera.release();
			previewOn = false;

			return false;
		}

		// Touchpad tap
		case KeyEvent.KEYCODE_DPAD_CENTER: // Alternative way to take a picture
		case KeyEvent.KEYCODE_ENTER: {

			camera.stopPreview();
			camera.release();
			previewOn = false; // Don't release the camera in surfaceDestroyed()

			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
			// Do something
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	// Handling of the camera preview
	class SurfaceHolderCallback implements SurfaceHolder.Callback {
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			if (null != camera) {

				try {
					// This camera parameter is set to fix a bug in XE12 that garbles the preview
					Camera.Parameters params = camera.getParameters();
					params.setPreviewFpsRange(5000, 5000);
					camera.setParameters(params);

					// Start the preview
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
				// Stop the preview and release the camera
				camera.stopPreview();
				camera.release();
			}
		}
	}

}
