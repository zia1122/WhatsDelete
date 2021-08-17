package com.malam.whatsdelet.nolastseen.hiddenchat.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.InterstitialAd;
import com.malam.whatsdelet.nolastseen.hiddenchat.Helper.DatabaseHelper;
import com.malam.whatsdelet.nolastseen.hiddenchat.Helper.DatabaseHelper_instagram;
import com.malam.whatsdelet.nolastseen.hiddenchat.Helper.DatabaseHelper_messenger;
import com.malam.whatsdelet.nolastseen.hiddenchat.Helper.DatabaseHelper_new;
import com.malam.whatsdelet.nolastseen.hiddenchat.Helper.DatabaseHelper_new_instagram;
import com.malam.whatsdelet.nolastseen.hiddenchat.Helper.DatabaseHelper_new_messenger;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.Shared;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.whatsdelete.Test.R;

import java.util.List;
import java.util.Stack;

public class Settings extends AppCompatActivity implements View.OnClickListener {

    RelativeLayout notify, media_notify, how_to, privacy, clear;
    SwitchCompat notify_switch, media_switch;
    ImageView notify_image, media_img, back_p;
    private FrameLayout frameLayout_1;
    private AdView adaptive_adView;
    private UnifiedNativeAd nativeAd;
    DatabaseHelper dbwhats;
    DatabaseHelper_new dbwhatsnew;
    DatabaseHelper_instagram dbwhatsinsta;
    DatabaseHelper_messenger dbwhatsmsg;
    DatabaseHelper_new_instagram dbwhatsnewinsta;
    DatabaseHelper_new_messenger dbwhatsnewmsg;
    public static InterstitialAd mInterstitialAd;
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
                frameLayout_1.setVisibility(View.VISIBLE);
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
                loading_native_advance_ad();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        notify = findViewById(R.id.notify_perm);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.intrestialAd));
        requestNewInterstitial();

        shimmer_animation = findViewById(R.id.ad_loader);
        shimmer_animation.startShimmerAnimation();
        load_adaptive_Banner();
        media_notify = findViewById(R.id.notify_perm_media);
        privacy = findViewById(R.id.privacy_policy);
        clear = findViewById(R.id.clearmessage_lay);
        media_img = findViewById(R.id.switch_image_notify_media);
        notify_image = findViewById(R.id.switch_image_notify);
        back_p = findViewById(R.id.back_btn);
        how_to = findViewById(R.id.watch_tutorial);
        notify.setOnClickListener(this);
        how_to.setOnClickListener(this);
        clear.setOnClickListener(this);
        media_notify.setOnClickListener(this);
        dbwhats = new DatabaseHelper(getApplicationContext());
        dbwhatsnew = new DatabaseHelper_new(getApplicationContext());
        dbwhatsinsta = new DatabaseHelper_instagram(getApplicationContext());
        dbwhatsnewinsta = new DatabaseHelper_new_instagram(getApplicationContext());
        dbwhatsmsg = new DatabaseHelper_messenger(getApplicationContext());
        dbwhatsnewmsg = new DatabaseHelper_new_messenger(getApplicationContext());

        privacy.setOnClickListener(this);
        back_p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        notify_switch = findViewById(R.id.nitify_switch);
        boolean notify = Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.media_switch_notify_pref), true, Settings.this);
        boolean media = Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.show_notification_pref), true, Settings.this);
        media_switch = findViewById(R.id.nitify_switch_mewdia);
        media_switch.setChecked(media);
        notify_switch.setChecked(notify);
        if (media)
            media_img.setImageResource(R.mipmap.on_toddle);
        else
            media_img.setImageResource(R.mipmap.off_toddle);

        if (notify)
            notify_image.setImageResource(R.mipmap.on_toddle);
        else
            notify_image.setImageResource(R.mipmap.off_toddle);
        notify_switch.setOnCheckedChangeListener(new
                                                         CompoundButton.OnCheckedChangeListener() {
                                                             @Override
                                                             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                                 if (isChecked)
                                                                     notify_image.setImageResource(R.mipmap.on_toddle);
                                                                 else
                                                                     notify_image.setImageResource(R.mipmap.off_toddle);
                                                                 Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.media_switch_notify_pref), isChecked, Settings.this);
                                                             }

                                                         });

        media_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    media_img.setImageResource(R.mipmap.on_toddle);
                else
                    media_img.setImageResource(R.mipmap.off_toddle);
                Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.show_notification_pref), isChecked, Settings.this);
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.privacy_policy:
                privacy_policy();
                break;
            case R.id.notify_perm:
                notify_switch.setChecked(!notify_switch.isChecked());
                break;
            case R.id.notify_perm_media:
                media_switch.setChecked(!media_switch.isChecked());
                break;
            case R.id.watch_tutorial:
                startActivity(new Intent(this, SliderActivity.class).putExtra("HomeAct", "Settings"));
                break;

            case R.id.clearmessage_lay:

                final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(Settings.this).create();
                View layout1 = LayoutInflater.from(Settings.this).inflate(R.layout.delet_dialog, findViewById(R.id.your_dialog_root_element));
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
                TextView del_msg = layout1.findViewById(R.id.del_msg);
                del_msg.setText(R.string.delete_chat);
                ok_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            dbwhats.deleteallNotes();
                            dbwhatsnew.deleteallNotes();
                            dbwhatsinsta.deleteallNotes();
                            dbwhatsnewinsta.deleteallNotes();
                            dbwhatsmsg.deleteallNotes();
                            dbwhatsnewmsg.deleteallNotes();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        alertDialog.dismiss();

                    }
                });
                cancel_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();

                    }
                });
                break;
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



    @Override
    public void onBackPressed() {
            finish();

    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(getResources().getString(R.string.vicky_s8)).addTestDevice(getResources().getString(R.string.zegar_device)).build();
        mInterstitialAd.loadAd(adRequest);
    }
}