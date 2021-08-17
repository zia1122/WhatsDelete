package com.malam.whatsdelet.nolastseen.hiddenchat.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.malam.whatsdelet.nolastseen.hiddenchat.Fragments.DownloadedStatus_frag;
import com.malam.whatsdelet.nolastseen.hiddenchat.Fragments.Status_frag;
import com.malam.whatsdelet.nolastseen.hiddenchat.Model.Note;
import com.malam.whatsdelet.nolastseen.hiddenchat.Service.FileOperations;
import com.malam.whatsdelet.nolastseen.hiddenchat.Service.FilesData;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.Shared;
import com.google.android.material.tabs.TabLayout;
import com.whatsdelete.Test.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Downloaded_status extends AppCompatActivity {
    View root_view;
    static public Context context;

    Boolean images_found = false;
    ImageView back_btn;
    static ImageView delete;
    static boolean ad_request_sent = false;
    static int my_position = 0;
    public static int selected_items = 0;
    Boolean refresh_pressed = false, list_update_completed = false;
    static TextView title;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ViewPagerAdapter adapter;
    static LinearLayout delete_layout;
    static ImageView save;
    public static List<Note> selected_list = new ArrayList<>();
    public static int position_call = 0;
    private Intent msgrcv;
    private AdView adaptive_adView;
    private FrameLayout adContainerView;
    public static InterstitialAd mInterstitialAd;
    private ShimmerFrameLayout shimmer_animation;
    ProgressDialog downloading_progress_bar,deleting_progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloaded_status);
        context = getApplicationContext();
        FilesData.context = getApplicationContext();
        intilize_componenets();
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.intrestialAd));
        requestNewInterstitial();
        shimmer_animation = findViewById(R.id.ad_loader);
        shimmer_animation.startShimmerAnimation();
        load_adaptive_Banner();
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
        downloading_progress_bar = new ProgressDialog(Downloaded_status.this);
        downloading_progress_bar.setTitle(getResources().getString(R.string.please_wait_while_downloading));
        downloading_progress_bar.setCancelable(false);
        deleting_progress_bar = new ProgressDialog(Downloaded_status.this);
        deleting_progress_bar.setTitle(getResources().getString(R.string.please_wait_while_deleting));
        deleting_progress_bar.setCancelable(false);
        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.view_pager);
        setupViewPager(viewPager);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        MainActivity.statuses = true;
        back_btn = findViewById(R.id.back_btn);
        delete_layout = findViewById(R.id.delete_layout);
        delete = findViewById(R.id.delete);
        save = findViewById(R.id.save_top);
        title = findViewById(R.id.title);


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    delet_statuses();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new download_images().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
                    if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.long_pressd_status_pref), false, Downloaded_status.this)) {
                        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_status_pref), false, Downloaded_status.this);
                        Status_frag.mReAdapter.notifyDataSetChanged();
                    }
                    if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.long_pressd_downstatus_pref), false, Downloaded_status.this)) {
                        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_downstatus_pref), false, Downloaded_status.this);
                        DownloadedStatus_frag.mReAdapter.notifyDataSetChanged();
                    }
                    check_selected_items();
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
    public void dynamicSetTabLayoutMode(TabLayout tabLayout) {
        int tabWidth = calculateTabWidth(tabLayout);
        int screenWidth =  getApplicationContext().getResources().getDisplayMetrics().widthPixels;
        if (tabWidth <= screenWidth) {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
    }

    private int calculateTabWidth(TabLayout tabLayout) {
        int tabWidth = 0;
        for (int i = 0; i < tabLayout.getChildCount(); i++) {
            final View view = tabLayout.getChildAt(i);
            view.measure(0, 0);
            tabWidth += view.getMeasuredWidth();
        }
        return tabWidth;
    }

    @Override
    protected void onResume() {
        super.onResume();
        delete.setVisibility(View.GONE);
        save.setVisibility(View.VISIBLE);
    }

    public class download_images extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (downloading_progress_bar != null || !downloading_progress_bar.isShowing())
                downloading_progress_bar.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
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
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent saved_intent = new Intent(getResources().getString(R.string.intent_file_saved_images));
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(saved_intent);
            selected_items = 0;
            check_selected_items();
            Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_status_pref), false, context);
            Status_frag.mReAdapter.notifyDataSetChanged();
            Toast.makeText(context, "Image(s) Saved in Gallery", Toast.LENGTH_LONG).show();
            Status_frag.selected_list.clear();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (downloading_progress_bar.isShowing()) {
                        downloading_progress_bar.dismiss();
                    }
                }
            }, 1000);
        }
    }

    private void delet_statuses() {
        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(Downloaded_status.this).create();
        View layout1 = LayoutInflater.from(Downloaded_status.this).inflate(R.layout.delet_dialog, findViewById(R.id.your_dialog_root_element));
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
                new delete_statuses().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                alertDialog.dismiss();

            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });
    }
    public class delete_statuses extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (deleting_progress_bar != null || !deleting_progress_bar.isShowing())
                deleting_progress_bar.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < DownloadedStatus_frag.selected_list.size(); i++) {
                File file = new File(DownloadedStatus_frag.selected_list.get(i).getFile_path());
                file.delete();
                try {
                    DownloadedStatus_frag.muList.remove(DownloadedStatus_frag.selected_list.get(i));
                } catch (IndexOutOfBoundsException er) {
                } catch (Exception er) {
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_saved_files_pref), false, Downloaded_status.this);
            DownloadedStatus_frag.selected_list.clear();
            DownloadedStatus_frag.mReAdapter.notifyDataSetChanged();
            title.setText(getResources().getString(R.string.statu_saver));
            msgrcv = new Intent("refresh");
            LocalBroadcastManager.getInstance(Downloaded_status.this).sendBroadcast(msgrcv);
            selected_items = 0;
            check_selected_items();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (deleting_progress_bar.isShowing()) {
                        deleting_progress_bar .dismiss();
                    }
                }
            }, 1000);
        }
    }


    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Status_frag(), getResources().getString(R.string.statu_saver));
        adapter.addFragment(new DownloadedStatus_frag(), getResources().getString(R.string.view_downloaded));
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(tabLayout.getSelectedTabPosition());
    }


    public static void check_selected_items() {
        if (selected_items > 0) {
            title.setText("Selected Items ( " + selected_items + " )");
            delete_layout.setVisibility(View.VISIBLE);
            if (position_call == 1) {
                save.setVisibility(View.GONE);
                delete.setVisibility(View.VISIBLE);
            } else {
                save.setVisibility(View.VISIBLE);
                delete.setVisibility(View.GONE);
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
            delete.setVisibility(View.GONE);
            title.setText(getResources().getString(R.string.statu_saver));
            Status_frag.selected_list.clear();
            Status_frag.mReAdapter.notifyDataSetChanged();
            selected_items = 0;
            check_selected_items();
        } else if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.long_pressd_downstatus_pref), false, this)) {
            Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_downstatus_pref), false, this);
            delete.setVisibility(View.GONE);
            title.setText(getResources().getString(R.string.statu_saver));
            DownloadedStatus_frag.selected_list.clear();
            DownloadedStatus_frag.mReAdapter.notifyDataSetChanged();
            selected_items = 0;
            check_selected_items();

        }
        else {

            DownloadedStatus_frag.net_is_connected=false;
            Status_frag.net_is_connected=false;

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


}