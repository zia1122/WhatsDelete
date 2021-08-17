package com.malam.whatsdelet.nolastseen.hiddenchat.Fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.malam.whatsdelet.nolastseen.hiddenchat.Activities.Image_Viewer_Act;
import com.malam.whatsdelet.nolastseen.hiddenchat.Activities.MainActivity;
import com.malam.whatsdelet.nolastseen.hiddenchat.Activities.Video_View_Act;
import com.malam.whatsdelet.nolastseen.hiddenchat.Adapter.NotesAdapter_Messenger;
import com.malam.whatsdelet.nolastseen.hiddenchat.Model.Music_model;
import com.malam.whatsdelet.nolastseen.hiddenchat.Service.FilesData;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.Shared;
import com.whatsdelete.Test.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.malam.whatsdelet.nolastseen.hiddenchat.Activities.MainActivity.animation;
import static com.malam.whatsdelet.nolastseen.hiddenchat.Activities.MainActivity.progressDialog;
import static com.malam.whatsdelet.nolastseen.hiddenchat.Activities.MainActivity.refresh_dialog;

public class Deleted_images_frag extends Fragment {
    View root_view;
    static Context context;
    private RecyclerView recyclerView;
    LinearLayout nostatus_layout;
    TextView no_file_found;
    Button refresh;
    public static List<Music_model> muList = new ArrayList<>();
    public static My_Adapter mReAdapter;
    Boolean images_found = false;
    GridLayoutManager gLay;
    Boolean list_update_completed = false;
    public static List<Music_model> selected_list = new ArrayList<>();
    static int my_position = 0;
    private static final int CONTENT_TYPE = 0;
    private static final int AD_TYPE = 1;
    static Boolean ad_loaded = false;
    public static UnifiedNativeAd my_unifiedNativeAd = null;
    static Boolean net_is_connected = false;
    private TextView no_file_found_title;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        if (root_view == null) {
            root_view = LayoutInflater.from(context).inflate(R.layout.fragment_status_images, container, false);
            intilize_componenets();
            muList.clear();
            new bg_task().execute();
            LocalBroadcastManager.getInstance(context).registerReceiver(refresh_images, new IntentFilter(context.getResources().getString(R.string.intent_file_saved_images)));

        }
        return root_view;
    }

    private final BroadcastReceiver refresh_images = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (list_update_completed) {
                list_update_completed = false;
                new bg_task().execute();
            }
        }
    };


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

    private void intilize_componenets() {
        no_file_found = root_view.findViewById(R.id.no_file_found);
        no_file_found_title = root_view.findViewById(R.id.no_file_found_title);
        refresh = root_view.findViewById(R.id.refresh);
        recyclerView = root_view.findViewById(R.id.recyclerView);
        gLay = new GridLayoutManager(getContext(), 2);
        /*Recyclerview_Spaning_Model Recyclerview_Spaning_Model=new Recyclerview_Spaning_Model(9,1,2);
        gLay.setSpanSizeLookup(Recyclerview_Spaning_Model);*/
        net_is_connected = false;
        if (is_net_connected()) {
            gLay.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    //if (position > 5 && position % 6 == 0) {
                    if (position>1 && (position+1) % 4 == 0) {
                        return 1;
                    } else {
                        return 1;
                    }
                }
            });
        }

        recyclerView.setLayoutManager(gLay);
        nostatus_layout = root_view.findViewById(R.id.noChat);
        no_file_found_title.setText(getResources().getString(R.string.image_head));
        no_file_found.setText(getResources().getString(R.string.no_image_msg));
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(progressDialog !=null) {
                        MainActivity.progressDialog.show();
                        refresh_dialog.startAnimation(animation);
                        nostatus_layout.setVisibility(View.GONE);
                        muList.clear();
                        new bg_task().execute();
                    }
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
            mReAdapter = new My_Adapter((ArrayList<Music_model>) muList, getContext(), net_is_connected, 3);
            recyclerView.getRecycledViewPool().clear();
            recyclerView.setAdapter(mReAdapter);
            if (images_found) {
                recyclerView.setVisibility(View.VISIBLE);
                nostatus_layout.setVisibility(View.GONE);
            } else {
                nostatus_layout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        MainActivity.animation_start = false;
                        MainActivity.progressDialog.dismiss();
                    } catch (NullPointerException ner) {
                    } catch (Exception er) {
                    }
                }
            }, 300);
            list_update_completed = true;
        }
    }

    private void fetchImages() {

        DocumentFile[] documentFiles;
        muList.clear();
        images_found = false;
        try {
            NotesAdapter_Messenger.selected_list.clear();
        } catch (NullPointerException sd) {
        }

        list_update_completed = false;
        if (Build.VERSION.SDK_INT >= 29) {
            // uri is the path which we've saved in our shared pref
            File[] files;
            File parentDir = new File(String.valueOf(context.getExternalFilesDir("")));
            files = parentDir.listFiles();
            int ad_position = 0;
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().endsWith(".jpg") || files[i].getName().endsWith(".png") || files[i].getName().endsWith(".jpeg") /*|| files[i].getName().endsWith(".mp4") || files[i].getName().endsWith(".3gp") ||files[i].getName().endsWith(".avi")*/) {
                    Music_model music_model = new Music_model();
                    music_model.setFile_path(files[i].getAbsolutePath());
                    music_model.setIs_selected(false);
                    muList.add(music_model);
                    images_found = true;
                    if (net_is_connected)
                        if (ad_position == 2) {
                            music_model.setFile_path(files[i].getAbsolutePath());
                            music_model.setIs_selected(false);
                            muList.add(music_model);
                        }

                    ad_position++;
                }
            }
            /*
            Uri uri = Uri.parse(String.valueOf(context.getExternalFilesDir(Environment.DIRECTORY_DCIM)));
            DocumentFile fromTreeUri = DocumentFile.fromTreeUri(context, uri);

            documentFiles = fromTreeUri.listFiles();
            int ad_position = 0;
            for (int i = 0; i < documentFiles.length; i++) {

                if (documentFiles[i].getName().endsWith(".jpg") ||
                        documentFiles[i].getName().endsWith(".png")) {
                    Music_model music_model = new Music_model();
                    music_model.setFile_name(documentFiles[i].getName());
                    music_model.setFile_path(documentFiles[i].getUri().toString());
                    music_model.setIs_selected(false);
                    muList.add(music_model);
                    images_found = true;
                    if (net_is_connected)
                        if (ad_position == 2) {
                            music_model.setFile_path(documentFiles[i].getUri().toString());
                            music_model.setIs_selected(false);
                            muList.add(music_model);
                        }

                    ad_position++;
                    Log.d("TAG", "fetchImages" + documentFiles[i].getName());

                }
            }*/
        } else {
            File[] files;
            File parentDir = new File(Environment.getExternalStorageDirectory().toString() + FilesData.DELETED_MEDIA);


            files = parentDir.listFiles();

            int ad_position = 0;
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().endsWith(".jpg") || files[i].getName().endsWith(".png") /*|| files[i].getName().endsWith(".mp4") || files[i].getName().endsWith(".3gp") ||files[i].getName().endsWith(".avi")*/) {
                    Music_model music_model = new Music_model();
                    music_model.setFile_path(files[i].getAbsolutePath());
                    music_model.setIs_selected(false);
                    muList.add(music_model);
                    images_found = true;
                    if (net_is_connected)
                        if (ad_position == 2) {
                            music_model.setFile_path(files[i].getAbsolutePath());
                            music_model.setIs_selected(false);
                            muList.add(music_model);
                        }

                    ad_position++;
                }
            }
        }

        try {
        } catch (Exception ex) {
        }
    }

    public static class My_Adapter extends RecyclerView.Adapter<My_Adapter.MyHolder> {

        List<Music_model> muList;
        private static final int LIST_AD_DELTA = 2;
        int position_tab = 3;

        Boolean ad_request_sent = false;

        @Override
        public My_Adapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.whats_app_images_row_layot, parent, false);
      /*  switch (viewType) {
            case AD_TYPE:
                View unifiedNativeLayoutView = LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.row_item_for_native_ad_whats_app_images,
                        parent, false);
                return new MyHolder(unifiedNativeLayoutView);
            case CONTENT_TYPE:
                // Fall through.
            default:
                return new MyHolder(view);
        }*/

            MyHolder holder = new MyHolder(view);
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
            if (nativeAd.getMediaContent() != null) {
                mediaView.setVisibility(View.VISIBLE);
            } else mediaView.setVisibility(View.GONE);

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
                adView.getStarRatingView().setVisibility(View.VISIBLE);
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
                my_position = position;
                switch (getItemViewType(position)) {
                    case AD_TYPE:

                        holder.main_layout.setVisibility(View.GONE);
                        if (my_unifiedNativeAd != null) {

                            try {
                                UnifiedNativeAdView adView = (UnifiedNativeAdView) LayoutInflater.from(context).inflate(R.layout.ad_unified_row_item_fourth, null);
                                populateUnifiedNativeAdView(my_unifiedNativeAd, adView);
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
                                ad_loaded = false;
                                holder.frameLayout_1.setVisibility(View.GONE);
                                holder.main_layout.setVisibility(View.GONE);
                                holder.progress.setVisibility(View.GONE);

                            }

                            @Override
                            public void onAdLoaded() {
                                super.onAdLoaded();
                                holder.frameLayout_1.setVisibility(View.VISIBLE);
                                holder.video_imageview.setVisibility(View.GONE);
                                holder.progress.setVisibility(View.GONE);
                                holder.main_layout.setVisibility(View.GONE);
                            }
                        }).build();
                        adLoader.loadAd(new AdRequest.Builder().addTestDevice(context.getResources().getString(R.string.vicky_s8)).addTestDevice(context.getResources().getString(R.string.zegar_device)).addTestDevice(context.getResources().getString(R.string.test_device_j5)).addTestDevice(context.getResources().getString(R.string.vicky_s8)).addTestDevice(context.getResources().getString(R.string.test_device_white)).addTestDevice("A86A0D556F68465C49063589837FCF98").build());
                        ad_request_sent = true;
                        return;
                }
                holder.main_layout.setVisibility(View.VISIBLE);
                holder.progress.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= 29) {
                    Uri uri = Uri.parse(muList.get(getRealPosition(position)).getFile_path());
                    if (uri.toString().endsWith(".jpg") || uri.toString().endsWith(".png")|| uri.toString().endsWith(".jpeg")) {
                        holder.video_imageview.setVisibility(View.GONE);
                        Glide.with(context).load(uri).into(holder.imgV);
                    } else {
                        holder.video_imageview.setVisibility(View.VISIBLE);
                    }


                } else {
                    File f = new File(muList.get(getRealPosition(position)).getFile_path());
                    if (f.getName().endsWith(".mp4") || f.getName().endsWith(".3gp") || f.getName().endsWith(".avi") || f.getName().endsWith(".3gp") || f.getName().endsWith(".M4V") || f.getName().endsWith(".flv")) {
                        holder.video_imageview.setVisibility(View.VISIBLE);
                    } else
                        holder.video_imageview.setVisibility(View.GONE);
                    Glide.with(context).load(f).into(holder.imgV);
                }

                if (!Shared.getInstance().getBooleanValueFromPreference(context.getResources().getString(R.string.long_pressd_deleted_images_pref), false, context)) {
                    muList.get(getRealPosition(position)).setIs_selected(false);
                    holder.selectedlayout.setVisibility(View.GONE);
                    //  holder.share_n_delete_layout.setVisibility(View.VISIBLE);
                } else {
                    holder.selectedlayout.setVisibility(View.VISIBLE);
                    holder.share_n_delete_layout.setVisibility(View.GONE);
                }
                if (muList.get(getRealPosition(position)).getIs_selected()) {
                    holder.selectedImage.setVisibility(View.VISIBLE);
                } else {
                    holder.selectedImage.setVisibility(View.GONE);
                }
                holder.share_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri photoURI = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", new File(muList.get(getRealPosition(position)).getFile_path()));
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("image/*");
                        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.putExtra(Intent.EXTRA_STREAM, photoURI);
                        context.startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.share_image_via)));
                    }
                });
                holder.delete_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                        alertDialog.setTitle(context.getResources().getString(R.string.delete));
                        alertDialog.setMessage(context.getResources().getString(R.string.dialoug_message));
                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File file = new File(muList.get(getRealPosition(position)).getFile_path());
                                if (file.exists()) {
                                    file.delete();
                                }
                                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                                muList.remove(getRealPosition(position));
                                notifyDataSetChanged();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

                    }
                });
                holder.imgV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Boolean is_video = false;
                        File f = new File(muList.get(getRealPosition(position)).getFile_path());
                        is_video = f.getName().endsWith(".mp4") || f.getName().endsWith(".3gp") || f.getName().endsWith(".avi") || f.getName().endsWith(".3gp") || f.getName().endsWith(".M4V") || f.getName().endsWith(".flv");

                        if (Shared.getInstance().getBooleanValueFromPreference(context.getResources().getString(R.string.long_pressd_deleted_images_pref), false, context)) {
                            if (muList.get(getRealPosition(position)).getIs_selected()) {
                                muList.get(getRealPosition(position)).setIs_selected(false);
                                MainActivity.selected_items--;
                                selected_list.remove(muList.get(getRealPosition(position)));

                            } else {
                                muList.get(getRealPosition(position)).setIs_selected(true);
                                MainActivity.selected_items++;
                                selected_list.add(muList.get(getRealPosition(position)));
                            }
                            MainActivity.check_selected_items();
                            notifyDataSetChanged();
                            return;
                        }

                        Shared.getInstance().saveIntToPreferences(context.getResources().getString(R.string.from_status_pref), position_tab, context);
                        Intent intent = null;
                        if (!is_video) {
                            intent = new Intent(context, Image_Viewer_Act.class);
                        } else intent = new Intent(context, Video_View_Act.class);

                        intent.putExtra("pos", "" + getRealPosition(position));
                        intent.putExtra("delete_btn", false);
                        intent.putExtra("is_video", is_video);
                        intent.putExtra(context.getResources().getString(R.string.is_net_connected_intent), net_is_connected);
                        Shared.getInstance().saveIntToPreferences(context.getResources().getString(R.string.image_position_pref), getRealPosition(position), context);
                        intent.putExtra("path", muList.get(getRealPosition(position)).getFile_path());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });
                holder.imgV.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (!Shared.getInstance().getBooleanValueFromPreference(context.getResources().getString(R.string.long_pressd_deleted_images_pref), false, context)) {
                            Shared.getInstance().saveBooleanToPreferences(context.getResources().getString(R.string.long_pressd_deleted_images_pref), true, context);
                            MainActivity.selected_items++;
                            muList.get(getRealPosition(position)).setIs_selected(true);
                            MainActivity.check_selected_items();
                            notifyDataSetChanged();
                            selected_list.add(muList.get(getRealPosition(position)));
                        } else {
                            return false;
                        }
                        Shared.getInstance().saveIntToPreferences(context.getResources().getString(R.string.delete_tabs_position_pref), position_tab + 1, context);
                        Shared.getInstance().saveStringToPreferences(context.getResources().getString(R.string.delete_diloug_title_pref), context.getResources().getString(R.string.delete_images_title), context);
                        Shared.getInstance().saveStringToPreferences(context.getResources().getString(R.string.delete_dialoug_msg_pref), context.getResources().getString(R.string.delete_images_msg), context);
                        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
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
           /* if (is_net_availible) {
                if (position > 5 && position % LIST_AD_DELTA == 0) {
                    return AD_TYPE;
                }
                return CONTENT_TYPE;
            }*/
            if (net_is_connected) {
                if (position > 1 && (position + 1) % 4 == 0) {
                    return AD_TYPE;
                }
                return CONTENT_TYPE;
            }
            return CONTENT_TYPE;
        }
/*
        @Override
        public int getItemCount() {
            return muList.size();
        }*/

        private int getRealPosition(int position) {

            return position;
        }

        @Override
        public int getItemCount() {

            return muList.size();
        }

        public My_Adapter(ArrayList<Music_model> mylist, Context context, Boolean is_net_availible, int position_tab) {
            this.muList = mylist;
            this.position_tab = position_tab;
        }

        class MyHolder extends RecyclerView.ViewHolder {
            ImageView imgV, selectedImage;
            TextView share_image, delete_image;
            RelativeLayout share_n_delete_layout, main_layout;
            LinearLayout video_imageview, selectedlayout;
            FrameLayout frameLayout_1;
            ProgressBar progress;

            public MyHolder(View itemView) {
                super(itemView);
                imgV = itemView.findViewById(R.id.imageView);
                video_imageview = itemView.findViewById(R.id.video_imageview);
                selectedlayout = itemView.findViewById(R.id.selectedlayout);
                main_layout = itemView.findViewById(R.id.main_layout);
                selectedImage = itemView.findViewById(R.id.selectedImage);
                share_image = itemView.findViewById(R.id.share_image);
                delete_image = itemView.findViewById(R.id.delete_image);
                progress = itemView.findViewById(R.id.progress);
                share_n_delete_layout = itemView.findViewById(R.id.share_n_delete_layout);
                frameLayout_1 = itemView.findViewById(R.id.frame_layout_adapter);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.delete_file_deletedimages_pref), false, context)) {
            muList.clear();
            new bg_task().execute();
        } else {
        }
        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.file_deleted_pref), false, getActivity());
        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.delete_file_deletedimages_pref), false, getActivity());
    }
}