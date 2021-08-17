package com.malam.whatsdelet.nolastseen.hiddenchat.Activities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;
import com.whatsdelete.Test.R;

import static com.malam.whatsdelet.nolastseen.hiddenchat.Activities.MainActivity.ALLOW_KEY;
import static com.malam.whatsdelet.nolastseen.hiddenchat.Activities.MainActivity.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;
import static com.malam.whatsdelet.nolastseen.hiddenchat.Activities.MainActivity.getFromPref;


public class SliderActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private TextView[] dots;
    private int[] layouts;
    private TextView btnSkip;
    private ImageView  btndone, btnNext,buttonBack;
    private SpringDotsIndicator indicator;
    boolean next = true;
    public static final String COMPLETED_ONBOARDING_PREF_NAME = "started";
    boolean notify_allow = true;
    public static boolean howtouse_call = false;
    boolean notifycall = false;
    boolean storage_allow = true;
    public static InterstitialAd mInterstitialAd;
    String HomeAct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_sliding);
        HomeAct = getIntent().getStringExtra("HomeAct");

        viewPager = findViewById(R.id.view_pager);
        btnSkip = findViewById(R.id.btnSkip);
        btndone = findViewById(R.id.btn_done);
        btnNext = findViewById(R.id.btn_next);
        indicator = findViewById(R.id.spring_dots_indicator);
        buttonBack = findViewById(R.id.backButton);

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        // Check if we need to display our OnboardingSupportFragment
        sharedPreferences.edit().putBoolean(
                SliderActivity.COMPLETED_ONBOARDING_PREF_NAME, true).apply();
        // layouts of all welcome sliders
        layouts = new int[]{
                R.layout.slider_first,
                R.layout.slider_seocnd,
                R.layout.slider_third};

        // adding bottom dots

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        indicator.setViewPager(viewPager);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomeScreen();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                }
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(-1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                }
            }
        });
        btndone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomeScreen();

            }
        });
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.intrestialAd));
        if (!howtouse_call)
            requestNewInterstitial();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(getResources().getString(R.string.zegar_device)).addTestDevice(getResources().getString(R.string.zegar_device)).build();
        mInterstitialAd.loadAd(adRequest);
    }


    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        if (mInterstitialAd.isLoaded()) mInterstitialAd.show();
        else {
            startActivity(new Intent(SliderActivity.this, Storage_permission.class));
            finish();
        }
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                startActivity(new Intent(SliderActivity.this, Storage_permission.class));
                finish();
            }
        });


    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {

            // changing the next button text 'NEXT' / 'Proceed'

            if (position == 0) {
                // last page. make button text to Proceed
                next = true;
                btndone.setVisibility(View.GONE);
                btnSkip.setVisibility(View.VISIBLE);
                btnNext.setVisibility(View.VISIBLE);
                buttonBack.setVisibility(View.GONE);
            }
            else if (position == 2) {
                // last page. make button text to Proceed
                next = true;
                btndone.setVisibility(View.VISIBLE);
                btnSkip.setVisibility(View.GONE);
                btnNext.setVisibility(View.GONE);
                buttonBack.setVisibility(View.VISIBLE);
            }
            else {
                // still pages are left
                btndone.setVisibility(View.GONE);
                btnSkip.setVisibility(View.GONE);
                btnNext.setVisibility(View.VISIBLE);
                buttonBack.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };

    boolean hasNotificationAccess() {
        ContentResolver contentResolver = getContentResolver();
        String enabledNotificationListeners = android.provider.Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = getPackageName();
        // check to see if the enabledNotificationListeners String contains our package name
        return !(enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName));
    }

    private void showNotifcation_sAlert() {/*
    final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
    View layout1 = LayoutInflater.from(this).inflate(R.layout.notification_access_dialog, (ViewGroup) findViewById(R.id.your_dialog_root_element));
    layout1.setMinimumWidth(100);
    alertDialog.setView(layout1);
    alertDialog.setCancelable(false);
    alertDialog.show();
    try {
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    } catch (NullPointerException ne) {
    } catch (Exception e) {
    }
    Button ok_btn = (Button) layout1.findViewById(R.id.ok_btn);
    ImageView cancel_btn = (ImageView) layout1.findViewById(R.id.cancel_btn);
    ok_btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Intent go_to_settings = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                go_to_settings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(go_to_settings);
            } catch (ActivityNotFoundException Aeo) {
                Toast.makeText(SliderActivity.this, getResources().getString(R.string.opps_somthing_went_wrong), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(SliderActivity.this, getResources().getString(R.string.opps_somthing_went_wrong), Toast.LENGTH_LONG).show();
            }
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
    alertDialog.show();*/


        Intent go_to_settings = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        go_to_settings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(go_to_settings);
    }

    @Override
    public void onBackPressed() {
        if (HomeAct.equalsIgnoreCase("HomeAct") || HomeAct.equalsIgnoreCase("Settings")) {
            finish();
        }
    }

    private void marshmallow_permissions_WRITE_EXTERNAL_STORAGE() {
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


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    myViewPagerAdapter = new MyViewPagerAdapter();
                    viewPager.setAdapter(myViewPagerAdapter);
                    viewPager.setCurrentItem(4);
                    storage_allow = true;
                } else {
                    mypermissions();/* showSettingsAlert();*/
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;

                // other 'case' lines to check for other
                // permissions this app might request
            }
        }
    }

    private void showSettingsAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(SliderActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setCancelable(false);
        alertDialog.setMessage("App needs to access Permissions.");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DON'T ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mypermissions();
                    }
                });
        alertDialog.show();
    }

    private void mypermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            marshmallow_permissions_WRITE_EXTERNAL_STORAGE();
        }

    }


    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);


            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (notifycall) {
            viewPager.setAdapter(myViewPagerAdapter);
            viewPager.setCurrentItem(4);
        }
    }
}