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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class CapituloScreen extends Activity{

	private TextView agora;
	private String audio;
	private List<Integer> avances =  new ArrayList<Integer>();
	private int capitulo;
	private GridView gridview2;
	private MyAdapter grilla;
	private Boolean[] playing = new Boolean[] {false,false,false,false,false,false,false,false,false,false,false,false};
	private TextView subtitulocap;
	private List<String> texts = new ArrayList<String>();
	private TextView titulocap;
	private TextView tvi;

	public void calcularAvances(){

		try{

			InputStream mInputStream = getResources().openRawResource(R.raw.lecciones);
			String json=null;
			BufferedReader r = new BufferedReader(new InputStreamReader(mInputStream));
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
				total.append(line);
			}
			json = total.toString();
			Log.v("json",json);

			JSONObject main = new JSONObject(json);
			JSONArray mArray = main.getJSONArray("tiempo");
			for(int i=0;i<mArray.length();i++){
				JSONObject mObject = mArray.getJSONObject(i);
				if(i==capitulo){
					int key=capitulo+1;
					for(int j=0;j<mObject.getJSONArray(String.valueOf(key)).length();j++){
						avances.add(mObject.getJSONArray(String.valueOf(key)).getInt(j));
					}
				}
			}
		}
		catch(Exception e){

		}
	}

	public void calcularTextos(){

		try{

			InputStream mInputStream = getResources().openRawResource(R.raw.lecciones);
			String json=null;
			BufferedReader r = new BufferedReader(new InputStreamReader(mInputStream));
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
				total.append(line);
			}
			json = total.toString();
			Log.v("json",json);

			JSONObject main = new JSONObject(json);
			JSONArray mArray = main.getJSONArray("nombre");
			for(int i=0;i<mArray.length();i++){
				JSONObject mObject = mArray.getJSONObject(i);
				if(i==capitulo){
					int key = capitulo+1;
					for(int j=0;j<mObject.getJSONArray(String.valueOf(key)).length();j++){
						texts.add(mObject.getJSONArray(String.valueOf(key)).getString(j));
					}
				}
			}
		}
		catch(Exception e){

		}
	}




	@Override
	public void onCreate(Bundle paramBundle){

		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		super.onCreate(paramBundle);
		setContentView(R.layout.capitulo);
		Bundle localBundle = getIntent().getExtras();
		this.capitulo = localBundle.getInt("capitulo");
		Arrays.fill(this.playing, Boolean.FALSE);

		if (localBundle.getBoolean("servicio")){
			int j = localBundle.getInt("leccion", 0);
			this.playing[j] = true;
		}
		Log.v("ImprimiendoCAPIUTULO", this.capitulo+"");
		calcularTextos();
		calcularAvances();
		this.titulocap = ((TextView)findViewById(R.id.titulocap));
		this.subtitulocap = ((TextView)findViewById(R.id.subtitulocap));
		this.tvi = ((TextView)findViewById(R.id.textitem2r));
		this.agora = ((TextView)findViewById(R.id.ahora));
		this.titulocap.setTextColor(Color.WHITE);
		this.subtitulocap.setTextColor(Color.WHITE);
		this.tvi.setTextColor(R.drawable.fondoredondo);
		this.agora.setTextColor(R.drawable.fondoredondo);
		Typeface localTypeface = Typeface.createFromAsset(getAssets(), "fonts/vilamorena.ttf");
		this.tvi.setTypeface(localTypeface);
		this.titulocap.setTypeface(localTypeface);
		this.subtitulocap.setTypeface(localTypeface);
		this.agora.setTypeface(localTypeface);
		this.gridview2 = ((GridView)findViewById(R.id.gridview2));
		this.grilla = new MyAdapter(this);
		this.gridview2.setAdapter(this.grilla);
		File localFile = Environment.getExternalStorageDirectory();
		int i = 1 + this.capitulo;
		this.tvi.setText("Capitulo: " + i);
		this.audio = new File(localFile.getAbsolutePath() + "/Android/data/com.cuacfm.istoegalego.org/mp3/" + i + ".mp3").getAbsolutePath();
	}

	public void onResume()
	{
		super.onResume();
	}

	public class MyAdapter
	extends BaseAdapter
	{
		private Context context;

		public MyAdapter(Context paramContext)
		{
			this.context = paramContext;
		}

		public int getCount()
		{
			return texts.size();
		}

		public Object getItem(int paramInt)
		{
			return null;
		}

		public long getItemId(int paramInt)
		{
			return 0L;
		}

		public View getView(final int paramInt, View paramView, ViewGroup paramViewGroup)
		{
			View localView = getLayoutInflater().inflate(R.layout.capitulos2, null);
			TextView localTextView = (TextView)localView.findViewById(R.id.textitem2);
			final ImageView localImageView = (ImageView)localView.findViewById(R.id.play);
			localTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/vilamorena.ttf"));
			localTextView.setTextColor(-1);
			localTextView.setGravity(112);
			localTextView.setText(texts.get(paramInt));
			localView.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {

					if(!playing[paramInt]){
						Intent localIntent2 = new Intent(context, LocalService.class);
						localIntent2.putExtra("audio", audio);
						localIntent2.putExtra("position", paramInt);
						localIntent2.putExtra("avance", avances.get(paramInt));
						localIntent2.putExtra("leccion", texts.get(paramInt));
						localIntent2.putExtra("capi", capitulo);
						startService(localIntent2); 
						
						if (texts.get(paramInt).length() > 15) {
							agora.setText(texts.get(paramInt).subSequence(0, 15) + "...");
						}
						else{
							agora.setText(texts.get(paramInt));
						}
						
						//put other element to init mode
						for(int i = 0;i<playing.length-1;i++){
							playing[i]=false;
						}
						localImageView.setBackgroundResource(R.drawable.stop);
						playing[paramInt]=true;
						notifyDataSetChanged();
					}
					else{
						localImageView.setBackgroundResource(R.drawable.play);
						playing[paramInt]=false;
						agora.setText("Elixe unha lección");
						Intent i = new Intent(context,LocalService.class);
						stopService(i);
					}
				}
			});
			if (!playing[paramInt]){
				localImageView.setBackgroundResource(R.drawable.play);
			}
			else{
				//current playing
				localImageView.setBackgroundResource(R.drawable.stop);
			}

			return localView;
		}
	}
}

