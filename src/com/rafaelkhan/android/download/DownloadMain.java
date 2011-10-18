package com.rafaelkhan.android.download;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
			this.storageDir = new File(f + "/download");
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

		EditText saveAsField = (EditText) findViewById(R.id.save_as_field);
		String saveFile = saveAsField.getText().toString();

		// check value
		if (urlString.equals("")) {
			// notify user of invalid url
			Toast.makeText(this, "Invalid URL", 0).show();
		} else {
			if (!urlString.startsWith("http://")) {
				urlString = "http://" + urlString;
			}
			new Downloader().execute(urlString, saveFile);
		}
	}

	private class Downloader extends AsyncTask<String, String, String> {

		private URL url;
		private HttpURLConnection http;
		private BufferedInputStream in;
		private BufferedOutputStream bout;
		private String fileName; // file name to save as
		private int fileSize;

		@Override
		protected String doInBackground(String... s) {
			if (!s[1].equals("")) { // get file name
				this.fileName = s[1];
			} else {
				if (s[0].contains("/")) {
					this.fileName = s[0].substring(s[0].lastIndexOf("/") + 1);
				} else {
					this.fileName = s[0];
				}
			}

			this.downloadFile(s[0]);
			return null;
		}

		private void downloadFile(String urlString) {
			this.openUrl(urlString);
			this.createInputStream();
			this.createOutputStream();
			this.download();
			this.closeStreams();
		}

		// open URL from string
		private void openUrl(String urlString) {
			try {
				this.url = new URL(urlString);
			} catch (MalformedURLException e) {
				Log.e(DownloadMain.LOGTAG, e.toString());
			}
		}

		private void createInputStream() {
			// create InputStream with URL
			InputStream is = null;
			try {
				this.http = (HttpURLConnection) url.openConnection();
				this.fileSize = this.http.getContentLength();
				is = this.http.getInputStream();
			} catch (IOException e) {
				Log.e(DownloadMain.LOGTAG, e.toString());
			}

			this.in = new BufferedInputStream(is);
		}

		private void createOutputStream() {
			// create a fileoutput to /sdcard/download
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(DownloadMain.this.storageDir
						+ this.fileName);
			} catch (FileNotFoundException e) {
				Log.e(DownloadMain.LOGTAG, e.toString());
			}
			this.bout = new BufferedOutputStream(fos, 1024);
		}

		private void download() {
			byte[] data = new byte[1024];
			// read data from inputstream, write to buffered output stream
			try {
				int x = 0;
				int totalBytes = 0;
				while ((x = this.in.read(data, 0, 1024)) >= 0) {
					this.bout.write(data, 0, x);
					totalBytes += x;

					String b = Integer.toString(totalBytes);
					String tb = Integer.toString(this.fileSize);
					this.publishProgress(b, tb);
				}
			} catch (IOException e) {
				Log.e(DownloadMain.LOGTAG, e.toString());
			}
		}

		private void closeStreams() {
			// close streams
			try {
				this.http.disconnect();
				this.bout.close();
				this.in.close();
			} catch (IOException e) {
				Log.e(DownloadMain.LOGTAG, e.toString());
			}
		}

		@Override
		protected void onProgressUpdate(String... s) {
			TextView tv = (TextView) findViewById(R.id.textView1);
			tv.setText(s[0] + " / " + s[1] + "B's");

			float a = Integer.parseInt(s[0]);
			float b = Integer.parseInt(s[1]);
			float percent = ((a / b) * 100);

			ProgressBar pb = (ProgressBar) findViewById(R.id.progress_bar);
			pb.setProgress((int) Math.abs(percent));
		}

		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(
					DownloadMain.this,
					"Saved " + DownloadMain.this.storageDir + "/"
							+ this.fileName, 0).show();
			TextView tv = (TextView) findViewById(R.id.textView1);
			tv.append("\nDownload complete!");
		}
	}
}