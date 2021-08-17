package com.malam.whatsdelet.nolastseen.hiddenchat.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.whatsdelete.Test.R;

import static com.malam.whatsdelet.nolastseen.hiddenchat.Activities.MainActivity.ALLOW_KEY;
import static com.malam.whatsdelet.nolastseen.hiddenchat.Activities.MainActivity.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;
import static com.malam.whatsdelet.nolastseen.hiddenchat.Activities.MainActivity.getFromPref;

public class Storage_permission extends AppCompatActivity {
    TextView access;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_permission);
        access = findViewById(R.id.access);
        access.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                marshmallow_permissions_WRITE_EXTERNAL_STORAGE();



            }
        });
    }


    private void marshmallow_permissions_WRITE_EXTERNAL_STORAGE() {

        if (Build.VERSION.SDK_INT >= 29) {
            startActivity(new Intent(Storage_permission.this, HomeActivity.class));
            finish();
        } else {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (getFromPref(this, ALLOW_KEY)) {

                } else if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,},
                                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    } else {
                        // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,},
                                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!hasNotificationAccess()) {
            notify_alert();
        }

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            }


    }

    boolean hasNotificationAccess() {
        ContentResolver contentResolver = getContentResolver();
        String enabledNotificationListeners = android.provider.Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = getPackageName();
        // check to see if the enabledNotificationListeners String contains our package name
        return !(enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName));
    }

    private void notify_alert() {
        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
        View layout1 = LayoutInflater.from(this).inflate(R.layout.notification_access_dialog, findViewById(R.id.your_dialog_root_element));
        layout1.setMinimumWidth(100);
        alertDialog.setView(layout1);
        alertDialog.setCancelable(false);
        alertDialog.show();
        try {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (NullPointerException ne) {
        } catch (Exception e) {
        }
        Button ok_btn = layout1.findViewById(R.id.ok_btn);
        ImageView cancel_btn = layout1.findViewById(R.id.cancel_btn);
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showNotifcation_sAlert();
                alertDialog.dismiss();
            }

        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void showNotifcation_sAlert() {


        Intent go_to_settings = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        go_to_settings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(go_to_settings);
    }
}