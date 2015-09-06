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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SplashScreen extends Activity{
	protected boolean _active = true;
	private int _download;
	protected int _splashTime = 1000;
	public int indexRes =1;
	public List<String> direcciones = new ArrayList<String>();
	protected SharedPreferences.Editor editor;
	private Button splashdescarga;
	private TextView splashexp;
	public RelativeLayout splashfondo;
	private TextView splashsubtitulo;
	private TextView splashtitulo;
	public boolean ultimo = false;
	private SplashScreen mSplash;
	public DescargarRecursos descargarRecursos;
	private NotificationManager notificationManager;

	public void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		setContentView(R.layout.splash);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mSplash=this;

		descargarRecursos = new DescargarRecursos(mSplash);

		this._download = getSharedPreferences("ieg", 2).getInt("recursos", 0);
		File localFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.cuacfm.istoegalego.org/mp3/");
		if (localFile.exists()){
			if (localFile.listFiles().length < 13) {
				this._download = 0;//file already exists but we must download
				//launch asyn task
				DescargarUrlsBase dub = new DescargarUrlsBase(mSplash);
				dub.execute();
			}
			else if(localFile.listFiles().length == 13){
				this._download =1;
				//cancel previsous notif
				notificationManager = (NotificationManager)mSplash.getApplicationContext().getSystemService("notification");
				notificationManager.cancel(42);
			}
		}
		else{
			Log.v("", this._download+"");
			//launch asyn task
			DescargarUrlsBase dub = new DescargarUrlsBase(mSplash);
			dub.execute();
		}

		if(_download==1){

			new CountDownTimer(_splashTime, 400) {//CountDownTimer(edittext1.getText()+edittext2.getText()) also parse it to long

				public void onTick(long millisUntilFinished) {
				}

				public void onFinish() {
					//if we dont need to download resources just open in one second
					finish();
					Intent intent = new Intent();
					intent.setClass(mSplash, IstoeGalegoScreen.class);
					startActivity(intent);
					finish();
				}
			}
			.start();

		}
	}
	public void setUI(){
		//we have all necesarry resources
		this.splashfondo = ((RelativeLayout)findViewById(R.id.splashfondo));
		this.splashtitulo = ((TextView)findViewById(R.id.splashtitulo));
		this.splashsubtitulo = ((TextView)findViewById(R.id.splashsubtitulo));
		this.splashexp = ((TextView)findViewById(R.id.splashexp));
		this.splashdescarga = ((Button)findViewById(R.id.splashdescarga));
		this.splashdescarga.setOnTouchListener(new View.OnTouchListener()
		{
			public boolean onTouch(View paramAnonymousView, MotionEvent motionEvent)
			{
				if (motionEvent.getAction() == 0)
				{
					Log.v("tocado", "position esxtra");
					ScaleAnimation localScaleAnimation3 = new ScaleAnimation(1.0F, 1.2F, 1.0F, 1.2F, 1, 1.0F, 1, 0.0F);
					localScaleAnimation3.setFillAfter(true);
					localScaleAnimation3.setDuration(500L);
					splashdescarga.startAnimation(localScaleAnimation3);
				}
				if (motionEvent.getAction() == 2)
				{
					Log.v("laX", motionEvent.getX()+"");
					Log.v("Laay", motionEvent.getY()+"");
					if (motionEvent.getY() < 0.0F)
					{
						ScaleAnimation scaleAnimation2 = new ScaleAnimation(1.2F, 1.0F, 1.2F, 1.0F, 1, 1.0F, 1, 0.0F);
						scaleAnimation2.setFillAfter(true);
						scaleAnimation2.setDuration(500L);
						splashdescarga.startAnimation(scaleAnimation2);
					}
				}
				if ((motionEvent.getAction() == 1) && (motionEvent.getY() > 0.0F))
				{
					ScaleAnimation scaleAnimation1 = new ScaleAnimation(1.2F, 1.0F, 1.2F, 1.0F, 1, 1.0F, 1, 0.0F);
					scaleAnimation1.setFillAfter(true);
					scaleAnimation1.setDuration(500L);
					splashdescarga.startAnimation(scaleAnimation1);
					//launch service to unzip and download file
					Intent intent = new Intent(Intent.ACTION_SYNC, null, mSplash, DownloadService.class);
					intent.putExtra("url", direcciones.get(0));
					intent.putExtra("requestId", 101);

					startService(intent);
				}
				return true;
			}
		});
		Typeface tTypeface = Typeface.createFromAsset(getAssets(), "fonts/vilamorena.ttf");
		this.splashtitulo.setTypeface(tTypeface);
		this.splashsubtitulo.setTypeface(tTypeface);
		this.splashexp.setTypeface(tTypeface);
		this.splashdescarga.setTypeface(tTypeface);
		this.splashfondo.setVisibility(0);
	}

	public boolean onTouchEvent(MotionEvent paramMotionEvent)
	{
		if (paramMotionEvent.getAction() == 0) {
			this._active = false;
		}
		return true;
	}
}
