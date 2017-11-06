package com.plugin.gcm;


import java.io.IOException;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.media.AudioManager;

import android.content.res.AssetFileDescriptor;

import com.smartfmone.smartfmone.v1.R;

public class PushHandlerActivity extends Activity
{
	private static String TAG = "PushHandlerActivity"; 

	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate");

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        
		
		
		
				Bundle extrass = getIntent().getExtras();
				Bundle originalExtras = extrass.getBundle("pushBundle");
				
				String PushadditionalData= originalExtras.getString("additionalData");
		
		if(PushadditionalData.equals("Security"))		
		{
			final MediaPlayer ring = MediaPlayer.create(getApplicationContext(), R.raw.silent);
			ring.setVolume(1.0f,1.0f);
			/*
			AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE); 
			int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
			
			
			int maxVolume = 50;
			float log1=(float)(Math.log(maxVolume-currentVolume)/Math.log(maxVolume)); 
			audio.setVolume(1-log1);
			*/
			
			
			ring.setLooping(false);        
			ring.start();
			
			
			
			 new AlertDialog.Builder(this).setMessage(originalExtras.getString("message")).setNeutralButton("Close", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
				ring.stop();
				//forceMainActivityReload();
finish();

            }
        }).show();
		
		}
		else
		{
			final MediaPlayer ring = MediaPlayer.create(getApplicationContext(), R.raw.siren); 
/*
			AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE); 
			int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
			
			int maxVolume = 50;
			
			float log1=(float)(Math.log(maxVolume-currentVolume)/Math.log(maxVolume)); 
			audio.setVolume(1-log1);	
*/
			ring.setVolume(1.0f,1.0f);
			ring.setLooping(true);        
			ring.start();
			
			
				
				//originalExtras.getString("message");
			 new AlertDialog.Builder(this).setMessage(""+originalExtras.getString("message")).setNeutralButton("Close", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
				ring.stop();
				//forceMainActivityReload();
finish();

            }
        }).show();
		}
		
		
		boolean isPushPluginActive = PushPlugin.isActive();
		processPushBundle(isPushPluginActive);

		

		if (!isPushPluginActive) {
			//forceMainActivityReload();
		}
	}

	/**
	 * Takes the pushBundle extras from the intent, 
	 * and sends it through to the PushPlugin for processing.
	 */
	private void processPushBundle(boolean isPushPluginActive)
	{
		Bundle extras = getIntent().getExtras();

		if (extras != null)	{
			Bundle originalExtras = extras.getBundle("pushBundle");
            
            originalExtras.putBoolean("foreground", false);
            originalExtras.putBoolean("coldstart", !isPushPluginActive);

			PushPlugin.sendExtras(originalExtras);
		}
	}

	/**
	 * Forces the main activity to re-launch if it's unloaded.
	 */
	private void forceMainActivityReload()
	{
		PackageManager pm = getPackageManager();
		Intent launchIntent = pm.getLaunchIntentForPackage(getApplicationContext().getPackageName());    		
		startActivity(launchIntent);
	}

  @Override
  protected void onResume() {
    super.onResume();
    final NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.cancelAll();
  }

}
