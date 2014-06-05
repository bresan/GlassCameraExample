package com.bresan.glass.examples.CameraExample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.AudioManager; // For option 2
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.glass.media.Sounds; // For option 2

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends Activity {
	private SurfaceHolder surfaceHolder;
	private Camera camera;
	private boolean previewOn;

	Context context = this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.camera_preview);

		// Set up the camera preview UX
		getWindow().setFormat(PixelFormat.UNKNOWN);
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.camerapreview);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(new SurfaceHolderCallback());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		// Hardware camera button
		case KeyEvent.KEYCODE_CAMERA: {

			// Release the camera before the picture is taken
			camera.stopPreview();
			camera.release();
			previewOn = false;

			// Return false to allow the camera button to do its default action
			return false;
		}
		// Touchpad tap
		case KeyEvent.KEYCODE_DPAD_CENTER: // Alternative way to take a picture
		case KeyEvent.KEYCODE_ENTER: {
			android.util.Log.d("CameraActivity", "Tap.");

			// Option 1: release the camera and use the ACTION_IMAGE_CAPTURE intent
			camera.stopPreview();
			camera.release();
			previewOn = false; // Don't release the camera in surfaceDestroyed()

			// Use the image capture intent to take the picture and process it with onActivityResult()
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, 0);

			// Option 2: capture the picture yourself and process it with a Camera.PictureCallback

			// Play a sound to indicate the take picture action succeeded
			// AudioManager audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			// audio.playSoundEffect(Sounds.SUCCESS);

			// Take the picture
			// camera.takePicture(null, null, new SavePicture());

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
