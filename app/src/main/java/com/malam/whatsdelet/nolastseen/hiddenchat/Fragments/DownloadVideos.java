package com.malam.whatsdelet.nolastseen.hiddenchat.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.malam.whatsdelet.nolastseen.hiddenchat.Activities.DownloadedStatuses;
import com.malam.whatsdelet.nolastseen.hiddenchat.Activities.Image_Viewer_Act;
import com.malam.whatsdelet.nolastseen.hiddenchat.Model.File_model;
import com.malam.whatsdelet.nolastseen.hiddenchat.Service.FilesData;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.Shared;
import com.whatsdelete.Test.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class DownloadVideos extends Fragment {
    View root_view;
    static Context context;

    private static RecyclerView recyclerView;
    static LinearLayout nostatus_layout;
    public static ArrayList<File_model> muList = new ArrayList<File_model>();
    public static My_Adapter mReAdapter;
    static Boolean images_found = false;
    GridLayoutManager gLay;
    Button refresh;
    static ProgressBar progressbar;

    public static Boolean net_is_connected = false;
    public static UnifiedNativeAd my_unifiedNativeAd = null;
    static Boolean ad_request_sent = false;
    public static List<File_model> selected_list = new ArrayList<File_model>();
    public static int my_position = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity().getApplicationContext();
        if (root_view == null) {
            root_view = LayoutInflater.from(context).inflate(R.layout.fragment_download_videos, container, false);
            muList.clear();
            intilize_componenets();
            new bg_task().execute();
            LocalBroadcastManager.getInstance(context).registerReceiver(refresh_status, new IntentFilter("refresh"));
            LocalBroadcastManager.getInstance(context).registerReceiver(refresh_status, new IntentFilter("refresh_images"));
            LocalBroadcastManager.getInstance(context).registerReceiver(refresh_status, new IntentFilter("ref_download"));
        }
        return root_view;
    }

    private BroadcastReceiver refresh_status = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            new bg_task().execute();
        }
    };

    private void intilize_componenets() {


        progressbar = (ProgressBar) root_view.findViewById(R.id.progressbar);
        recyclerView = root_view.findViewById(R.id.recyclerView);
        refresh = (Button) root_view.findViewById(R.id.refresh);

        gLay = new GridLayoutManager(getContext(), 2);
        if (is_net_connected())
            gLay.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (position>1 && (position+1) % 4 == 0) {
                        return 1;
                    } else {
                        return 1;
                    }
                }
            });
        recyclerView.setLayoutManager(gLay);
        nostatus_layout = (LinearLayout) root_view.findViewById(R.id.noChat);
    }

    public static class bg_task extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            fetchImages();
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            recyclerView.getRecycledViewPool().clear();
            mReAdapter = new My_Adapter(muList, context, net_is_connected, 2);
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


        }

    }

    public static Boolean is_net_connected() {
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

    private static void fetchImages() {
        try {


            try {
                String path = Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/.Statuses";
                File destFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM)
                        + "/DownloadedStatuses");
                path = Environment.getExternalStorageDirectory().toString() + FilesData.getSavedFilesLocation(context);
                Log.d("test", "onStart: " + path);
                File dir=null;
                if (Build.VERSION.SDK_INT>=29){
                    dir=destFile;
                }
                else
                    dir = new File(path);

                File[] files = dir.listFiles();


                for (int i = 0; i < files.length; i++) {

                    if (files[i].getName().endsWith(".3GPP") || files[i].getName().endsWith(".3gp") || files[i].getName().endsWith(".mp4")
                            || files[i].getName().endsWith(".flv") || files[i].getName().endsWith(".mkv") || files[i].getName().endsWith(".webm")
                            || files[i].getName().endsWith(".avi")) {
/*
                    my_holder.muList.add(files[i].getAbsolutePath());*/
                        File_model file_model = new File_model();
                        file_model.setFile_name(files[i].getName());
                        file_model.setFile_path(files[i].getAbsolutePath());
                        file_model.setIs_selected(false);
                        file_model.set_Last_modified(String.valueOf(files[i].lastModified()));

                        muList.add(file_model);

                    }

                }


            } catch (Exception ex) {
            }

            List<File_model> muList_new = new ArrayList<>();
            muList_new.addAll(muList);
            muList.clear();

            int position = 0;
            for (int i = 0; i < muList_new.size(); i++) {
                if (muList_new.get(i).getFile_name().endsWith(".3gp") || muList_new.get(i).getFile_name().endsWith(".mp4")
                        || muList_new.get(i).getFile_name().endsWith(".flv") || muList_new.get(i).getFile_name().endsWith(".mkv")
                        || muList_new.get(i).getFile_name().endsWith(".webm") || muList_new.get(i).getFile_name().endsWith(".avi")) {
                    File_model file_model = new File_model();
                    file_model.setFile_path(muList_new.get(i).getFile_path());
                    file_model.setIs_selected(false);
                    file_model.set_Last_modified(muList_new.get(i).get_Last_modified());
                    position++;
                    if (net_is_connected)
                        if (position == 2) {
                            file_model.setFile_path(muList_new.get(i).getFile_path());
                            file_model.setIs_selected(false);
                            file_model.set_Last_modified(muList_new.get(i).get_Last_modified());
                            muList.add(file_model);
                        }
                    muList.add(file_model);
                }
            }

            Collections.sort(muList, new Comparator<File_model>() {
                @Override
                public int compare(File_model file_model, File_model t1) {
                    return t1.get_Last_modified().compareTo(file_model.get_Last_modified());
                }

            });
        } catch (NullPointerException asd) {
        } catch (Exception asd) {
        }
    }
    public static class My_Adapter extends RecyclerView.Adapter<My_Adapter.MyHolder> {
        ArrayList<File_model> muList;
        int position_call = 1;
        Context context;
        private static final int CONTENT_TYPE = 0;
        private static final int AD_TYPE = 1;
        private static final int LIST_AD_DELTA = 2;
        My_Adapter.MyHolder new_holder;

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
        public void onBindViewHolder(final My_Adapter.MyHolder holder, final int position) {
            try {
                new_holder = holder;
                my_position = position;
                switch (getItemViewType(position)) {
                    case AD_TYPE:
                        holder.progress.setVisibility(View.VISIBLE);
                        holder.main_layout.setVisibility(View.GONE);
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
                        VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();
                        NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
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

                File f = new File(muList.get(getRealPosition(position)).getFile_path());
                if (f.getName().endsWith(".mp4") || f.getName().endsWith(".3gp") || f.getName().endsWith(".avi") || f.getName().endsWith(".3gp") || f.getName().endsWith(".M4V") || f.getName().endsWith(".flv")) {
                    holder.video_imageview.setVisibility(View.VISIBLE);
                } else holder.video_imageview.setVisibility(View.GONE);
                if (!Shared.getInstance().getBooleanValueFromPreference(context.getResources().getString(R.string.long_pressd_downstatus_pref), false, context)) {
                    muList.get(getRealPosition(position)).setIs_selected(false);
                    holder.selectedlayout.setVisibility(View.GONE);
                    holder.share_n_delete_layout.setVisibility(View.GONE);
                } else {
                    holder.selectedlayout.setVisibility(View.GONE);
                    holder.share_n_delete_layout.setVisibility(View.GONE);
                }
                if (muList.get(getRealPosition(position)).getIs_selected()) {
                    holder.selectedImage.setVisibility(View.VISIBLE);
                } else holder.selectedImage.setVisibility(View.GONE);
                Glide.with(context).load(f).into(holder.imgV);


                holder.imgV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Shared.getInstance().getBooleanValueFromPreference(context.getResources().getString(R.string.long_pressd_downstatus_pref), false, context)) {
                            if (muList.get(getRealPosition(position)).getIs_selected()) {
                                muList.get(getRealPosition(position)).setIs_selected(false);
                                 DownloadedStatuses.selected_items--;
                                //Downloaded_status.selected_position.remove(muList.get(getRealPosition(position)));
                                selected_list.remove(muList.get(getRealPosition(position)));
                                //     selected_position.remove(position);

                            } else {
                                muList.get(getRealPosition(position)).setIs_selected(true);
                                DownloadedStatuses.selected_items++;
                                //Downloaded_status.selected_position.add(getRealPosition(position));
                                selected_list.add(muList.get(getRealPosition(position)));
                                //     selected_position.add(position);
                            }
                            DownloadedStatuses.check_selected_items();
                            notifyDataSetChanged();
                            return;
                        }

                        Boolean is_video = false;
                        File f = new File(muList.get(getRealPosition(position)).getFile_path());
                        if (f.getName().endsWith(".mp4") || f.getName().endsWith(".3gp") || f.getName().endsWith(".avi") || f.getName().endsWith(".3gp") || f.getName().endsWith(".M4V") || f.getName().endsWith(".flv")) {
                            is_video = true;
                        } else is_video = false;
                        Shared.getInstance().saveIntToPreferences(context.getResources().getString(R.string.image_position_pref), getRealPosition(position), context);
                        Shared.getInstance().saveIntToPreferences(context.getResources().getString(R.string.from_status_pref), position_call, context);
                        Intent intent = new Intent(context, Image_Viewer_Act.class);
                        intent.putExtra("pos", "" + getRealPosition(position));
                        intent.putExtra("delete_btn", false);
                        intent.putExtra("is_video", is_video);
                        intent.putExtra(context.getResources().getString(R.string.is_net_connected_intent), net_is_connected);
                        intent.putExtra("path", muList.get(getRealPosition(position)).getFile_path());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });
                holder.imgV.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (!Shared.getInstance().getBooleanValueFromPreference(context.getResources().getString(R.string.long_pressd_downstatus_pref), false, context)) {
                            Shared.getInstance().saveBooleanToPreferences(context.getResources().getString(R.string.long_pressd_downstatus_pref), true, context);
                            DownloadedStatuses.selected_items++;
                            muList.get(getRealPosition(position)).setIs_selected(true);
                            DownloadedStatuses.check_selected_items();
                            notifyDataSetChanged();
                            selected_list.add(muList.get(getRealPosition(position)));
                        } else {
                            return false;
                        }
                        Shared.getInstance().saveIntToPreferences(context.getResources().getString(R.string.delete_tabs_position_pref), position_call + 1, context);
                        Shared.getInstance().saveStringToPreferences(context.getResources().getString(R.string.delete_diloug_title_pref), context.getResources().getString(R.string.delete_status_title), context);
                        Shared.getInstance().saveStringToPreferences(context.getResources().getString(R.string.delete_dialoug_msg_pref), context.getResources().getString(R.string.delete_status_msg), context);
                        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            //deprecated in API 26
                            vibrator.vibrate(150);
                        }
                        return false;
                    }
                });
            } catch (IndexOutOfBoundsException iasd) {
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

        public My_Adapter(ArrayList<File_model> mylist, Context context, boolean c, int position_call) {
            this.muList = mylist;
            this.context = context;
            this.position_call = position_call;
        }

        class MyHolder extends RecyclerView.ViewHolder {
            ImageView imgV, selectedImage;
            LinearLayout video_imageview, selectedlayout;
            FrameLayout frameLayout_1;
            ProgressBar progress;
            RelativeLayout share_n_delete_layout, main_layout;
            Animation animation;


            public MyHolder(View itemView) {
                super(itemView);

                imgV = itemView.findViewById(R.id.imageView);
                video_imageview = itemView.findViewById(R.id.video_imageview);
                selectedlayout = itemView.findViewById(R.id.selectedlayout);
                progress = itemView.findViewById(R.id.progress);
                selectedImage = itemView.findViewById(R.id.selectedImage);
                share_n_delete_layout = itemView.findViewById(R.id.share_n_delete_layout);
                main_layout = itemView.findViewById(R.id.main_layout);
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
            new DownloadImages.bg_task().execute();
        }
        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.delete_file_status_pref), false, context);
    }
}