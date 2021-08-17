package com.malam.whatsdelet.nolastseen.hiddenchat.Activities;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
import com.malam.whatsdelet.nolastseen.hiddenchat.Adapter.Chat_contact_Adapter;
import com.malam.whatsdelet.nolastseen.hiddenchat.Adapter.Chat_contact_Adapter_Instagram;
import com.malam.whatsdelet.nolastseen.hiddenchat.Adapter.Chat_contact_Adapter_Messenger;
import com.malam.whatsdelet.nolastseen.hiddenchat.Helper.DatabaseHelper_instagram;
import com.malam.whatsdelet.nolastseen.hiddenchat.Helper.DatabaseHelper_new_instagram;
import com.malam.whatsdelet.nolastseen.hiddenchat.Model.Note_new_instagram;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.Shared;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.WrapContentLinearLayoutManager;
import com.whatsdelete.Test.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Contact_Chat_Act_Instagram extends AppCompatActivity {
    ImageView delete, back;
    CircleImageView userImage;
    TextView userName, no_chat_found;
    Chat_contact_Adapter_Instagram mAdapter;
    DatabaseHelper_new_instagram db;
    private final List<Note_new_instagram> notesList = new ArrayList<>();
    private final List<Note_new_instagram> notesList_new = new ArrayList<>();
    RecyclerView recyclerView;
    InterstitialAd mInterstitialAd;
    Button reply_on_messenger;
    FrameLayout frameLayout_1, frame_layout_native_ad;
    private UnifiedNativeAd nativeAd;
    public DatabaseHelper_instagram databaseHelper;

    private FrameLayout adContainerView;
    private AdView adaptive_adView;
    int ad_poistion = 0;
    ProgressBar progressbar;
    Boolean loading_list = false;
    private ShimmerFrameLayout shimmer_animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.intrestialAd));
        requestNewInterstitial();
        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_specific_chat_pref), false, Contact_Chat_Act_Instagram.this);
        shimmer_animation = findViewById(R.id.ad_loader);
        shimmer_animation.startShimmerAnimation();
        load_adaptive_Banner();
        initilize_components();
        getteing_prefrence();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initlize_list();
            }
        }, 500);
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter(getResources().getString(R.string.intent_name_instagram)));
    }


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
                slideView(frameLayout_1, frameLayout_1.getHeight(), 0);
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
        adLoader.loadAd(new AdRequest.Builder()
                .addTestDevice(getResources().getString(R.string.vicky_s8)).addTestDevice(getResources().getString(R.string.zegar_device)).addTestDevice(getResources().getString(R.string.test_device_j5)).addTestDevice(getResources().getString(R.string.vicky_s8)).addTestDevice(getResources().getString(R.string.test_device_white)).addTestDevice("A86A0D556F68465C49063589837FCF98").build());
    }

    public void slideView(final View view, int currentHeight, int newHeight) {

        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(currentHeight, newHeight)
                .setDuration(1000);

        /* We use an update listener which listens to each tick
         * and manually updates the height of the view  */

        slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation1) {
                Integer value = (Integer) animation1.getAnimatedValue();
                view.getLayoutParams().height = value.intValue();
                view.requestLayout();
            }
        });


        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.play(slideAnimator);
        animationSet.start();
    }

    private void load_adaptive_Banner() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        adContainerView = findViewById(R.id.fl_adplaceholder);
        // Step 1 - Create an AdView and set the ad unit ID on it.
        adaptive_adView = new AdView(this);
        adaptive_adView.setAdUnitId(getString(R.string.bannerAd));
        adContainerView.addView(adaptive_adView);


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
                adContainerView.setVisibility(View.VISIBLE);
                shimmer_animation.setVisibility(View.GONE);
                shimmer_animation.stopShimmerAnimation();
            }
        });
    }

    private final BroadcastReceiver onNotice = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (loading_list) {
                return;
            }
            try {

                Date dt = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                String time1 = sdf.format(dt);
                Note_new_instagram note_new = new Note_new_instagram();
                String contact_name = intent.getStringExtra("title");
                String chat_name = Shared.getInstance().getStringValueFromPreference(getResources().getString(R.string.contact_name_pref), "", getApplicationContext());
                if (contact_name.equals(chat_name)) {
                    note_new.setNote(contact_name);
                    note_new.setNumber(intent.getStringExtra("text"));
                    note_new.setSelected(false);
                    note_new.setTime(time1);
                    note_new.setTimestamp(time1);
                    notesList_new.add(note_new);
                    try {
                        mAdapter.notifyDataSetChanged();
                    } catch (NullPointerException as) {
                    } catch (Exception asd) {
                    }
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                            } catch (NullPointerException ner) {
                            } catch (Exception er) {
                            }
                        }
                    });
                }
            } catch (NullPointerException asd) {
            } catch (Exception asd) {
            }
        }
    };

    private void getteing_prefrence() {
        userName.setText(Shared.getInstance().getStringValueFromPreference(getResources().getString(R.string.contact_name_pref), "", this));
        String input = userName.getText().toString();
        input = input.replace(" ", "");
        input = input.trim();
        String path = Environment.getExternalStorageDirectory() + "/.Profile Thumbnail/";
        Glide.with(this).load(path + "Image-instagram_" + input + ".png")
                .skipMemoryCache(false).centerCrop()
                .into(userImage);
    }

    private void initilize_components() {
        progressbar = findViewById(R.id.progressbar);
        userImage = findViewById(R.id.userImage);
        delete = findViewById(R.id.delete);
        back = findViewById(R.id.back);
        userName = findViewById(R.id.userName);
        no_chat_found = findViewById(R.id.no_chat_found);
        recyclerView = findViewById(R.id.recyclerView);
        reply_on_messenger = findViewById(R.id.reply_on_whatsapp);
        reply_on_messenger.setText(getResources().getString(R.string.reply_on_instagram));
        reply_on_messenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                before_open_whatsapp();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this));
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Chat_contact_Adapter_Instagram.selected_msgs.size() < 1) {
                    Toast.makeText(Contact_Chat_Act_Instagram.this, getResources().getString(R.string.please_select_atleast_one_image_delete), Toast.LENGTH_LONG).show();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(Contact_Chat_Act_Instagram.this);
                builder.setTitle(getResources().getString(R.string.delete_msgs));
                builder.setMessage(getResources().getString(R.string.delete_msgs_msg))
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                String name = "";
                                for (int i = 0; i <= Chat_contact_Adapter_Instagram.selected_msgs.size(); i++) {
                                    try {
                                        name = Chat_contact_Adapter_Instagram.selected_msgs.get(i).getNumber();
                                    } catch (IndexOutOfBoundsException asd) {
                                    } catch (Exception er) {
                                    }

                                    List<Note_new_instagram> notesList = new ArrayList<>();
                                    notesList.clear();
                                    notesList.addAll(db.getAllNotes());
                                    for (int j = 0; j < notesList.size(); j++) {
                                        if (name.equalsIgnoreCase(notesList.get(j).getNumber())) {
                                            db.deleteNote(notesList.get(j));
                                        }
                                    }
                                }
                                initlize_list();
                                Chat_contact_Adapter_Instagram.selected_msgs.clear();
                                Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_specific_chat_pref), false, Contact_Chat_Act_Instagram.this);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
            }
        });
    }

    private void before_open_whatsapp() {
        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
        View layout1 = LayoutInflater.from(this).inflate(R.layout.whats_open_dialog, findViewById(R.id.your_dialog_root_element));
        layout1.setMinimumWidth(100);
        alertDialog.setView(layout1);
        alertDialog.show();
        try {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (NullPointerException ne) {
        } catch (Exception e) {
        }
        Button reply = layout1.findViewById(R.id.reply);
        LinearLayout cancel_btn = layout1.findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                open_messenger();
                alertDialog.dismiss();
            }

        });
        alertDialog.show();
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


    void open_messenger() {
        try {
            Intent i = getPackageManager().getLaunchIntentForPackage("com.instagram.android");
            startActivity(i);
        } catch (ActivityNotFoundException asd) {
            Toast.makeText(Contact_Chat_Act_Instagram.this, "Instagram not found !", Toast.LENGTH_LONG).show();
        } catch (NullPointerException ad) {
            Toast.makeText(Contact_Chat_Act_Instagram.this, "Opps Something went wrong !", Toast.LENGTH_LONG).show();
        } catch (Exception er) {
            Toast.makeText(Contact_Chat_Act_Instagram.this, "Opps Something went wrong !", Toast.LENGTH_LONG).show();
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(getResources().getString(R.string.zegar_device)).build();
        mInterstitialAd.loadAd(adRequest);
    }

    private void initlize_list() {
        db = new DatabaseHelper_new_instagram(this);
        databaseHelper = new DatabaseHelper_instagram(this);
        new bg_task().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public class bg_task extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading_list = true;
            progressbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            loading_chat_list();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (notesList_new.size() < 1) {
                delete.setVisibility(View.GONE);
            } else {
                delete.setVisibility(View.VISIBLE);
            }
            recyclerView.setVisibility(View.GONE);
            mAdapter = new Chat_contact_Adapter_Instagram(Contact_Chat_Act_Instagram.this, notesList_new, ad_poistion);
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                    } catch (NullPointerException ner) {
                    } catch (Exception er) {
                    }
                }
            });
            if (notesList_new.size() < 1) {
                no_chat_found.setVisibility(View.VISIBLE);
                Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.chat_clear_instagram_pref), true, Contact_Chat_Act_Instagram.this);
                finish();
            } else no_chat_found.setVisibility(View.GONE);
            if (loading_list)
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loading_list = false;
                        recyclerView.setVisibility(View.VISIBLE);
                        progressbar.setVisibility(View.GONE);
                        mAdapter.notifyDataSetChanged();
                    }
                }, 1000);
        }
    }

    private void loading_chat_list() {
        notesList.clear();
        notesList_new.clear();
        notesList.addAll(db.getAllNotes());

        for (int i = notesList.size() - 1; i >= 0; i--) {
            if (notesList.get(i).getNote().equalsIgnoreCase(userName.getText().toString())) {
                Note_new_instagram note_new = new Note_new_instagram();
                note_new.setNote(notesList.get(i).getNote());
                note_new.setNumber(notesList.get(i).getNumber());
                note_new.setSelected(false);
                notesList_new.add(note_new);
            }
        }

        notesList.clear();
        ad_poistion = notesList_new.size() - 2;
        notesList.addAll(notesList_new);
        notesList_new.clear();
        for (int i = 0; i <= notesList.size() - 1; i++) {
            if (notesList.get(i).getNote().equalsIgnoreCase(userName.getText().toString())) {
                Note_new_instagram note_new = new Note_new_instagram();
                note_new.setNote(notesList.get(i).getNote());
                note_new.setNumber(notesList.get(i).getNumber());
                note_new.setSelected(false);
                note_new.setPath(notesList.get(i).getPath());
                /*if (ad_poistion > 0)
                    if (i == ad_poistion) {
                        Note_new_messenger note_new1 = new Note_new_messenger();
                        note_new1.setNote("");
                        note_new1.setNumber("");
                        note_new1.setSelected(false);
                        note_new1.setPath("");
                        notesList_new.add(note_new);
                    }*/
                notesList_new.add(note_new);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Chat_contact_Adapter_Instagram.my_unifiedNativeAd = null;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onNotice);
        Chat_contact_Adapter.selected_msgs.clear();
        String input = userName.getText().toString();
        input = input.replace(" ", "");
        input = input.trim();
        Shared.getInstance().saveIntToPreferences(input + getResources().getString(R.string.num_of_unread_msgs_pref), 0, getApplicationContext());
        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.chat_clear_messenger_pref), false, Contact_Chat_Act_Instagram.this);
        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.chat_clear_instagram_pref), false, Contact_Chat_Act_Instagram.this);
        Shared.getInstance().saveBooleanToPreferences(input, false, Contact_Chat_Act_Instagram.this);
        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_specific_chat_pref), false, Contact_Chat_Act_Instagram.this);
        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.chat_clear_pref), false, getApplicationContext());
        if (mInterstitialAd.isLoaded()) mInterstitialAd.show();
        else {

            if (!MainActivity.is_main_act_active) {
                startActivity(new Intent(Contact_Chat_Act_Instagram.this, MainActivity.class));
            }
            finish();
        }
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                if (!MainActivity.is_main_act_active) {
                    startActivity(new Intent(Contact_Chat_Act_Instagram.this, MainActivity.class));
                }
                finish();
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        loading_list = true;
    }

    @Override
    protected void onDestroy() {
        Chat_contact_Adapter_Messenger.my_unifiedNativeAd = null;
        super.onDestroy();
    }
}