package com.malam.whatsdelet.nolastseen.hiddenchat.Activities;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.app.PendingIntent;
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
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
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
import com.malam.whatsdelet.nolastseen.hiddenchat.Helper.DatabaseHelper;
import com.malam.whatsdelet.nolastseen.hiddenchat.Helper.DatabaseHelper_new;
import com.malam.whatsdelet.nolastseen.hiddenchat.Model.Action;
import com.malam.whatsdelet.nolastseen.hiddenchat.Model.Note;
import com.malam.whatsdelet.nolastseen.hiddenchat.Model.Note_new;
import com.malam.whatsdelet.nolastseen.hiddenchat.Service.ChatHead;
import com.malam.whatsdelet.nolastseen.hiddenchat.Service.NotificationService;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.KeyboardUtils;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.Shared;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.WrapContentLinearLayoutManager;
import com.whatsdelete.Test.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Contact_Chat_Act_fro_Notification extends AppCompatActivity {
    ImageView userImage, delete, back;
    TextView userName, no_chat_found;
    Chat_contact_Adapter mAdapter;
    DatabaseHelper_new db;
    public static ImageView share;
    DatabaseHelper main_db;
    private List<Note_new> notesList = new ArrayList<>();
    private final List<Note_new> notesList_new = new ArrayList<>();
    RecyclerView recyclerView;
    InterstitialAd mInterstitialAd;
    Button reply_on_whatsapp;
    FrameLayout frameLayout_1, frame_layout_native_ad;
    private UnifiedNativeAd nativeAd;
    public DatabaseHelper databaseHelper;
    Boolean from_notification = false;

    private AdView adaptive_adView;

    EditText chat_edit_txt;
    ImageView send;
    LinearLayout direct_send_layout;
    Boolean loading_list = false, is_act_running = false, back_pressed = false;
    int ad_poistion = 0;
    ProgressBar progressbar;
    String input_trim;
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

        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_specific_chat_pref), false, Contact_Chat_Act_fro_Notification.this);
        shimmer_animation = findViewById(R.id.ad_loader);
        shimmer_animation.startShimmerAnimation();
        load_adaptive_Banner();
        initilize_components();
        getteing_prefrence();
        initlize_list();
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));
        checkReplyAction();
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
        frameLayout_1 = findViewById(R.id.fl_adplaceholder);
        // Step 1 - Create an AdView and set the ad unit ID on it.
        adaptive_adView = new AdView(this);
        adaptive_adView.setAdUnitId(getString(R.string.bannerAd_notification_chat));
        frameLayout_1.addView(adaptive_adView);


        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice(getResources().getString(R.string.vicky_s8))
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

    private final BroadcastReceiver onNotice = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!is_act_running) {
                return;
            }
            if (loading_list) {
                try {
                    bg_task bg_task = new bg_task();
                    if (bg_task.getStatus() == AsyncTask.Status.FINISHED) {
                        loading_list = false;
                    } else if (bg_task.getStatus() != AsyncTask.Status.RUNNING) {
                        loading_list = false;
                    }
                } catch (NullPointerException asd) {
                } catch (Exception asd) {
                }
                if (loading_list) {
                    return;
                }
            }
            Date dt = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
            String time1 = sdf.format(dt);

            String contact_name = intent.getStringExtra("title");
            String contact_msg = intent.getStringExtra("text");
            String chat_name = Shared.getInstance().getStringValueFromPreference(getResources().getString(R.string.contact_name_pref), "", getApplicationContext());
            if (contact_name.equals(chat_name)) {
                Note_new note_new = new Note_new();
                note_new.setNote(userName.getText().toString());
                note_new.setNumber(contact_msg);
                note_new.setSelected(false);
                note_new.setPath(getResources().getString(R.string.from_receiver));
                note_new.setTime(time1);
                note_new.setTimestamp(time1);
                notesList_new.add(note_new);
                try {
                    mAdapter.notifyDataSetChanged();
                } catch (NullPointerException asd) {
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

            checkReplyAction();
        }
    };

    private void getteing_prefrence() {
        String user_name_string = null;
        try {
            user_name_string = getIntent().getStringExtra(getResources().getString(R.string.contact_name_pref));
            from_notification = true;
        } catch (NullPointerException asd) {
        } catch (Exception er) {
        }
        if (user_name_string == null) {
            userName.setText(Shared.getInstance().getStringValueFromPreference(getResources().getString(R.string.contact_name_pref), "", this));
            from_notification = false;
        } else userName.setText(user_name_string);

        /*  Glide.with(this).load(Shared.getInstance().getStringValueFromPreference(getResources().getString(R.string.contact_image_pref), "", this))
                .skipMemoryCache(false).centerCrop()
                .into(userImage);*/
        input_trim = userName.getText().toString();
        input_trim = input_trim.replace(" ", "");
        input_trim = input_trim.trim();
        String path = Environment.getExternalStorageDirectory() + "/.Profile Thumbnail/";
        Glide.with(this).load(path + "Image-" + input_trim + ".png")
                .skipMemoryCache(false).centerCrop()
                .into(userImage);
    }

    private void initilize_components() {
        progressbar = findViewById(R.id.progressbar);
        userImage = findViewById(R.id.userImage);
        delete = findViewById(R.id.delete);
        share = findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("ID", "Yes");

                String msg = "";
                String name = "";
                for (int i = 0; i <= Chat_contact_Adapter.selected_msgs.size(); i++) {
                    try {
                        msg = Chat_contact_Adapter.selected_msgs.get(i).getNumber();
                        name = Chat_contact_Adapter.selected_msgs.get(i).getNote();

                    } catch (IndexOutOfBoundsException asd) {
                    } catch (Exception er) {
                    }
                                    /*try {
                                        db.deleteNote(Chat_contact_Adapter.selected_msgs.get(i));
                                    } catch (IndexOutOfBoundsException iasd) {
                                    } catch (Exception er) {
                                    }*/
                    List<Note_new> notesList = new ArrayList<>();
                    notesList.clear();
                    notesList.addAll(db.getAllNotes());
                    for (int j = 0; j < notesList.size(); j++) {
                        if (name.equalsIgnoreCase(notesList.get(j).getNote())) {
                            if (msg.equalsIgnoreCase(notesList.get(j).getNumber())) {
                                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                                /*This will be the actual content you wish you share.*/
                                String shareBody = msg;
                                /*The type of the content is text, obviously.*/
                                intent.setType("text/plain");
                                /*Applying information Subject and Body.*/
                                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_msg_subject));
                                intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                                /*Fire!*/
                                startActivity(Intent.createChooser(intent, getString(R.string.share_using)));
                            }
                        }
                    }
                    Log.e("ID", "loop" + i);
                }


            }
        });

        back = findViewById(R.id.back);
        userName = findViewById(R.id.userName);
        no_chat_found = findViewById(R.id.no_chat_found);
        recyclerView = findViewById(R.id.recyclerView);
        reply_on_whatsapp = findViewById(R.id.reply_on_whatsapp);
        chat_edit_txt = findViewById(R.id.chat_edit_txt);

        KeyboardUtils.addKeyboardToggleListener(this, new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                if (isVisible) {
                    try {
                        frameLayout_1.setVisibility(View.GONE);
                    } catch (NullPointerException asd) {
                    }
                } else {
                    try {
                        frameLayout_1.setVisibility(View.VISIBLE);
                    } catch (NullPointerException asd) {
                    }
                }
            }
        });
        send = findViewById(R.id.send);
        direct_send_layout = findViewById(R.id.direct_send_layout);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = chat_edit_txt.getText().toString();
                if (string.isEmpty()) {
                    Toast.makeText(Contact_Chat_Act_fro_Notification.this, "Please enter a message", Toast.LENGTH_LONG).show();
                    return;
                }
                if (NotificationService.replyActions.size() < 1) {
                    Toast.makeText(Contact_Chat_Act_fro_Notification.this, "list is empty", Toast.LENGTH_LONG).show();
                    return;
                }


                Action action = NotificationService.replyActions.get(userName.getText().toString());

                if (action == null) {
                    Toast.makeText(Contact_Chat_Act_fro_Notification.this, "Cannot reply", Toast.LENGTH_LONG).show();

                    return;
                }
                try {
                    action.sendReply(getApplicationContext(), string);

                    Date dt = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                    String time1 = sdf.format(dt);
                    db.insertNote(userName.getText().toString(), userName.getText().toString(), chat_edit_txt.getText().toString(), time1, getResources().getString(R.string.from_sender), false, 0);
                    Note_new note_new = new Note_new();
                    note_new.setNote(userName.getText().toString());
                    note_new.setNumber(chat_edit_txt.getText().toString());
                    note_new.setSelected(false);
                    note_new.setPath(getResources().getString(R.string.from_sender));
                    note_new.setTime(time1);
                    note_new.setTimestamp(time1);
                    notesList_new.add(note_new);
                    mAdapter.notifyDataSetChanged();

                    try {
                        recyclerView.scrollToPosition(notesList_new.size() - 1);
                    } catch (NullPointerException asd) {
                    } catch (Exception er) {
                    }

                    int ID = 0;
                    List<Note> notesList_main = new ArrayList<>(main_db.getAllNotes());
                    for (int i = 0; i < notesList_main.size(); i++) {
                        ID = notesList_main.get(i).getId();
                        if (notesList_main.get(i).getNote().equals(userName.getText().toString())) {
                            break;
                        }
                    }
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                    Date c = Calendar.getInstance().getTime();
                    String formattedDate = df.format(c);
                    SimpleDateFormat dateFormat_4_itration = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    String date_4_itration = dateFormat_4_itration.format(dt);
                    Note note = new Note();
                    note.setNote(userName.getText().toString());
                    note.setNumber(chat_edit_txt.getText().toString());
                    note.setId(ID);
                    note.setTime(time1);
                    note.setTimestamp(formattedDate);
                    note.setStatus(false);
                    note.setKey(0);
                    note.setTime_n_date_4_itration(date_4_itration);
                    main_db.updateNote(note);
                    Intent msgrcv = new Intent(getResources().getString(R.string.broad_cast_intent_for_main_chat));
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(msgrcv);

                    chat_edit_txt.setText("");
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                } catch (NullPointerException asd) {
                    Toast.makeText(getApplicationContext(), "Opps something went wrong please try again", Toast.LENGTH_LONG).show();
                }
            }
        });
        reply_on_whatsapp.setOnClickListener(new View.OnClickListener() {
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
       /* LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);*/
        /*recyclerView.setLayoutManager(linearLayoutManager);*/

        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this));
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Chat_contact_Adapter.selected_msgs.size() < 1) {
                    Toast.makeText(Contact_Chat_Act_fro_Notification.this, getResources().getString(R.string.please_select_atleast_one_image_delete), Toast.LENGTH_LONG).show();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(Contact_Chat_Act_fro_Notification.this);
                builder.setTitle(getResources().getString(R.string.delete_msgs));
                builder.setMessage(getResources().getString(R.string.delete_msgs_msg))
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                String name = "";
                                for (int i = 0; i <= Chat_contact_Adapter.selected_msgs.size(); i++) {
                                    try {
                                        name = Chat_contact_Adapter.selected_msgs.get(i).getNumber();
                                    } catch (IndexOutOfBoundsException asd) {
                                    } catch (Exception er) {
                                    }
                                    /*try {
                                        db.deleteNote(Chat_contact_Adapter.selected_msgs.get(i));
                                    } catch (IndexOutOfBoundsException iasd) {
                                    } catch (Exception er) {
                                    }*/
                                    List<Note_new> notesList = new ArrayList<>();
                                    notesList.clear();
                                    notesList.addAll(db.getAllNotes());
                                    for (int j = 0; j < notesList.size(); j++) {
                                        if (name.equalsIgnoreCase(notesList.get(j).getNumber())) {
                                            db.deleteNote(notesList.get(j));
                                        }
                                    }
                                }
                                initlize_list();
                                Chat_contact_Adapter.selected_msgs.clear();
                                Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_specific_chat_pref), false, Contact_Chat_Act_fro_Notification.this);
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

    private void checkReplyAction() {
        if (NotificationService.replyActions.get(this.userName.getText().toString()) == null) {
            direct_send_layout.setVisibility(View.GONE);
            this.reply_on_whatsapp.setVisibility(View.VISIBLE);
            return;
        }

        direct_send_layout.setVisibility(View.VISIBLE);
        direct_send_layout.setBackgroundColor(getResources().getColor(R.color.White));
        this.reply_on_whatsapp.setVisibility(View.GONE);

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

                openWhatsappContact();
                alertDialog.dismiss();
            }

        });
        alertDialog.show();
    }

    void openWhatsappContact() {

      /*  PackageManager packageManager = getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);

        try {
            String url = "https://api.whatsapp.com/send?phone="+ "+923145845705" +"&text=" + URLEncoder.encode("Hello  how are you ", "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                startActivity(i);
            }
        } catch (Exception e){
            e.printStackTrace();
        }*/

      /* Uri uri = Uri.parse("smsto:" + "03145845705");
        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
        i.putExtra("sms_body", "hello hwo are you ");
        i.setPackage("com.whatsapp");
        startActivity(i);*/

        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.enter_ur_msg));
        try {
            startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, getResources().getString(R.string.whatsapp_not_installed), Toast.LENGTH_LONG).show();
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(getResources().getString(R.string.zegar_device)).build();
        mInterstitialAd.loadAd(adRequest);
    }

    private void initlize_list() {
        db = new DatabaseHelper_new(this);
        main_db = new DatabaseHelper(this);
        databaseHelper = new DatabaseHelper(this);
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
            mAdapter = new Chat_contact_Adapter(false, Contact_Chat_Act_fro_Notification.this, notesList_new, ad_poistion);
            recyclerView.setAdapter(mAdapter);
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        recyclerView.scrollToPosition(notesList_new.size() - 1);
                    } catch (NullPointerException ner) {
                    } catch (Exception er) {
                    }
                }
            });
            if (notesList_new.size() < 1) {
                no_chat_found.setVisibility(View.VISIBLE);
                Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.chat_clear_pref), true, Contact_Chat_Act_fro_Notification.this);
                // if (!MainActivity.is_main_act_active) {
                if (!isActivityRunning(MainActivity.class)) {
                    startActivity(new Intent(Contact_Chat_Act_fro_Notification.this, MainActivity.class));
                    finish();
                } else {
                    finish();
                }
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
        notesList = new ArrayList<>();
        notesList.clear();
        notesList_new.clear();
        notesList.addAll(db.getAllNotes());

        for (int i = notesList.size() - 1; i >= 0; i--) {
            if (notesList.get(i).getNote().equalsIgnoreCase(userName.getText().toString())) {
                Note_new note_new = new Note_new();
                note_new.setNote(notesList.get(i).getNote());
                note_new.setNumber(notesList.get(i).getNumber());
                note_new.setSelected(false);
                note_new.setPath(notesList.get(i).getPath());
                note_new.setTimestamp(notesList.get(i).getTimestamp());
                note_new.setTime(notesList.get(i).getTime());
                note_new.setstatus(notesList.get(i).getstatus());
                notesList_new.add(note_new);
            }
        }
        notesList.clear();
        ad_poistion = notesList_new.size() - 2;
        notesList.addAll(notesList_new);
        notesList_new.clear();

        for (int i = 0; i <= notesList.size() - 1; i++) {
            if (notesList.get(i).getNote().equalsIgnoreCase(userName.getText().toString())) {
                Note_new note_new = new Note_new();
                note_new.setNote(notesList.get(i).getNote());
                note_new.setNumber(notesList.get(i).getNumber());
                note_new.setSelected(false);
                note_new.setTime(notesList.get(i).getTime());
                note_new.setPath(notesList.get(i).getPath());
                note_new.setTimestamp(notesList.get(i).getTimestamp());
                note_new.setstatus(notesList.get(i).getstatus());

                /*if (ad_poistion > 0)
                    if (i == ad_poistion) {
                        Note_new note_new1 = new Note_new();
                        note_new1.setNote("");
                        note_new1.setNumber("");
                        note_new1.setSelected(false);
                        note_new1.setPath("");
                        notesList_new.add(note_new);
                    }*/
                notesList_new.add(note_new);
            }
        }

     /*   Note_new note= new Note_new();
        note.setNote("dont_show");
        note.setNumber("dont_show");
        note.setSelected(false);
        note.setPath("dont_show");
        notesList_new.add(note);*/
    }


    @Override
    public void onBackPressed() {
        if (Chat_contact_Adapter.selected_msgs.size() > 0) {
            Chat_contact_Adapter.selected_msgs.clear();
        }
        if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.long_pressd_specific_chat_pref), false, getApplicationContext())) {
            Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_specific_chat_pref), false, getApplicationContext());
            mAdapter.notifyDataSetChanged();
            return;
        }
        back_pressed = true;
        Chat_contact_Adapter.my_unifiedNativeAd = null;
        Chat_contact_Adapter.selected_msgs.clear();
        String input = userName.getText().toString();
        input = input.replace(" ", "");
        input = input.trim();
        Shared.getInstance().saveIntToPreferences(input + getResources().getString(R.string.num_of_unread_msgs_pref), 0, getApplicationContext());
        Shared.getInstance().saveBooleanToPreferences(input, false, this);
        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_specific_chat_pref), false, Contact_Chat_Act_fro_Notification.this);
        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.chat_clear_pref), false, getApplicationContext());
        if (mInterstitialAd.isLoaded()) mInterstitialAd.show();
        else {
            if (from_notification) {
                // if (!MainActivity.is_main_act_active) {
                if (!isActivityRunning(MainActivity.class)) {
                    startActivity(new Intent(Contact_Chat_Act_fro_Notification.this, MainActivity.class));
                }
            }
            finish();
        }
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                if (from_notification) {
                    //if (!MainActivity.is_main_act_active) {
                    if (!isActivityRunning(MainActivity.class)) {
                        startActivity(new Intent(Contact_Chat_Act_fro_Notification.this, MainActivity.class));
                    }
                }
                finish();
            }
        });
    }

    protected Boolean isActivityRunning(Class activityClass) {

        try {
            ActivityManager activityManager = (ActivityManager) getBaseContext().getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

            for (ActivityManager.RunningTaskInfo task : tasks) {
                if (activityClass.getCanonicalName().equalsIgnoreCase(task.baseActivity.getClassName()))
                    return true;
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        loading_list = true;
        is_act_running = false;
        Shared.getInstance().saveBooleanToPreferences(input_trim + "is_chat_open", false, Contact_Chat_Act_fro_Notification.this);
        String input = userName.getText().toString();
        input = input.replace(" ", "");
        input = input.trim();

        Shared.getInstance().saveIntToPreferences(input + getResources().getString(R.string.num_of_unread_msgs_pref), 0, Contact_Chat_Act_fro_Notification.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        is_act_running = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        is_act_running = true;
        Shared.getInstance().saveBooleanToPreferences(input_trim + "is_chat_open", true, Contact_Chat_Act_fro_Notification.this);
        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.app_is_open_pref), true, Contact_Chat_Act_fro_Notification.this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Chat_contact_Adapter.my_unifiedNativeAd = null;

        try {
            if (back_pressed) {
                return;
            }
            Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.app_is_open_pref), false, Contact_Chat_Act_fro_Notification.this);
            try {
                stopService(new Intent(Contact_Chat_Act_fro_Notification.this, ChatHead.class));
            } catch (Exception asd) {
            }
        } catch (Exception asd) {
        }
    }


    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.app_is_open_pref), false, Contact_Chat_Act_fro_Notification.this);
    }
}