package com.malam.whatsdelet.nolastseen.hiddenchat.Fragments;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
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


public class DownloadImages extends Fragment {
    private static final String APP_NAME_CONSTANT = "WhatsDelete";
    View root_view;
    static Context context;

    private static RecyclerView recyclerView;
    static LinearLayout nostatus_layout;
    public static List<File_model> imageModel = new ArrayList<File_model>();
    public static My_Adapter mReAdapter;
    static Boolean images_found = false;
    GridLayoutManager gLay;
    Button refresh;
    static ProgressBar progressbar;
    static Boolean loading_completed = true;


    public static Boolean net_is_connected = false;
    public static UnifiedNativeAd my_unifiedNativeAd = null;
    static Boolean ad_request_sent = false;
    public static List<File_model> selected_list = new ArrayList<File_model>();
    public static int my_position = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();
        if (root_view == null) {
            root_view = LayoutInflater .from(context).inflate(R.layout.fragment_download_images, container, false);
            intilize_componenets();
           new bg_task().execute();
            LocalBroadcastManager.getInstance(context).registerReceiver(refresh_status, new IntentFilter(getResources().getString(R.string.intent_file_saved_images)));
            LocalBroadcastManager.getInstance(context).registerReceiver(refresh_status, new IntentFilter(getResources().getString(R.string.intent_delete_images)));
        }

        return root_view;
    }

    private BroadcastReceiver refresh_status = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

               new bg_task().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
        protected void onPreExecute() {
            super.onPreExecute();
            loading_completed = false;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            fetchImages();
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mReAdapter = new My_Adapter((ArrayList<File_model>) imageModel, context, net_is_connected,2);
            recyclerView.setAdapter(mReAdapter);
            loading_completed =true;
            Intent msgrcv = new Intent("Msg");
            LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (imageModel.size() > 0) {
                        images_found = true;
                        recyclerView.setVisibility(View.VISIBLE);
                        nostatus_layout.setVisibility(View.GONE);
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
            },800);

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
            if (Build.VERSION.SDK_INT>=29){
                Uri collection;
                collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
                String[] projection = new String[] {
                        MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.DISPLAY_NAME,
                        MediaStore.Images.Media.SIZE
                };

                String selection = MediaStore.Images.Media.DISPLAY_NAME +
                        " LIKE ?";

                String[] selectionArgs = new String[] {
                        APP_NAME_CONSTANT+"%"
                };
                String sortOrder = MediaStore.Video.Media.DISPLAY_NAME + " ASC";

                try (Cursor cursor = context.getContentResolver().query(
                        collection,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder

                )) {
                    int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                    int nameColumn =
                            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);

                    int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);

                    while (cursor.moveToNext()) {
                        // Get values of columns for a given video.
                        long id = cursor.getLong(idColumn);
                        String name = cursor.getString(nameColumn);
                        int size = cursor.getInt(sizeColumn);
                        Log.d("Android11Problem",name);
                        Uri contentUri = ContentUris.withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                        // Stores column values and the contentUri in a local object
                        // that represents the media file.
                        File_model file_model = new File_model();
                        file_model.setFile_name(name);
                        file_model.setFile_path(String.valueOf(contentUri));
                        file_model.setIs_selected(false);
                        File file=new File(String.valueOf(contentUri));
                        file_model.set_Last_modified(String.valueOf(file.lastModified()));
                        imageModel.add(file_model);
                    }



                }

                catch (Exception ex){
                    Log.d("Android11Problem",ex.toString());
                }


            }
            else {
                String path = Environment.getExternalStorageDirectory().toString() + FilesData.SAVED_Status_LOCATION;
                Log.d("test", "onStart: " + path);
                File dir = new File(path);
                File[] files = dir.listFiles();


                for (int i = 0; i < files.length; i++) {
                    if (files[i].getName().endsWith(".jpg") || files[i].getName().endsWith(".png")) {
                        File_model file_model = new File_model();
                        file_model.setFile_name(files[i].getName());
                        file_model.setFile_path(files[i].getAbsolutePath());
                        file_model.setIs_selected(false);
                        file_model.set_Last_modified(String.valueOf(files[i].lastModified()));
                        imageModel.add(file_model);
                    }

                }
            }


        } catch (Exception ex) {
            Log.d("Android11Problem",ex.toString());
        }
        Collections.sort(imageModel, new Comparator<File_model>() {
            @Override
            public int compare(File_model file_model, File_model t1) {
                return t1.get_Last_modified().compareTo(file_model.get_Last_modified());
            }

        });
        List<File_model> imageModel_new = new ArrayList<>();
        imageModel_new.addAll(imageModel);
        imageModel.clear();

        int position = 0;
        for (int i = 0; i < imageModel_new.size(); i++) {
            if (imageModel_new.get(i).getFile_name().endsWith(".jpg") || imageModel_new.get(i).getFile_name().endsWith(".png")) {
                File_model file_model = new File_model();
                file_model.setFile_path(imageModel_new.get(i).getFile_path());
                file_model.setIs_selected(false);
                file_model.set_Last_modified(imageModel_new.get(i).get_Last_modified());
                position++;
                if (net_is_connected)
                    if (position == 2) {
                        file_model.setFile_path(imageModel_new.get(i).getFile_path());
                        file_model.setIs_selected(false);
                        file_model.set_Last_modified(imageModel_new.get(i).get_Last_modified());
                        imageModel.add(file_model);
                    }
                imageModel.add(file_model);
            }
        }


    }
    public static class My_Adapter extends RecyclerView.Adapter<My_Adapter.MyHolder> {
        List<File_model> imageModel;
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

                if (Build.VERSION.SDK_INT>=29){
                    Uri uri= Uri.parse(imageModel.get(position).getFile_path());
                    try {

                        Glide.with(context).load(uri).skipMemoryCache(false).centerCrop()
                                .into(holder.imgV);
                    } catch (OutOfMemoryError asd) {
                    } catch (Exception asd) {
                    }
                }
                else{
                   // final Uri iri = Uri.parse(imageModel.get(getRealPosition(position)).getFile_path());
                    File f = new File(imageModel.get(position).getFile_path());
                    try {
                        /*holder.imgV.setImageURI(iri);*/
                        Glide.with(context).load(f).skipMemoryCache(false).centerCrop()
                                .into(holder.imgV);
                    } catch (OutOfMemoryError asd) {
                    } catch (Exception asd) {
                    }
                }

               /* if (Build.VERSION.SDK_INT >= 29) {
                    Uri uri = Uri.parse(imageModel.get(getRealPosition(position)).getFile_path());

                    if (uri.toString().endsWith(".jpg") ||  uri.toString().endsWith(".png")) {

                        Glide.with(context).load(uri).skipMemoryCache(true).centerCrop()
                                .into(holder.imgV);
                    }

                }else {
                    File f = new File(imageModel.get(getRealPosition(position)).getFile_path());

                    if (f.getName().endsWith(".jpg") || f.getName().endsWith(".png")) {
                        Glide.with(context).load(f).skipMemoryCache(true).centerCrop()
                                .into(holder.imgV);
                    }

                }*/


                holder.imgV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Shared.getInstance().getBooleanValueFromPreference(context.getResources().getString(R.string.long_pressd_downstatus_pref), false, context)) {
                            if (imageModel.get(getRealPosition(position)).getIs_selected()) {
                                imageModel.get(getRealPosition(position)).setIs_selected(false);
                               DownloadedStatuses.selected_items--;
                                //Downloaded_status.selected_position.remove(imageModel.get(getRealPosition(position)));
                                selected_list.remove(imageModel.get(getRealPosition(position)));
                                //     selected_position.remove(position);

                            } else {
                                imageModel.get(getRealPosition(position)).setIs_selected(true);
                               DownloadedStatuses.selected_items++;
                                //Downloaded_status.selected_position.add(getRealPosition(position));
                                selected_list.add(imageModel.get(getRealPosition(position)));
                                //     selected_position.add(position);
                            }
                            DownloadedStatuses.check_selected_items();
                            notifyDataSetChanged();
                            return;
                        }

                        Boolean is_video = false;
                        File f = new File(imageModel.get(getRealPosition(position)).getFile_path());
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
                        intent.putExtra("path", imageModel.get(getRealPosition(position)).getFile_path());
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
                            imageModel.get(getRealPosition(position)).setIs_selected(true);
                            DownloadedStatuses.check_selected_items();
                            notifyDataSetChanged();
                            selected_list.add(imageModel.get(getRealPosition(position)));
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
            return imageModel.size();
        }

        public My_Adapter(ArrayList<File_model> mylist, Context context, boolean c, int position_call) {
            this.imageModel = mylist;
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
            imageModel.clear();
            new bg_task().execute();
        }
        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.delete_file_status_pref), false, context);
    }

}