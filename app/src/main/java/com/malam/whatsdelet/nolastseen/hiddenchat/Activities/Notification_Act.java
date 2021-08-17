package com.malam.whatsdelet.nolastseen.hiddenchat.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.Shared;
import com.whatsdelete.Test.R;

public class Notification_Act extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    ImageView back;
    LinearLayout show_notifcation_layout, sound_notifcation_layout, vibrate_notifcation_layout;
    SwitchCompat show_notifcation_switch, sound_notifcation_switch, vibrate_notifcation_switch;

    private UnifiedNativeAd nativeAd;
    FrameLayout frameLayout_1;
    UnifiedNativeAdView adView;
    private FrameLayout adContainerView;
    private AdView adaptive_adView;
    InterstitialAd mInterstitialAd;
    private ShimmerFrameLayout shimmer_animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_);
        loading_adz();
        intilize_components();
        getting_prefrence();
    }

    private void loading_adz() {

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.intrestialAd));
        requestNewInterstitial();
        shimmer_animation = findViewById(R.id.ad_loader);
        shimmer_animation.startShimmerAnimation();
        load_adaptive_Banner();
        loading_native_advance_ad();
    }


    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(getResources().getString(R.string.zegar_device)).addTestDevice(getResources().getString(R.string.zegar_device)).build();
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


        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
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

    private void getting_prefrence() {
        show_notifcation_switch.setChecked(Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.show_notification_pref), true, Notification_Act.this));
        sound_notifcation_switch.setChecked(Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.make_sound_notification_pref), true, Notification_Act.this));
        vibrate_notifcation_switch.setChecked(Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.make_vibration_notification_pref), true, Notification_Act.this));
    if (!show_notifcation_switch.isChecked()){

        sound_notifcation_switch.setEnabled(false);
        vibrate_notifcation_switch.setEnabled(false);
        sound_notifcation_layout.setEnabled(false);
        vibrate_notifcation_layout.setEnabled(false);
    }
    }

    private void intilize_components() {
        back = (ImageView) findViewById(R.id.back);

        show_notifcation_layout = (LinearLayout) findViewById(R.id.show_notifcation_layout);
        sound_notifcation_layout = (LinearLayout) findViewById(R.id.sound_notifcation_layout);
        vibrate_notifcation_layout = (LinearLayout) findViewById(R.id.vibrate_notifcation_layout);

        show_notifcation_switch = (SwitchCompat) findViewById(R.id.show_notifcation_switch);
        sound_notifcation_switch = (SwitchCompat) findViewById(R.id.sound_notifcation_switch);
        vibrate_notifcation_switch = (SwitchCompat) findViewById(R.id.vibrate_notifcation_switch);

        back.setOnClickListener(this);
        show_notifcation_layout.setOnClickListener(this);
        sound_notifcation_layout.setOnClickListener(this);
        vibrate_notifcation_layout.setOnClickListener(this);

        show_notifcation_switch.setOnCheckedChangeListener(this);
        sound_notifcation_switch.setOnCheckedChangeListener(this);
        vibrate_notifcation_switch.setOnCheckedChangeListener(this);
    }


    // Native ad//
    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);


        // Set other ad assets.
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
//            ((RatingBar) adView.getStarRatingView())
//                    .setRating(nativeAd.getStarRating().floatValue());
//            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.GONE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.GONE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAd.getVideoController();

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {


            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
                    // refresh.setEnabled(true);

                    super.onVideoEnd();
                }
            });
        } else {

            //refresh.setEnabled(true);
        }
    }

    private void loading_native_advance_ad() {
        AdLoader.Builder builder = new AdLoader.Builder(this, getResources().getString(R.string.nativeID));
        frameLayout_1 = (FrameLayout) findViewById(R.id.fl_adplaceholder);
        adView = (UnifiedNativeAdView) getLayoutInflater().inflate(R.layout.ad_unified_new, null);
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
                populateUnifiedNativeAdView(unifiedNativeAd, adView);
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
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                frameLayout_1.setVisibility(View.VISIBLE);

            }
        }).build();
        adLoader.loadAd(new AdRequest.Builder().addTestDevice("B0687CF922A4AE9513C727FBD2893DC5")
                .addTestDevice("A86A0D556F68465C49063589837FCF98").build());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                onBackPressed();
                break;
            case R.id.show_notifcation_layout:
                show_notifcation_switch.setChecked(!show_notifcation_switch.isChecked());
                break;
            case R.id.sound_notifcation_layout:
                sound_notifcation_switch.setChecked(!sound_notifcation_switch.isChecked());
                break;
            case R.id.vibrate_notifcation_layout:
                vibrate_notifcation_switch.setChecked(!vibrate_notifcation_switch.isChecked());
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.show_notifcation_switch:
                Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.show_notification_pref), isChecked, Notification_Act.this);
                if (isChecked) {
                    sound_notifcation_switch.setEnabled(true);
                    vibrate_notifcation_switch.setEnabled(true);
                    sound_notifcation_layout.setEnabled(true);
                    vibrate_notifcation_layout.setEnabled(true);
                } else {
                    sound_notifcation_switch.setEnabled(false);
                    vibrate_notifcation_switch.setEnabled(false);
                    sound_notifcation_layout.setEnabled(false);
                    vibrate_notifcation_layout.setEnabled(false);
                }
                break;
            case R.id.sound_notifcation_switch:
                Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.make_sound_notification_pref), isChecked, Notification_Act.this);
                break;
            case R.id.vibrate_notifcation_switch:
                Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.make_vibration_notification_pref), isChecked, Notification_Act.this);
                break;
            default:
                break;
        }
    }
}
