package com.malam.whatsdelet.nolastseen.hiddenchat.Fragments;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
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
import com.malam.whatsdelet.nolastseen.hiddenchat.Activities.MainActivity;
import com.malam.whatsdelet.nolastseen.hiddenchat.Activities.VideoPlayer_Act;
import com.malam.whatsdelet.nolastseen.hiddenchat.Model.Music_model;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.Shared;
import com.whatsdelete.Test.R;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.malam.whatsdelet.nolastseen.hiddenchat.Activities.MainActivity.animation;
import static com.malam.whatsdelet.nolastseen.hiddenchat.Activities.MainActivity.refresh_dialog;
import static com.malam.whatsdelet.nolastseen.hiddenchat.Service.FilesData.DELETED_MEDIA;

public class Deleted_videos_frag extends Fragment implements View.OnClickListener {
View root_view;
Context context;

Boolean net_is_connected = false;
private RecyclerView recyclerView;
LinearLayout nostatus_layout;
public static List<Music_model> muList = new ArrayList<>();
public static My_Adapter mReAdapter;
Boolean images_found = false;
GridLayoutManager gLay;
Button refresh;
public static List<Music_model> selected_list = new ArrayList<>();

Boolean refresh_pressed = false, list_update_completed = false;
TextView no_file_found;

 UnifiedNativeAd my_unifiedNativeAd;
private static final int CONTENT_TYPE = 0;

private static final int AD_TYPE = 1;

Boolean ad_loaded = false;
int my_position = 0;
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
        LocalBroadcastManager.getInstance(context).registerReceiver(refresh_videos, new IntentFilter(context.getResources().getString(R.string.intent_file_saved_videos)));
    }
    return root_view;
}


private BroadcastReceiver refresh_videos = new BroadcastReceiver() {
    
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
            net_is_connected = true;
            return true;
        } else {
            net_is_connected = false;
            return false;
        }
    } else {
        return false;
    }
}
private void intilize_componenets() {
    recyclerView = root_view.findViewById(R.id.recyclerView);
    refresh = (Button) root_view.findViewById(R.id.refresh);
    no_file_found = (TextView) root_view.findViewById(R.id.no_file_found);
    no_file_found_title = (TextView) root_view.findViewById(R.id.no_file_found_title);
    gLay = new GridLayoutManager(getContext(), 2);
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
    nostatus_layout = (LinearLayout) root_view.findViewById(R.id.noChat);
    refresh.setOnClickListener(this);
}

@Override
public void onClick(View v) {
    try {
        MainActivity.progressDialog.show();
        refresh_dialog.startAnimation(animation);
        refresh_pressed = true;
        new bg_task().execute();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

public class bg_task extends AsyncTask<Void, Void, Void> {
    
    @Override
    protected Void doInBackground(Void... voids) {
        fetchVidoes();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mReAdapter = new My_Adapter((ArrayList<Music_model>) muList, context,net_is_connected);
        recyclerView.getRecycledViewPool().clear();
        recyclerView.setAdapter(mReAdapter);
        if (images_found) {
            recyclerView.setVisibility(View.VISIBLE);
            nostatus_layout.setVisibility(View.GONE);
        } else {
            no_file_found_title.setText(context.getResources().getString(R.string.video_head));
            no_file_found.setText(context.getResources().getString(R.string.no_video_msg));
             nostatus_layout.setVisibility(View.VISIBLE);
        }
        if (refresh_pressed) {
            Toast.makeText(context, getResources().getString(R.string.list_updated), Toast.LENGTH_LONG).show();
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
        refresh_pressed = false;
        list_update_completed = true;
    }
}

private void fetchVidoes() {
    muList.clear();
    try {
        String path = Environment.getExternalStorageDirectory().toString() + DELETED_MEDIA;
        Log.d("test", "onStart: " + path);
        File dir = new File(path);
        File[] files = dir.listFiles();
        
        Arrays.sort(files, Collections.reverseOrder());
        
        int ad_position = 0;
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().endsWith(".mp4") ||
                        files[i].getName().endsWith(".3gp") ||
                        files[i].getName().endsWith(".avi") ||
                        files[i].getName().endsWith(".wmv")
                        || files[i].getName().endsWith(".flv") ||
                        files[i].getName().endsWith(".M4V") ||
                        files[i].getName().endsWith(".WEBM")) {
                Music_model music_model = new Music_model();
                music_model.setFile_name(files[i].getName());
                music_model.setFile_path(files[i].getAbsolutePath());
                music_model.setFile_size(files[i].length());
                music_model.setIs_selected(false);
                muList.add(music_model);
                if (net_is_connected)
                    if (ad_position == 2) {
                        music_model.setFile_path(files[i].getAbsolutePath());
                        music_model.setIs_selected(false);
                        muList.add(music_model);
                    }
                
                ad_position++;
            }
        }
        if (muList.size() > 0) {
            images_found = true;
        } else {
            images_found = false;
        }
    } catch (Exception ex) {
    }
}
public class My_Adapter extends RecyclerView.Adapter<My_Adapter.MyHolder> {
    List<Music_model> muList;
    Context mycontxt;
    
    private static final int LIST_AD_DELTA = 2;
    Boolean is_net_availible;
    
    Boolean ad_request_sent = false;
    @Override
    public My_Adapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_row_layot, parent, false);
     /*
        switch (viewType) {
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
        if (nativeAd.getMediaContent() != null) {
            mediaView.setVisibility(View.VISIBLE);
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
            Glide.with(mycontxt).load(muList.get(getRealPosition(position)).getFile_path()).into(holder.imgV);
            holder.music_file_size.setText(getHumanReadableSize(muList.get(getRealPosition(position)).getFile_size()));
            holder.music_name.setText(muList.get(getRealPosition(position)).getFile_name());
            if (!Shared.getInstance().getBooleanValueFromPreference(context.getResources().getString(R.string.long_pressd_deleted_video_pref), false, context)) {
                muList.get(getRealPosition(position)).setIs_selected(false);
                holder.selectedlayout.setVisibility(View.GONE);
             //   holder.share_n_delete_layout.setVisibility(View.VISIBLE);
            }
            else {
                holder.share_n_delete_layout.setVisibility(View.GONE);
                holder.selectedlayout.setVisibility(View.VISIBLE);
            }
            if (muList.get(getRealPosition(position)).getIs_selected()) {
                holder.selectedImage.setVisibility(View.VISIBLE);
            } else {
                holder.selectedImage.setVisibility(View.GONE);
            }
            holder.main_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    
                    if (Shared.getInstance().getBooleanValueFromPreference(context.getResources().getString(R.string.long_pressd_deleted_video_pref), false, context)) {
                        if (muList.get(getRealPosition(position)).getIs_selected()) {
                            muList.get(getRealPosition(position)).setIs_selected(false);
                            MainActivity.selected_items--;
                            //MainActivity.selected_position.remove(muList.get(position));
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
                        mycontxt.startActivity(new Intent(context, VideoPlayer_Act.class).putExtra(mycontxt.getResources().getString(R.string.path_of_video_intent),muList.get(getRealPosition(position)).getFile_path()).setFlags(FLAG_ACTIVITY_NEW_TASK));
                    }
                }
            });
            
            holder.share_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri photoURI = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", new File(muList.get(getRealPosition(position)).getFile_path()));
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("mp4/*");
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    
                    intent.putExtra(Intent.EXTRA_STREAM, photoURI);
                    startActivity(Intent.createChooser(intent, getResources().getString(R.string.share_video_via)));
                }
            });
            holder.delete_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setTitle(getResources().getString(R.string.delete));
                    alertDialog.setMessage(getResources().getString(R.string.dialoug_message));
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
            holder.main_layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    
                    if (!Shared.getInstance().getBooleanValueFromPreference(context.getResources().getString(R.string.long_pressd_deleted_video_pref), false, context)) {
                        Shared.getInstance().saveBooleanToPreferences(context.getResources().getString(R.string.long_pressd_deleted_video_pref), true, context);
                        MainActivity.selected_items++;
                        muList.get(getRealPosition(position)).setIs_selected(true);
                        MainActivity.check_selected_items();
                        notifyDataSetChanged();
                        selected_list.add(muList.get(getRealPosition(position)));
                    } else return false;
                    Shared.getInstance().saveIntToPreferences(getResources().getString(R.string.delete_tabs_position_pref), 5, context);
                    Shared.getInstance().saveStringToPreferences(getResources().getString(R.string.delete_diloug_title_pref), getResources().getString(R.string.delete_video_title), context);
                    Shared.getInstance().saveStringToPreferences(getResources().getString(R.string.delete_dialoug_msg_pref), getResources().getString(R.string.delete_video_msg), context);
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
                if (position > 0 && position % LIST_AD_DELTA == 0) {
                    return AD_TYPE;
                }
                return CONTENT_TYPE;
            }*/
        if (net_is_connected) {
            if (position>1 && (position+1) % 4 == 0) {
                return AD_TYPE;
            }
            return CONTENT_TYPE;
        }
        return CONTENT_TYPE;
    }
    
    
    private int getRealPosition(int position) {
           /* if (is_net_availible) {
                if (LIST_AD_DELTA == 0) {
                    return position;
                } else {
                    return position - position / LIST_AD_DELTA;
                }
            }*/
        return position;
    }
    
    @Override
    public int getItemCount() {
   /*     if (is_net_availible) {
            int additionalContent = 0;
            // if (muList.size() > 0 && LIST_AD_DELTA > 0 && muList.size() > LIST_AD_DELTA) {
            if (my_position == 2) {
                additionalContent = (muList.size() + (muList.size() / LIST_AD_DELTA)) / LIST_AD_DELTA;
                additionalContent = 1;
            }
            return muList.size() + additionalContent;
        }*/
        return muList.size();
    }

/*

        @Override
        public int getItemCount() {
            return muList.size();
        }
*/
    
    public My_Adapter(ArrayList<Music_model> mylist, Context context,Boolean is_net_availible) {
        this.muList = mylist;
        this.mycontxt = context;
     }
    
    class MyHolder extends RecyclerView.ViewHolder {
        ImageView imgV, selectedImage;
        TextView music_name, music_file_size;
        LinearLayout selectedlayout;
        RelativeLayout share_n_delete_layout,main_layout;
        TextView share_image, delete_image;
        FrameLayout frameLayout_1;
        ProgressBar progress;
        
        public MyHolder(View itemView) {
            super(itemView);
            
            imgV = itemView.findViewById(R.id.imageView);
            selectedImage = itemView.findViewById(R.id.selectedImage);
            music_name = itemView.findViewById(R.id.music_name);
            music_file_size = itemView.findViewById(R.id.music_file_size);
            main_layout = itemView.findViewById(R.id.main_layout);
            selectedlayout = itemView.findViewById(R.id.selectedlayout);
            share_image = itemView.findViewById(R.id.share_image);
            delete_image = itemView.findViewById(R.id.delete_image);
            progress = itemView.findViewById(R.id.progress);
            share_n_delete_layout = itemView.findViewById(R.id.share_n_delete_layout);
            frameLayout_1 = (FrameLayout) itemView.findViewById(R.id.frame_layout_adapter);
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