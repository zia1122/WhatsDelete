package com.malam.whatsdelet.nolastseen.hiddenchat.Activities;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.tabs.TabLayout;
import com.malam.whatsdelet.nolastseen.hiddenchat.Fragments.ImagesFragment;
import com.malam.whatsdelet.nolastseen.hiddenchat.Fragments.VideosFragment;
import com.malam.whatsdelet.nolastseen.hiddenchat.Model.Note;
import com.malam.whatsdelet.nolastseen.hiddenchat.Service.FileOperations;
import com.malam.whatsdelet.nolastseen.hiddenchat.Service.FilesData;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.Shared;
import com.whatsdelete.Test.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static com.malam.whatsdelet.nolastseen.hiddenchat.Activities.MainActivity.ALLOW_KEY;
import static com.malam.whatsdelet.nolastseen.hiddenchat.Activities.MainActivity.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;
import static com.malam.whatsdelet.nolastseen.hiddenchat.Activities.MainActivity.getFromPref;

public class NewWhatsappStatus extends AppCompatActivity {
    ViewPager viewPager;
    TabLayout tabLayout;
    String path;
    static public Context context;
    static TextView title;
    Boolean images_found = false;
    ImageView back_btn;
    static ImageView delete;
    static boolean ad_request_sent = false;
    static int my_position = 0;
    public static int selected_items = 0;
    Boolean refresh_pressed = false, list_update_completed = false;
    static LinearLayout delete_layout;
    static ImageView save;
    public static List<Note> selected_list = new ArrayList<>();
    public static int position_call = 0;
    private Intent msgrcv;
    private AdView adaptive_adView;
    private FrameLayout adContainerView;
    public static InterstitialAd mInterstitialAd;
    private ShimmerFrameLayout shimmer_animation;
    ProgressDialog downloading_progress_bar, deleting_progress_bar;
    static Boolean firstTimePermission;
    SharedPreferences sharedPref;
    Bitmap bitmap;
    String fileFullPath;
    int jpgQuality, pngQuality;
    public static String APP_DIR;
    ProgressDialog progressDialog;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_whatsapp_status);
        context = NewWhatsappStatus.this;
        intilize_componenets();
        path = getIntent().getStringExtra("path");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.intrestialAd));
        //requestNewInterstitial();
        shimmer_animation = findViewById(R.id.ad_loader);
        shimmer_animation.startShimmerAnimation();
        load_adaptive_Banner();
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


        viewPager.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        progressDialog = new ProgressDialog(this);

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

        String path = Environment.getExternalStorageDirectory() + "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses";
        File file = new File(path);
        String startDir, secondDir, finalDirPath;

        if (file.exists()) {
            startDir = "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses";
        } else {

            startDir = "WhatsApp%2FMedia%2F.Statuses";
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

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri;
        if (resultCode == RESULT_OK) {
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


    public class SectionPagerAdapter extends FragmentPagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ImagesFragment();
                case 1:
                default:
                    return new VideosFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Images";
                case 1:
                default:
                    return "Videos";
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
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(getResources().getString(R.string.zegar_device)).addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        AdSize adSize = getAdSize();
        // Step 4 - Set the adaptive ad size on the ad view.
        adaptive_adView.setAdSize(adSize);

        // Step 5 - Start loading the ad in the background.
        adaptive_adView.loadAd(adRequest);

        adaptive_adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                adContainerView.setVisibility(View.GONE);
                shimmer_animation.setVisibility(View.GONE);
                shimmer_animation.stopShimmerAnimation();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adContainerView.setVisibility(View.VISIBLE);
                shimmer_animation.setVisibility(View.GONE);
                shimmer_animation.stopShimmerAnimation();
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

    private void intilize_componenets() {
        downloading_progress_bar = new ProgressDialog(NewWhatsappStatus.this);
        downloading_progress_bar.setTitle(getResources().getString(R.string.please_wait_while_downloading));
        downloading_progress_bar.setCancelable(false);
        /*deleting_progress_bar = new ProgressDialog(NewWhatsappStatus.this);
        deleting_progress_bar.setTitle(getResources().getString(R.string.please_wait_while_deleting));
        deleting_progress_bar.setCancelable(false);*/
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        //MainActivity.statuses = true;
        back_btn = findViewById(R.id.back_btn);
        delete_layout = findViewById(R.id.delete_layout);
        // delete = findViewById(R.id.delete);
        save = findViewById(R.id.saveGallery);
        title = findViewById(R.id.title);


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               download_images();
               download_Videos();
            }
        });

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

                try {
                    tabLayout.getTabAt(position).select();
                    position_call = position;
                    selected_items = 0;
                    Intent msgrcv = new Intent("Msg");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
                    switch (position) {
                        case 0:
                            ImagesFragment.select_list.clear();
                            Shared.getInstance().saveIntToPreferences(getResources().getString(R.string.delete_tabs_position_pref), 1, context);
                            break;
                        case 1:
                            ImagesFragment.select_list.clear();
                            Shared.getInstance().saveIntToPreferences(getResources().getString(R.string.delete_tabs_position_pref), 2, context);

                            break;
                        default:
                            break;
                    }
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//        dynamicSetTabLayoutMode(tabLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        save.setVisibility(View.VISIBLE);
    }

    public void download_images() {

        OutputStream fos;
        Bitmap bitmap = null;
         for (int i = 0; i < ImagesFragment.select_list.size(); i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Uri imageUri = Uri.parse(ImagesFragment.select_list.get(i).getFile_path());
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ContentResolver resolver = getContentResolver();
                ContentValues values = new ContentValues();

                values.put(MediaStore.MediaColumns.DISPLAY_NAME, getString(R.string.app_name) + ImagesFragment.select_list.get(i).getFile_name());
                if (ImagesFragment.select_list.get(i).getFile_path().endsWith(".png"))
                    values.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                else
                    values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");

                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM + File.separator + getString(R.string.app_name));


                Uri imageUri1 = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Log.d("Android11Problem", imageUri1.toString());
                try {
                    fos = resolver.openOutputStream(imageUri1);
                    assert bitmap != null;

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    Objects.requireNonNull(fos);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    FileOperations.saveAndRefreshFiles(new File(ImagesFragment.select_list.get(i).getFile_path()));
                    File imageFile = new File(ImagesFragment.select_list.get(i).getFile_path());
                    File destFile = new File(Environment.getExternalStorageDirectory().toString() + FilesData.getSavedFilesLocation(NewWhatsappStatus.this), imageFile.getName());

                    MediaScannerConnection.scanFile(this,
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

        }
        Intent saved_intent = new Intent(getResources().getString(R.string.intent_file_saved_images));
                        LocalBroadcastManager.getInstance(this).sendBroadcast(saved_intent);
        selected_items = 0;
        check_selected_items();
        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_images_pref), false, context);
         ImagesFragment.mReAdapter.notifyDataSetChanged();
         ImagesFragment.select_list.clear();
          Toast.makeText(context, getResources().getString(R.string.image_in_gallery), Toast.LENGTH_LONG).show();
    }


    public void download_Videos() {

        OutputStream fos;
        Bitmap bitmap = null;
                        for(
        int i = 0; i<VideosFragment.selected_list.size();i++)

        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                File destFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM)
                        + "/DownloadedStatuses");
                InputStream inputStream = null;

                try {
                    inputStream = context.getContentResolver().openInputStream(Uri.parse(VideosFragment.selected_list.get(i).getFile_path()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (!destFile.exists()) {
                    destFile.mkdirs();
                }
                File file = new File(VideosFragment.selected_list.get(i).getFile_path());
                File imageFile = new File(destFile, file.getName());
                if (!imageFile.exists()) {
                    try {
                        imageFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    OutputStream out = new FileOutputStream(imageFile);
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    out.close();
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                continue;
            }
            try {
                FileOperations.saveAndRefreshFiles(new File(VideosFragment.selected_list.get(i).getFile_path()));

                File videoFile = new File(VideosFragment.selected_list.get(i).getFile_path());
                File destFile = new File(Environment.getExternalStorageDirectory().toString() + FilesData.getSavedFilesLocation(NewWhatsappStatus.this), videoFile.getName());

                MediaScannerConnection.scanFile(this,
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
            new Timer().schedule(new TimerTask() {

                @Override
                public void run() {
                    finish();

                }
            },3000);

    }
        Intent saved_intent_video = new Intent(getResources().getString(R.string.intent_file_saved_videos));
                        LocalBroadcastManager.getInstance(this).sendBroadcast(saved_intent_video);
        selected_items = 0;
        check_selected_items();
        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_images_pref), false, context);
        VideosFragment.mReAdapter.notifyDataSetChanged();
        VideosFragment.selected_list.clear();
            }



     /*try {
        File imageFile = new File(VideosFragment.selected_list.get(i).getFile_path());
        File destFile = new File(Environment.getExternalStorageDirectory().toString() + FilesData.getSavedstatusesLocation(), imageFile.getName());
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
    }*/

    public static void check_selected_items() {
        if (selected_items > 0) {
            title.setText("Selected Items ( " + selected_items + " )");
            delete_layout.setVisibility(View.VISIBLE);
            if (position_call == 1) {
                save.setVisibility(View.VISIBLE);
            } else {
                save.setVisibility(View.VISIBLE);
            }
        } else {
            title.setText(R.string.statu_saver);
            delete_layout.setVisibility(View.GONE);
            Shared.getInstance().saveBooleanToPreferences("long_pressd_status_pref", false, context);
            Shared.getInstance().saveBooleanToPreferences("long_pressd_downstatus_pref", false, context);
        }
    }



    public boolean is_net_availabel() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {

        if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.long_pressd_status_pref), false, this)) {
            Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_status_pref), false, this);
            title.setText(getResources().getString(R.string.statu_saver));
            ImagesFragment.select_list.clear();
            VideosFragment.selected_list.clear();
            ImagesFragment.mReAdapter.notifyDataSetChanged();
            VideosFragment.mReAdapter.notifyDataSetChanged();
            selected_items = 0;
            check_selected_items();
        }
        else {

            ImagesFragment.net_is_connected=false;
            VideosFragment.net_is_connected=false;

            if (mInterstitialAd.isLoaded()) mInterstitialAd.show();
            else {
                position_call=0;
                finish();
            }
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    position_call=0;
                    finish();
                }
            });
        }

    }


    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(getResources().getString(R.string.vicky_s8)).addTestDevice(getResources().getString(R.string.zegar_device)).build();
        mInterstitialAd.loadAd(adRequest);
    }
    public static String getShortName(String path)
    {
        if (path.endsWith("/")) path = path.substring(0, path.length() - 1);

        int pos = path.lastIndexOf('/');

        if (pos == -1) return path;

        return path.substring(pos + 1);
    }

}