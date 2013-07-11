package com.guillaumecendre.android.launcher;

import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import android.os.Vibrator;
import android.graphics.Matrix;
import android.os.Bundle;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

    private Vibrator myVib;
	
	@Override	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

		Button settingsButton = (Button)findViewById(R.id.settingsButton);
		Button homeButton = (Button)findViewById(R.id.homeButton);
		Button photoButton = (Button)findViewById(R.id.photoButton);
		
		settingsButton.setHapticFeedbackEnabled(true);
		homeButton.setHapticFeedbackEnabled(true);
		photoButton.setHapticFeedbackEnabled(true);
		
		settingsButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Log.e("Click event", "Settings button tapped");
				myVib.vibrate(50);
			}			
		});
		
		homeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Log.e("Click event", "Home button tapped");
				myVib.vibrate(50);
			}			
		});
		
		photoButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Log.e("Click event", "Photo button tapped");
				myVib.vibrate(50);
			}			
		});
		
		
		ImageView view1Object = (ImageView)findViewById(R.id.view1);
		
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		
		int screenHeight = size.y;
		int screenWidth = size.x;
		
		
		BitmapFactory.Options bitmapOpts = new BitmapFactory.Options();
		bitmapOpts.inSampleSize = 2;
		Bitmap firstLoadedBmp = BitmapFactory.decodeResource(getResources(), R.drawable.ls, bitmapOpts);
		
		/*Bitmap scaledBitmap = resizeBitmapWhileKeepingRatio(firstLoadedBmp, screenHeight);
		firstLoadedBmp.recycle();
		Bitmap croppedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, screenWidth, screenHeight);
		scaledBitmap.recycle();
		
		if (firstLoadedBmp.getHeight() > screenHeight) {
			//croppedBitmap = Bitmap.createBitmap(firstLoadedBmp, 0, 0, widthToTake, screenHeight);
		} else {
			//croppedBitmap = Bitmap.createScaledBitmap(firstLoadedBmp, screenHeight+(), screenHeight, true);
		}*/
		
		Bitmap blurredBmp = this.fastblur(firstLoadedBmp, 5);
		
		firstLoadedBmp.recycle();
		view1Object.setImageBitmap(blurredBmp);
		
		
		TextView textView1Object = (TextView)findViewById(R.id.textView1);
		TextView textView2Object = (TextView)findViewById(R.id.textView2);		
		TextView textView3Object = (TextView)findViewById(R.id.textView3);		
		
		textView2Object.setText(getUsername()+"'s phone");
		
		
		Calendar calendar1 = Calendar.getInstance();
		String hourOfDay = "00";
		String minute = "00";
		if (calendar1.get(Calendar.MINUTE) < 10) {
			minute = "0" + calendar1.get(Calendar.MINUTE);
		} else {
			minute = "" + calendar1.get(Calendar.MINUTE);
		}		
		
		hourOfDay = "" + calendar1.get(Calendar.HOUR_OF_DAY);
		textView1Object.setText(hourOfDay + ":" + minute);
		
		
		
		Typeface latoLight = Typeface.createFromAsset(getAssets(), "Lato-Light.ttf");
		Typeface latoHairline = Typeface.createFromAsset(getAssets(), "Lato-Hairline.ttf");
		textView1Object.setTypeface(latoHairline);
		textView2Object.setTypeface(latoLight);		
		textView3Object.setTypeface(latoLight);		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	public Bitmap resizeBitmapWhileKeepingRatio(Bitmap srcBitmap, int i_height) {
		
		int oldHeight = srcBitmap.getHeight();
		int scaleFactor = i_height / oldHeight;
		
		int newWidth = srcBitmap.getWidth() * scaleFactor;
		int newHeight = oldHeight * scaleFactor;
		
		Bitmap resizedBmp = Bitmap.createScaledBitmap(srcBitmap, newWidth, newHeight, true);
		
		return resizedBmp;
	}
	
	
	public String getUsername(){
	    AccountManager manager = AccountManager.get(this); 
	    Account[] accounts = manager.getAccountsByType("com.google"); 
	    List<String> possibleEmails = new LinkedList<String>();

	    for (Account account : accounts) {
	      // TODO: Check possibleEmail against an email regex or treat
	      // account.name as an email address only for certain account.type values.
	      possibleEmails.add(account.name);
	    }

	    if(!possibleEmails.isEmpty() && possibleEmails.get(0) != null){
	        String email = possibleEmails.get(0);
	        String[] parts = email.split("@");
	        if(parts.length > 0 && parts[0] != null)
	            return parts[0];
	        else
	            return null;
	    }else
	        return null;
	}
	
	
	
	public Bitmap fastblur(Bitmap sentBitmap, int radius) {


        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        sentBitmap.recycle();
        
        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = ( 0xff000000 & pix[yi] ) | ( dv[rsum] << 16 ) | ( dv[gsum] << 8 ) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }
	

}
