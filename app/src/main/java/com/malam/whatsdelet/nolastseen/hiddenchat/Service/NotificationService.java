package com.malam.whatsdelet.nolastseen.hiddenchat.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.orhanobut.hawk.Hawk;
import com.malam.whatsdelet.nolastseen.hiddenchat.Activities.Contact_Chat_Act_fro_Notification;
import com.malam.whatsdelet.nolastseen.hiddenchat.Activities.Lock_Screen_QW;
import com.malam.whatsdelet.nolastseen.hiddenchat.Helper.DatabaseHelper;
 import com.malam.whatsdelet.nolastseen.hiddenchat.Model.Action;
import com.malam.whatsdelet.nolastseen.hiddenchat.Model.Note;
import com.malam.whatsdelet.nolastseen.hiddenchat.Helper.DatabaseHelper_new;
 import com.malam.whatsdelet.nolastseen.hiddenchat.Model.Note_new;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.NotificationUtils;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.Shared;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.Utils;
import com.whatsdelete.Test.R;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class NotificationService extends NotificationListenerService {
Context context;
Bitmap icon_bitmap;
String message, name_new,name_old,message_new, message_new1, title, new_title;

private List<Note> notesList = new ArrayList<>();
DatabaseHelper_new databaseHelper_new;
String lastmsg = "";
String title2 = "";
String table_name = "";
String final_title = "";

String my_msg = "";
public static Map<String, Action> replyActions = new HashMap();
Intent msgrcv;
Boolean broad_cast_gone = false;
private boolean status = false;
Note existmessage_;
Note_new existmessage;

String[] events = {};
ArrayList<String> body_txt = new ArrayList<>();
ArrayList<String> body_txt_whatsapp = new ArrayList<>();
 private static int value = 0;
int total_unread_mesgs = 0;
NotificationManager notificationManager;


private BroadcastReceiver remove_notifications = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            notificationManager.cancelAll();
        } catch (NullPointerException asd) {
        } catch (Exception asd) {
        }
        try {
            body_txt_whatsapp.clear();
            body_txt.clear();
         } catch (NullPointerException asd) {
        } catch (Exception asd) {
        }
    }
};

public void onCreate() {
    super.onCreate();
    Hawk.init(this).build();
    databaseHelper_new = new DatabaseHelper_new(this);
      context = getApplicationContext();
    
    LocalBroadcastManager.getInstance(this).registerReceiver(remove_notifications, new IntentFilter(getResources().getString(R.string.remove_all_notification_broadcast)));
      /*  try{
        replyActions.putAll(Hawk.get(getResources().getString(R.string.reply_actions_pref), new HashMap<String, Action>()));
        }catch (NullPointerException asd){}catch (Exception erer){}*/
}

@Override
public void onNotificationRemoved(StatusBarNotification sbn) {
    super.onNotificationRemoved(sbn);
    String pack = sbn.getPackageName();
    if (pack.equalsIgnoreCase(getPackageName())) {
        body_txt.clear();
    }
}

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
@Override
public void onNotificationPosted(StatusBarNotification sbn) {
    try {
        String pack = sbn.getPackageName();
        System.out.println("package Name==> " + pack);
        String ticker = "";
        int key = 0;
        title = sbn.getNotification().extras.getString("android.title");
        message = "" + sbn.getNotification().extras.get("android.text");
        if (pack.contains("com.whatsapp"))
            try {
                key = Integer.parseInt("" + sbn.getNotification().extras.get("last_row_id"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
    
        if (sbn.getNotification().tickerText != null) {
            ticker = sbn.getNotification().tickerText.toString();
        }
        
        
        if (message.length() < 1) {
            return;
        }
        if (/*System.currentTimeMillis() - sbn.getPostTime() < 1800 &&*/
                pack.contains("com.whatsapp")) {
            
            String msg_string1 = " ";
            try {
                msg_string1 = String.valueOf(message.charAt(0));
            } catch (StringIndexOutOfBoundsException asds) {
                return;
            } catch (IndexOutOfBoundsException asd) {
                return;
            } catch (Exception asd) {
                return;
            }
            if (!msg_string1.equals(" ")) {
                message = " " + message;
            }
            if (title == null)
                return;
            if (title.contains("WA Business") || title.contains("Calling") || title.contains("Deleting")
                        || title.contains("WhatsApp") || title.contains("Ringing") || (title.contains("You"))
                        || message.contains("new messages") || message.contains("calling") || title.contains("missed call")
                        || title.contains("missed voice call") || title.contains("Backup")) {
                return;
            }
            if (message.contains("Incoming video call") || message.contains("Missed voice call")
                        || title.contains("Deleting") || message.contains("Incoming voice call") || message.contains("missed calls") || message.contains("Video call")
            ) {
                return;
            }
            DatabaseHelper_new db_;
            db_ = new DatabaseHelper_new(this);
            if (message.contains("This message was deleted")) {
                existmessage = db_.CheckIsDataAlreadyInDBorNot(key);
                status = true;
            } else status = false;
            try {
                icon_bitmap = sbn.getNotification().largeIcon;
                if (icon_bitmap == null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Icon largeIcon = sbn.getNotification().getLargeIcon();
                        if (largeIcon != null) {
                            icon_bitmap = drawableToBitmap(largeIcon.loadDrawable(getApplicationContext()));
                        }
                    }
                }
                title2 = title;
                try {
                    String msg_string = String.valueOf(my_msg.charAt(0));
                    if (!msg_string.equals(" ")) {
                        my_msg = " " + my_msg;
                    }
                } catch (IndexOutOfBoundsException asd) {
                } catch (Exception asd) {
                    asd.printStackTrace();
                }
                my_msg = message;
                try {
                    if (my_msg.substring(my_msg.length()).equalsIgnoreCase(" ")) {
                        my_msg = my_msg.substring(0, my_msg.length() - 1);
                    }
                } catch (IndexOutOfBoundsException asd) {
                    asd.printStackTrace();
                } catch (Exception e) {
                }
                message_new = my_msg;
                try {
                    if (title2.contains("@")) {
                        int start_index = title2.indexOf("@");
                        start_index++;
                        message_new = title2.substring(0, start_index - 1) + ":" + message_new;
                        title2 = title2.substring(start_index);
                    }
                } catch (IndexOutOfBoundsException asd) {
                    asd.printStackTrace();
                } catch (Exception asd) {
                }
                try {
                    if (title2.contains(":")) {
                        
                        int start_index = title2.indexOf(":");
                        start_index++;
                        message_new = title2.substring(start_index) + ":" + message_new;
                        title2 = title2.substring(0, start_index - 1);
                    }
                } catch (IndexOutOfBoundsException asd) {
                    asd.printStackTrace();
                } catch (Exception asd) {
                }
                try {
                    if (title2.contains("(")) {
                        String str1 = title2.substring(title2.indexOf("("), title2.indexOf(")"));
                        title2 = title2.substring(0, title2.indexOf("("));
                    }
                } catch (IndexOutOfBoundsException asd) {
                    asd.printStackTrace();
                } catch (Exception asd) {
                }
                Boolean contact_exist = false;
                Boolean message_exist = false;
                String title3 = title2;
                try {
                    if (title3.substring(title3.length() - 1).equalsIgnoreCase(" ")) {
                        title3 = title3.substring(0, title3.length() - 1);
                        title2 = title3;
                    }
                } catch (IndexOutOfBoundsException asd) {
                    asd.printStackTrace();
                } catch (Exception asd) {
                }
                try {
                    if (title3.substring(title3.length() - 1).equalsIgnoreCase(" ")) {
                        title3 = title3.substring(0, title3.length() - 1);
                        title2 = title3;
                    }
                } catch (IndexOutOfBoundsException asd) {
                    asd.printStackTrace();
                } catch (Exception asd) {
                }
                try {
                    if (title3.substring(title3.length() - 1).equalsIgnoreCase(" ")) {
                        title3 = title3.substring(0, title3.length() - 1);
                        title2 = title3;
                    }
                } catch (IndexOutOfBoundsException asd) {
                    asd.printStackTrace();
                } catch (Exception asd) {
                }
                for (int i = title3.length(); i > 0; i--) {
                    try {
                        String asdsa = String.valueOf(title3.charAt(i));
                        if (asdsa.equals(" ")) {
                            title3 = title3.substring(0, title3.length() - 1);
                            
                        } else break;
                    } catch (IndexOutOfBoundsException asd) {
                        asd.printStackTrace();
                    } catch (Exception asd) {
                    }
                }
                Action action = NotificationUtils.getQuickReplyAction(sbn.getNotification(), this.getPackageName());
                if (action != null) {
                    replyActions.put(title3, action);
                }
                try {
                    String asdsa = String.valueOf(message_new.charAt(0));
                    if (!asdsa.equals(" ")) {
                        message_new = " " + message_new;
                    }
                } catch (IndexOutOfBoundsException asd) {
                } catch (Exception asd) {
                }
                try {
                    if (!message_new.substring(message_new.length()).equalsIgnoreCase(" ")) {
                        message_new = " " + message_new;
                    }
                } catch (IndexOutOfBoundsException asd) {
                } catch (Exception asd) {
                }
                title2 = title3;
                final_title = title2;
                notesList.clear();
                int a = 0;
                DatabaseHelper db;
                db = new DatabaseHelper(this);
                notesList.addAll(db.getAllNotes());
                
                name_old=title;
                for (int i = 0; i < notesList.size(); i++) {
                    String as = notesList.get(i).getNote();
                    a = notesList.get(i).getId();
                    
                    if (as.equalsIgnoreCase(final_title)) {
                        if (notesList.get(i).getNumber().equals(message_new)) {
                            message_exist = true;
                            break;
                        }
                        contact_exist = true;
                        existmessage_ = notesList.get(i);
                        break;
                    }
    
                  
                }
                if (message_new.equals(message_new1) || message_new.equals(message_new1 + " ") || message_new.equals(" " + message_new1 + " ") || message_new.equals(" " + message_new1)) {
                    if (name_old.equals(title)&&message_exist)
                        return;
                }
                if (message_exist) {
                    return;
                }
                if (final_title.contains("Calling") || final_title.contains("WhatsApp") || final_title.contains("Ringing")
                            || message_new.contains("new messages") || message_new.contains("Missed group voice call") || message_new.contains("Incoming group voice call") || message_new.contains("Incoming group video call") || message_new.contains("calling") || final_title.equalsIgnoreCase("Finished backup")
                            || final_title.contains("Missed voice call") || final_title.contains("Missed video call") || final_title.contains("Backup") || final_title.contains("You")) {
                    
                    return;
                }
                if (message_new.contains("Incoming video call") || message_new.contains("Ringing") || message_new.contains("Calling")
                            || message_new.contains("Incoming call") || message_new.contains("Video call") || message_new.contains("Ongoing voice call") || message_new.contains("Ongoing video call")
                ) {
                    return;
                }
                if (final_title.contains("Deleting") || final_title.contains("Couldn't complete backup")) {
               return; }
                
                String input = title3;
                input = input.replace(" ", "");
                input = input.trim();
                table_name = input;
                new_title = final_title;
                Date dt = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd mm yyyy");
                SimpleDateFormat dateFormat_4_itration = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String time1 = sdf.format(dt);
                String date = dateFormat.format(dt);
                String date_4_itration = dateFormat_4_itration.format(dt);
                Date c = Calendar.getInstance().getTime();
                System.out.println("Current time => " + c);
                Log.d("Date= ", "date " + date_4_itration);
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                String formattedDate = df.format(c);
                if (contact_exist) {
                    Note note = new Note();
                    if (!status) {
                        note.setNote(final_title);
                        note.setNumber(message_new);
                        System.out.println("my_message8>" + message_new);
                        note.setId(a);
                        note.setTime(time1);
                        note.setTimestamp(formattedDate);
                        note.setTime_n_date_4_itration(date_4_itration);
                        note.setKey(key);
                        db.updateNote(note);
                    } else {
                        existmessage_.setStatus(status);
                        db.updateNote(existmessage_);
                    }
                } else {
                    if (!status) {
                        Log.e(TAG, "Contact not Exsit in list" + final_title);
                        long id = db.insertNote(final_title, final_title, message_new, time1, formattedDate, date_4_itration, getResources().getString(R.string.from_receiver), key, status);
                    }
                }
                
                int num_of_unread_msgs = Shared.getInstance().getIntValueFromPreference(table_name + getResources().getString(R.string.num_of_unread_msgs_pref), 0, context);
                if (!status) num_of_unread_msgs++;
                total_unread_mesgs = Shared.getInstance().getIntValueFromPreference(getResources().getString(R.string.total_unread_msgs_pref), 0, context);
                total_unread_mesgs = total_unread_mesgs + num_of_unread_msgs;
                Shared.getInstance().saveIntToPreferences(getResources().getString(R.string.total_unread_msgs_pref), total_unread_mesgs, context);
                
                Shared.getInstance().saveIntToPreferences(table_name + getResources().getString(R.string.num_of_unread_msgs_pref), num_of_unread_msgs, context);
                
                Shared.getInstance().saveBooleanToPreferences(table_name, true, context);
                msgrcv = new Intent(getResources().getString(R.string.intent_name));
                msgrcv.putExtra("package", pack);
                msgrcv.putExtra("ticker", ticker);
                msgrcv.putExtra("title", final_title);
                msgrcv.putExtra("text", message_new);
                msgrcv.putExtra("time", time1);
                msgrcv.putExtra("key", key);
                msgrcv.putExtra("status", status);
                if (!broad_cast_gone) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            broad_cast_gone = false;
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(msgrcv);
                        }
                    }, 300);
                    broad_cast_gone = true;
                }
                if (status) {
                    
                    
                    existmessage.setKey(key);
                    existmessage.setstatus(status);
                    databaseHelper_new.updateNote(existmessage);
                    return;
                }
                
                databaseHelper_new.insertNote(final_title, final_title, message_new, time1, getResources().getString(R.string.from_receiver), status, key);
                
            } catch (IndexOutOfBoundsException asd) {
            } catch (NullPointerException erer) {
            } catch (Exception er) {
            }
            saveImage(icon_bitmap, table_name);
            lastmsg = my_msg;
            message_new1 = message_new;
            if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.media_switch_notify_pref), true, context)) {
                String input = final_title;
                input = input.replace(" ", "");
                input = input.trim();
                if (!Shared.getInstance().getBooleanValueFromPreference(input + "is_chat_open", false, context)) {
                  if (Shared.getInstance().getBooleanValueFromPreference(context.getResources().getString(R.string.media_switch_notify_pref),true,context))
                      addNotification();
                }
            }
            if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.show_head_chat_pref), false, getApplicationContext()) && !Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.app_is_open_pref), false, getApplicationContext())) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(context)) {
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                            Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.open_chat_head_service_pref), true, getApplicationContext());
                            Utils.scheduleJob(getApplicationContext(), final_title, message_new, false, false);
                        } else {
                            ContextCompat.startForegroundService(getApplicationContext(), new Intent(context, ChatHead.class).putExtra(Utils.EXTRA_MSG, message_new).putExtra(Utils.EXTRA_TITLE, final_title).putExtra(Utils.EXTRA_IS_MESSENGER, false).putExtra(Utils.EXTRA_IS_INSTA, false));
                        }
                    }
                } else {
                    ContextCompat.startForegroundService(getApplicationContext(), new Intent(context, ChatHead.class).putExtra(Utils.EXTRA_MSG, message_new).putExtra(Utils.EXTRA_TITLE, final_title).putExtra(Utils.EXTRA_IS_MESSENGER, false).putExtra(Utils.EXTRA_IS_INSTA, false));
                }
            }
            if (!broad_cast_gone) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        broad_cast_gone = false;
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(msgrcv);
                        
                    }
                }, 300);
                broad_cast_gone = true;
            }
            message_new = "";
        }
         } catch (Exception asd) {
        asd.printStackTrace();
    }
    super.onNotificationPosted(sbn);
}


public void showNotification(Context context, String title, String body, Intent intent, int notificationId, Boolean is_whatsapp, Boolean is_messenger) {
    notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    Random r = new Random();
       /* int random_int = r.nextInt(999 - 28) + 28;
        int notificationId = random_int;*/
    String channelId = getPackageCodePath();
    String channelName = getResources().getString(R.string.app_name);
    int importance = NotificationManager.IMPORTANCE_HIGH;
    
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
    
    Bitmap large_icon = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_circled);
    int color_code, small_icon;
    if (is_whatsapp) {
        color_code = getResources().getColor(R.color.whastapp_color);
        small_icon = R.mipmap.whatsapp_notification_icon;
    } else if (is_messenger) {
        color_code = getResources().getColor(R.color.messenger_color_code);
        small_icon = R.mipmap.messenger_notifcation_icon;
    } else {
        color_code = getResources().getColor(R.color.insta_color_code);
        small_icon = R.mipmap.instagram_small_icon;
    }
    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                                                  .setContentTitle(title)
                                                  .setSmallIcon(small_icon)
                                                  .setColor(color_code)
                                                  .setLargeIcon(large_icon)
                                                  .setAutoCancel(true)
                                                .setOngoing(false)
                                                  .setContentText(body);
    
    
    mBuilder.setContentIntent(resultPendingIntent);
    mBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
    notificationManager.notify(notificationId, mBuilder.build());
    NotificationCompat.InboxStyle inboxStyle =
            new NotificationCompat.InboxStyle();
    if (is_whatsapp) {
        body_txt_whatsapp.add(body);
        int mysize = 0;
        if (body_txt_whatsapp.size() > 7) {
            mysize = body_txt_whatsapp.size() - 7;
        }
        
        for (int i = mysize; i < body_txt_whatsapp.size(); i++) {
            inboxStyle.addLine(body_txt_whatsapp.get(i));
        }
    }
    
    mBuilder.setStyle(inboxStyle);
    
    notificationManager.notify(notificationId, mBuilder.build());
    
}

private void addNotification() {
    
    int notificationId = 2;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        Intent contact_intent;
        if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.change_pass_pref), false, getApplicationContext())) {
            contact_intent = new Intent(context, Lock_Screen_QW.class);
            contact_intent.putExtra(getResources().getString(R.string.open_whatsapp_act), true);
            contact_intent.putExtra(getResources().getString(R.string.open_messenger_act), false);
            contact_intent.putExtra(getResources().getString(R.string.open_insta_act), false);
        } else {
            contact_intent = new Intent(context, Contact_Chat_Act_fro_Notification.class);
        }
        
        
        contact_intent.putExtra(getResources().getString(R.string.contact_name_pref), final_title);
        contact_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        showNotification(context, final_title, message_new, contact_intent, notificationId, true, false);
        return;
    }
    int color_code, small_icon;
    color_code = getResources().getColor(R.color.whastapp_color);
    small_icon = R.mipmap.whatsapp_notification_icon;
    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_circled);
    NotificationCompat.Builder builder =
            new NotificationCompat.Builder(this)
                    .setLargeIcon(bitmap)
                    .setColor(color_code)
                    .setSmallIcon(small_icon)
                    .setContentTitle(final_title)
                    .setContentText(message_new)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
    
    Intent notificationIntent;
    if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.change_pass_pref), false, getApplicationContext())) {
        notificationIntent = new Intent(context, Lock_Screen_QW.class);
        notificationIntent.putExtra(getResources().getString(R.string.open_whatsapp_act), true);
        notificationIntent.putExtra(getResources().getString(R.string.open_messenger_act), false);
        notificationIntent.putExtra(getResources().getString(R.string.open_insta_act), false);
    } else {
        notificationIntent = new Intent(context, Contact_Chat_Act_fro_Notification.class);
    }
    
    
    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    notificationIntent.putExtra(getResources().getString(R.string.contact_name_pref), final_title);
    
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT);
    builder.setContentIntent(pendingIntent);
    
    // Add as notification
    notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    Random r = new Random();
    int random_int = r.nextInt(999 - 28) + 28;
    
    NotificationCompat.InboxStyle inboxStyle =
            new NotificationCompat.InboxStyle();
    int mysize = 0;
    if (body_txt_whatsapp.size() > 7) {
        mysize = body_txt_whatsapp.size() - 7;
    }
    
    for (int i = mysize; i < body_txt_whatsapp.size() - 1; i++) {
        inboxStyle.addLine(body_txt.get(i));
    }
    inboxStyle.addLine(message);
    builder.setStyle(inboxStyle);
    
    notificationManager.notify(notificationId, builder.build());
}

public static Bitmap drawableToBitmap (Drawable drawable) {
    Bitmap bitmap = null;
    
    if (drawable instanceof BitmapDrawable) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        if(bitmapDrawable.getBitmap() != null) {
            return bitmapDrawable.getBitmap();
        }
    }
    
    if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
        bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
    } else {
        bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
    }
    
    Canvas canvas = new Canvas(bitmap);
    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
    drawable.draw(canvas);
    return bitmap;
}
private String saveImage(Bitmap bitmap, String str) {
    String file = Environment.getExternalStorageDirectory().toString();
    StringBuilder sb = new StringBuilder();
    sb.append(file);
    sb.append("/.Profile Thumbnail");
    File file2 = new File(sb.toString());
    file2.mkdirs();
    StringBuilder sb2 = new StringBuilder();
    sb2.append("Image-");
    sb2.append(str);
    sb2.append(".png");
    File file3 = new File(file2, sb2.toString());
    if (file3.exists()) {
        file3.delete();
    }
    try {
        FileOutputStream fileOutputStream = new FileOutputStream(file3);
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
    return file3.getAbsolutePath();
}
}