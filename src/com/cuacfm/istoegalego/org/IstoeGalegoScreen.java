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


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class IstoeGalegoScreen extends Activity{

	private TextView capitulo;
	private TextView extras;
	private GridView gridview;
	private TextView subtitulo;
	private TextView titulo;
	public IstoeGalegoScreen mIsto;

	public void onCreate(Bundle paramBundle){
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		super.onCreate(paramBundle);
		setContentView(R.layout.main);

		mIsto=this;

		this.titulo = ((TextView)findViewById(R.id.titulo));
		this.subtitulo = ((TextView)findViewById(R.id.subtitulo));
		this.capitulo = ((TextView)findViewById(R.id.capitulo));
		this.extras = ((TextView)findViewById(R.id.extras));
		this.titulo.setTextColor(Color.WHITE);
		this.subtitulo.setTextColor(Color.WHITE);
		this.capitulo.setTextColor(R.drawable.fondoredondo);
		this.extras.setTextColor(Color.WHITE);
		this.gridview = ((GridView)findViewById(R.id.gridview));
		this.gridview.setAdapter(new MyAdapter(this));
		Typeface localTypeface = Typeface.createFromAsset(getAssets(), "fonts/vilamorena.ttf");
		this.titulo.setTypeface(localTypeface);
		this.subtitulo.setTypeface(localTypeface);
		this.capitulo.setTypeface(localTypeface);
		this.extras.setTypeface(localTypeface);
		this.extras.setOnTouchListener(new View.OnTouchListener(){
			public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent){
				if (paramAnonymousMotionEvent.getAction() == 0)
				{
					Log.v("tocado", "position esxtra");
					ScaleAnimation localScaleAnimation3 = new ScaleAnimation(1.0F, 1.2F, 1.0F, 1.2F, 1, 1.0F, 1, 0.0F);
					localScaleAnimation3.setFillAfter(true);
					localScaleAnimation3.setDuration(500L);
					extras.startAnimation(localScaleAnimation3);
				}
				if (paramAnonymousMotionEvent.getAction() == 2)
				{
					Log.v("laX", paramAnonymousMotionEvent.getX()+"");
					Log.v("Laay", paramAnonymousMotionEvent.getY()+"");
					if (paramAnonymousMotionEvent.getY() < 0.0F)
					{
						ScaleAnimation localScaleAnimation2 = new ScaleAnimation(1.2F, 1.0F, 1.2F, 1.0F, 1, 1.0F, 1, 0.0F);
						localScaleAnimation2.setFillAfter(true);
						localScaleAnimation2.setDuration(500L);
						extras.startAnimation(localScaleAnimation2);
					}
				}
				if ((paramAnonymousMotionEvent.getAction() == 1) && (paramAnonymousMotionEvent.getY() > 0.0F))
				{
					ScaleAnimation localScaleAnimation1 = new ScaleAnimation(1.2F, 1.0F, 1.2F, 1.0F, 1, 1.0F, 1, 0.0F);
					localScaleAnimation1.setFillAfter(true);
					localScaleAnimation1.setDuration(500L);
					extras.startAnimation(localScaleAnimation1);
					Intent localIntent = new Intent();
					localIntent.setClass(IstoeGalegoScreen.this, CapituloScreen.class);
					localIntent.putExtra("capitulo", 12);
					localIntent.putExtra("servicio", false);
					localIntent.addFlags(67108864);
					startActivity(localIntent);
				}
				return true;
			}
		});
	}

	public class MyAdapter
	extends BaseAdapter
	{
		private Context context;
		private String[] texts = new String[12];

		public MyAdapter(Context paramContext)
		{
			texts[0] = "1";
			texts[1] = "2";
			texts[2] = "3";
			texts[3] = "4";
			texts[4] = "5";
			texts[5] = "6";
			texts[6] = "7";
			texts[7] = "8";
			texts[8] = "9";
			texts[9] = "10";
			texts[10] = "11";
			texts[11] = "12";
			this.context = paramContext;
		}

		public int getCount()
		{
			return this.texts.length;
		}

		public Object getItem(int paramInt)
		{
			return null;
		}

		public long getItemId(int paramInt)
		{
			return 0;
		}

		@SuppressLint("ViewHolder")
		public View getView(final int position, View paramView, ViewGroup paramViewGroup){
			View localView = getLayoutInflater().inflate(R.layout.capitulos, null);
			TextView localTextView = (TextView)localView.findViewById(R.id.textitem);
			localTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/vilamorena.ttf"));
			localTextView.setTextColor(-1);
			localTextView.setText(this.texts[position]);
			localView.setOnTouchListener(new OnTouchListener() {

				public boolean onTouch(View v, MotionEvent event) {
					Intent localIntent = new Intent();
					localIntent.setClass(mIsto, CapituloScreen.class);
					localIntent.putExtra("capitulo", position);
					localIntent.putExtra("servicio", false);
					localIntent.addFlags(67108864);
					mIsto.startActivity(localIntent);
					Log.v("tocado", position+"");
					return false;
				}
			});
			return localView;
		}
	}
}
