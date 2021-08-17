package com.malam.whatsdelet.nolastseen.hiddenchat.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.malam.whatsdelet.nolastseen.hiddenchat.Activities.MainActivity;
import com.malam.whatsdelet.nolastseen.hiddenchat.Model.Music_model;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.Shared;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.whatsdelete.Test.R;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.malam.whatsdelet.nolastseen.hiddenchat.Activities.MainActivity.animation;
import static com.malam.whatsdelet.nolastseen.hiddenchat.Activities.MainActivity.refresh_dialog;
import static com.malam.whatsdelet.nolastseen.hiddenchat.Service.FilesData.DELETED_MEDIA;

public class Deleted_docs_frag extends Fragment {
    View root_view;
    Context context;

    private RecyclerView recyclerView;
    LinearLayout nostatus_layout;
    public static List<Music_model> muList = new ArrayList<>();
    public static My_Adapter mReAdapter;
    Boolean images_found = false;
    GridLayoutManager gLay;
    TextView no_file_found;
    Button refresh;
    public static List<Music_model> selected_list = new ArrayList<>();
    Boolean list_update_completed = false;


    private static final int CONTENT_TYPE = 0;

    private static final int AD_TYPE = 1;
    private static final int LIST_AD_DELTA = 4;

    public UnifiedNativeAd my_unifiedNativeAd = null;
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
            LocalBroadcastManager.getInstance(context).registerReceiver(refresh_audio, new IntentFilter(context.getResources().getString(R.string.intent_file_saved_docs)));

        }
        return root_view;
    }

    private final BroadcastReceiver refresh_audio = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (list_update_completed) {
                list_update_completed = false;
                new bg_task().execute();
            }
        }
    };
    private final BroadcastReceiver delete_audio = new BroadcastReceiver() {

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
            // Internet Available
            //No internet
            return netInfo.isConnected();
        } else {
            return false;
        }
    }

    private void intilize_componenets() {
        refresh = root_view.findViewById(R.id.refresh);
        recyclerView = root_view.findViewById(R.id.recyclerView);
        no_file_found = root_view.findViewById(R.id.no_file_found);
        no_file_found_title = root_view.findViewById(R.id.no_file_found_title);
        recyclerView = root_view.findViewById(R.id.recyclerView);
        gLay = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(gLay);
        nostatus_layout = root_view.findViewById(R.id.noChat);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.progressDialog != null) {
                    try {
                        MainActivity.progressDialog.show();
                    } catch (Exception qwe) {
                    }
                }
                refresh_dialog.startAnimation(animation);
                nostatus_layout.setVisibility(View.GONE);
                muList.clear();
                new bg_task().execute();
            }
        });
    }

    public class bg_task extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            fetchaudio();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mReAdapter = new My_Adapter((ArrayList<Music_model>) muList, getContext());
            recyclerView.getRecycledViewPool().clear();
            recyclerView.setAdapter(mReAdapter);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (muList.size() > 0) {
                            recyclerView.setVisibility(View.VISIBLE);
                            nostatus_layout.setVisibility(View.GONE);
                        } else {
                            nostatus_layout.setVisibility(View.VISIBLE);
                            no_file_found.setText(getResources().getString(R.string.no_docs_msg));
                            no_file_found_title.setText(getResources().getString(R.string.docs_head));
                        }
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

    private void fetchaudio() {
        try {
            muList.clear();
            String path = Environment.getExternalStorageDirectory().toString() + DELETED_MEDIA;
            File dir = new File(path);
            File[] files = dir.listFiles();
            Arrays.sort(files, Collections.reverseOrder());
            images_found = files.length > 0;
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().endsWith(".pdf")
                        || files[i].getName().endsWith(".xls")
                        || files[i].getName().endsWith(".docx")
                        || files[i].getName().endsWith(".zip")
                        || files[i].getName().endsWith(".rar")
                        || files[i].getName().endsWith(".txt")
                        || files[i].getName().endsWith(".doc")
                        || files[i].getName().endsWith(".xlsx")
                        || files[i].getName().endsWith(".ppt")) {
                    Music_model music_model = new Music_model();
                    music_model.setFile_name(files[i].getName());
                    music_model.setFile_path(files[i].getAbsolutePath());
                    music_model.setFile_size(files[i].length());

                    music_model.setIs_selected(false);
                    muList.add(music_model);
                }
            }
        } catch (Exception ex) {
        }
    }

    public class My_Adapter extends RecyclerView.Adapter<My_Adapter.MyHolder> {
        List<Music_model> muList;
        Context mycontxt;
        Boolean ad_request_sent = false;

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doc_row_layot, parent, false);
    /*    switch (viewType) {
            case AD_TYPE:
                View unifiedNativeLayoutView = LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.row_item_for_native_ad,
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

        @Override
        public void onBindViewHolder(final MyHolder holder, final int position) {
            try {
            
/*
            switch (getItemViewType(position)) {
                case AD_TYPE:
                    if (my_unifiedNativeAd != null) {
                        UnifiedNativeAdView adView = (UnifiedNativeAdView) LayoutInflater.from(context).inflate(R.layout.ad_unified_row_item_audio, null);
                        populateUnifiedNativeAdView(my_unifiedNativeAd, adView);
                        try {
                            holder.frameLayout_1.removeAllViews();
                            holder.frameLayout_1.addView(adView);
                            holder.frameLayout_1.setVisibility(View.VISIBLE);
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
                            // You must call destroy on old ads when you are done with them,
                            // otherwise you will have a memory leak.
               */
/* if (nativeAd != null) {
                    nativeAd.destroy();
                }
                nativeAd = unifiedNativeAd;*//*

                            //FrameLayout frameLayout = root_view.findViewById(R.id.fl_adplaceholder);
                            my_unifiedNativeAd = unifiedNativeAd;
                            UnifiedNativeAdView adView = (UnifiedNativeAdView) LayoutInflater.from(context).inflate(R.layout.ad_unified_row_item_audio, null);
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
                            try {
                                holder.frameLayout_1.setVisibility(View.GONE);
                            } catch (NullPointerException asd) {
                            } catch (Exception asd) {
                            }
                            ad_request_sent = false;
                        }
                        
                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            try {
                                holder.frameLayout_1.setVisibility(View.VISIBLE);
                            } catch (NullPointerException asd) {
                            } catch (Exception asd) {
                            }
                        }
                    }).build();
                    adLoader.loadAd(new AdRequest.Builder().addTestDevice(context.getResources().getString(R.string.zegar_device)).addTestDevice(context.getResources().getString(R.string.test_device_j5)).addTestDevice(context.getResources().getString(R.string.vicky_s8)).addTestDevice(context.getResources().getString(R.string.test_device_white)).addTestDevice("A86A0D556F68465C49063589837FCF98").build());
                    ad_request_sent = true;
                    return;
            }
*/
                File f = new File(muList.get(getRealPosition(position)).getFile_path());
                if (f.getName().endsWith(".pdf")) {
                    holder.type_text.setText("pdf");
                } else if (f.getName().endsWith(".zip")) {
                    holder.type_text.setText("zip");
                } else if (f.getName().endsWith(".txt")) {
                    holder.type_text.setText("txt");
                } else if (f.getName().endsWith(".doc") || f.getName().endsWith(".docx")) {
                    holder.type_text.setText("doc");
                } else if (f.getName().endsWith(".rar")) {
                    holder.type_text.setText("rar");
                } else if (f.getName().endsWith(".xlsx")) {
                    holder.type_text.setText("xlsx");
                }

                holder.music_file_size.setText(getHumanReadableSize(muList.get(getRealPosition(position)).getFile_size()));
                holder.music_name.setText(muList.get(getRealPosition(position)).getFile_name());
                if (!Shared.getInstance().getBooleanValueFromPreference(context.getResources().getString(R.string.long_pressd_del_ducoments_pref), false, context)) {
                    muList.get(getRealPosition(position)).setIs_selected(false);
                }
                if (muList.get(getRealPosition(position)).getIs_selected()) {
                    holder.selectedImage.setVisibility(View.VISIBLE);
                } else holder.selectedImage.setVisibility(View.GONE);

                holder.main_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Shared.getInstance().getBooleanValueFromPreference(context.getResources().getString(R.string.long_pressd_del_ducoments_pref), false, context)) {
                            if (muList.get(getRealPosition(position)).getIs_selected()) {
                                muList.get(getRealPosition(position)).setIs_selected(false);
                                MainActivity.selected_items--;
                                //MainActivity.selected_position.remove(muList.get(getRealPosition(position)));
                                selected_list.remove(muList.get(getRealPosition(position)));
                            } else {
                                muList.get(getRealPosition(position)).setIs_selected(true);
                                MainActivity.selected_items++;
                                //MainActivity.selected_position.add(position);
                                selected_list.add(muList.get(getRealPosition(position)));
                            }

                            MainActivity.check_selected_items();
                            notifyDataSetChanged();
                        } else {
                            Uri audio_uri = null;
                            File file = new File(muList.get(getRealPosition(position)).getFile_path());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                audio_uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
                            } else {
                                audio_uri = Uri.parse(String.valueOf(file));
                            }
   /* Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        intent.setDataAndType(audio_uri, "audio/*");
                        mycontxt.startActivity(Intent.createChooser(intent, getResources().getString(R.string.play_sound_via)));
                     */

                            try {
                                opendocFile(context, audio_uri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                holder.main_layout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (!Shared.getInstance().getBooleanValueFromPreference(context.getResources().getString(R.string.long_pressd_del_ducoments_pref), false, context)) {
                            Shared.getInstance().saveBooleanToPreferences(context.getResources().getString(R.string.long_pressd_del_ducoments_pref), true, context);
                            MainActivity.selected_items++;
                            muList.get(getRealPosition(position)).setIs_selected(true);
                            MainActivity.check_selected_items();
                            notifyDataSetChanged();
                            selected_list.add(muList.get(getRealPosition(position)));
                        } else return false;
                        Shared.getInstance().saveIntToPreferences(getResources().getString(R.string.delete_tabs_position_pref), 8, context);
                        Shared.getInstance().saveStringToPreferences(getResources().getString(R.string.delete_diloug_title_pref), getResources().getString(R.string.delete_audio_title), context);
                        Shared.getInstance().saveStringToPreferences(getResources().getString(R.string.delete_dialoug_msg_pref), getResources().getString(R.string.delete_audio_msg), context);
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

        private void opendocFile(Context context, Uri url) throws IOException {
            // Create URI
            Uri uri = url;

            Intent intent = new Intent(Intent.ACTION_VIEW);
            // Check what kind of file you are trying to open, by comparing the url with extensions.
            // When the if condition is matched, plugin sets the correct intent (mime) type,
            // so Android knew what application to use to open the file
            if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else if (url.toString().contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
            } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
                // ZIP Files
                intent.setDataAndType(uri, "application/zip");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else if (url.toString().contains(".rtf")) {
                // RTF file
                intent.setDataAndType(uri, "application/rtf");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
                // WAV audio file
                intent.setDataAndType(uri, "audio/x-wav");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else if (url.toString().contains(".gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
                // JPG file
                intent.setDataAndType(uri, "image/jpeg");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else if (url.toString().contains(".txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
                // Video files
                intent.setDataAndType(uri, "video/*");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                //if you want you can also define the intent type for any other file

                //additionally use else clause below, to manage other unknown extensions
                //in this case, Android will show all applications installed on the device
                //so you can choose which application to use
                intent.setDataAndType(uri, "*/*");
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    
/*

        @Override
        public int getItemCount() {
            return muList.size();
        }
*/

        @Override
        public int getItemCount() {
  /*      int additionalContent = 0;
        if (muList.size() > 0 && LIST_AD_DELTA > 0 && muList.size() > LIST_AD_DELTA) {
            additionalContent = muList.size() / LIST_AD_DELTA;
            additionalContent = (muList.size() + (muList.size() / LIST_AD_DELTA)) / LIST_AD_DELTA;
        }*/
            return muList.size();
        }

        private int getRealPosition(int position) {

            return position;

        }

        @Override
        public int getItemViewType(int position) {
        /*if (notesList.get(position).getNote().equalsIgnoreCase("for_native_ad")) {
            return AD_TYPE;
        }*/
    /*    if (position > 0 && position % LIST_AD_DELTA == 0) {
            return AD_TYPE;
        }*/
            return CONTENT_TYPE;
        }

        public My_Adapter(ArrayList<Music_model> mylist, Context context) {
            this.muList = mylist;
            this.mycontxt = context;
        }

        class MyHolder extends RecyclerView.ViewHolder {
            ImageView selectedImage;
            TextView type_text, music_name, music_file_size;
            RelativeLayout main_layout;
            FrameLayout frameLayout_1;

            public MyHolder(View itemView) {
                super(itemView);

                type_text = itemView.findViewById(R.id.type_text);
                selectedImage = itemView.findViewById(R.id.selectedImage);
                music_name = itemView.findViewById(R.id.music_name);
                music_file_size = itemView.findViewById(R.id.music_file_size);
                main_layout = itemView.findViewById(R.id.main_layout);
                frameLayout_1 = itemView.findViewById(R.id.fl_adplaceholder);


            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.file_deleted_pref), false, getActivity())) {
            muList.clear();
            new bg_task().execute();
        }
        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.file_deleted_pref), false, getActivity());
    }

    private String getHumanReadableSize(long vide_size) {
        if (vide_size < 1024) {
            return vide_size + " Bytes";
        } else if ((double) (vide_size) < Math.pow(1024.0d, 2.0d)) {
            return "" + (vide_size / 1024) + " KB";
        } else if ((double) (vide_size) < Math.pow(1024.0d, 3.0d)) {

            double newvalue = vide_size / Math.pow(1024.0d, 2.0d);


            return new DecimalFormat("#.##").format(newvalue) + " MB";
        } else {
            double newvalue = vide_size / Math.pow(1024.0d, 3.0d);
            return new DecimalFormat("#.##").format(newvalue) + " GB";
        }
    }
}