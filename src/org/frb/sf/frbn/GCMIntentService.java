/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.frb.sf.frbn;

import static org.frb.sf.frbn.CommonUtilities.SENDER_ID;
import static org.frb.sf.frbn.CommonUtilities.displayMessage;

import org.frb.sf.frbn.data.Message;
import org.frb.sf.frbn.data.MessageListActivity;
import org.frb.sf.frbn.data.MessageManager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

//    @SuppressWarnings("hiding")
    private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        displayMessage(context, getString(R.string.gcm_registered));
    	SharedPreferences appPreferences = getSharedPreferences("SETTINGS", MODE_PRIVATE);    	
    	String email = appPreferences.getString("EMAIL", "[no email!]");
        ServerUtilities.register(context, registrationId, email);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        displayMessage(context, getString(R.string.gcm_unregistered));
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            ServerUtilities.unregister(context, registrationId);
        } else {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
            Log.i(TAG, "Ignoring unregister callback");
        }
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        //String message = getString(R.string.gcm_message);
        //String message = getString(R.string.gcm_message) + ": " + intent.getExtras().getString("message");        
        
        String message = intent.getExtras().getString("message");
        String title = intent.getExtras().getString("title");
        String district = intent.getExtras().getString("district");
        String category = intent.getExtras().getString("category");
        String timestamp = intent.getExtras().getString("timestamp");
        
        //Date dt = new Date(Long.parseLong(timestamp, 10));
        
        MessageManager manager= new MessageManager(context);
        Message messageObj = new Message(message, title, category, district, Long.parseLong(timestamp));
        manager.addMessageItem(messageObj);
        
        displayMessage(context, message);
        // notifies user
        generateNotification(context, messageObj);
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        displayMessage(context, message);
        // notifies user
        //generateNotification(context, message);
    }

    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, Message message) {
        //int icon = R.drawable.ic_stat_gcm;
        //long when = System.currentTimeMillis();
    	
        Log.i(TAG, "Received message: " + message.toString());
        
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        //Intent notificationIntent = new Intent(context, MainActivity.class);
        Intent notificationIntent = new Intent(context, MessageListActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        
        Notification notification = new NotificationCompat.Builder(context)
        .setTicker("Message from FRB")
        .setAutoCancel(true)
        .setContentTitle(message.getTitle())
        .setContentText(message.getMessageBody())
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentIntent(intent)
        .build();        
        
//        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
        
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        /*
        Notification notification = new Notification(icon, message, when);
        String title = context.getString(R.string.app_name);
        Intent notificationIntent = new Intent(context, MainActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        */
        
        //notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }

}
