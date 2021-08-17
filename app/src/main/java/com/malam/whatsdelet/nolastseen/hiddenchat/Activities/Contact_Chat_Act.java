package com.malam.whatsdelet.nolastseen.hiddenchat.Activities;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import com.malam.whatsdelet.nolastseen.hiddenchat.Service.NotificationService;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.KeyboardUtils;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.Shared;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.WrapContentLinearLayoutManager;
import com.orhanobut.hawk.Hawk;
import com.whatsdelete.Test.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Contact_Chat_Act extends AppCompatActivity {
    ImageView userImage, delete, back;
    TextView userName, no_chat_found;
    public static ImageView share;
    Chat_contact_Adapter mAdapter;
    String input_trim;
    DatabaseHelper_new db;
    private List<Note_new> notesList = new ArrayList<>();
    private final List<Note_new> notesList_new = new ArrayList<>();
    public static RecyclerView recyclerView;
    InterstitialAd mInterstitialAd;
    Button reply_on_whatsapp;
    FrameLayout frame_layout_native_ad;
    private UnifiedNativeAd nativeAd;
    public DatabaseHelper databaseHelper;
    Boolean from_notification = false;
    Boolean loading_list = false, is_act_running = false, back_pressed = false;

    private FrameLayout frameLayout_1;
    private AdView adaptive_adView;
    EditText chat_edit_txt;
    ImageView send;
    LinearLayout direct_send_layout;
    int ad_poistion = 0;
    DatabaseHelper main_db;
    ProgressBar progressbar;
    private final boolean deleted = false;
    private boolean delete_clicked = false;
    private ShimmerFrameLayout shimmer_animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Hawk.init(this).build();
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.intrestialAd));
        requestNewInterstitial();
        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_specific_chat_pref), false, Contact_Chat_Act.this);
        shimmer_animation=findViewById(R.id.ad_loader);
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
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));
        checkReplyAction();

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


    public static void slideView(final View view, int currentHeight, int newHeight) {

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

    private final BroadcastReceiver onNotice = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (loading_list) {
                return;
            }
            Date dt = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
            String time1 = sdf.format(dt);

            String contact_name = intent.getStringExtra("title");
            String contact_msg = intent.getStringExtra("text");
            boolean status = intent.getBooleanExtra("status", false);
            String chat_name = Shared.getInstance().getStringValueFromPreference(getResources().getString(R.string.contact_name_pref), "", getApplicationContext());
            if (contact_name.equals(chat_name)) {
                if (!status) {
                    Note_new note_new = new Note_new();
                    note_new.setNote(userName.getText().toString());
                    note_new.setNumber(contact_msg);
                    note_new.setSelected(false);
                    note_new.setPath(getResources().getString(R.string.from_receiver));
                    note_new.setTime(time1);
                    note_new.setstatus(status);
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
                } else {

                    try {
                        mAdapter.notifystatus();
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
            }
            checkReplyAction();


        }
    };

    private void getteing_prefrence() {

        userName.setText(Shared.getInstance().getStringValueFromPreference(getResources().getString(R.string.contact_name_pref), "", this));
        input_trim = userName.getText().toString();
        input_trim = input_trim.replace(" ", "");
        input_trim = input_trim.trim();

        String input = userName.getText().toString();
        input = input.replace(" ", "");
        input = input.trim();
        String path = Environment.getExternalStorageDirectory() + "/.Profile Thumbnail/";
        Glide.with(this).load(path + "Image-" + input + ".png")
                .skipMemoryCache(false).centerCrop()
                .into(userImage);

    }

    private void initilize_components() {
        progressbar = findViewById(R.id.progressbar);
        userImage = findViewById(R.id.userImage);
        delete = findViewById(R.id.delete);
        share = findViewById(R.id.share);
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
                    Toast.makeText(Contact_Chat_Act.this, "Please enter a message", Toast.LENGTH_LONG).show();
                    return;
                }
                if (NotificationService.replyActions.size() < 1) {
                    Toast.makeText(Contact_Chat_Act.this, "list is empty", Toast.LENGTH_LONG).show();
                    return;
                }


                Action action = NotificationService.replyActions.get(userName.getText().toString());
                if (action == null) {
                    Toast.makeText(Contact_Chat_Act.this, "Cannot reply", Toast.LENGTH_LONG).show();
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
                    note_new.setKey(0);
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
        });        /*LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);


        linearLayoutManager.setStackFromEnd(true);*/
        /*recyclerView.setLayoutManager(linearLayoutManager);*/

        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this));
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Chat_contact_Adapter.selected_msgs.size() < 1) {
                    Toast.makeText(Contact_Chat_Act.this, getResources().getString(R.string.please_select_atleast_one_image_delete), Toast.LENGTH_LONG).show();
                    return;
                }
                final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(Contact_Chat_Act.this).create();
                View layout1 = LayoutInflater.from(Contact_Chat_Act.this).inflate(R.layout.delet_dialog, findViewById(R.id.your_dialog_root_element));
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
                        alertDialog.dismiss();
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
                                        db.deleteNote(notesList.get(j));
                                    }
                                }
                            }
                            Log.e("ID", "loop" + i);
                        }

                        delete_clicked = true;
                        initlize_list();


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

    }

    private void checkReplyAction() {
        if (NotificationService.replyActions.get(this.userName.getText().toString()) == null) {
            direct_send_layout.setVisibility(View.GONE);
            this.reply_on_whatsapp.setVisibility(View.VISIBLE);
            return;
        }

        direct_send_layout.setVisibility(View.VISIBLE);
        this.reply_on_whatsapp.setVisibility(View.GONE);
        /*
        if (Action.isQuickReply()) {
            direct_send_layout.setVisibility(View.VISIBLE);
            this.reply_on_whatsapp.setVisibility(View.GONE);
            return;
        } else {
            direct_send_layout.setVisibility(View.GONE);
            this.reply_on_whatsapp.setVisibility(View.VISIBLE);
        }*/


      /*  String mimeString = "vnd.android.cursor.item/vnd.com.whatsapp.video.call";
        String mimeString_voice_call = "vnd.android.cursor.item/vnd.com.whatsapp.voip.call";
        Cursor cursor;
        String displayName = null;
        String name=Shared.getInstance().getStringValueFromPreference(getResources().getString(R.string.contact_name_pref), "", this);
        Long _id;
        ContentResolver resolver = getApplicationContext().getContentResolver();
        cursor = resolver.query(ContactsContract.Data.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME);
        while (cursor.moveToNext()) {
            _id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Data._ID));
            displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            String mimeType = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.MIMETYPE));

            if (displayName.equals(name)) {
                if (mimeType.equals(mimeString)) {
                    String data = "content://com.android.contacts/data/" + _id;
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_VIEW);
                    sendIntent.setDataAndType(Uri.parse(data), mimeString_voice_call);
                    sendIntent.setPackage("com.whatsapp");
                    startActivity(sendIntent);
                }
            }
        }*/

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

       /* PackageManager packageManager = getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);

        try {
            String url = "https://api.whatsapp.com/send?phone=" + "+923145845705" + "&text=" + URLEncoder.encode("Hello  how are you ", "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                startActivity(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
/*

       Uri uri = Uri.parse("smsto:" + "03145845705");
        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
        i.putExtra("sms_body", "hello hwo are you ");
        i.setPackage("com.whatsapp");
        startActivity(i);
*/

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
        main_db = new DatabaseHelper(this);
        db = new DatabaseHelper_new(this);
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
            mAdapter = new Chat_contact_Adapter(true, Contact_Chat_Act.this, notesList_new, ad_poistion);
            recyclerView.setAdapter(mAdapter);
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
                if (deleted) {
                    DatabaseHelper db_ = new DatabaseHelper(getApplicationContext());
                    Note note = db_.getNote(db_.CheckIsDataAlreadyInDBorNot(Note.COLUMN_NUMBER, userName.getText().toString()));
                    db_.deleteNote(note);
                }
                no_chat_found.setVisibility(View.VISIBLE);
                Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.chat_clear_pref), true, Contact_Chat_Act.this);
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

            if (delete_clicked) {
                delete_clicked = false;
                int ID = 0;
                List<Note> notesList_main = new ArrayList<>(main_db.getAllNotes());
                for (int i = 0; i < notesList_main.size(); i++) {
                    ID = notesList_main.get(i).getId();
                    if (notesList_main.get(i).getNote().equals(userName.getText().toString())) {
                        break;
                    }
                }
                Chat_contact_Adapter.selected_msgs.clear();
                Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_specific_chat_pref), false, Contact_Chat_Act.this);

                Date dt = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd mm yyyy");
                SimpleDateFormat dateFormat_4_itration = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String time1 = sdf.format(dt);
                String date = dateFormat.format(dt);
                String date_4_itration = dateFormat_4_itration.format(dt);
                Log.e("notesList_new.size()", "" + notesList_new.size());

                if (notesList_new.size() > 0) {
                    Note_new note1 = notesList_new.get(notesList_new.size() - 1);
                    Note note = new Note();
                    note.setNote(note1.getNote());
                    note.setNumber(note1.getNumber());
                    note.setTime_n_date_4_itration(date_4_itration);
                    note.setId(ID);
                    note.setTime(note1.getTime());
                    note.setPath(note1.getPath());
                    note.setTimestamp(note1.getTimestamp());
                    note.setStatus(note1.getstatus());
                    Log.e("note", "" + note);

                    main_db.updateNote(note);
                    Intent msgrcv = new Intent(getResources().getString(R.string.broad_cast_intent_for_main_chat));
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(msgrcv);
                }

            }
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
        Chat_contact_Adapter.selected_msgs.clear();

        if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.long_pressd_specific_chat_pref),
                false, getApplicationContext())) {
            Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_specific_chat_pref), false, getApplicationContext());
            mAdapter.notifyDataSetChanged();
            return;
        }
        Chat_contact_Adapter.my_unifiedNativeAd = null;

        String input = userName.getText().toString();
        input = input.replace(" ", "");
        input = input.trim();
        Shared.getInstance().saveIntToPreferences(input + getResources().getString(R.string.num_of_unread_msgs_pref), 0, getApplicationContext());

        Shared.getInstance().saveBooleanToPreferences(input, false, this);
        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_specific_chat_pref), false, Contact_Chat_Act.this);
        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.chat_clear_pref), false, getApplicationContext());
        if (mInterstitialAd.isLoaded()) mInterstitialAd.show();
        else {
            if (from_notification) {
                if (!MainActivity.is_main_act_active) {
                    startActivity(new Intent(Contact_Chat_Act.this, MainActivity.class));
                }
            }
            finish();
        }
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                if (from_notification) {
                    if (!MainActivity.is_main_act_active) {
                        startActivity(new Intent(Contact_Chat_Act.this, MainActivity.class));
                    }
                }
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_specific_chat_pref), false, getApplicationContext());

        Chat_contact_Adapter.my_unifiedNativeAd = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        loading_list = true;
        is_act_running = false;
        Shared.getInstance().saveBooleanToPreferences(input_trim + "is_chat_open", false, Contact_Chat_Act.this);
        String input = userName.getText().toString();
        input = input.replace(" ", "");
        input = input.trim();

        Shared.getInstance().saveIntToPreferences(input + getResources().getString(R.string.num_of_unread_msgs_pref), 0, Contact_Chat_Act.this);
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
        Shared.getInstance().saveBooleanToPreferences(input_trim + "is_chat_open", true, Contact_Chat_Act.this);
        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.app_is_open_pref), true, Contact_Chat_Act.this);

    }

}