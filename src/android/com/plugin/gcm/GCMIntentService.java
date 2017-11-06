package com.plugin.gcm;

import org.json.JSONException;
import org.json.JSONObject;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.net.Uri;
import android.app.PendingIntent.CanceledException;
import android.provider.Settings;
import android.media.MediaPlayer;

import com.smartfmone.smartfmone.v1.R;

import com.google.android.gcm.GCMBaseIntentService;

@SuppressLint("NewApi")
public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";
	
	public GCMIntentService() {
		super("GCMIntentService");
	}

	@Override
	public void onRegistered(Context context, String regId) {

		Log.v(TAG, "onRegistered: "+ regId);

		JSONObject json;

		try
		{
			json = new JSONObject().put("event", "registered");
			json.put("regid", regId);

			Log.v(TAG, "onRegistered: " + json.toString());

			// Send this JSON data to the JavaScript application above EVENT should be set to the msg type
			// In this case this is the registration ID
			PushPlugin.sendJavascript( json );

		}
		catch( JSONException e)
		{
			// No message to the user is sent, JSON failed
			Log.e(TAG, "onRegistered: JSON exception");
		}
	}

	@Override
	public void onUnregistered(Context context, String regId) {
		Log.d(TAG, "onUnregistered - regId: " + regId);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.d(TAG, "onMessage - context: " + context);

		// Extract the payload from the message
		Bundle extras = intent.getExtras();
		if (extras != null)
		{
			// if we are in the foreground, just surface the payload, else post it to the statusbar
            if (PushPlugin.isInForeground()) {
				extras.putBoolean("foreground", true);
                //PushPlugin.sendExtras(extras);
				createNotification(context, extras);
			}
			else {
				extras.putBoolean("foreground", false);

                // Send a notification if there is a message
                if (extras.getString("message") != null && extras.getString("message").length() != 0) {
                    createNotification(context, extras);
					
					Context context2 = getApplicationContext();

                    PendingIntent pendingIntent;
                    Intent notifyIntent = new Intent();
                    notifyIntent.setClass(context2, com.plugin.gcm.PushHandlerActivity.class);
					notifyIntent.putExtra("pushBundlee", extras);
                    pendingIntent =  PendingIntent.getActivity(context2, 0, notifyIntent, 0);
                    try {
                      pendingIntent.send();
                  } catch (CanceledException e1) {
                      // TODO Auto-generated catch block
                      e1.printStackTrace();
                  }
                }
            }
        }
	}

	public void createNotification(Context context, Bundle extras)
	{
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		String appName = getAppName(this);

		Intent notificationIntent = new Intent(this, PushHandlerActivity.class);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		notificationIntent.putExtra("pushBundle", extras);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean vibration = app_preferences.getBoolean("com.plugin.gcm.vibration", true);
        boolean sound = app_preferences.getBoolean("com.plugin.gcm.sound", true);
        boolean light = app_preferences.getBoolean("com.plugin.gcm.light", true);
        String ringtonePath = app_preferences.getString("com.plugin.gcm.ringtone", "defValue");
        int defaults = 0;
        if (vibration) defaults = defaults | Notification.DEFAULT_VIBRATE;
        //if (sound) defaults = defaults | Notification.DEFAULT_SOUND;
        if (light) defaults = defaults | Notification.DEFAULT_LIGHTS;
        Log.v(TAG, new Integer(defaults).toString());

		
		NotificationCompat.Builder mBuilder =
			new NotificationCompat.Builder(context)				
				.setSmallIcon(context.getApplicationInfo().icon)
				.setWhen(System.currentTimeMillis())
				.setContentTitle(extras.getString("title"))
				.setTicker(extras.getString("title"))
				.setContentText(extras.getString("message"))
				.setContentIntent(contentIntent)
				.setAutoCancel(true);
		int notId = 0;
        if (!ringtonePath.equals("") && sound) {
			
			if(ringtonePath.equals("defValue"))
        	{
				/*
        		final MediaPlayer mp = MediaPlayer.create(this, Settings.System.DEFAULT_NOTIFICATION_URI); 
        		mp.start();
				*/
				
				String PushadditionalData= extras.getString("additionalData");
				if(PushadditionalData.equals("Security"))
				{
					//mNotificationManager.notify((String) appName, notId, mBuilder.build());
					Uri sounds=Uri.parse("android.resource://"+context.getPackageName()+"/"+R.raw.silent);
					mBuilder.setSound(sounds);
					mNotificationManager.notify(appName, notId, mBuilder.build());
				}
				else
				{
					Uri sounds=Uri.parse("android.resource://"+context.getPackageName()+"/"+R.raw.siren);
					mBuilder.setSound(sounds);
					mNotificationManager.notify(appName, notId, mBuilder.build());
				}        		
        	}
        	else
        	{
				/*
				Uri uri = Uri.parse(ringtonePath);
				Log.v(TAG, "Playing Sound: " + uri.toString());
				Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), uri);
				r.play();
				*/
				
				String PushadditionalData= extras.getString("additionalData");
				if(PushadditionalData.equals("Security"))
				{					
					//mNotificationManager.notify((String) appName, notId, mBuilder.build());
					Uri sounds=Uri.parse("android.resource://"+context.getPackageName()+"/"+R.raw.silent);
					mBuilder.setSound(sounds);
					mNotificationManager.notify(appName, notId, mBuilder.build());
				}
				else
				{
					Uri sounds=Uri.parse("android.resource://"+context.getPackageName()+"/"+R.raw.siren);
					mBuilder.setSound(sounds);
					mNotificationManager.notify(appName, notId, mBuilder.build());
				}				
        	}
			
			//mBuilder.setSound(null);
        }

		String message = extras.getString("message");
		if (message != null) {
			mBuilder.setContentText(message);
		} else {
			mBuilder.setContentText("<missing message content>");
		}

		String msgcnt = extras.getString("msgcnt");
		if (msgcnt != null) {
			mBuilder.setNumber(Integer.parseInt(msgcnt));
		}

		
		
		try {
			notId = Integer.parseInt(extras.getString("notId"));
		}
		catch(NumberFormatException e) {
			Log.e(TAG, "Number format exception - Error parsing Notification ID: " + e.getMessage());
		}
		catch(Exception e) {
			Log.e(TAG, "Number format exception - Error parsing Notification ID" + e.getMessage());
		}
		
		/*
		mNotificationManager.notify((String) appName, notId, mBuilder.build());	*/
	}
	 

	private static String getAppName(Context context)
	{
		CharSequence appName = 
				context
					.getPackageManager()
					.getApplicationLabel(context.getApplicationInfo());
		
		return (String)appName;
	}
	
	@Override
	public void onError(Context context, String errorId) {
		Log.e(TAG, "onError - errorId: " + errorId);
	}

}
