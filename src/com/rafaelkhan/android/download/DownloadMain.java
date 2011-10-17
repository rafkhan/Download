package com.rafaelkhan.android.download;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class DownloadMain extends Activity {

	public static String LOGTAG = "Download.";

	public boolean storageAvailable; // if storage is available
	public File storageDir = null; // external storage directory

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		this.storageAvailable = this.checkStorage();
	}

	/*
	 * check if external storage is available
	 */
	public boolean checkStorage() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File f = Environment.getExternalStorageDirectory();
			this.storageDir = new File(f + "download");
			return true;
		} else {
			this.storageDir = null;
			return false;
		}
	}

	/*
	 * Called when the "Go!" button is pressed
	 */
	public void goButton(View view) {
		EditText urlField = (EditText) findViewById(R.id.url_field);
		String urlString = urlField.getText().toString(); // get string contents

		// check value
		if (urlString.equals("")) {
			// notify user of invalid url
			Toast.makeText(this, "Invalid URL", 0).show();
		} else {
			new Downloader().execute(urlString);
		}
	}

	private class Downloader extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... s) {

			return null;
		}

		private void downloadFile(String urlString) {
			// open URL
			URL url = null;
			try {
				url = new URL(urlString);
			} catch (MalformedURLException e) {
				Log.e(DownloadMain.LOGTAG, e.toString());
			}

			// create InputStream with URL
			InputStream is = null;
			try {
				is = url.openStream();
			} catch (IOException e) {
				Log.e(DownloadMain.LOGTAG, e.toString());
			}

			BufferedInputStream in = new BufferedInputStream(is);

			// create a fileoutput to /sdcard/download
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(DownloadMain.this.storageDir);
			} catch (FileNotFoundException e) {
				Log.e(DownloadMain.LOGTAG, e.toString());
			}
			BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);

			byte[] data = new byte[1024];

			// read data from inputstream, write to buffered output stream
			try {
				int x = 0;
				while ((x = in.read(data, 0, 1024)) >= 0) {
					bout.write(data, 0, x);
				}
			} catch (IOException e) {
				Log.e(DownloadMain.LOGTAG, e.toString());
			}

			// close streams
			try {
				bout.close();
				in.close();
			} catch (IOException e) {
				Log.e(DownloadMain.LOGTAG, e.toString());
			}
		}

		@Override
		protected void onProgressUpdate(String... result) {

		}
	}
}