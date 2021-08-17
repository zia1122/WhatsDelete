package com.malam.whatsdelet.nolastseen.hiddenchat.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.os.storage.StorageManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.malam.whatsdelet.nolastseen.hiddenchat.Adapter.NotesAdapter;
import com.malam.whatsdelet.nolastseen.hiddenchat.Fragments.Deleted_audio_frag;
import com.malam.whatsdelet.nolastseen.hiddenchat.Fragments.Deleted_docs_frag;
import com.malam.whatsdelet.nolastseen.hiddenchat.Fragments.Deleted_images_frag;
import com.malam.whatsdelet.nolastseen.hiddenchat.Fragments.Deleted_videos_frag;
import com.malam.whatsdelet.nolastseen.hiddenchat.Fragments.Deleted_voice_frag;
import com.malam.whatsdelet.nolastseen.hiddenchat.Fragments.Instagram_Frag;
import com.malam.whatsdelet.nolastseen.hiddenchat.Fragments.Messanger_Frag;
import com.malam.whatsdelet.nolastseen.hiddenchat.Fragments.Status_frag;
import com.malam.whatsdelet.nolastseen.hiddenchat.Fragments.Whats_App_Frag;
import com.malam.whatsdelet.nolastseen.hiddenchat.Helper.DatabaseHelper;
import com.malam.whatsdelet.nolastseen.hiddenchat.Helper.DatabaseHelper_new;
import com.malam.whatsdelet.nolastseen.hiddenchat.Model.Note_new;
import com.malam.whatsdelet.nolastseen.hiddenchat.Service.FileOperations;
import com.malam.whatsdelet.nolastseen.hiddenchat.Service.FileSystemObserverService;
import com.malam.whatsdelet.nolastseen.hiddenchat.Service.FilesData;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.Shared;
import com.onesignal.OneSignal;
import com.whatsdelete.Test.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private static final int MY_REQUEST_CODE = 652;
    private static final String TAG = "Whatsadel";
    public static boolean status = false;
    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static ViewPagerAdapter adapter, adapter_saved_status;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 200;
    public static final String ALLOW_KEY = "ALLOWED";
    public static final String CAMERA_PREF = "camera_pref";
    public static boolean statuses = false;
    Boolean first_time_loaded = false, whatsapps_oppened_from_app = false;
    public static TextView title;
    String path;
    private AppUpdateManager appUpdateManager;
    public static String file_old = "";

    public static ImageView delete_icon, refresh, refresh_dialog, save;
    public static LinearLayout refresh_n_whatsapp_layout, delete;
    LinearLayout status_saver_layout, chat_layout, images_layout, videos_layout, audio_layout, voice_layout, ducoment_layout;
    TextView status_saver_txtview, chat_txtview, images_txtview, videos_txtview, audio_txtview, voice_textview, ducoment_textview;
    public static Context context;
    public static int selected_items = 0, selected_tab_position = 0;
    String Audio;
    public DatabaseHelper db;
    DatabaseHelper_new databaseHelper_new;
    public static ImageView back_btn, open_whatsapp;
    public static InterstitialAd mInterstitialAd;
    public static Animation animation;
    public static Boolean animation_start = false, got_to_pin_set_act = false;
    public static android.app.AlertDialog progressDialog;

    static public String[] titles = {"Images", "Videos", "Audios", "Voices", "Documents"};
    private FrameLayout adContainerView;
    private AdView adaptive_adView;
    public static Boolean is_main_act_active = true;
    public static int position_call = 0;
    RelativeLayout drawer;
    public static ImageView  settings;
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 1044;
    UnifiedNativeAd nativeAd;
    private ShimmerFrameLayout shimmer_animation;
    Button statussaver;
    SharedPreferences sharedPref;
    static Boolean firstTimePermission;
    private static final String ONESIGNAL_APP_ID = "d21089d2-78c1-461c-acc6-1f82431dfa03";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FilesData.context = getApplicationContext();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        statussaver = findViewById(R.id.statussaver);
        statussaver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Downloaded_status.class));
            }
        });
        db = new DatabaseHelper(this);
        databaseHelper_new = new DatabaseHelper_new(this);
        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.chat_clear_pref), false, getApplicationContext());
        animation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        shimmer_animation=findViewById(R.id.ad_loader);
        shimmer_animation.startShimmerAnimation();

        load_adaptive_Banner();
        if (Build.VERSION.SDK_INT>=29){
            Audio = Environment.getExternalStorageDirectory().getAbsolutePath() + FilesData.WHATSAPP_STATUSES_LOCATION_ANDROID_11;
        }else {
            Audio = Environment.getExternalStorageDirectory().getAbsolutePath() + FilesData.WHATSAPP_STATUSES_LOCATION;
        }
        try {
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                animation_start = false;
                                    progressDialog.dismiss();
                                Toast.makeText(MainActivity.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                            } catch (IllegalArgumentException qwe) {
                            } catch (Exception qwe) {
                            }
                        }
                    }, 3000);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        } catch (IllegalArgumentException qwe) {
        } catch (Exception qwe) {
        }
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.intrestialAd));
        requestNewInterstitial();
        initilize_components();
        context = this;
        selected_items = 0;
        listners();
        try {
            NotesAdapter.selected_list.clear();
        } catch (NullPointerException sd) {
        }

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(appUpdateInfo, IMMEDIATE, MainActivity.this, MY_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        firstTimePermission = sharedPref.getBoolean("firstTimePermission", true);
        if (Build.VERSION.SDK_INT >= 29) {
            if (firstTimePermission == true) {

                openDirectory();
                firstTimePermission = false;
                editor.putBoolean("firstTimePermission", firstTimePermission);
                editor.commit();

            }

        } else {
            marshmallow_permissions_WRITE_EXTERNAL_STORAGE();
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void openDirectory() {

        String path = Environment.getExternalStorageDirectory() + "/Android/media/com.whatsapp/WhatsApp/Media";
        File file = new File(path);
        String startDir, secondDir, finalDirPath;

        if (file.exists()) {
            startDir = "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia";
        } else {

            startDir = "WhatsApp%2FMedia";
        }

        StorageManager sm = (StorageManager) getSystemService(Context.STORAGE_SERVICE);

        Intent intent = sm.getPrimaryStorageVolume().createOpenDocumentTreeIntent();


        Uri uri = intent.getParcelableExtra("android.provider.extra.INITIAL_URI");

        String scheme = uri.toString();

        Log.d("TAG", "INITIAL_URI scheme: " + scheme);

        scheme = scheme.replace("/root/", "/document/");

        finalDirPath = scheme + "%3A" + startDir;

        uri = Uri.parse(finalDirPath);

        intent.putExtra("android.provider.extra.INITIAL_URI", uri);

        Log.d("TAG", "uri: " + uri.toString());

        try {
            startActivityForResult(intent, 6);
        } catch (ActivityNotFoundException ignored) {

        }
    }

    private void showWrongPathDialog() {
        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
        View layout1 = LayoutInflater.from(this).inflate(R.layout.dilaog_path_not_found, findViewById(R.id.your_dialog_root_element));
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
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 29) {
                    openDirectory();
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
        alertDialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (progressDialog !=null || progressDialog.isShowing()) {
                progressDialog.dismiss();
//                progressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri;
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Log.e(TAG, "Update flow failed! Result code: " + resultCode);

            }
            if (data != null) {
                uri = data.getData();
                if (uri.getPath().endsWith(".Statuses")) {
                    Log.d("TAG", "onActivityResult: " + uri.getPath());
                    final int takeFlags = data.getFlags()
                            & (Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        getContentResolver().takePersistableUriPermission(uri, takeFlags);
                    }
                    // these are my SharedPerfernce values for remembering the path
                    Shared.getInstance().saveBooleanToPreferences(FilesData.HAS_PERMISSIONS_ANDROID_11, true, context);
                    Shared.getInstance().saveStringToPreferences(FilesData.STATUSES_FOLDER_PATH, uri.toString(), context);

                    // save any boolean in pref if user given the right path so we can use the path
                    // in future and avoid to ask permission more than one time

                    recreate();

                } else {
                    // dialog when user gave wrong path
                    showWrongPathDialog();
                }

            }
        }
    }

    private void load_adaptive_Banner() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        adContainerView = findViewById(R.id.ad_view_container);
        // Step 1 - Create an AdView and set the ad unit ID on it.
        adaptive_adView = new AdView(this);
        adaptive_adView.setAdUnitId(getString(R.string.bannerAd));
        adContainerView.addView(adaptive_adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(getResources().getString(R.string.vicky_s8)).addTestDevice(getResources().getString(R.string.zegar_device)).addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        AdSize adSize = getAdSize();
        // Step 4 - Set the adaptive ad size on the ad view.
        adaptive_adView.setAdSize(adSize);

        // Step 5 - Start loading the ad in the background.
        adaptive_adView.loadAd(adRequest);
        adaptive_adView.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                shimmer_animation.setVisibility(View.GONE);
                shimmer_animation.stopShimmerAnimation();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                shimmer_animation.stopShimmerAnimation();
                shimmer_animation.setVisibility(View.GONE);
            }
        });
    }
    private AdSize getAdSize() {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(getResources().getString(R.string.vicky_s8)).addTestDevice(getResources().getString(R.string.zegar_device)).build();
        mInterstitialAd.loadAd(adRequest);
    }

    private void listners() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                try {
                    viewPager.setCurrentItem(tab.getPosition(), true);

                } catch (IndexOutOfBoundsException ie) {
                } catch (Exception e) {
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    //case 0:
                        /*title.setText(getResources().getString(R.string.chat));
                        refresh.setVisibility(View.VISIBLE);
                        refresh_n_whatsapp_layout.setVisibility(View.VISIBLE);
                        delete.setVisibility(View.GONE);
                        selected_tab_position = 1;
                        status_saver_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        images_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        videos_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        audio_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        voice_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        ducoment_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        chat_layout.setBackground(getResources().getDrawable(R.mipmap.tab_active));

                        break;*/
                    case 0:
                        title.setText(getResources().getString(R.string.images));
                        refresh.setVisibility(View.VISIBLE);
                        refresh_n_whatsapp_layout.setVisibility(View.VISIBLE);
                        delete.setVisibility(View.GONE);
                        selected_tab_position = 1;
                        //chat_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        videos_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        audio_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        voice_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        ducoment_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        status_saver_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        images_layout.setBackground(getResources().getDrawable(R.mipmap.tab_active));
                        break;
                    case 1:
                        title.setText(getResources().getString(R.string.videos));
                        refresh.setVisibility(View.VISIBLE);
                        refresh_n_whatsapp_layout.setVisibility(View.VISIBLE);
                        delete.setVisibility(View.GONE);
                        selected_tab_position = 2;
                        voice_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        ducoment_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                       // chat_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        images_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        status_saver_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        audio_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        videos_layout.setBackground(getResources().getDrawable(R.mipmap.tab_active));
                        break;
                    case 2:
                        title.setText(getResources().getString(R.string.audio));
                        refresh.setVisibility(View.VISIBLE);
                        refresh_n_whatsapp_layout.setVisibility(View.VISIBLE);
                        delete.setVisibility(View.GONE);
                        selected_tab_position = 3;
                        //chat_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        images_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        audio_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        voice_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        ducoment_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        status_saver_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        videos_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        audio_layout.setBackground(getResources().getDrawable(R.mipmap.tab_active));
                        break;
                    case 3:
                        title.setText(getResources().getString(R.string.voice));
                        refresh.setVisibility(View.VISIBLE);
                        refresh_n_whatsapp_layout.setVisibility(View.VISIBLE);
                        delete.setVisibility(View.GONE);
                        selected_tab_position = 4;
                       // chat_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        images_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        videos_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        ducoment_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        status_saver_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        audio_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        voice_layout.setBackground(getResources().getDrawable(R.mipmap.tab_active));
                        break;
                    case 4:
                        title.setText(getResources().getString(R.string.ducoments));
                        refresh.setVisibility(View.VISIBLE);
                        refresh_n_whatsapp_layout.setVisibility(View.VISIBLE);
                        delete.setVisibility(View.GONE);
                        selected_tab_position = 5;
                        audio_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        voice_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        //chat_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        images_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        status_saver_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        videos_layout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        ducoment_layout.setBackground(getResources().getDrawable(R.mipmap.tab_active));
                        break;

                    default:
                        break;
                }

                selected_items = 0;
                try {
                    if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.long_pressd_pref), false, MainActivity.this)) {
                        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_pref), false, MainActivity.this);
                        Whats_App_Frag.mAdapter.notifyDataSetChanged();
                    }
                    if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.long_pressd_instagram_pref), false, context)) {
                        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_instagram_pref), false, context);
                        Instagram_Frag.mAdapter.notifyDataSetChanged();
                    }
                    if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.long_pressd_messenger_pref), false, context)) {
                        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_messenger_pref), false, context);
                        Messanger_Frag.mAdapter.notifyDataSetChanged();
                    }

                    if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.long_pressd_status_pref), false, MainActivity.this)) {
                        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_status_pref), false, MainActivity.this);
                        Status_frag.mReAdapter.notifyDataSetChanged();
                    }

                    if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.long_pressd_del_ducoments_pref), false, MainActivity.this)) {
                        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_del_ducoments_pref), false, MainActivity.this);
                        Deleted_docs_frag.mReAdapter.notifyDataSetChanged();
                    }

                    if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.long_pressd_deleted_voice_pref), false, MainActivity.this)) {
                        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_deleted_voice_pref), false, MainActivity.this);
                        Deleted_voice_frag.mReAdapter.notifyDataSetChanged();
                    }
                    if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.long_pressd_deleted_images_pref), false, MainActivity.this)) {
                        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_deleted_images_pref), false, MainActivity.this);
                        Deleted_images_frag.mReAdapter.notifyDataSetChanged();
                    }
                    if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.long_pressd_deleted_video_pref), false, MainActivity.this)) {
                        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_deleted_video_pref), false, MainActivity.this);
                        Deleted_videos_frag.mReAdapter.notifyDataSetChanged();
                    }
                    if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.long_pressd_deleted_audio_pref), false, MainActivity.this)) {
                        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_deleted_audio_pref), false, MainActivity.this);
                        Deleted_audio_frag.mReAdapter.notifyDataSetChanged();
                    }
                } catch (NullPointerException asd) {
                } catch (Exception er) {
                }
                try {
                    NotesAdapter.selected_list.clear();
                } catch (NullPointerException asd) {
                } catch (Exception er) {
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    boolean hasNotificationAccess() {
        ContentResolver contentResolver = getContentResolver();
        String enabledNotificationListeners = android.provider.Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = getPackageName();
        // check to see if the enabledNotificationListeners String contains our package name
        return !(enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName));
    }

    private void initilize_components() {
        status_saver_layout = findViewById(R.id.status_saver_layout);
        chat_layout = findViewById(R.id.chat_layout);
        images_layout = findViewById(R.id.images_layout);
        videos_layout = findViewById(R.id.videos_layout);
        audio_layout = findViewById(R.id.audio_layout);
        voice_layout = findViewById(R.id.voices_layout);
        ducoment_layout = findViewById(R.id.ducoment_layout);

        status_saver_txtview = findViewById(R.id.status_saver_txtview);
        chat_txtview = findViewById(R.id.chat_txtview);
        images_txtview = findViewById(R.id.images_txtview);
        videos_txtview = findViewById(R.id.videos_txtview);

        progressDialog = new android.app.AlertDialog.Builder(MainActivity.this).create();
        try {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (NullPointerException ne) {
        } catch (Exception e) {
        }
        View layout1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.refresh_dialog, findViewById(R.id.your_dialog_root_element));
        layout1.setMinimumWidth(100);
        progressDialog.setView(layout1);
        progressDialog.setCancelable(false);
        refresh_dialog = layout1.findViewById(R.id.progress);

        open_whatsapp = findViewById(R.id.open_whatsapp);
        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.view_pager);
        settings = findViewById(R.id.action_settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, com.malam.whatsdelet.nolastseen.hiddenchat.Activities.Settings.class));
            }
        });
        open_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent intent1 = getPackageManager().getLaunchIntentForPackage("com.whatsapp");
                    startActivity(intent1);
                    whatsapps_oppened_from_app = true;

                } catch (ActivityNotFoundException asd) {
                    Toast.makeText(MainActivity.this, "" + getResources().getString(R.string.opps_somthing_went_wrong), Toast.LENGTH_LONG).show();
                } catch (Exception asd) {
                    Toast.makeText(MainActivity.this, "" + getResources().getString(R.string.opps_somthing_went_wrong), Toast.LENGTH_LONG).show();
                }


            }
        });

        /*chat_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewPager.setCurrentItem(0, true);
            }
        });*/
        images_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewPager.setCurrentItem(0, true);
            }
        });
        videos_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewPager.setCurrentItem(1, true);
            }
        });
        audio_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewPager.setCurrentItem(2, true);
            }
        });
        voice_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewPager.setCurrentItem(3, true);
            }
        });
        ducoment_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewPager.setCurrentItem(4, true);
            }
        });

        Intent intent = getIntent();
        try {
            position_call = (int) intent.getExtras().get(getResources().getString(R.string.is_image_intent));

        } catch (Resources.NotFoundException e) {
            position_call = 0;
            e.printStackTrace();
        } catch (NullPointerException e) {
            position_call = 0;
            e.printStackTrace();
        }
        title = findViewById(R.id.title);
        delete = findViewById(R.id.delete_layout);
        delete_icon = findViewById(R.id.delete);
        save = findViewById(R.id.save_top);
        refresh = findViewById(R.id.refresh);
        setupViewPager(viewPager);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        refresh_n_whatsapp_layout = findViewById(R.id.refresh_n_whatsapp_layout);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!animation_start) {
                    animation_start = true;
                    try {
                        if (progressDialog !=null || progressDialog.isShowing()) {
                        progressDialog.show();
                        }
                    } catch (NullPointerException ner) {
                    } catch (Exception er) {
                    }
                    refresh_dialog.startAnimation(animation);
                    Intent msgrcv = new Intent(getResources().getString(R.string.intent_name_refresh));
                    LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
                }
            }
        });


        delete_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(MainActivity.this).create();
                View layout1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.delet_dialog, findViewById(R.id.your_dialog_root_element));
                layout1.setMinimumWidth(100);
                alertDialog.setView(layout1);
                alertDialog.show();
                try {
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                } catch (NullPointerException ne) {
                } catch (Exception e) {
                }
                LinearLayout ok_btn = layout1.findViewById(R.id.delete_ok);
                LinearLayout cancel_btn = layout1.findViewById(R.id.delete_cancle);

                ok_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        switch (Shared.getInstance().getIntValueFromPreference(getResources().getString(R.string.delete_tabs_position_pref), 0, MainActivity.this)) {
                            case 1:
                                String name = null;
                                System.out.println("chat_position_delete selected list size==> " + NotesAdapter.selected_list.size());
                                for (int i = 0; i < NotesAdapter.selected_list.size(); i++) {
                                    name = NotesAdapter.selected_list.get(i).getNote();
                                    try {
                                        try {
                                            db.deleteNote(NotesAdapter.selected_list.get(i));
                                            System.out.println("selected list databse data deleted of == " + name + " at position ==> " + i);
                                            List<Note_new> notesList = new ArrayList<>();
                                            notesList.clear();
                                            notesList.addAll(databaseHelper_new.getAllNotes());
                                            for (int j = 0; j < notesList.size(); j++) {
                                                if (name.equals(notesList.get(j).getNote())) {
                                                    databaseHelper_new.deleteNote(notesList.get(j));
                                                    String input = name;
                                                    input = input.replace(" ", "");
                                                    input = input.trim();
                                                    Shared.getInstance().saveIntToPreferences(input + getResources().getString(R.string.num_of_unread_msgs_pref), 0, getApplicationContext());
                                                }
                                            }
                                        } catch (IndexOutOfBoundsException iasd) {
                                            System.out.println("INdexoutofboundException==> " + iasd);
                                            Toast.makeText(MainActivity.this, "Opps Somthenig went wrong please try again ", Toast.LENGTH_LONG).show();
                                        } catch (Exception er) {
                                            Toast.makeText(MainActivity.this, "Opps Somthenig went wrong please try again ", Toast.LENGTH_LONG).show();
                                        }
                                    } catch (IndexOutOfBoundsException asd) {
                                        Toast.makeText(MainActivity.this, "Opps Somthenig went wrong please try again ", Toast.LENGTH_LONG).show();
                                        System.out.println("chat_position_delete name INdexoutBond Excerption==> " + "Name == " + name + " IndexoutofboundEception ==> " + asd);
                                    } catch (Exception er) {
                                    }
                                }
                                NotesAdapter.selected_pos.clear();
                                title.setText(getResources().getString(R.string.home));
                                try {
                                    NotesAdapter.selected_list.clear();
                                } catch (NullPointerException sd) {
                                }
                                Intent msgrcv = new Intent(getResources().getString(R.string.broad_cast_intent_for_main_chat));
                                LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(msgrcv);

                                break;
                            case 2:
                                break;
                            case 3:
                                for (int i = 0; i < Status_frag.selected_list.size(); i++) {
                                    File file = new File(Status_frag.selected_list.get(i).getFile_path());
                                    file.delete();
                                    try {
                                        Status_frag.muList.remove(Status_frag.selected_list.get(i));
                                    } catch (IndexOutOfBoundsException er) {
                                    } catch (Exception er) {
                                    }
                                }
                                Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_saved_files_pref), false, MainActivity.this);
                                Status_frag.selected_list.clear();
                                Status_frag.mReAdapter.notifyDataSetChanged();
                                title.setText(getResources().getString(R.string.whatsapp_status));
                                msgrcv = new Intent("refresh");
                                LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(msgrcv);
                                break;

                            case 7:
                                for (int i = 0; i < Deleted_voice_frag.selected_list.size(); i++) {
                                    File file = new File(Deleted_voice_frag.selected_list.get(i).getFile_path());
                                    file.delete();
                                    try {
                                        Deleted_voice_frag.muList.remove(Deleted_voice_frag.selected_list.get(i));
                                    } catch (IndexOutOfBoundsException er) {
                                    } catch (Exception er) {
                                    }
                                }
                                Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_deleted_voice_pref), false, MainActivity.this);
                                Deleted_voice_frag.selected_list.clear();
                                Deleted_voice_frag.mReAdapter.notifyDataSetChanged();
                                title.setText(getResources().getString(R.string.whatsapp_audio));
                                msgrcv = new Intent(getResources().getString(R.string.intent_file_saved_voice));
                                LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(msgrcv);

                                break;

                            case 4:
                                for (int i = 0; i < Deleted_images_frag.selected_list.size(); i++) {
                                    File file = new File(Deleted_images_frag.selected_list.get(i).getFile_path());
                                    file.delete();
                                    try {
                                        Deleted_images_frag.muList.remove(Deleted_images_frag.selected_list.get(i));
                                    } catch (IndexOutOfBoundsException er) {
                                    } catch (Exception er) {
                                    }
                                }
                                Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_deleted_images_pref), false, MainActivity.this);
                                Deleted_images_frag.selected_list.clear();
                                Deleted_images_frag.mReAdapter.notifyDataSetChanged();
                                title.setText(getResources().getString(R.string.deleted_media));
                                msgrcv = new Intent(getResources().getString(R.string.intent_file_saved_images));
                                LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(msgrcv);
                                break;
                            case 5:
                                for (int i = 0; i < Deleted_videos_frag.selected_list.size(); i++) {
                                    File file = new File(Deleted_videos_frag.selected_list.get(i).getFile_path());
                                    file.delete();
                                    try {
                                        Deleted_videos_frag.muList.remove(Deleted_videos_frag.selected_list.get(i));
                                    } catch (IndexOutOfBoundsException er) {
                                    } catch (Exception er) {
                                    }
                                }
                                Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_deleted_video_pref), false, MainActivity.this);
                                Deleted_videos_frag.selected_list.clear();
                                Deleted_videos_frag.mReAdapter.notifyDataSetChanged();
                                title.setText(getResources().getString(R.string.deleted_media));
                                msgrcv = new Intent(getResources().getString(R.string.intent_file_saved_videos));
                                LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(msgrcv);
                                break;
                            case 6:
                                for (int i = 0; i < Deleted_audio_frag.selected_list.size(); i++) {
                                    File file = new File(Deleted_audio_frag.selected_list.get(i).getFile_path());
                                    file.delete();
                                    try {
                                        Deleted_audio_frag.muList.remove(Deleted_audio_frag.selected_list.get(i));
                                    } catch (IndexOutOfBoundsException er) {
                                    } catch (Exception er) {
                                    }
                                }
                                Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_deleted_audio_pref), false, MainActivity.this);
                                Deleted_audio_frag.selected_list.clear();
                                Deleted_audio_frag.mReAdapter.notifyDataSetChanged();
                                title.setText(getResources().getString(R.string.deleted_media));
                                msgrcv = new Intent(getResources().getString(R.string.intent_file_saved_audio));
                                LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(msgrcv);
                                break;
                            case 8:
                                for (int i = 0; i < Deleted_docs_frag.selected_list.size(); i++) {
                                    File file = new File(Deleted_docs_frag.selected_list.get(i).getFile_path());
                                    file.delete();
                                    try {
                                        Deleted_docs_frag.muList.remove(Deleted_docs_frag.selected_list.get(i));
                                    } catch (IndexOutOfBoundsException er) {
                                    } catch (Exception er) {
                                    }
                                }
                                Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_del_ducoments_pref), false, MainActivity.this);
                                Deleted_docs_frag.selected_list.clear();
                                Deleted_docs_frag.mReAdapter.notifyDataSetChanged();
                                title.setText(getResources().getString(R.string.deleted_media));
                                msgrcv = new Intent(getResources().getString(R.string.intent_file_saved_docs));
                                LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(msgrcv);

                                break;
                            default:
                                break;
                        }
                        /* selected_position.clear();*/
                        selected_items = 0;
                        delete.setVisibility(View.GONE);
                        refresh.setVisibility(View.VISIBLE);
                        refresh_n_whatsapp_layout.setVisibility(View.VISIBLE);
                        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_pref), false, MainActivity.this);
                        alertDialog.dismiss();

                        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.chat_clear_pref), false, getApplicationContext());
                        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.chat_clear_messenger_pref), false, getApplicationContext());

                    }
                });
                cancel_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();

                    }
                });
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                switch (Shared.getInstance().getIntValueFromPreference(getResources().getString(R.string.delete_tabs_position_pref), 0, MainActivity.this)) {
                    case 1:

                        break;
                    case 2:
                        for (int i = 0; i < Status_frag.selected_list.size(); i++) {
                            try {
                                File imageFile = new File(Status_frag.selected_list.get(i).getFile_path());
                                File destFile = new File(Environment.getExternalStorageDirectory().toString() + FilesData.getSavedFilesLocation(), imageFile.getName());
                                FileOperations.saveAndRefreshFiles(imageFile, destFile);

                                MediaScannerConnection.scanFile(getApplicationContext(),
                                        new String[]{destFile.toString()}, null,
                                        new MediaScannerConnection.OnScanCompletedListener() {
                                            public void onScanCompleted(String path, Uri uri) {
                                                Log.i("ExternalStorage", "Scanned " + path + ":");
                                                Log.i("ExternalStorage", "-> uri=" + uri);
                                            }
                                        });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        Intent saved_intent = new Intent(getResources().getString(R.string.intent_file_saved_images));
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(saved_intent);
                        selected_items = 0;
                        check_selected_items();
                        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_status_pref), false, context);
                        Status_frag.mReAdapter.notifyDataSetChanged();
                        Toast.makeText(context, "Image(s) Saved in Gallery", Toast.LENGTH_LONG).show();

                        break;
                    case 3:
                        break;


                    case 7:
                        for (int i = 0; i < Deleted_voice_frag.selected_list.size(); i++) {
                            File file = new File(Deleted_voice_frag.selected_list.get(i).getFile_path());
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                            Toast.makeText(context, "File Saved in Gallery", Toast.LENGTH_LONG).show();

                        }
                        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_deleted_voice_pref), false, MainActivity.this);
                        Deleted_voice_frag.selected_list.clear();
                        Deleted_voice_frag.mReAdapter.notifyDataSetChanged();
                        title.setText(getResources().getString(R.string.whatsapp_audio));

                        Intent delete_audio = new Intent(getResources().getString(R.string.intent_name_delete_audio));
                                        /*delete_audio.putExtra("package", "");
                                        delete_audio.putExtra("ticker", "");
                                        delete_audio.putExtra("title", "");
                                        delete_audio.putExtra("text", "");*/
                        LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(delete_audio);
                        break;

                    case 4:
                        for (int i = 0; i < Deleted_images_frag.selected_list.size(); i++) {
                            File file = new File(Deleted_images_frag.selected_list.get(i).getFile_path());
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                            Toast.makeText(context, "File Saved in Gallery", Toast.LENGTH_LONG).show();

                        }
                        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_deleted_images_pref), false, MainActivity.this);
                        Deleted_images_frag.selected_list.clear();
                        Deleted_images_frag.mReAdapter.notifyDataSetChanged();
                        title.setText(getResources().getString(R.string.deleted_media));
                        break;
                    case 5:
                        for (int i = 0; i < Deleted_videos_frag.selected_list.size(); i++) {
                            File file = new File(Deleted_videos_frag.selected_list.get(i).getFile_path());
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                            Toast.makeText(context, "File Saved in Gallery", Toast.LENGTH_LONG).show();

                        }
                        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_deleted_video_pref), false, MainActivity.this);
                        Deleted_videos_frag.selected_list.clear();
                        Deleted_videos_frag.mReAdapter.notifyDataSetChanged();
                        title.setText(getResources().getString(R.string.deleted_media));
                        break;
                    case 6:
                        for (int i = 0; i < Deleted_audio_frag.selected_list.size(); i++) {
                            File file = new File(Status_frag.selected_list.get(i).getFile_path());
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                            Toast.makeText(context, "File Saved in Gallery", Toast.LENGTH_LONG).show();

                        }
                        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_deleted_audio_pref), false, MainActivity.this);
                        Deleted_audio_frag.selected_list.clear();
                        Deleted_audio_frag.mReAdapter.notifyDataSetChanged();
                        title.setText(getResources().getString(R.string.deleted_media));
                        break;
                    case 8:
                        for (int i = 0; i < Deleted_docs_frag.selected_list.size(); i++) {
                            File file = new File(Status_frag.selected_list.get(i).getFile_path());
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                            Toast.makeText(context, "File Saved in Gallery", Toast.LENGTH_LONG).show();

                        }
                        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_del_ducoments_pref), false, MainActivity.this);
                        Deleted_docs_frag.selected_list.clear();
                        Deleted_docs_frag.mReAdapter.notifyDataSetChanged();
                        title.setText(getResources().getString(R.string.deleted_media));
                        break;
                    default:
                        break;
                }
                /* selected_position.clear();*/
                selected_items = 0;
                delete.setVisibility(View.GONE);
                refresh.setVisibility(View.VISIBLE);
                refresh_n_whatsapp_layout.setVisibility(View.VISIBLE);
                Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_pref), false, MainActivity.this);

                Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.chat_clear_pref), false, getApplicationContext());
                Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.chat_clear_messenger_pref), false, getApplicationContext());


            }
        });
    }


    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter_saved_status = new ViewPagerAdapter(getSupportFragmentManager());
      //  adapter.addFragment(new Chat_frag(), "Chat");
        adapter.addFragment(new Deleted_images_frag(), "Images");
        adapter.addFragment(new Deleted_videos_frag(), "Videos");
        adapter.addFragment(new Deleted_audio_frag(), "Audios");
        adapter.addFragment(new Deleted_voice_frag(), "Voices");
        adapter.addFragment(new Deleted_docs_frag(), "Documents");
        viewPager.setAdapter(adapter);
        switch (position_call) {
            case 0:

                viewPager.setCurrentItem(0);
                break;
            default:
                viewPager.setCurrentItem(position_call);
                break;

        }
    }

    private void sendFeedback() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("*/*");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.account_email)});
        i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name) + " FeedBack");
        startActivity(createEmailOnlyChooserIntent(i, "Send via email"));
    }

    public Intent createEmailOnlyChooserIntent(Intent source, CharSequence chooserTitle) {
        Stack<Intent> intents = new Stack<Intent>();
        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
                "", null));
        List<ResolveInfo> activities = getPackageManager()
                .queryIntentActivities(i, 0);

        for (ResolveInfo ri : activities) {
            Intent target = new Intent(source);
            target.setPackage(ri.activityInfo.packageName);
            intents.add(target);
        }
        if (!intents.isEmpty()) {
            Intent chooserIntent = Intent.createChooser(intents.remove(0),
                    chooserTitle);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                    intents.toArray(new Parcelable[intents.size()]));
            return chooserIntent;
        } else {
            return Intent.createChooser(source, chooserTitle);
        }
    }

    private void show_rating_dialoug() {
        final Dialog dialog;
        dialog = new Dialog(MainActivity.this);
        try {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (NullPointerException ne) {
        } catch (Exception e) {
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.rating_dialoug);
        Button submit = dialog.findViewById(R.id.submit);
        final RatingBar rating_bar = dialog.findViewById(R.id.rating_bar);
        rating_bar.setMax(5);
        rating_bar.setNumStars(5);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rating_bar.getRating() <= 3) {
                    sendFeedback();
                    dialog.dismiss();
                } else {
                    final String appPackageName = getPackageName();
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                .parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id="
                                        + appPackageName)));
                    }
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
        // The headline is guaranteed to be in every UnifiedNativeAd.

        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.GONE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.GONE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.GONE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }
        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.GONE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.GONE);
        }
        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.GONE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.GONE);
        }
        adView.setNativeAd(nativeAd);
        VideoController vc = nativeAd.getVideoController();
        if (vc.hasVideoContent()) {
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    super.onVideoEnd();
                }
            });
        } else {

        }
    }



    private void privacy_policy() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCancelable(true);
        LayoutInflater inflater = getLayoutInflater();

        View view = LayoutInflater.from(this).inflate(R.layout.privacy_policy, null);

        alertDialog.setView(view);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        TextView textView = view.findViewById(R.id.google_link);
        TextView textView2 = view.findViewById(R.id.google_link2);
        TextView textView3 = view.findViewById(R.id.email_link);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://policies.google.com/privacy"));
                startActivity(browserIntent);

            }
        });
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://policies.google.com/privacy"));
                startActivity(browserIntent);

            }
        });
        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("*/*");
                // i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(crashLogFile));
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.privacy_policy_email_us_link)});
                i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name) + " Feedback");

                startActivity(createEmailOnlyChooserIntent(i, "Send via email"));


            }
        });
        alertDialog.show();
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.dark_mode_pref), b, MainActivity.this);
        startActivity(new Intent(MainActivity.this, MainActivity.class));
        finish();
    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    public static void check_selected_items() {
        if (selected_items > 0) {
            title.setText("Selected Items ( " + selected_items + " )");
            delete.setVisibility(View.VISIBLE);
            refresh.setVisibility(View.GONE);
            refresh_n_whatsapp_layout.setVisibility(View.GONE);
        } else {
            title.setText(titles[position_call]);
            delete.setVisibility(View.GONE);
            refresh.setVisibility(View.VISIBLE);
            refresh_n_whatsapp_layout.setVisibility(View.VISIBLE);
            Shared.getInstance().saveBooleanToPreferences("long_pressd_pref", false, context);
            Shared.getInstance().saveBooleanToPreferences("long_pressd_images_pref", false, context);
            Shared.getInstance().saveBooleanToPreferences(context.getResources().getString(R.string.long_pressd_deleted_audio_pref), false, context);
            Shared.getInstance().saveBooleanToPreferences(context.getResources().getString(R.string.long_pressd_deleted_voice_pref), false, context);
            Shared.getInstance().saveBooleanToPreferences(context.getResources().getString(R.string.long_pressd_deleted_video_pref), false, context);
            Shared.getInstance().saveBooleanToPreferences(context.getResources().getString(R.string.long_pressd_deleted_images_pref), false, context);
            Shared.getInstance().saveBooleanToPreferences("long_pressd_video_pref", false, context);
            Shared.getInstance().saveBooleanToPreferences("long_pressd_status_pref", false, context);
            Shared.getInstance().saveBooleanToPreferences("long_pressd_whats_app_pref", false, context);
            Shared.getInstance().saveBooleanToPreferences("long_pressd_messenger_pref", false, context);
            Shared.getInstance().saveBooleanToPreferences("long_pressd_instagram_pref", false, context);
        }
    }


    public static Boolean getFromPref(Context context, String key) {
        SharedPreferences myPrefs = context.getSharedPreferences(CAMERA_PREF, Context.MODE_PRIVATE);
        return (myPrefs.getBoolean(key, false));
    }


    @Override
    public void onBackPressed() {
        if (statuses) {
            back_statuses();
        } else {
             if (viewPager.getCurrentItem() > 1) {
                viewPager.setCurrentItem(1);
            } else {
                int tab_position = Shared.getInstance().getIntValueFromPreference(getResources().getString(R.string.delete_tabs_position_pref), 2, this);

               /* if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.long_pressd_pref), false, this)) {
                    Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_pref), false, this);
                    // NotesAdapter.de_select_all();
                    Whats_App_Frag.mAdapter.notifyDataSetChanged();
                    title.setText("Chat");
                    delete.setVisibility(View.GONE);
                    refresh.setVisibility(View.VISIBLE);
                    refresh_n_whatsapp_layout.setVisibility(View.VISIBLE);
                    selected_items = 0;

                }*/ if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.long_pressd_deleted_audio_pref), false, this)) {
                    Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_deleted_audio_pref), false, this);
                    delete.setVisibility(View.GONE);
                    refresh.setVisibility(View.VISIBLE);
                    refresh_n_whatsapp_layout.setVisibility(View.VISIBLE);
                    title.setText(getResources().getString(R.string.audio));
                    Deleted_audio_frag.mReAdapter.notifyDataSetChanged();
                    Deleted_audio_frag.selected_list.clear();
                    selected_items = 0;
                } else if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.long_pressd_deleted_images_pref), false, this)) {
                    Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_deleted_images_pref), false, this);
                    delete.setVisibility(View.GONE);
                    refresh.setVisibility(View.VISIBLE);
                    refresh_n_whatsapp_layout.setVisibility(View.VISIBLE);
                    title.setText(getResources().getString(R.string.images));
                    Deleted_images_frag.mReAdapter.notifyDataSetChanged();
                    Deleted_images_frag.selected_list.clear();
                    selected_items = 0;
                } else if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.long_pressd_deleted_video_pref), false, this)) {
                    Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_deleted_video_pref), false, this);
                    delete.setVisibility(View.GONE);
                    refresh.setVisibility(View.VISIBLE);
                    refresh_n_whatsapp_layout.setVisibility(View.VISIBLE);
                    title.setText(getResources().getString(R.string.videos));
                    Deleted_videos_frag.selected_list.clear();
                    Deleted_videos_frag.mReAdapter.notifyDataSetChanged();
                    selected_items = 0;
                } else if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.long_pressd_deleted_voice_pref), false, this)) {
                    Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_deleted_voice_pref), false, this);
                    delete.setVisibility(View.GONE);
                    refresh.setVisibility(View.VISIBLE);
                    refresh_n_whatsapp_layout.setVisibility(View.VISIBLE);
                    title.setText(getResources().getString(R.string.voice));
                    Deleted_voice_frag.mReAdapter.notifyDataSetChanged();
                    Deleted_voice_frag.selected_list.clear();
                    selected_items = 0;
                } else if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.long_pressd_del_ducoments_pref), false, this)) {
                    Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_del_ducoments_pref), false, this);
                    delete.setVisibility(View.GONE);
                    refresh.setVisibility(View.VISIBLE);
                    refresh_n_whatsapp_layout.setVisibility(View.VISIBLE);
                    title.setText(getResources().getString(R.string.documents));
                    Deleted_docs_frag.mReAdapter.notifyDataSetChanged();
                    Deleted_docs_frag.selected_list.clear();
                    selected_items = 0;
                } else {
                    Whats_App_Frag.unifiedNativeAd = null;
                    Messanger_Frag.unifiedNativeAd = null;
                    Instagram_Frag.unifiedNativeAd = null;
                    Deleted_images_frag.my_unifiedNativeAd = null;
                    if (mInterstitialAd.isLoaded()) mInterstitialAd.show();
                    else {
                        finish();
                    }
                    mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                            finish();

                        }
                    });
                }
            }
            try {
                NotesAdapter.selected_list.clear();
            } catch (NullPointerException asd) {
            } catch (Exception erwqer) {
            }
            is_main_act_active = false;
        }

    }

    private void exite_dialog() {
        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
        View layout1 = LayoutInflater.from(this).inflate(R.layout.exit_dialog, findViewById(R.id.your_dialog_root_element));
        layout1.setMinimumWidth(100);
        alertDialog.setView(layout1);

        if (nativeAd != null) {
            FrameLayout frameLayout = layout1.findViewById(R.id.adview_fram);
            UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater().inflate(R.layout.ad_unified_new, null);
            populateUnifiedNativeAdView(nativeAd, adView);

            frameLayout.addView(adView);
        }

        alertDialog.show();
        try {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (NullPointerException ne) {
        } catch (Exception e) {
        }
        Button cancel_btn = layout1.findViewById(R.id.no_btn);
        Button ok_btn = layout1.findViewById(R.id.ok_btn);
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                alertDialog.dismiss();
            }

        });
        Button rate_btn = layout1.findViewById(R.id.rate_btn);
        rate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_rating_dialoug();
                alertDialog.dismiss();
            }

        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void back_statuses() {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        //adapter.addFragment(new Chat_frag(), getResources().getString(R.string.chat));
        adapter.addFragment(new Deleted_images_frag(), getResources().getString(R.string.images));
        adapter.addFragment(new Deleted_videos_frag(), getResources().getString(R.string.videos));
        adapter.addFragment(new Deleted_audio_frag(), getResources().getString(R.string.audio));
        adapter.addFragment(new Deleted_voice_frag(), getResources().getString(R.string.voices));
        adapter.addFragment(new Deleted_docs_frag(), getResources().getString(R.string.ducoments));
        viewPager.setAdapter(adapter);
        MainActivity.title.setText(getResources().getString(R.string.status));
        MainActivity.viewPager.setCurrentItem(1);
        MainActivity.tabLayout.setVisibility(View.VISIBLE);
        statuses = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            is_main_act_active = false;
            Whats_App_Frag.unifiedNativeAd = null;
            Messanger_Frag.unifiedNativeAd = null;
            Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_pref), false, this);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        position_call = 0;
        is_main_act_active = true;
        check_selected_items();
        if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.chat_clear_pref), false, MainActivity.this)) {
            String name = "";
            try {
                name = NotesAdapter.notesList.get(Shared.getInstance().getIntValueFromPreference(getResources().getString(R.string.chat_position_pref), 0, MainActivity.this)).getNote();
                try {
                    db.deleteNote(NotesAdapter.notesList.get(Shared.getInstance().getIntValueFromPreference(getResources().getString(R.string.chat_position_pref), 0, MainActivity.this)));
                    System.out.println("chat_position_main_Act==> " + Shared.getInstance().getIntValueFromPreference(context.getResources().getString(R.string.chat_position_pref), 0, MainActivity.this));
                    List<Note_new> notesList = new ArrayList<>();
                    notesList.clear();
                    Intent msgrcv = new Intent(getResources().getString(R.string.intent_name_refresh));
                    LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(msgrcv);
                } catch (IndexOutOfBoundsException asd) {

                } catch (Exception er) {
                }
            } catch (IndexOutOfBoundsException iasd) {
                System.out.println("chat_position_main_Act_ WhatsApp INdex out Exception==> " + iasd);
            } catch (Exception er) {
                System.out.println("chat_position_main_Act_ WhatsApp Exception==> " + er);
            }
        }

        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.chat_clear_pref), false, getApplicationContext());
        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.chat_clear_messenger_pref), false, getApplicationContext());
        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.chat_clear_instagram_pref), false, getApplicationContext());


        if (!status) {
            Intent intent = getIntent();
            try {
                position_call = (int) intent.getExtras().get(getResources().getString(R.string.is_image_intent));

            } catch (Resources.NotFoundException e) {
                position_call = 0;
                e.printStackTrace();
            } catch (NullPointerException e) {
                position_call = 0;
                e.printStackTrace();
            }
            {
                if (!first_time_loaded) {
                    setupViewPager(viewPager);
                    first_time_loaded = true;
                }
            }
        } else setupViewPager(viewPager);

        if (got_to_pin_set_act) {
            Boolean asd = Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.pin_set_pref), false, MainActivity.this);
            if (asd) {

            } else {
//                drawer_change_password_switch.setChecked(false);
            }
            got_to_pin_set_act = false;
        }
        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.from_saved_files_pref), false, MainActivity.this);
        if (whatsapps_oppened_from_app) {
            whatsapps_oppened_from_app = false;
            startActivity(new Intent(MainActivity.this, MainActivity.class));
            finish();
        }

        try {
            stopService(new Intent(MainActivity.this, FileSystemObserverService.class));
        } catch (NullPointerException asd) {
        } catch (Exception asd) {
        }
        ContextCompat.startForegroundService(getApplicationContext(), new Intent(MainActivity.this, FileSystemObserverService.class));
        if (!hasNotificationAccess()) {
            showNotifcation_sAlert();
        }
        //mypermissions();
        appUpdateManager.getAppUpdateInfo()
                .addOnSuccessListener(
                        new OnSuccessListener<AppUpdateInfo>() {
                            @Override
                            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                                /*...*/
                                if (appUpdateInfo.updateAvailability()
                                        == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
// If an in-app update is already running, resume the update.
                                    try {
                                        appUpdateManager.startUpdateFlowForResult(
                                                appUpdateInfo,
                                                IMMEDIATE,
                                                MainActivity.this,
                                                MY_REQUEST_CODE);
                                    } catch (IntentSender.SendIntentException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
    }


    private void showNotifcation_sAlert() {


        Intent go_to_settings = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        go_to_settings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(go_to_settings);
    }

    private void mypermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(this, Storage_permission.class));
            finish();
        }

    }


}
