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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.RemoteViews;

public class DescargarRecursos extends AsyncTask<String, Long, Integer>{

	Intent intent;
	final Notification notification;
	final NotificationManager notificationManager;
	PendingIntent pendingInt;
	final PendingIntent pendingIntent;
	private SplashScreen mSplash;
	private Long progress=0L;

	public DescargarRecursos(SplashScreen mSplash) {

		this.mSplash=mSplash;
		intent = new Intent(mSplash, IstoeGalegoScreen.class);
		notification = new Notification(R.drawable.ic_launcher, "Descargando episodio", System.currentTimeMillis());
		notificationManager = (NotificationManager)mSplash.getApplicationContext().getSystemService("notification");
		pendingInt = PendingIntent.getActivity(mSplash.getApplicationContext(), 0, new Intent(), 0);
		pendingIntent = PendingIntent.getActivity(mSplash.getApplicationContext(), 0, this.intent, 0);


	}
	@Override
	protected Integer doInBackground(String... paramVarArgs)
	{
		this.notification.flags = (0x2 | this.notification.flags);
		this.notification.contentView = new RemoteViews(mSplash.getApplicationContext().getPackageName(), R.layout.download_progress);
		this.notification.contentIntent = this.pendingInt;
		this.notification.contentView.setImageViewResource(R.id.status_icon, R.drawable.ic_launcher);
		this.notification.contentView.setTextViewText(R.id.status_text, "Descargando cousas para: Isto é Galego");
		this.notification.contentView.setTextColor(R.id.status_text, Color.WHITE);
		this.notification.contentView.setProgressBar(R.id.status_progress, 100, 0, false);
		this.notificationManager.notify(42, this.notification);

//		mSplash.ultimo = saveFileFromUrl(paramVarArgs);
		//delete notificaition 
		this.notificationManager.cancel(42);

		return mSplash.indexRes;
	}

	@Override
	protected void onPostExecute(Integer paramInteger){
		SharedPreferences localSharedPreferences = mSplash.getSharedPreferences("ieg", 2);
		if (mSplash.ultimo){
			mSplash.editor = localSharedPreferences.edit();
			mSplash.editor.putInt("recursos", 1);
			mSplash.editor.commit();
			this.notification.flags = (0x10 | this.notification.flags);
			this.notification.contentView = new RemoteViews(mSplash.getApplicationContext().getPackageName(), 2130903043);
			this.notification.contentIntent = this.pendingIntent;
			this.notification.contentView.setImageViewResource(R.id.status_icon, R.drawable.ic_launcher);
			this.notification.contentView.setTextViewText(R.id.status_text,"Comezar o cursiño!!");
			this.notification.contentView.setProgressBar(R.id.status_progress, 100, 100, false);
			this.notificationManager.notify(42, this.notification);
			mSplash.splashfondo.setVisibility(8);
			mSplash.finish();
		}
	}

	@Override
	protected void onProgressUpdate(Long... paramVarArgs){
		if(progress==120L){
			//unziping
			this.notification.flags = (0x2 | this.notification.flags);
			this.notification.contentView = new RemoteViews(mSplash.getApplicationContext().getPackageName(), R.layout.download_progress);
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

//	private boolean saveFileFromUrl(String... paramVarArgs) {
//		int count;
//		try{
//			URL url = new URL(paramVarArgs[0]);
//			URLConnection conection = url.openConnection();
//			conection.setRequestProperty("Connection", "keep-alive");
//			conection.setDoOutput(true);
//			conection.connect();
//
//			// progress bar
//			int lenghtOfFile = conection.getContentLength();
//
//			// download the file
//			InputStream input = new BufferedInputStream(url.openStream(),32*1024);
//
//			File mFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.cuacfm.istoegalego.org/mp3/");
//			mFile.mkdirs();
//			File mFile2 = new File(mFile.getAbsolutePath(), "ieg.zip");
//
//			// Output stream
//			OutputStream output = new FileOutputStream(mFile2.toString());
//
//			byte data[] = new byte[32*1024];
//
//			long total = 0;
//
//			while ((count = input.read(data)) != -1) {
//				total += count;
//				// publishing the progress
//				progress = (total * 100) /lenghtOfFile;
//				Log.v("progress...",progress+"");
//				publishProgress((total * 100) /lenghtOfFile);
//
//				// writing data to file
//				output.write(data, 0, count);
//			}
//
//			// flushing output
//			output.flush();
//
//			// closing streams
//			output.close();
//			input.close();
//
//			//prepare notification to decompress
//			progress=120L;
//			publishProgress(120L);
//
//			//decompress file
//			String zipFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.cuacfm.istoegalego.org/mp3/ieg.zip"; 
//			String unzipLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.cuacfm.istoegalego.org/mp3/"; 
//
//			Decompress d = new Decompress(zipFile, unzipLocation); 
//			boolean unziprocess = d.unzip(this); 
//			//delete file if all gone well
//			if(mFile2.exists() && unziprocess){
//				mFile2.delete();
//			}
//
//			//set preferences to finish downnload and unzip process
//
//			SharedPreferences localSharedPreferences = mSplash.getSharedPreferences("ieg", 2);
//			mSplash.editor = localSharedPreferences.edit();
//			mSplash.editor.putInt("recursos", 1);
//			mSplash.editor.commit();
//
//
//			return true;
//
//		} catch (Exception e) {
//			Log.e("Error: ", e.getMessage());
//			return false;
//		}
//	}
//	
	public void progress(){
		progress=120L;
		publishProgress(120L);
	}
}

