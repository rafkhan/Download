package com.rafaelkhan.android.download;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class DownloadMain extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
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

		@Override
		protected void onProgressUpdate(String... result) {

		}
	}
}