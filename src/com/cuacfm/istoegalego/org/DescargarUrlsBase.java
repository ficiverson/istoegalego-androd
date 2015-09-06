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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class DescargarUrlsBase extends AsyncTask<Void, Void, String>{

	private SplashScreen mSplash;

	public DescargarUrlsBase(SplashScreen mSplash){

		this.mSplash=mSplash;
	}

	protected String doInBackground(Void... paramVarArgs){


		URL mURl = null;
		String json =null;
		try{
			mURl = new URL("http://programacion.cuacfm.org/android/ieg/recursosV2.json");
			
			URLConnection urlConnection = mURl.openConnection();
			InputStream mInput = urlConnection.getInputStream();

			BufferedReader r = new BufferedReader(new InputStreamReader(mInput));
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
				total.append(line);
			}
			json = total.toString();
			Log.v("json",json);
		}
		catch(Exception e){
			Log.v("exception contacting cuacfm /android/ieg/recursosV2.json",e.toString());
		}



		return json;
	}

	protected void onPostExecute(String json){


		if(json==null){
			//toast error con la red
			Toast toast = Toast.makeText(mSplash, "Error con la red inténtalo más tarde. Gracias", Toast.LENGTH_SHORT);
			toast.show();
		}
		else{
			//set interface
			try{
				JSONObject main = new JSONObject(json);
				JSONArray mArray = main.getJSONArray("audios_mp3");
				for(int i=0;i<mArray.length();i++){
					mSplash.direcciones.add(mArray.getString(i));
				}
				mSplash.setUI();
			}
			catch(Exception e){
				//toast error receiving data
				Toast toast = Toast.makeText(mSplash, "Error inesperado inténtalo más tarde.", Toast.LENGTH_SHORT);
				toast.show();
			}
		}
	}


}

