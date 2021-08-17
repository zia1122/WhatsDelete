package com.malam.whatsdelet.nolastseen.hiddenchat.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.malam.whatsdelet.nolastseen.hiddenchat.Fragments.DownloadImages;
import com.malam.whatsdelet.nolastseen.hiddenchat.Fragments.DownloadVideos;
import com.malam.whatsdelet.nolastseen.hiddenchat.Model.Music_model;
import com.malam.whatsdelet.nolastseen.hiddenchat.Model.Note;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.Shared;
import com.whatsdelete.Test.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadedStatuses extends AppCompatActivity {
    ImageView back_btn;
    ViewPager viewPager;
    TabLayout tabLayout;
    static TextView title;
    static ImageView delete;
    static ImageView share;
    public static int selected_items = 0;
    static LinearLayout delete_layout;
    private AdView adaptive_adView;
    public static List<Note> selected_list = new ArrayList<>();
    public static int position_call = 0;
    private Intent msgrcv;
    static public Context context;
    List<Music_model> muList;
    String path;
    private FrameLayout adContainerView;
    public static InterstitialAd mInterstitialAd;
    private ShimmerFrameLayout shimmer_animation;

    public   List<Fragment> mFragmentList = new ArrayList<>();
    public   List<String> mFragmentTitleList = new ArrayList<>();
    ProgressDialog deleting_progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloaded_statuses);
        context = getApplicationContext();

        path = getIntent().getStringExtra("path");
        muList= new ArrayList<>();

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.intrestialAd));
        requestNewInterstitial();
        shimmer_animation = findViewById(R.id.ad_loader);
        shimmer_animation.startShimmerAnimation();
        load_adaptive_Banner();
        initilize_components();

        viewPager.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
    }

    public class SectionPagerAdapter extends FragmentPagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new DownloadImages();
                case 1:
                default:
                    return new DownloadVideos();
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


    private void initilize_components() {

        deleting_progress_bar = new ProgressDialog(DownloadedStatuses.this);
        deleting_progress_bar.setTitle(getResources().getString(R.string.please_wait_while_deleting));
        deleting_progress_bar.setCancelable(false);
        MainActivity.statuses = true;
        back_btn = findViewById(R.id.back_btn);
        viewPager = findViewById(R.id.viewPagerDownload);
        tabLayout = findViewById(R.id.tabs);
        back_btn = findViewById(R.id.back_btn);
        delete_layout = findViewById(R.id.delete_layout);
        delete = findViewById(R.id.delete);
        share = findViewById(R.id.share);
        title = findViewById(R.id.title);

        //DeleteButton
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
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });


        //BackButton

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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

                    MainActivity.selected_items = 0;

                    Intent msgrcv = new Intent("Msg");
                    LocalBroadcastManager.getInstance(DownloadedStatuses.this).sendBroadcast(msgrcv);
                    check_selected_items();
                    //my_holder.selec_all.setChecked(false);
                    switch (position) {
                        case 0:
                            DownloadImages.selected_list.clear();
                            Shared.getInstance().saveIntToPreferences(getResources().getString(R.string.delete_tabs_position_pref), 3, DownloadedStatuses.this);
                            break;
                        case 1:
                            Shared.getInstance().saveIntToPreferences(getResources().getString(R.string.delete_tabs_position_pref), 4, DownloadedStatuses.this);
                           DownloadVideos.selected_list.clear();
                            break;
                        default:
                            break;
                    }
                } catch (NullPointerException e) {
                } catch (Exception e) {
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        delete.setVisibility(View.GONE);
        share.setVisibility(View.GONE);
    }
    private void delet_statuses() {
        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(DownloadedStatuses.this).create();
        View layout1 = LayoutInflater.from(DownloadedStatuses.this).inflate(R.layout.delet_dialog, findViewById(R.id.your_dialog_root_element));
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
                new video_statuses().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
            for (int i = 0; i < DownloadImages.selected_list.size(); i++) {
                File file = new File(DownloadImages.selected_list.get(i).getFile_path());
                file.delete();
                try {
                    DownloadImages.imageModel.remove(DownloadImages.selected_list.get(i));
                } catch (IndexOutOfBoundsException er) {
                } catch (Exception er) {
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_saved_files_pref), false, DownloadedStatuses.this);
            DownloadImages.selected_list.clear();
            DownloadVideos.selected_list.clear();
            DownloadImages.mReAdapter.notifyDataSetChanged();
            DownloadVideos.mReAdapter.notifyDataSetChanged();
            title.setText(getResources().getString(R.string.statu_saver1));
            msgrcv = new Intent("refresh");
            LocalBroadcastManager.getInstance(DownloadedStatuses.this).sendBroadcast(msgrcv);
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

    public class video_statuses extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (deleting_progress_bar != null || !deleting_progress_bar.isShowing())
                deleting_progress_bar.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < DownloadVideos.selected_list.size(); i++) {
                File file = new File(DownloadVideos.selected_list.get(i).getFile_path());
                file.delete();
                try {
                    DownloadVideos.muList.remove(DownloadVideos.selected_list.get(i));
                } catch (IndexOutOfBoundsException er) {
                } catch (Exception er) {
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_saved_files_pref), false, DownloadedStatuses.this);
            DownloadImages.selected_list.clear();
            DownloadVideos.selected_list.clear();
            DownloadImages.mReAdapter.notifyDataSetChanged();
            DownloadVideos.mReAdapter.notifyDataSetChanged();
            title.setText(getResources().getString(R.string.statu_saver1));
            msgrcv = new Intent("refresh");
            LocalBroadcastManager.getInstance(DownloadedStatuses.this).sendBroadcast(msgrcv);
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

    public static void check_selected_items() {
        if (selected_items > 0) {
            title.setText("Selected Items ( " + selected_items + " )");
            delete_layout.setVisibility(View.VISIBLE);
            if (position_call == 1) {
                share.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
            } else {
                share.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
            }
        } else {
            title.setText(R.string.statu_saver1);
            delete_layout.setVisibility(View.GONE);
            Shared.getInstance().saveBooleanToPreferences("long_pressd_status_pref", false, context);
            Shared.getInstance().saveBooleanToPreferences("long_pressd_downstatus_pref", false, context);
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(getResources().getString(R.string.vicky_s8)).addTestDevice(getResources().getString(R.string.zegar_device)).build();
        mInterstitialAd.loadAd(adRequest);
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

    @Override
    public void onBackPressed() {

        if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.long_pressd_status_pref), false, this)) {
            Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_status_pref), false, this);
            delete.setVisibility(View.GONE);
            title.setText(getResources().getString(R.string.statu_saver1));
            DownloadImages.selected_list.clear();
            DownloadVideos.selected_list.clear();
            DownloadImages.mReAdapter.notifyDataSetChanged();
            DownloadVideos.mReAdapter.notifyDataSetChanged();
            selected_items = 0;
            check_selected_items();
        } else if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.long_pressd_downstatus_pref), false, this)) {
            Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_downstatus_pref), false, this);
            delete.setVisibility(View.GONE);
            title.setText(getResources().getString(R.string.statu_saver1));
            DownloadImages.selected_list.clear();
            DownloadVideos.selected_list.clear();
            DownloadImages.mReAdapter.notifyDataSetChanged();
            DownloadVideos.mReAdapter.notifyDataSetChanged();
            selected_items = 0;
            check_selected_items();

        }
        else {

            DownloadImages.net_is_connected=false;
            DownloadVideos.net_is_connected=false;

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

}