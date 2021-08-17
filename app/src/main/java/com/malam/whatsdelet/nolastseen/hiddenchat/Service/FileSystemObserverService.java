package com.malam.whatsdelet.nolastseen.hiddenchat.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.FileObserver;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.malam.whatsdelet.nolastseen.hiddenchat.Activities.Lock_Screen_QW;
import com.malam.whatsdelet.nolastseen.hiddenchat.Activities.MainActivity;
import com.malam.whatsdelet.nolastseen.hiddenchat.Model.Music_model;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.Shared;
import com.whatsdelete.Test.R;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.os.FileObserver.CREATE;
import static android.os.FileObserver.DELETE;
import static android.os.FileObserver.MOVED_TO;
import static com.malam.whatsdelet.nolastseen.hiddenchat.Activities.MainActivity.file_old;

public class FileSystemObserverService extends Service {
    String ImagesinternalPath, VoicesinternalPath, VideosinternalPath, AudiosinternalPath, DocumentsinternalPath;
    Context context;
    Random rand = new Random();
    FileObserver fo1;
    public List<Music_model> muList = new ArrayList<>();
    FixedFileObserver fixedFileObserverImages;
    FixedFileObserverVideo fixedFileObserverVideos;
    FixedFileObserverVoice fixedFileObserverVoice;
    FixedFileObserverAudio fixedFileObserverAudios;
    FixedFileObserverDocuments fixedFileObserverDocuments;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private String getfolder(File dir) {
        int name = 0;
        File[] listFile = dir.listFiles();
        String returnr = listFile[0].toString();
        for (int i = 0; i < listFile.length; i++) {
            String name_string = listFile[i].toString();
            String[] index = name_string.split("/");
            if (((index[index.length - 1])).matches("\\d+(?:\\.\\d+)?")) {
                if (name < Integer.parseInt(index[index.length - 1])) {
                    name = Integer.parseInt(index[index.length - 1]);
                    returnr = listFile[i].toString();
                }
            }
        }
        return returnr;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                String NOTIFICATION_CHANNEL_ID = getPackageName();
                String channelName = getResources().getString(R.string.app_name);
                NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
                chan.setLightColor(Color.BLUE);
                chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.createNotificationChannel(chan);
                Intent contact_intent = new Intent(context, MainActivity.class);
                if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.change_pass_pref), false, getApplicationContext())) {
                    contact_intent = new Intent(context, Lock_Screen_QW.class);
                    contact_intent.putExtra(getResources().getString(R.string.open_main), false);
                    contact_intent.putExtra(getResources().getString(R.string.open_mainactivity), true);
                    contact_intent.putExtra(getResources().getString(R.string.open_insta_act), false);
                } else {
                    contact_intent = new Intent(context, MainActivity.class);
                    contact_intent.putExtra(getResources().getString(R.string.is_image_intent), 0);
                }

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addNextIntent(contact_intent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
                Notification notification = notificationBuilder.setOngoing(true)
                        .setSmallIcon(R.mipmap.ic_launcher_noti)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setContentText(getResources().getString(R.string.you_will_be_notify_when_status_avalible))
                        .setPriority(NotificationManager.IMPORTANCE_MIN)
                        .setCategory(Notification.CATEGORY_SERVICE)
                        .setContentIntent(resultPendingIntent)
                        .build();
                startForeground(369, notification);
            }
        } catch (Exception asd) {
            stopSelf();
        }
    }

    private void startObserving() {
        fixedFileObserverImages = new FixedFileObserver(ImagesinternalPath) {
            @Override
            public void onEvent(int event, String file) {
                Log.e("Event call", "event");
                if (event == DELETE && !file.equals(".probe")) {
                    if (!(file + DELETE).equals(file_old)) {
                        if (file.endsWith(".png") || file.endsWith(".jpg")) {
                            if (Shared.getInstance().getBooleanValueFromPreference(context.getResources().getString(R.string.show_notification_pref), true, context))
                                addNotification_for_messenger(true);
                            //FilesData.getDeletedMedia(file);
                            FileOperations.copyToAppDirectory(context,file);
                            Intent saved_intent = new Intent(getResources().getString(R.string.intent_file_saved_images));
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(saved_intent);
                        }
                    }
                    file_old = file + DELETE;
                }
                if (event == MOVED_TO ||event == CREATE  && !file.equals(".probe")) {
                    if (!(file + MOVED_TO).equals(file_old)) {
                        if (Build.VERSION.SDK_INT>=29){

                            FileOperations.copyToAppDirectory(context,Environment.getExternalStorageDirectory().getAbsolutePath() + FilesData.WHATSAPP_IMAGES_LOCATION_ANDROID_11+"/"+file);
                           // FilesData.scrapWhatsAppImages(FilesData.WHATSAPP_IMAGES_LOCATION_ANDROID_11, file,context);
                        }else {
                            FilesData.scrapWhatsAppImages(FilesData.WHATSAPP_IMAGES_LOCATION, file,context);
                        }
                    }
                    file_old = (file + MOVED_TO);

                }
            }
        };
        fixedFileObserverImages.startWatching();
        fixedFileObserverVideos = new FixedFileObserverVideo(VideosinternalPath) {
            @Override
            public void onEvent(int event, String file) {
                Log.e("Event call", "event");
                if (event == DELETE && !file.equals(".probe")) {
                    if (!(file + DELETE).equals(file_old)) {
                        if (Shared.getInstance().getBooleanValueFromPreference(context.getResources().getString(R.string.show_notification_pref), true, context))
                            addNotification_for_messenger(false);
                      //  FilesData.getDeletedMediaVideio(file);
                        FileOperations.copyToAppDirectory(context,file);
                        Intent saved_intent = new Intent(getResources().getString(R.string.intent_file_saved_videos));
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(saved_intent);
                    }
                    file_old = (file + DELETE);
                }
                if (event == MOVED_TO || event==CREATE && !file.equals(".probe")) {
//                    if (!(file + MOVED_TO).equals(file_old)) {
                    if (Build.VERSION.SDK_INT>=29){
                        FilesData.scrapWhatsAppImages(FilesData.WHATSAPP_VIDEOS_LOCATION_ANDROID_11, file,context);
                    }else {
                        FilesData.scrapWhatsAppImages(FilesData.WHATSAPP_VIDEOS_LOCATION, file,context);
                    }
//                    }
                    file_old = (file + MOVED_TO);
                }
            }
        };
        fixedFileObserverVideos.startWatching();
        fixedFileObserverVoice = new FixedFileObserverVoice(VoicesinternalPath) {
            @Override
            public void onEvent(int event, String file) {
                Log.e("Event call", "event");
                if (event == DELETE && !file.equals(".probe")) {
                    if (!(file + DELETE).equals(file_old)) {

                        if (Shared.getInstance().getBooleanValueFromPreference(context.getResources().getString(R.string.show_notification_pref), true, context))
                            addNotification_for_messenger_voice();
                        FilesData.getDeletedMediaVoice(file);

                        Intent saved_intent = new Intent(getResources().getString(R.string.intent_file_saved_voice));
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(saved_intent);
                    }
                    file_old = (file + DELETE);
                }
                if (event == MOVED_TO && !file.equals(".probe")) {
                    if (!(file + MOVED_TO).equals(file_old)) {
                        FilesData.scrapWhatsAppVoice(file);
                    }
                    file_old = (file + MOVED_TO);

                }
            }
        };
        fixedFileObserverVoice.startWatching();
        fixedFileObserverAudios = new FixedFileObserverAudio(AudiosinternalPath) {
            @Override
            public void onEvent(int event, String file) {
                Log.e("Event call", "event");

                if (event == DELETE && !file.equals(".probe")) {
                    if (!(file + DELETE).equals(file_old)) {
                        if (Shared.getInstance().getBooleanValueFromPreference(context.getResources().getString(R.string.show_notification_pref), true, context))
                            addNotification_for_messenger_audio();
                        FilesData.getDeletedMediaAudio(file);
                        Intent saved_intent = new Intent(getResources().getString(R.string.intent_file_saved_audio));
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(saved_intent);
                    }
                    file_old = (file + DELETE);
                }
                if (event == MOVED_TO && !file.equals(".probe")) {
                    if (!(file + MOVED_TO).equals(file_old)) {
                        if (Build.VERSION.SDK_INT>=29){
                            FilesData.scrapWhatsAppImages(FilesData.WHATSAPP_AUDIOS_LOCATION_ANDROID_11, file,context);
                        }else {
                            FilesData.scrapWhatsAppImages(FilesData.WHATSAPP_AUDIOS_LOCATION, file,context);
                        }
                    }
                    file_old = (file + MOVED_TO);
                }
            }
        };
        fixedFileObserverAudios.startWatching();

        fixedFileObserverDocuments = new FixedFileObserverDocuments(DocumentsinternalPath) {
            @Override
            public void onEvent(int event, String file) {
                Log.e("Event call", "event");

                if (event == DELETE && !file.equals(".probe")) {
                    if (!(file + DELETE).equals(file_old)) {
                        if (Shared.getInstance().getBooleanValueFromPreference(context.getResources().getString(R.string.show_notification_pref), true, context))
                            addNotification_for_messenger_documents(true);
                        FilesData.getDeletedMediaAudio(file);
                        Intent saved_intent = new Intent(getResources().getString(R.string.intent_file_saved_docs));
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(saved_intent);
                    }
                    file_old = (file + DELETE);
                }
                if (event == MOVED_TO || event==CREATE && !file.equals(".probe")) {
                    if (!(file + MOVED_TO).equals(file_old)) {
                        if (Build.VERSION.SDK_INT>=29){
                            FilesData.scrapWhatsAppImages(FilesData.WHATSAPP_DOCUMENTS_LOCATION_ANDROID_11, file,context);
                        }else {
                            FilesData.scrapWhatsAppImages(FilesData.WHATSAPP_DOCUMENTS_LOCATION, file,context);
                        }
                    }
                    file_old = (file + MOVED_TO);
                }
            }
        };
        fixedFileObserverDocuments.startWatching();
    }

    private void addNotification_for_messenger_documents(boolean b) {
        Intent contact_intent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.change_pass_pref), false, getApplicationContext())) {
                contact_intent = new Intent(context, Lock_Screen_QW.class);
                contact_intent.putExtra(getResources().getString(R.string.open_main), false);
                contact_intent.putExtra(getResources().getString(R.string.open_mainactivity), true);
                contact_intent.putExtra(getResources().getString(R.string.open_insta_act), false);
            } else {
                contact_intent = new Intent(context, MainActivity.class);
            }
            contact_intent.putExtra(getResources().getString(R.string.is_image_intent), 5);
            contact_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            showNotification_4_videos(context, getResources().getString(R.string.app_name),
                    getResources().getString(R.string.documents_status_availible_please_check), contact_intent);
            return;
        }
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher_noti) //set icon for notification
                        .setContentTitle(getResources().getString(R.string.app_name)) //set title of notification
                        .setContentText(getResources().getString(R.string.new_status_availible_please_check))//this is notification message
                        .setAutoCancel(true) // makes auto cancel of notification
                        .setPriority(NotificationCompat.PRIORITY_MIN); //set priority of notification
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra(getResources().getString(R.string.is_audio_intent), 6);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.putExtra(getResources().getString(R.string.app_name), DocumentsinternalPath);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = rand.nextInt(1000);
        manager.notify(notificationId, builder.build());
    }

    private void addNotification_for_messenger_voice() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Intent contact_intent;
            if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.change_pass_pref), false, getApplicationContext())) {
                contact_intent = new Intent(context, Lock_Screen_QW.class);
                contact_intent.putExtra(getResources().getString(R.string.open_main), false);
                contact_intent.putExtra(getResources().getString(R.string.open_mainactivity), true);
                contact_intent.putExtra(getResources().getString(R.string.open_insta_act), false);
            } else {
                contact_intent = new Intent(context, MainActivity.class);
            }
            contact_intent.putExtra(getResources().getString(R.string.is_image_intent), 4);
            contact_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            showNotification_4_videos(context, getResources().getString(R.string.app_name),
                    getResources().getString(R.string.voice_status_availible_please_check), contact_intent);
            return;
        }
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher_noti) //set icon for notification
                        .setContentTitle(getResources().getString(R.string.app_name)) //set title of notification
                        .setContentText(getResources().getString(R.string.new_status_availible_please_check))//this is notification message
                        .setAutoCancel(true) // makes auto cancel of notification
                        .setPriority(NotificationCompat.PRIORITY_MIN); //set priority of notification
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra(getResources().getString(R.string.is_image_intent), 4);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.putExtra(getResources().getString(R.string.app_name), VoicesinternalPath);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = rand.nextInt(1000);
        manager.notify(notificationId, builder.build());
    }

    private void addNotification_for_messenger_audio() {
        Intent contact_intent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.change_pass_pref), false, getApplicationContext())) {
                contact_intent = new Intent(context, Lock_Screen_QW.class);
                contact_intent.putExtra(getResources().getString(R.string.open_main), false);
                contact_intent.putExtra(getResources().getString(R.string.open_mainactivity), true);
                contact_intent.putExtra(getResources().getString(R.string.open_insta_act), false);
            } else {
                contact_intent = new Intent(context, MainActivity.class);
            }
            contact_intent.putExtra(getResources().getString(R.string.is_image_intent), 3);
            contact_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            showNotification_4_videos(context, getResources().getString(R.string.app_name),
                    getResources().getString(R.string.audio_status_availible_please_check), contact_intent);
            return;
        }
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher_noti) //set icon for notification
                        .setContentTitle(getResources().getString(R.string.app_name)) //set title of notification
                        .setContentText(getResources().getString(R.string.new_status_availible_please_check))//this is notification message
                        .setAutoCancel(true) // makes auto cancel of notification
                        .setPriority(NotificationCompat.PRIORITY_MIN); //set priority of notification
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra(getResources().getString(R.string.is_audio_intent), 4);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.putExtra(getResources().getString(R.string.app_name), AudiosinternalPath);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = rand.nextInt(1000);
        manager.notify(notificationId, builder.build());
    }
    public void showNotification(Context context, String title, String body, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = rand.nextInt(1000);
        String channelId = getPackageCodePath();
        String channelName = getResources().getString(R.string.app_name);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
        notificationManager.createNotificationChannel(mChannel);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0, PendingIntent.FLAG_UPDATE_CURRENT
        );
        notificationManager.createNotificationChannel(mChannel);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher_noti)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setContentText(body);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(notificationId, mBuilder.build());
    }

    public void showNotification_4_videos(Context context, String title, String body, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Random r = new Random();
        int notificationId = rand.nextInt(1000);
        String channelId = getPackageCodePath();
        String channelName = getResources().getString(R.string.app_name);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel mChannel = new NotificationChannel(
                channelId, channelName, importance);
        notificationManager.createNotificationChannel(mChannel);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        notificationManager.createNotificationChannel(mChannel);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher_noti)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setContentText(body);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(notificationId, mBuilder.build());
    }

    private void addNotification_for_messenger(Boolean file_is_image) {
        Intent contact_intent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.change_pass_pref), false, getApplicationContext())) {
                contact_intent = new Intent(context, Lock_Screen_QW.class);
                contact_intent.putExtra(getResources().getString(R.string.open_main), false);
                contact_intent.putExtra(getResources().getString(R.string.open_mainactivity), true);
                contact_intent.putExtra(getResources().getString(R.string.open_insta_act), false);
            } else {
                contact_intent = new Intent(context, MainActivity.class);
            }
            if (file_is_image) {
                contact_intent.putExtra(getResources().getString(R.string.is_image_intent), 1);
            } else {
                contact_intent.putExtra(getResources().getString(R.string.is_image_intent), 2);
            }
            contact_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if (file_is_image)
                showNotification(context, getResources().getString(R.string.app_name), getResources().getString(R.string.image_status_availible_please_check), contact_intent);
            else {
                showNotification_4_videos(context, getResources().getString(R.string.app_name), getResources().getString(R.string.video_status_availible_please_check), contact_intent);
            }
            return;
        }
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher_noti) //set icon for notification
                        .setContentTitle(getResources().getString(R.string.app_name)) //set title of notification
                        .setContentText(getResources().getString(R.string.new_status_availible_please_check))//this is notification message
                        .setAutoCancel(true) // makes auto cancel of notification
                        .setPriority(NotificationCompat.PRIORITY_MIN); //set priority of notification
        if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.change_pass_pref), false, getApplicationContext())) {
            contact_intent = new Intent(context, Lock_Screen_QW.class);
            contact_intent.putExtra(getResources().getString(R.string.open_main), false);
            contact_intent.putExtra(getResources().getString(R.string.open_mainactivity), true);
            contact_intent.putExtra(getResources().getString(R.string.open_insta_act), false);
        } else {
            contact_intent = new Intent(this, MainActivity.class);
        }
        if (file_is_image) {
            contact_intent.putExtra(getResources().getString(R.string.is_image_intent), 1);
            contact_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            contact_intent.putExtra(getResources().getString(R.string.app_name), ImagesinternalPath);
        } else {
            contact_intent.putExtra(getResources().getString(R.string.is_image_intent), 2);
            contact_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            contact_intent.putExtra(getResources().getString(R.string.app_name), VideosinternalPath);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, contact_intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = rand.nextInt(1000);
        manager.notify(notificationId, builder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                String NOTIFICATION_CHANNEL_ID = getPackageName();
                String channelName = getResources().getString(R.string.app_name);
                NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
                chan.setLightColor(Color.BLUE);
                chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.createNotificationChannel(chan);
                Intent contact_intent = new Intent(context, MainActivity.class);
                if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.change_pass_pref), false, getApplicationContext())) {
                    contact_intent = new Intent(context, Lock_Screen_QW.class);
                    contact_intent.putExtra(getResources().getString(R.string.open_main), false);
                    contact_intent.putExtra(getResources().getString(R.string.open_mainactivity), true);
                    contact_intent.putExtra(getResources().getString(R.string.open_insta_act), false);
                } else {
                    contact_intent = new Intent(context, MainActivity.class);
                    contact_intent.putExtra(getResources().getString(R.string.is_image_intent), 0);
                }
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addNextIntent(contact_intent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
                Notification notification = notificationBuilder.setOngoing(true)
                        .setSmallIcon(R.mipmap.ic_launcher_noti)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setContentText(getResources().getString(R.string.you_will_be_notify_when_status_avalible))
                        .setPriority(NotificationManager.IMPORTANCE_MIN)
                        .setCategory(Notification.CATEGORY_SERVICE)
                        .setContentIntent(resultPendingIntent)
                        .build();
                startForeground(369, notification);
            }
        } catch (Exception asd) {
            Toast.makeText(context, "Service stop", Toast.LENGTH_LONG).show();
            System.out.print("exception " + asd);
            stopSelf();
        }

        if (Build.VERSION.SDK_INT>=29){
            ImagesinternalPath = FilesData.WHATSAPP_IMAGES_LOCATION_ANDROID_11;
            VideosinternalPath = FilesData.WHATSAPP_VIDEOS_LOCATION_ANDROID_11;
            AudiosinternalPath = FilesData.WHATSAPP_AUDIOS_LOCATION_ANDROID_11;
        }else {
            ImagesinternalPath = FilesData.WHATSAPP_IMAGES_LOCATION;
            VideosinternalPath = FilesData.WHATSAPP_VIDEOS_LOCATION;
            AudiosinternalPath = FilesData.WHATSAPP_AUDIOS_LOCATION;
        }

        if (Build.VERSION.SDK_INT>=29) {
            ImagesinternalPath = Environment.getExternalStorageDirectory().getAbsolutePath() + FilesData.WHATSAPP_IMAGES_LOCATION_ANDROID_11;
            VideosinternalPath = Environment.getExternalStorageDirectory().getAbsolutePath() + FilesData.WHATSAPP_VIDEOS_LOCATION_ANDROID_11;
            String patht = Environment.getExternalStorageDirectory().toString() + FilesData.WHATSAPP_VOICES_LOCATION_ANDROID_11;
            File di = new File(patht);
            try {
                VoicesinternalPath = getfolder(di) + "/";
            } catch (Exception e) {
                VoicesinternalPath = Environment.getExternalStorageDirectory().getAbsolutePath() + FilesData.WHATSAPP_VOICES_LOCATION_ANDROID_11;
                e.printStackTrace();
            }
            AudiosinternalPath = Environment.getExternalStorageDirectory().getAbsolutePath() + FilesData.WHATSAPP_AUDIOS_LOCATION_ANDROID_11;
            DocumentsinternalPath = Environment.getExternalStorageDirectory().getAbsolutePath() + FilesData.WHATSAPP_DOCUMENTS_LOCATION_ANDROID_11;
        }else {
            ImagesinternalPath = Environment.getExternalStorageDirectory().getAbsolutePath() + FilesData.WHATSAPP_IMAGES_LOCATION;
            VideosinternalPath = Environment.getExternalStorageDirectory().getAbsolutePath() + FilesData.WHATSAPP_VIDEOS_LOCATION;
            String patht = Environment.getExternalStorageDirectory().toString() + FilesData.WHATSAPP_VOICES_LOCATION;
            File di = new File(patht);
            try {
                VoicesinternalPath = getfolder(di) + "/";
            } catch (Exception e) {
                VoicesinternalPath = Environment.getExternalStorageDirectory().getAbsolutePath() + FilesData.WHATSAPP_VOICES_LOCATION;
                e.printStackTrace();
            }
            AudiosinternalPath = Environment.getExternalStorageDirectory().getAbsolutePath() + FilesData.WHATSAPP_AUDIOS_LOCATION;
            DocumentsinternalPath = Environment.getExternalStorageDirectory().getAbsolutePath() + FilesData.WHATSAPP_DOCUMENTS_LOCATION;
        }
        StringBuilder stringBuilder5 = new StringBuilder();
        stringBuilder5.append(ImagesinternalPath);
        startObserving();

        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.service_is_running_pref), true, getApplicationContext());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (fo1 != null) {
            fo1.stopWatching();
        }
        if (fixedFileObserverImages != null) {
            fixedFileObserverImages.stopWatching();
        }
        if (fixedFileObserverAudios != null) {
            fixedFileObserverAudios.stopWatching();
        }
        if (fixedFileObserverVideos != null) {
            fixedFileObserverVideos.stopWatching();
        }
        if (fixedFileObserverVoice != null) {
            fixedFileObserverVoice.stopWatching();
        }
        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.service_is_running_pref), false, getApplicationContext());
        super.onDestroy();
    }

}