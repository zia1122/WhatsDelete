package com.malam.whatsdelet.nolastseen.hiddenchat.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.malam.whatsdelet.nolastseen.hiddenchat.Service.FileSystemObserverService;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.Shared;
import com.whatsdelete.Test.R;

public class Splash_Act extends AppCompatActivity {

    private int SPLASH_TIME_OUT = 5000;
    InterstitialAd mInterstitialAd;
    String test_device_white, test_device_j5, vicky_S8;
    Handler handler;
    Runnable runnable, runnable1;
    int progress = 0;
    Boolean start_progress = false, act_stop = false;
    String packg_name = null;
    Button startButton;


    Boolean isFirstTime() {
        if (!hasNotificationAccess()) {
            return false;
        }
            return ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;



    }
    Boolean isFirstTime29(){
        if (!hasNotificationAccess()) {
            return false;
        }
        return true;
    }



    boolean hasNotificationAccess() {
        ContentResolver contentResolver = getContentResolver();
        String enabledNotificationListeners = android.provider.Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = getPackageName();
        // check to see if the enabledNotificationListeners String contains our package name
        return !(enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        //tv1 = findViewById(R.id.tv1);
        try {
            packg_name = Shared.getInstance().getStringValueFromPreference(getResources().getString(R.string.promotion_package_name_pref), null, getApplicationContext());
        } catch (NullPointerException asd) {
        } catch (Exception asd) {
        }
        try {
            packg_name = getIntent().getStringExtra("promo_package_name");
        } catch (NullPointerException asd) {
            Toast.makeText(Splash_Act.this, "Intent Exception NUll", Toast.LENGTH_SHORT).show();
        } catch (Exception asd) {
            Toast.makeText(Splash_Act.this, "Intent Exception", Toast.LENGTH_SHORT).show();
        }
        if (packg_name != null) {
            startActivity(new Intent(Splash_Act.this, Promotion_act.class).putExtra("package_name", packg_name));
            Shared.getInstance().saveStringToPreferences(getResources().getString(R.string.promotion_package_name_pref), null, getApplicationContext());
            finish();
            return;
        }
        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_pref), false, this);
        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_deleted_images_pref), false, this);
        test_device_white = getResources().getString(R.string.test_device_white);
        vicky_S8 = getResources().getString(R.string.vicky_s8);
        test_device_j5 = getResources().getString(R.string.test_device_j5);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.intrestialAd));
        requestNewInterstitial();
        handler = new Handler();
        //startButton = findViewById(R.id.startButton);





        runnable = new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= 29) {
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {
                        Boolean pin_set = Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.pin_set_pref), false, Splash_Act.this);
                        Boolean change_pass_pref = Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.change_pass_pref), false, Splash_Act.this);
                        if (pin_set && change_pass_pref) {
                            startActivity(new Intent(Splash_Act.this, Lock_Screen_QW.class));
                            finish();
                            return;
                        }
                        if (!isFirstTime29()) {
                            startActivity(new Intent(Splash_Act.this, SliderActivity.class).putExtra("HomeAct", "splash"));
                        } else
                            startActivity(new Intent(Splash_Act.this, HomeActivity.class));
                        finish();



                    }
                    mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                            Boolean pin_set = Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.pin_set_pref), false, Splash_Act.this);
                            Boolean change_pass_pref = Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.change_pass_pref), false, Splash_Act.this);
                            if (!act_stop) {
                                if (pin_set && change_pass_pref) {
                                    startActivity(new Intent(Splash_Act.this, Lock_Screen_QW.class));

                                    finish();
                                    return;
                                }
                                if (!isFirstTime29()) {
                                    startActivity(new Intent(Splash_Act.this, SliderActivity.class).putExtra("HomeAct", "splash"));
                                } else
                                    startActivity(new Intent(Splash_Act.this, HomeActivity.class));
                                finish();
                            }

                        }

                    });
                }
                else {
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {
                        Boolean pin_set = Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.pin_set_pref), false, Splash_Act.this);
                        Boolean change_pass_pref = Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.change_pass_pref), false, Splash_Act.this);
                        if (pin_set && change_pass_pref) {
                            startActivity(new Intent(Splash_Act.this, Lock_Screen_QW.class));
                            finish();
                            return;
                        }
                        if (!isFirstTime()) {
                            startActivity(new Intent(Splash_Act.this, SliderActivity.class).putExtra("HomeAct", "splash"));
                        } else
                            startActivity(new Intent(Splash_Act.this, HomeActivity.class));
                        finish();



                    }
                    mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                            Boolean pin_set = Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.pin_set_pref), false, Splash_Act.this);
                            Boolean change_pass_pref = Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.change_pass_pref), false, Splash_Act.this);
                            if (!act_stop) {
                                if (pin_set && change_pass_pref) {
                                    startActivity(new Intent(Splash_Act.this, Lock_Screen_QW.class));

                                    finish();
                                    return;
                                }
                                if (!isFirstTime()) {
                                    startActivity(new Intent(Splash_Act.this, SliderActivity.class).putExtra("HomeAct", "splash"));
                                } else
                                    startActivity(new Intent(Splash_Act.this, HomeActivity.class));
                                finish();
                            }

                        }

                    });
                }

            }
        };
        runnable1 = new Runnable() {
            @Override
            public void run() {
                progress++;
                start_progress = false;

            }
        };


    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(test_device_j5).addTestDevice(test_device_white).addTestDevice(vicky_S8).addTestDevice(getResources().getString(R.string.zegar_device)).build();
        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            handler.removeCallbacks(runnable);
        } catch (NullPointerException asd) {
        } catch (Exception er) {
        }
        try {
            handler.removeCallbacks(null);

        } catch (NullPointerException asd) {
        } catch (Exception er) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!is_net_connected()){
            SPLASH_TIME_OUT=1000;
        }
        try {
            stopService(new Intent(Splash_Act.this, FileSystemObserverService.class));
        } catch (NullPointerException asd) {
        } catch (Exception asd) {
        }
        ContextCompat.startForegroundService(getApplicationContext(), new Intent(Splash_Act.this, FileSystemObserverService.class));
        handler.postDelayed(runnable, SPLASH_TIME_OUT);
        progress = 0;
        new Thread(new Runnable() {
            public void run() {
                while (progress < 100) {
                    progress += 1;
                    // Update the progress bar and display the
                    //current value in the text view
                    handler.post(new Runnable() {
                        public void run() {
                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        Thread.sleep(60);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    public boolean is_net_connected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        act_stop = true;
    }

    @Override
    public void onBackPressed() {

    }
}
