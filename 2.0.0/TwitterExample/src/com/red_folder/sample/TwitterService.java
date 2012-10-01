package com.red_folder.sample;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.red_folder.phonegap.plugin.backgroundservice.BackgroundService;

public class TwitterService extends BackgroundService {

	private static final String TAG = TwitterService.class.getSimpleName();
	
	@Override
	protected JSONObject doWork() {
		if (newTweet()) { 
			Log.d(TAG, "New Tweet - I'll need to send a notification");
			showNotification("New Tweet","Click to open");
		} else
			Log.d(TAG, "No new Tweets, back to sleep I go");
			
		return null;
	}
	

	@Override
	protected JSONObject getConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected JSONObject initialiseLatestResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onTimerDisabled() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onTimerEnabled() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setConfig(JSONObject arg0) {
		// TODO Auto-generated method stub

	}
	
	private String getMaxID() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		return sp.getString("MaxID", "");
	}
	
	private void setMaxID(String maxID) {
		if (maxID != null) {
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = sp.edit();
			editor.putString("MaxID", maxID);
			editor.commit();
		}
	}

	private Boolean newTweet() {
		Boolean result = false;
		String oldMaxID = getMaxID();
		String newMaxID = oldMaxID;
		
		HttpClient httpClient;
		HttpGet getMethod;
		HttpResponse response;
		InputStream responseStream;
		try {
			httpClient = new DefaultHttpClient();
			getMethod = new HttpGet("http://search.twitter.com/search.json?q=phonegap&rpp=1&page1");
			response = httpClient.execute(getMethod);
			responseStream = response.getEntity().getContent();
		
			StringBuilder dataString = new StringBuilder();
			char[] buffer = new char[1024];
			Reader reader = new InputStreamReader(responseStream, "UTF-8");
			int charCount;
			while ((charCount = reader.read(buffer, 0, buffer.length)) > 0){
				dataString.append(buffer, 0, charCount);
			}
			
			JSONObject data = new JSONObject(dataString.toString());
		
			if (data.has("max_id_str")) {
				newMaxID = data.getString("max_id_str");

				if (!newMaxID.equals(oldMaxID)) {
					setMaxID(newMaxID);
					result = true;
				}
			}

		} catch (Exception ex) {
			// Do something with the error in production code
			Log.d(TAG, "ERROR");
			Log.d(TAG, ex.getMessage());
		} finally {
			// Close out the response stream and any open connections in production code
		}
		
		return result;
	}
	
	public void showNotification( String contentTitle, String contentText ) {
		int icon = R.drawable.ic_stat_notification;
        long when = System.currentTimeMillis();
        
        Notification notification = new Notification(icon, contentTitle, when);
		
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
		Intent notificationIntent = new Intent(this, TwitterExampleActivity.class);
		
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(this, contentTitle, contentText, contentIntent);
        
        NotificationManager nm = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, notification);
	}
	
}
