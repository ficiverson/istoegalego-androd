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

import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class LocalService extends Service{
	private int NOTIFICATION = 1043;
	private int actual;
	private String audio;
	private int avance;
	private Thread background;
	private int capi;
	protected SharedPreferences.Editor editor;
	private String leccion;
	private NotificationManager mNM;
	private boolean mediaready = false;
	MediaPlayer mp;
	private int posicion;
	private int total;
	private boolean mustNotificate=true;
	private Handler mHandler=new Handler() { 
		@Override 
		public void handleMessage(Message msg) { 
			if(mp!=null && mp.isPlaying()){
				Log.v("entradoen para el handler","sip");

				int i = total / 1000 % 60;
				int j = total / 60000 % 60;

				int p = mp.getCurrentPosition() / 1000 % 60;
				int q = mp.getCurrentPosition() / 60000 % 60;

				if (i < 10) {
					showNotification(q+":"+p+"/" + j + ":0" + i, leccion);
				}
				else{
					showNotification(q+":"+p+"/" + j + ":" + i, leccion);
				}
			}
		} 
	};

	private void showNotification(String paramString1, String paramString2){
		String str = "IEG " + paramString2;
		Notification localNotification = new Notification(R.drawable.ic_launcher, str, System.currentTimeMillis());
		Intent localIntent = new Intent(getApplicationContext(), CapituloScreen.class);
		localIntent.putExtra("leccion", this.posicion);
		localIntent.putExtra("servicio", true);
		localIntent.putExtra("capitulo", this.capi);
		PendingIntent localPendingIntent = PendingIntent.getActivity(this, 0, localIntent, 134217728);
		localNotification.flags = 32;
		localNotification.setLatestEventInfo(this, str, paramString1, localPendingIntent);
		this.mNM.notify(this.NOTIFICATION, localNotification);
	}

	public IBinder onBind(Intent paramIntent)
	{
		return null;
	}

	public void onCreate()
	{
		this.mp = new MediaPlayer();
		this.mp.setLooping(false);
	}

	public void onDestroy()
	{
		this.mediaready = false;
		this.mp.stop();
		this.mp.reset();
		this.mp.release();
		this.mp = null;
		mHandler.removeCallbacksAndMessages(null);
		this.mNM.cancelAll();
	}

	public void onStart(Intent paramIntent, int paramInt){
		this.audio = paramIntent.getStringExtra("audio");
		this.leccion = paramIntent.getStringExtra("leccion");
		this.avance = paramIntent.getIntExtra("avance", 0);
		this.posicion = paramIntent.getIntExtra("position", 0);
		this.capi = paramIntent.getIntExtra("capi", 0);
		Log.v("service", "Entrando en el onstrat");

		this.mNM = ((NotificationManager)getSystemService("notification"));

		new Thread(new Runnable() {
			public void run() {
				//set up MediaPlayer    

				try {   
					mp.reset();
					mp.setDataSource(audio);
					mp.prepare();
					mp.start();
					mp.seekTo(avance*1000);

				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch blockotification
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();

		mp.setOnPreparedListener(new OnPreparedListener() {

			public void onPrepared(MediaPlayer mp) {
				total = mp.getDuration();
			}
		});


		//start backgropund srvice for notification
		background =  new Thread(new Runnable() {

			public void run() {

				while(mustNotificate){
					Log.v("entrandpo para el handler","sip");
					try {
						Thread.sleep(400L);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					mHandler.sendMessage(new Message());

					if(mp==null || !mp.isPlaying()){
						mustNotificate=false;
					}
				}
			}
		});
		background.start();


	}

}
