package com.malam.whatsdelet.nolastseen.hiddenchat.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.malam.whatsdelet.nolastseen.hiddenchat.Activities.MainActivity;
import com.malam.whatsdelet.nolastseen.hiddenchat.Activities.Promotion_act;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.Shared;
import com.whatsdelete.Test.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Random;

import static android.content.ContentValues.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    Map<String, String> data;
    String package_name = null,promo_icon_path,promo_banner_path;
    RemoteMessage my_RemoteMessage;
    Bitmap icon,banner_image;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        my_RemoteMessage = remoteMessage;
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        data = remoteMessage.getData();

        icon=null;
        banner_image=null;
        promo_banner_path=null;
        promo_icon_path=null;
        try {
            package_name = data.get("promo_package_name");
        } catch (NullPointerException asd) {
        }
        try {
            promo_icon_path = data.get("promo_app_icon");
        } catch (NullPointerException asd) {
        }
        try {
            promo_banner_path = data.get("promo_app_banner");
        } catch (NullPointerException asd) {
        }
        if (promo_banner_path!=null)
        banner_image=getBitmapFromURL(promo_banner_path);

        if (promo_icon_path!=null)
        icon=getBitmapFromURL(promo_icon_path);

        if (package_name != null) { }
            if (my_RemoteMessage.getNotification().getTitle().equalsIgnoreCase("whatsdelet WhatsApp")) {
                if (Shared.getInstance().getIntValueFromPreference(getResources().getString(R.string.total_unread_msgs_pref), 0, getApplicationContext()) > 0)
                sendNotification(my_RemoteMessage.getNotification().getTitle(), my_RemoteMessage.getNotification().getBody());

            } else {
                sendNotification_promo(""+my_RemoteMessage.getNotification().getTitle(), my_RemoteMessage.getNotification().getBody(), package_name,banner_image,icon);

            }
       // sendNotification_promo("Promo "+my_RemoteMessage.getNotification().getTitle(), my_RemoteMessage.getNotification().getBody(), package_name);



        /*try {
            if (Shared.getInstance().getIntValueFromPreference(getResources().getString(R.string.total_unread_msgs_pref), 0, getApplicationContext()) > 0)
                sendNotification(my_RemoteMessage.getNotification().getTitle(), my_RemoteMessage.getNotification().getBody());
        } catch (NullPointerException asd) {
        } catch (Exception asd) {
        }*/
        // Check if message contains a data payload.
        if (my_RemoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + my_RemoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (my_RemoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + my_RemoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.


    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }

    private void scheduleJob() {
        // [START dispatch_job]

      /*  OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
                .build();
        WorkManager.getInstance().beginWith(work).enqueue();*/
        // [END dispatch_job]
    }
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    private void sendNotification(String title, String messageBody) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.app_name);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    getResources().getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Random r = new Random();
        int random_int = r.nextInt(999 - 28) + 28;
        notificationManager.notify(random_int /* ID of notification */, notificationBuilder.build());
        System.out.println("whatsdelet App Notification");
    }

    private void sendNotification_promo(String title, String messageBody, String package_name, Bitmap promo_image, Bitmap promo_icon) {
        Shared.getInstance().saveStringToPreferences(getResources().getString(R.string.promotion_package_name_pref), package_name, getApplicationContext());
        Intent intent = new Intent(this, Promotion_act.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("package_name", package_name);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.app_name);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(icon)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(promo_image)
                        .bigLargeIcon(promo_icon))
                        .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    getResources().getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        Random r = new Random();
        int random_int = r.nextInt(999 - 28) + 28;
        notificationManager.notify(random_int /* ID of notification */, notificationBuilder.build());
        System.out.println("whatsdelet Promo Notification");

    }

}
