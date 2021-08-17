package com.malam.whatsdelet.nolastseen.hiddenchat.Fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.malam.whatsdelet.nolastseen.hiddenchat.Activities.Image_Viewer_Act;
import com.malam.whatsdelet.nolastseen.hiddenchat.Activities.MainActivity;
import com.malam.whatsdelet.nolastseen.hiddenchat.Activities.NewWhatsappStatus;
import com.malam.whatsdelet.nolastseen.hiddenchat.Model.Music_model;
import com.malam.whatsdelet.nolastseen.hiddenchat.Service.FileOperations;
import com.malam.whatsdelet.nolastseen.hiddenchat.Service.FilesData;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.Shared;
import com.whatsdelete.Test.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static com.malam.whatsdelet.nolastseen.hiddenchat.Activities.MainActivity.animation;
import static com.malam.whatsdelet.nolastseen.hiddenchat.Activities.MainActivity.progressDialog;
import static com.malam.whatsdelet.nolastseen.hiddenchat.Activities.MainActivity.refresh_dialog;

public class ImagesFragment extends Fragment {
    static public Context context;
    View root_view;
    ImageView save;
    String path=FilesData.WHATSAPP_STATUSES_LOCATION;
    String s;
    private RecyclerView recyclerView;
    LinearLayout nostatus_layout;
    public static ArrayList<Music_model> muList = new ArrayList<>();
    public static My_Adapter mReAdapter;
    Boolean images_found = false;
    GridLayoutManager gLay;
    Button refresh;
    Boolean loading_completed = true;
    public static int position_call = 0;
    Boolean long_pressed=false;
    Boolean ad_loaded = false;
    ProgressBar progressbar;
    public static int selected_items = 0;
    ProgressDialog downloading_progress_bar,deleting_progress_bar;
    ArrayList<Uri> imageUris;
    public static Boolean net_is_connected = false;
    public static UnifiedNativeAd my_unifiedNativeAd = null;
    static Boolean ad_request_sent = false;
    public static List<Music_model> select_list = new ArrayList<>();
    static int my_position = 0;
    static LinearLayout delete_layout;
    InterstitialAd mInterstitialAd;
    DocumentFile[] documentFiles;
    String infoName;
    ImageView imageViewNew;
    ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        // Inflate the layout for this fragment
        if (root_view == null) {
            root_view = LayoutInflater.from(context).inflate(R.layout.fragment_images, container, false);
            muList.clear();
            intilize_componenets();
            new bg_task().execute();
            LocalBroadcastManager.getInstance(context).registerReceiver(refresh_status, new IntentFilter("refresh"));
        }
        imageUris = new ArrayList<Uri>();
        path = getActivity().getIntent().getStringExtra("path");
       /* save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new download_images().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });*/
        //infoName = getArguments().getString("name");
        progressDialog = new ProgressDialog(getActivity());
        return root_view;
    }

    private BroadcastReceiver refresh_status = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            progressbar.setVisibility(View.VISIBLE);
            new bg_task().execute();

        }
    };


    private void intilize_componenets() {

        downloading_progress_bar = new ProgressDialog(getActivity());
        downloading_progress_bar.setTitle(getResources().getString(R.string.please_wait_while_downloading));
        downloading_progress_bar.setCancelable(false);
        progressbar = (ProgressBar) root_view.findViewById(R.id.progressbar);
        recyclerView = root_view.findViewById(R.id.recyclerView);
        refresh = (Button) root_view.findViewById(R.id.refresh);
        //save = root_view.findViewById(R.id.saveTop);
        delete_layout = root_view.findViewById(R.id.delete_layout);

        gLay = new GridLayoutManager(getContext(), 2);
        if (is_net_connected())
            gLay.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (position > 1 && (position + 1) % 4 == 0) {
                        return 1;
                    } else {
                        return 1;
                    }
                }
            });
        recyclerView.setLayoutManager(gLay);
        // recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(context));
        nostatus_layout = (LinearLayout) root_view.findViewById(R.id.noChat);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    progressDialog.show();
                    refresh_dialog.startAnimation(animation);
                    muList.clear();
                    mReAdapter.notifyDataSetChanged();
                    new bg_task().execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public class bg_task extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            fetchImages();
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            recyclerView.getRecycledViewPool().clear();
            mReAdapter = new ImagesFragment.My_Adapter(muList,context,net_is_connected,1);
            recyclerView.setAdapter(mReAdapter);
            if (muList.size() > 0) {
                images_found = true;
            } else {
                images_found = false;
            }
            if (images_found) {
                recyclerView.setVisibility(View.VISIBLE);
                nostatus_layout.setVisibility(View.GONE);
            } else {
                nostatus_layout.setVisibility(View.VISIBLE);
            }
            progressbar.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        MainActivity.animation_start = false;
                        progressDialog.dismiss();
                    } catch (NullPointerException ner) {
                    } catch (Exception er) {
                    }
                }
            }, 300);
        }
    }
    public Boolean is_net_connected() {
        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mgr.getActiveNetworkInfo();

        if (netInfo != null) {
            if (netInfo.isConnected()) {
                // Internet Available
                net_is_connected = true;
                return true;
            } else {
                //No internet
                net_is_connected = false;
                return false;
            }
        } else {
            return false;
        }
    }
    private void fetchImages() {
       muList.clear();;


        try {
            if (Build.VERSION.SDK_INT >= 29) {
                // uri is the path which we've saved in our shared pref

                Uri uri= Uri.parse(Shared.getInstance().getStringValueFromPreference(FilesData.STATUSES_FOLDER_PATH, FilesData.WHATSAPP_STATUSES_LOCATION_ANDROID_11,context));
                DocumentFile fromTreeUri = DocumentFile.fromTreeUri(context, uri);
                documentFiles= fromTreeUri.listFiles();

                for (int i = 0; i < documentFiles.length; i++) {

                    if (documentFiles[i].getName().endsWith(".jpg") ||
                            documentFiles[i].getName().endsWith(".png")) {
                        Music_model music_model = new Music_model();
                        music_model.setFile_name(documentFiles[i].getName());
                        music_model.setFile_path(documentFiles[i].getUri().toString());
                        music_model.setIs_selected(false);
                        muList.add(music_model);
                        Log.d("TAG", "fetchImages" + documentFiles[i].getName());

                    }
                }
            }
            else {
                String path;
                 path = Environment.getExternalStorageDirectory().toString() + FilesData.WHATSAPP_STATUSES_LOCATION;

                Log.d("test", "onStart: " + path);
                File dir = new File(path);
                File[] files = dir.listFiles();
                assert files != null;
                Arrays.sort(files, new Comparator<File>() {
                    public int compare(File f1, File f2) {
                        return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
                    }
                });

                for (int i = 0; i < files.length; i++) {
                    if (files[i].getName().endsWith(".jpg") || files[i].getName().endsWith(".png"))/* || files[i].getName().endsWith(".mp4") || files[i].getName().endsWith(".3gp") || files[i].getName().endsWith(".avi") || files[i].getName().endsWith(".flv") || files[i].getName().endsWith(".wmv"))*/ {
                        Music_model music_model = new Music_model();
                        music_model.setFile_path(files[i].getAbsolutePath());
                        music_model.setIs_selected(false);
                        muList.add(music_model);
                        if (net_is_connected)
                            if (i == 2) {
                                music_model.setFile_path(files[i].getAbsolutePath());
                                music_model.setIs_selected(false);
                                muList.add(music_model);
                            }
                    }
                }
            }


        } catch (Exception ex) {

        }
    }

    public class My_Adapter extends RecyclerView.Adapter<My_Adapter.MyHolder> {
        List<Music_model> muList;
        int position_call=0;
        Context context;
        private static final int CONTENT_TYPE = 0;
        private static final int AD_TYPE = 1;
        private static final int LIST_AD_DELTA = 2;
        MyHolder new_holder;

        @Override
        public My_Adapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.status_row_layot, parent, false);
            My_Adapter.MyHolder holder = new My_Adapter.MyHolder(view);
            return holder;

        }

        private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
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
            if (nativeAd.getMediaContent() == null) {
                mediaView.setVisibility(View.GONE);
            } else {
                mediaView.setVisibility(View.VISIBLE);
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

        @Override
        public void onBindViewHolder(final ImagesFragment.My_Adapter.MyHolder holder, final int position) {
            try {
                new_holder = holder;
                my_position = position;
                switch (getItemViewType(position)) {
                    case AD_TYPE:
                        holder.progress.setVisibility(View.VISIBLE);
                        holder.main_layout.setVisibility(View.INVISIBLE);
                        if (my_unifiedNativeAd != null) {
                            UnifiedNativeAdView adView = (UnifiedNativeAdView) LayoutInflater.from(context).inflate(R.layout.ad_unified_row_item_fourth, null);
                            populateUnifiedNativeAdView(my_unifiedNativeAd, adView);
                            try {
                                holder.frameLayout_1.removeAllViews();
                                holder.frameLayout_1.addView(adView);
                                holder.frameLayout_1.setVisibility(View.VISIBLE);
                                holder.main_layout.setVisibility(View.GONE);
                                 holder.progress.setVisibility(View.GONE);
                            } catch (NullPointerException asd) {
                            }
                            return;
                        }
                        if (ad_request_sent) {
                            return;
                        }
                        AdLoader.Builder builder = new AdLoader.Builder(context, context.getResources().getString(R.string.nativeID_receyler_view));
                        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                            // OnUnifiedNativeAdLoadedListener implementation.
                            @Override
                            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                                my_unifiedNativeAd = unifiedNativeAd;
                                //FrameLayout frameLayout = root_view.findViewById(R.id.fl_adplaceholder);
                                UnifiedNativeAdView adView = (UnifiedNativeAdView) LayoutInflater.from(context).inflate(R.layout.ad_unified_row_item_fourth, null);
                                populateUnifiedNativeAdView(unifiedNativeAd, adView);
                                try {
                                    holder.frameLayout_1.removeAllViews();
                                    holder.frameLayout_1.addView(adView);
                                } catch (NullPointerException asd) {
                                }
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
                                holder.frameLayout_1.setVisibility(View.GONE);
                                holder.progress.setVisibility(View.GONE);
                                holder.main_layout.setVisibility(View.GONE);
                                ad_request_sent = false;
                            }

                            @Override
                            public void onAdLoaded() {
                                super.onAdLoaded();
                                holder.frameLayout_1.setVisibility(View.VISIBLE);
                                holder.progress.setVisibility(View.GONE);
                                holder.main_layout.setVisibility(View.GONE);
                                holder.video_imageview.setVisibility(View.GONE);
                            }
                        }).build();
                        adLoader.loadAd(new AdRequest.Builder().addTestDevice(context.getResources().getString(R.string.zegar_device)).addTestDevice(context.getResources().getString(R.string.test_device_j5)).addTestDevice(context.getResources().getString(R.string.vicky_s8)).addTestDevice(context.getResources().getString(R.string.test_device_white)).addTestDevice("A86A0D556F68465C49063589837FCF98").build());
                        ad_request_sent = true;
                        return;
                }
                holder.main_layout.setVisibility(View.VISIBLE);
                holder.progress.setVisibility(View.GONE);


                if (Build.VERSION.SDK_INT >= 29) {
                    Uri uri = Uri.parse(muList.get(getRealPosition(position)).getFile_path());

                    if (uri.toString().endsWith(".jpg") ||  uri.toString().endsWith(".png")) {
                        holder.video_imageview.setVisibility(View.GONE);
                    } else holder.video_imageview.setVisibility(View.VISIBLE);
                    if (!Shared.getInstance().getBooleanValueFromPreference(context.getResources().getString(R.string.long_pressd_status_pref), false, context)) {
                        muList.get(getRealPosition(position)).setIs_selected(false);
                        holder.selectedlayout.setVisibility(View.GONE);
                        holder.share_n_delete_layout.setVisibility(View.GONE);
                    } else {
                        holder.selectedlayout.setVisibility(View.VISIBLE);
                        holder.share_n_delete_layout.setVisibility(View.GONE);
                    }
                    if (muList.get(getRealPosition(position)).getIs_selected()) {
                        holder.selectedImage.setVisibility(View.VISIBLE);
                    } else holder.selectedImage.setVisibility(View.GONE);
                    Glide.with(context).load(uri).skipMemoryCache(true).centerCrop()
                            .into(holder.imgV);

                }else {
                    File f = new File(muList.get(getRealPosition(position)).getFile_path());

                    if (f.getName().endsWith(".jpg") || f.getName().endsWith(".png")) {
                        holder.video_imageview.setVisibility(View.GONE);
                    } else holder.video_imageview.setVisibility(View.VISIBLE);
                    if (!Shared.getInstance().getBooleanValueFromPreference(context.getResources().getString(R.string.long_pressd_status_pref), false, context)) {
                        muList.get(getRealPosition(position)).setIs_selected(false);
                        holder.selectedlayout.setVisibility(View.GONE);
                        holder.share_n_delete_layout.setVisibility(View.GONE);
                    } else {
                        holder.selectedlayout.setVisibility(View.VISIBLE);
                        holder.share_n_delete_layout.setVisibility(View.GONE);
                    }
                    if (muList.get(getRealPosition(position)).getIs_selected()) {
                        holder.selectedImage.setVisibility(View.VISIBLE);
                    } else holder.selectedImage.setVisibility(View.GONE);
                    Glide.with(context).load(f).skipMemoryCache(true).centerCrop()
                            .into(holder.imgV);

                }






                holder.imgV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if (Shared.getInstance().getBooleanValueFromPreference(context.getResources().getString(R.string.long_pressd_status_pref), false, context)) {
                            if (muList.get(getRealPosition(position)).getIs_selected()) {
                                muList.get(getRealPosition(position)).setIs_selected(false);
                                NewWhatsappStatus.selected_items--;
                                //MainActivity.selected_position.remove(muList.get(getRealPosition(position)));
                                select_list.remove(muList.get(getRealPosition(position)));
                                //     selected_position.remove(position);

                            } else {
                                muList.get(getRealPosition(position)).setIs_selected(true);
                                NewWhatsappStatus.selected_items++;
                                //NewWhatsappStatus.selected_position.add(getRealPosition(position));
                                select_list.add(muList.get(getRealPosition(position)));
                                //     selected_position.add(position);
                            }
                           // NewWhatsappStatus.check_selected_items();
                            NewWhatsappStatus.check_selected_items();
                            notifyDataSetChanged();
                            Intent msgrcv = new Intent("Msg");
                            LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);

                            notifyItemChanged(position);
                            return;
                        }



                        /*Boolean is_video = false;
                        File f = new File(muList.get(getRealPosition(position)).getFile_path());
                        if (f.getName().endsWith(".mp4") || f.getName().endsWith(".3gp") || f.getName().endsWith(".avi") || f.getName().endsWith(".3gp") || f.getName().endsWith(".M4V") || f.getName().endsWith(".flv") || f.getName().endsWith(".png") || f.getName().endsWith(".jpeg")) {
                            is_video = true;
                        } else is_video = false;*/
                        Shared.getInstance().saveStringToPreferences(getResources().getString(R.string.vidoe_path), muList.get(getRealPosition(position)).getFile_path(), getContext());
                        Intent intent = new Intent(context, Image_Viewer_Act.class);
                        intent.putExtra("pos", "" + getRealPosition(position));
                        intent.putExtra("delete_btn", false);
                       // intent.putExtra("is_video", is_video);
                        intent.putExtra(context.getResources().getString(R.string.is_net_connected_intent), net_is_connected);
                        intent.putExtra("path", muList.get(getRealPosition(position)).getFile_path());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });

                holder.saveTop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new download_images().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                });

                holder.shareImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                holder.imgV.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (!Shared.getInstance().getBooleanValueFromPreference(context.getResources().getString(R.string.long_pressd_status_pref), false, context)) {
                            Shared.getInstance().saveBooleanToPreferences(context.getResources().getString(R.string.long_pressd_status_pref), true, context);
                            NewWhatsappStatus.selected_items++;
                            muList.get(getRealPosition(position)).setIs_selected(true);
                           NewWhatsappStatus.check_selected_items();
                            Intent msgrcv = new Intent("Msg");
                            LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
                            select_list.add(muList.get(getRealPosition(position)));
                            notifyDataSetChanged();
                        } else {
                            return false;
                        }
                        Shared.getInstance().saveIntToPreferences(context.getResources().getString(R.string.delete_tabs_position_pref), position_call+1, context);
                        Shared.getInstance().saveStringToPreferences(context.getResources().getString(R.string.delete_diloug_title_pref), context.getResources().getString(R.string.delete_status_title), context);
                        Shared.getInstance().saveStringToPreferences(context.getResources().getString(R.string.delete_dialoug_msg_pref), context.getResources().getString(R.string.delete_status_msg), context);
                        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            //deprecated in API 26
                            try {
                                vibrator.vibrate(100);
                            } catch (NullPointerException asd) {
                            } catch (Exception asd) {
                            }
                        }
                        return false;
                    }
                });

            }/**/ catch (IndexOutOfBoundsException iasd) {
            } catch (Exception er) {
            }
        }

        @Override
        public int getItemViewType(int position) {

            if (net_is_connected) {
                if (position>1 && (position+1) % 4 == 0) {
                    return AD_TYPE;
                }
                return CONTENT_TYPE;
            }
            return CONTENT_TYPE;
        }

        private int getRealPosition(int position) {

            return position;
        }

        @Override
        public int getItemCount() {

            return muList.size();
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
                for (int i = 0; i < ImagesFragment.select_list.size(); i++) {
                    try {
                        File imageFile = new File(ImagesFragment.select_list.get(i).getFile_path());
                        File destFile = new File(Environment.getExternalStorageDirectory().toString() + FilesData.getSavedFilesLocation(), imageFile.getName());
                        FileOperations.saveAndRefreshFiles(imageFile, destFile);
                        MediaScannerConnection.scanFile(getActivity().getApplicationContext(),
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
                LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).sendBroadcast(saved_intent);
                selected_items = 0;
                //check_selected_items();
                Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_status_pref), false, context);
                ImagesFragment.mReAdapter.notifyDataSetChanged();
                Toast.makeText(context, "Image(s) Saved in Gallery", Toast.LENGTH_LONG).show();
                ImagesFragment.select_list.clear();
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

        public My_Adapter(ArrayList<Music_model> mylist,Context context,boolean c,int position_call) {
            this.muList = mylist;
            this.context=context;
            this.position_call=position_call;
        }

        class MyHolder extends RecyclerView.ViewHolder {
            ImageView imgV;
            ImageView selectedImage,saveTop,shareImage;
            LinearLayout video_imageview, selectedlayout;
            FrameLayout frameLayout_1;
            ProgressBar progress;
            RelativeLayout share_n_delete_layout,main_layout;
            Animation animation;


            public MyHolder(View itemView) {
                super(itemView);

               try {
                   imgV = itemView.findViewById(R.id.imageView);
                   saveTop = itemView.findViewById(R.id.saveTop);
                   shareImage = itemView.findViewById(R.id.shareImage);
                   video_imageview = itemView.findViewById(R.id.video_imageview);
                   selectedlayout = itemView.findViewById(R.id.selectedlayout);
                   progress = itemView.findViewById(R.id.progress);
                   selectedImage = itemView.findViewById(R.id.selectedImage);
                   share_n_delete_layout = itemView.findViewById(R.id.share_n_delete_layout);
                   main_layout = itemView.findViewById(R.id.main_layout);
               }catch (NullPointerException asd) {
               } catch (Exception asd) {
               }
                try {
                    frameLayout_1 = (FrameLayout) itemView.findViewById(R.id.frame_layout_adapter);
                } catch (NullPointerException asd) {
                } catch (Exception asd) {
                }
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.delete_file_status_pref), false, getActivity())) {
            muList.clear();
            new bg_task().execute();
        }
        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.delete_file_status_pref), false, context);
    }


    /*public static void check_selected_items() {
        if (selected_items > 0) {
          //  title.setText("Selected Items ( " + selected_items + " )");
            delete_layout.setVisibility(View.VISIBLE);
           *//* if (position_call == 1) {
                save.setVisibility(View.GONE);
               // delete.setVisibility(View.VISIBLE);
            } else {
                save.setVisibility(View.VISIBLE);
               // delete.setVisibility(View.GONE);
            }*//*
        } else {
           // title.setText(R.string.statu_saver);
            //delete_layout.setVisibility(View.GONE);
            Shared.getInstance().saveBooleanToPreferences("long_pressd_status_pref", false, context);
            Shared.getInstance().saveBooleanToPreferences("long_pressd_downstatus_pref", false, context);
        }
    }*/





}