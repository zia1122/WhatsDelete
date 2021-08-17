package com.malam.whatsdelet.nolastseen.hiddenchat.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.malam.whatsdelet.nolastseen.hiddenchat.Fragments.Deleted_images_frag;
import com.malam.whatsdelet.nolastseen.hiddenchat.Fragments.Status_frag;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.Shared;
import com.whatsdelete.Test.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Video_View_Act extends AppCompatActivity implements View.OnClickListener {
    VideoView videoView;
    String path, TAG = "whatsdelet";
    InterstitialAd mInterstitialAd;

    ImageView main_image, backArrow;
    private FrameLayout frameLayout_1;
    private AdView adaptive_adView;
    Boolean is_net_connected;
    private final ArrayList<String> ImagesArray = new ArrayList<String>();
    private UnifiedNativeAd nativeAd;
    private ShimmerFrameLayout shimmer_animation;

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

    private void loading_native_advance_ad() {
        AdLoader.Builder builder = new AdLoader.Builder(this, getResources().getString(R.string.nativeID));
        frameLayout_1 = findViewById(R.id.fl_adplaceholder);
        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            // OnUnifiedNativeAdLoadedListener implementation.
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                // You must call destroy on old ads when you are done with them,
                // otherwise you will have a memory leak.
                if (nativeAd != null) {
                    nativeAd.destroy();
                }
                nativeAd = unifiedNativeAd;
                FrameLayout frameLayout = findViewById(R.id.fl_adplaceholder);
                UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater().inflate(R.layout.ad_unified, null);
//                populateUnifiedNativeAdView(unifiedNativeAd, adView);
                frameLayout.removeAllViews();
                frameLayout.addView(adView);
            }
        });
        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();
        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();
        builder.withNativeAdOptions(adOptions);
        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                frameLayout_1.setVisibility(View.GONE);
                shimmer_animation.setVisibility(View.GONE);
                shimmer_animation.stopShimmerAnimation();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                frameLayout_1.setVisibility(View.VISIBLE);
                shimmer_animation.setVisibility(View.GONE);
                shimmer_animation.stopShimmerAnimation();

            }
        }).build();
        adLoader.loadAd(new AdRequest.Builder().addTestDevice(getResources().getString(R.string.vicky_s8)).addTestDevice(getResources().getString(R.string.zegar_device)).addTestDevice(getResources().getString(R.string.test_device_j5)).addTestDevice(getResources().getString(R.string.vicky_s8)).addTestDevice(getResources().getString(R.string.test_device_white)).addTestDevice("A86A0D556F68465C49063589837FCF98").build());
    }

    private void load_adaptive_Banner() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        frameLayout_1 = findViewById(R.id.fl_adplaceholder);
        adaptive_adView = new AdView(this);
        adaptive_adView.setAdUnitId(getString(R.string.bannerAd));
        frameLayout_1.addView(adaptive_adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(getResources().getString(R.string.vicky_s8))
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
//                loading_native_advance_ad();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                frameLayout_1.setVisibility(View.VISIBLE);
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

    int position = 0;
    int position_call = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video__view_);
        path = getIntent().getStringExtra(getResources().getString(R.string.video_path));
        initilize_components();
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.intrestialAd));
        requestNewInterstitial();
        path = getIntent().getStringExtra("path");
        is_net_connected = getIntent().getBooleanExtra(getResources().getString(R.string.is_net_connected_intent), false);
        position = Shared.getInstance().getIntValueFromPreference(getResources().getString(R.string.image_position_pref), 0, this);
        position_call = Shared.getInstance().getIntValueFromPreference(getResources().getString(R.string.from_status_pref), 1, this);
        intilioze_components();

        shimmer_animation = findViewById(R.id.ad_loader);
        shimmer_animation.startShimmerAnimation();
//        load_adaptive_Banner();
        if (position_call == 1) {
            if (position > 2) {
                position = position - 1;
            }
            for (int i = 0; i < Status_frag.muList.size(); i++) {
                String path = Status_frag.muList.get(i).getFile_path();
                /*ImagesArray.add(path);*/
                if (is_net_connected) {
                    if (i != 2) {
                        ImagesArray.add(path);
                    }
                } else {
                    ImagesArray.add(path);
                }
                Log.e("path", " " + path);
            }
        } else {
            if (position > 2) {
                position = position - 1;
            }
            for (int i = 0; i < Deleted_images_frag.muList.size(); i++) {
                String path = Deleted_images_frag.muList.get(i).getFile_path();
                //ImagesArray.add(path);
                if (is_net_connected) {
                    if (i != 2) {
                        ImagesArray.add(path);
                    }
                } else {
                    ImagesArray.add(path);
                }
                Log.e("path", " " + path);
            }
        }
    }


    private void intilioze_components() {
        main_image = findViewById(R.id.main_image);
        backArrow = findViewById(R.id.backArrow);


        backArrow.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backArrow:
                finish();
                break;
            /*case R.id.fab:
                show_options = !show_options;
                View_Animation.rotateFab(fab, show_options);
                if (show_options)
                    options_layout.setVisibility(View.VISIBLE);
                else options_layout.setVisibility(View.GONE);
                break;*/
            case R.id.saveLayout:
                try {
                    String newpath = Shared.saveAndRefreshFiles(new File(path));
                    File file = new File(newpath);
                    /*try {
                        MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
                    } catch (NullPointerException nera) {

                    } catch (Exception er) {
                    }*/
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                    Toast.makeText(this, "File Saved in Gallery", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Opps Something went wrong please try again", Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.deletelayout:
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Video_View_Act.this);
                alertDialog.setTitle(getResources().getString(R.string.delete));
                alertDialog.setMessage(getResources().getString(R.string.dialoug_message));
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newpath = null;
                        try {
                            newpath = Shared.saveAndRefreshFiles(new File(path));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        File file = new File(newpath);
                        if (file.exists()) {
                            file.delete();
                        }

                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                        if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.from_saved_files_pref), false, Video_View_Act.this)) {
                            Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.delete_file_saved_files_pref), true, getApplicationContext());

                        } else if (position_call == 1) {
                            Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.delete_file_status_pref), true, getApplicationContext());
                        } else if (position_call == 2) {
                            Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.delete_file_images_pref), true, getApplicationContext());

                        } else {
                            Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.delete_file_deletedimages_pref), true, getApplicationContext());

                        }
                        // mPager.setCurrentItem(current_poistion,true);
                        //  mPager.setCurrentItem(position,true);

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                break;
            case R.id.shareLayout:
                /*Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                share.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
                startActivity(Intent.createChooser(share, "Share Image via"));*/
                //shareImage();


                if (path.endsWith(".mp4") || path.endsWith(".3gp") || path.endsWith(".avi") || path.endsWith(".flv") || path.endsWith(".wmv")) {
                    Uri photoURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", new File(path));

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("video/*");
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    intent.putExtra(Intent.EXTRA_STREAM, photoURI);
                    startActivity(Intent.createChooser(intent, "Share Video via"));
                } else {
                    Uri photoURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", new File(path));
                    Uri myUri = Uri.parse(path);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/*");
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    intent.putExtra(Intent.EXTRA_STREAM, photoURI);
                    startActivity(Intent.createChooser(intent, "Share image via"));
                }
                break;
            default:
                break;
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(getResources().getString(R.string.zegar_device)).addTestDevice(getResources().getString(R.string.zegar_device)).build();
        mInterstitialAd.loadAd(adRequest);
    }

    private void initilize_components() {
        videoView = findViewById(R.id.video_view);
        videoView.setVideoPath(path);
        videoView.setVideoURI(Uri.parse(path));
        videoView.requestFocus();
        videoView.start();
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);


    }

    @Override
    public void onBackPressed() {
        if (videoView.isPlaying()) {
            videoView.stopPlayback();
        }
        if (mInterstitialAd.isLoaded()) mInterstitialAd.show();
        else finish();
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                finish();
            }
        });
    }
}