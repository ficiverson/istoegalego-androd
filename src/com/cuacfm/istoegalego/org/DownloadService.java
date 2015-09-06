/*
 * Copyright (C) 2015 Fernando Souto Gonzalez @ficiverson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cuacfm.istoegalego.org;


import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class DownloadService extends IntentService {

	public static final int STATUS_RUNNING = 0;
	public static final int STATUS_FINISHED = 1;
	public static final int STATUS_ERROR = 2;
	private Long progress=0L,newNotify=0L;
	private Notification notification;
	private NotificationManager notificationManager;
	private PendingIntent pendingInt;
	private PendingIntent pendingIntent;
	private Context mContext;

	private static final String TAG = "DownloadService";

	public DownloadService() {
		super(DownloadService.class.getName());

		mContext = this;
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		Log.d(TAG, "Service Started!");

		//		final ResultReceiver receiver = intent.getParcelableExtra("receiver");
		String url = intent.getStringExtra("url");

		Bundle bundle = new Bundle();

		if (!TextUtils.isEmpty(url)) {
			/* Update UI: Download Service is Running */
			//			receiver.send(STATUS_RUNNING, Bundle.EMPTY);

			try {
				sendNotification("");
				boolean results = saveFileFromUrl(url);

				/* Sending result back to activity */
				if (results) {
					bundle.putBoolean("result", results);
					//					receiver.send(STATUS_FINISHED, bundle);
					notifyDownloadSuccess();
				}
				else{
					cancelNotification();
				}
			} catch (Exception e) {

				/* Sending error message back to activity */
				bundle.putString(Intent.EXTRA_TEXT, e.toString());
				//				receiver.send(STATUS_ERROR, bundle);
			}
		}
		Log.d(TAG, "Service Stopping!");
		this.stopSelf();
	}

	private boolean saveFileFromUrl(String urlRecv) {
		int count;
		try{
			URL url = new URL(urlRecv);
			URLConnection conection = url.openConnection();
			conection.setRequestProperty("Connection", "keep-alive");
			conection.setDoOutput(true);
			conection.connect();

			// progress bar
			int lenghtOfFile = conection.getContentLength();

			// download the file
			InputStream input = new BufferedInputStream(url.openStream(),4*1024);

			File mFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.cuacfm.istoegalego.org/mp3/");
			mFile.mkdirs();
			File mFile2 = new File(mFile.getAbsolutePath(), "ieg.zip");

			// Output stream
			OutputStream output = new FileOutputStream(mFile2.toString());

			byte data[] = new byte[4*1024];

			long total = 0;
			newNotify = 0L;
			while ((count = input.read(data)) != -1) {
				total += count;
				// publishing the progress
				progress = (total * 100) /lenghtOfFile; 
				if(progress>newNotify){
					publishProgress(progress);
					
					newNotify = newNotify+4L;
					if(newNotify>100L){
						progress=100L;
					}
				}
				
				// writing data to file
				output.write(data, 0, count);
			}

			// flushing output
			output.flush();

			// closing streams
			output.close();
			input.close();

			//prepare notification to decompress
			progress=120L;
			publishProgress(progress);

			//decompress file
			String zipFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.cuacfm.istoegalego.org/mp3/ieg.zip"; 
			String unzipLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.cuacfm.istoegalego.org/mp3/"; 

			Decompress d = new Decompress(zipFile, unzipLocation); 
			boolean unziprocess = d.unzip(); 
			//delete file if all gone well
			if(mFile2.exists() && unziprocess){
				mFile2.delete();
			}

			//set preferences to finish downnload and unzip process
			SharedPreferences localSharedPreferences = mContext.getSharedPreferences("ieg", 2);
			Editor editor = localSharedPreferences.edit();
			editor.putInt("recursos", 1);
			editor.commit();

			return true;

		} catch (Exception e) {
			Log.e("Error: ", e.getMessage());
			return false;
		}
	}

	private void notifyDownloadSuccess(){
		SharedPreferences localSharedPreferences = mContext.getSharedPreferences("ieg", 2);
		Editor editor = localSharedPreferences.edit();
		editor.putInt("recursos", 1);
		editor.commit();

		this.notification.flags = (0x10 | this.notification.flags);
		this.notification.contentView = new RemoteViews("com.cuacfm.istoegalego.org", 2130903043);
		this.notification.contentIntent = this.pendingIntent;
		this.notification.contentView.setImageViewResource(R.id.status_icon, R.drawable.ic_launcher);
		this.notification.contentView.setTextViewText(R.id.status_text,"Comezar o cursiño!!");
		this.notification.contentView.setProgressBar(R.id.status_progress, 100, 100, false);
		this.notificationManager.notify(42, this.notification);
	}


	private void sendNotification(String msg) {


		notification = new Notification(R.drawable.ic_launcher, "Descargando episodio", System.currentTimeMillis());
		notificationManager = (NotificationManager)mContext.getApplicationContext().getSystemService("notification");

		this.notification.flags = (0x2 | this.notification.flags);
		pendingInt = PendingIntent.getActivity(this, 0, new Intent(), 0);
		Intent intentIstoScreen = new Intent(this, IstoeGalegoScreen.class);
		pendingIntent = PendingIntent.getActivity(this, 0, intentIstoScreen, 0);

		this.notification.contentView = new RemoteViews("com.cuacfm.istoegalego.org", R.layout.download_progress);
		this.notification.contentIntent = this.pendingInt;
		this.notification.contentView.setImageViewResource(R.id.status_icon, R.drawable.ic_launcher);
		this.notification.contentView.setTextViewText(R.id.status_text, "Descargando cousas para: Isto é Galego");
		this.notification.contentView.setTextColor(R.id.status_text, Color.WHITE);
		this.notification.contentView.setProgressBar(R.id.status_progress, 100, 0, false);
		this.notificationManager.notify(42, this.notification);
	}

	private void publishProgress(Long progress){
		if(progress==120L){
			//unziping
			this.notification.flags = (0x2 | this.notification.flags);
			this.notification.contentView = new RemoteViews("com.cuacfm.istoegalego.org", R.layout.download_progress);
			this.notification.contentIntent = this.pendingInt;
			this.notification.contentView.setImageViewResource(R.id.status_icon, R.drawable.ic_launcher);
			this.notification.contentView.setTextViewText(R.id.status_text, "Preparando todo. Falta pouco ;)");
			this.notification.contentView.setTextColor(R.id.status_text, Color.WHITE);
			this.notification.contentView.setProgressBar(R.id.status_progress, 100, 100, true);
			this.notificationManager.notify(42, this.notification);
		}
		else{
			this.notification.contentView.setProgressBar(R.id.status_progress, 100, (int) (long)progress, false);
			this.notificationManager.notify(42, this.notification);
		}
	}

	private void cancelNotification(){
		SharedPreferences localSharedPreferences = mContext.getSharedPreferences("ieg", 2);
		Editor editor = localSharedPreferences.edit();
		editor.putInt("recursos", 0);
		editor.commit();
		this.notificationManager.cancel(42);
	}

	public class DownloadException extends Exception {

		public DownloadException(String message) {
			super(message);
		}

		public DownloadException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}