package com.malam.whatsdelet.nolastseen.hiddenchat.Adapter;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.malam.whatsdelet.nolastseen.hiddenchat.Model.Note_new_instagram;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.Shared;
import com.whatsdelete.Test.R;

import java.util.ArrayList;
import java.util.List;


public class Chat_contact_Adapter_Instagram extends RecyclerView.Adapter<Chat_contact_Adapter_Instagram.MyViewHolder> {

    private Context context;
    private List<Note_new_instagram> notesList;
    public static List<Note_new_instagram> selected_msgs = new ArrayList<>();
    public static UnifiedNativeAdView adView;
    public static UnifiedNativeAd my_unifiedNativeAd = null;
    private static final int CONTENT_TYPE = 0;
    private static final int AD_TYPE = 1;
    private int LIST_AD_DELTA;
    Boolean ad_request_sent = false, long_pressed = false;
    int my_position = 0;
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView note, call_time_txtview, date_n_time;
        public TextView dot;
        public TextView timestamp;
        RelativeLayout main_layout;
        ImageView selected;
        FrameLayout frameLayout_1;
        ProgressBar progressbar_4_ad;
        RelativeLayout ad_layout;

        public MyViewHolder(View view) {
            super(view);
            note = view.findViewById(R.id.contact_chat_textview);
            call_time_txtview = view.findViewById(R.id.call_time_txtview);
            date_n_time = view.findViewById(R.id.date_n_time);
            dot = view.findViewById(R.id.dot);
            timestamp = view.findViewById(R.id.timestamp);
            main_layout = view.findViewById(R.id.main_layout);
            selected = view.findViewById(R.id.selected);
            ad_layout = (RelativeLayout) view.findViewById(R.id.ad_layout);

            frameLayout_1 = (FrameLayout) view.findViewById(R.id.frame_layout_adapter);
            progressbar_4_ad = (ProgressBar) view.findViewById(R.id.progressbar_4_ad);

        }
    }


    public Chat_contact_Adapter_Instagram(Context context, List<Note_new_instagram> notesList, int size) {
        this.context = context;
        this.notesList = notesList;
        LIST_AD_DELTA = size;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_contact_row_messenger, parent, false);
        /*switch (viewType) {
            case AD_TYPE:
                View unifiedNativeLayoutView = LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.row_item_for_native_ad_recyclerview,
                        parent, false);
                return new MyViewHolder(unifiedNativeLayoutView);
            case CONTENT_TYPE:
                // Fall through.
            default:
                return new MyViewHolder(itemView);
        }*/

        MyViewHolder viewHolder1 = new MyViewHolder(itemView);
        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        // final Note_new note = notesList.get(position);

        try {
            my_position = position;

            try {
                if (notesList.get(getRealPosition(position)).getNumber().equalsIgnoreCase("")) {
                    holder.note.setVisibility(View.GONE);
                }
                String msg = notesList.get(getRealPosition(position)).getNumber();
                String title = notesList.get(getRealPosition(position)).getNumber();
                Boolean gourp = false;
                try {
                    if (msg.contains(":")) {
                        gourp = true;
                        int start_index = title.indexOf(":");
                        start_index++;
                        msg = msg.substring(start_index) /*+ ":" + msg*/;
                        title = title.substring(0, start_index /*- 1*/);
                    }
                } catch (IndexOutOfBoundsException asd) {
                    asd.printStackTrace();
                } catch (Exception asd) {
                }
                if (gourp) {

                    String sourceString = "<b>" + title + "</b> " + msg;
                    holder.note.setText(Html.fromHtml(sourceString));
                } else {
                    holder.note.setText(msg);
                }
                //  holder.note.setText(msg);
                if (!Shared.getInstance().getBooleanValueFromPreference(context.getResources().getString(R.string.long_pressd_specific_chat_pref), false, context)) {
                    notesList.get(getRealPosition(position)).setSelected(false);
                }
                if (notesList.get(getRealPosition(position)).getSelected()) {
                    holder.selected.setVisibility(View.VISIBLE);
                } else {
                    holder.selected.setVisibility(View.GONE);
                }
                holder.call_time_txtview.setText(notesList.get(getRealPosition(position)).getNote());
                holder.main_layout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        long_pressed = true;
                        if (Shared.getInstance().getBooleanValueFromPreference(context.getResources().getString(R.string.long_pressd_specific_chat_pref), false, context)) {
                            return false;
                        }
                        notesList.get(getRealPosition(holder.getAdapterPosition())).setSelected(true);
                        selected_msgs.add(notesList.get(getRealPosition(holder.getAdapterPosition())));
                        notifyItemChanged(holder.getAdapterPosition());
                        // notifyDataSetChanged();
                        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            //deprecated in API 26
                            vibrator.vibrate(150);
                        }
                        Shared.getInstance().saveBooleanToPreferences(context.getResources().getString(R.string.long_pressd_specific_chat_pref), true, context);

                        return false;
                    }
                });
                holder.main_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (long_pressed) {
                            long_pressed = false;
                            return;
                        }
                        if (Shared.getInstance().getBooleanValueFromPreference(context.getResources().getString(R.string.long_pressd_specific_chat_pref), false, context)) {
                            if (notesList.get(getRealPosition(holder.getAdapterPosition())).getSelected()) {
                                selected_msgs.remove(notesList.get(getRealPosition(holder.getAdapterPosition())));
                            } else {
                                selected_msgs.add(notesList.get(getRealPosition(holder.getAdapterPosition())));
                            }
                            notesList.get(getRealPosition(holder.getAdapterPosition())).setSelected(!notesList.get(getRealPosition(holder.getAdapterPosition())).getSelected());
                            notifyItemChanged(holder.getAdapterPosition());
                            //notifyDataSetChanged();
                            if (selected_msgs.size() < 1) {
                                Shared.getInstance().saveBooleanToPreferences(context.getResources().getString(R.string.long_pressd_specific_chat_pref), false, context);
                            }
                        }
                    }
                });

            } catch (NullPointerException asd) {
            } catch (IndexOutOfBoundsException asd) {
            } catch (Exception asdasdsad) {
            }
            if (position > 2 && position == LIST_AD_DELTA) {
                if (my_unifiedNativeAd != null) {
                    try {
                        adView = (UnifiedNativeAdView) LayoutInflater.from(context).inflate(R.layout.ad_unified_new, null);
                        populateUnifiedNativeAdView(my_unifiedNativeAd, adView);
                        holder.frameLayout_1.removeAllViews();
                        holder.frameLayout_1.addView(adView);
                        holder.frameLayout_1.setVisibility(View.VISIBLE);
                        holder.ad_layout.setVisibility(View.VISIBLE);
                        holder.progressbar_4_ad.setVisibility(View.GONE);
                        notifyItemChanged(position);
                        return;
                    } catch (NullPointerException asd) {
                    }
                }
                if (ad_request_sent) {
                    return;
                }
                AdLoader.Builder builder = new AdLoader.Builder(context, context.getResources().getString(R.string.nativeID_receyler_view));
                builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        adView = (UnifiedNativeAdView) LayoutInflater.from(context).inflate(R.layout.ad_unified_new, null);
                        my_unifiedNativeAd = unifiedNativeAd;
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
                        holder.ad_layout.setVisibility(View.GONE);
                        holder.progressbar_4_ad.setVisibility(View.GONE);
                        slideView(holder.frameLayout_1, holder.frameLayout_1.getHeight(), 0);
                        ad_request_sent = false;
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        holder.frameLayout_1.setVisibility(View.VISIBLE);
                        holder.progressbar_4_ad.setVisibility(View.GONE);
                        notifyDataSetChanged();
                    }
                }).build();
                adLoader.loadAd(new AdRequest.Builder().addTestDevice(context.getResources().getString(R.string.zegar_device)).addTestDevice(context.getResources().getString(R.string.test_device_j5)).addTestDevice(context.getResources().getString(R.string.vicky_s8)).addTestDevice(context.getResources().getString(R.string.test_device_white)).addTestDevice("A86A0D556F68465C49063589837FCF98").build());
                ad_request_sent = true;
            } else {
                holder.frameLayout_1.setVisibility(View.GONE);
                holder.ad_layout.setVisibility(View.GONE);
                holder.progressbar_4_ad.setVisibility(View.GONE);
            }

        } catch (IndexOutOfBoundsException asd) {
        } catch (Exception er) {
        }
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

        /*  We use an animationSet to play the animation  */

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.play(slideAnimator);
        animationSet.start();
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
    public int getItemCount() {
        return notesList.size();
    }
/*
    private int getRealPosition(int position) {
        if (position < LIST_AD_DELTA) {
            return position;
        } else {
            try {
                return position - position / LIST_AD_DELTA;
            } catch (ArithmeticException asd) {
            } catch (Exception asd) {
            }
        }
        return position;
    }

    @Override
    public int getItemCount() {
        int additionalContent = 1;
        if (notesList.size() > 0 && LIST_AD_DELTA > 0 && notesList.size() > LIST_AD_DELTA) {
            additionalContent = notesList.size() / LIST_AD_DELTA;
        }
        return notesList.size() + additionalContent;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == LIST_AD_DELTA) {
            return AD_TYPE;
        }
        return CONTENT_TYPE;
    }*/
/*
    private int getRealPosition(int position) {
        *//*if (LIST_AD_DELTA == 0) {
            return position;
        } else {
            return position - position / LIST_AD_DELTA;
        }*//*
        if (position > 0 && position == LIST_AD_DELTA) {

            return position - position / LIST_AD_DELTA;
        } else {
            return position;
        }
    }*/

    private int getRealPosition(int position) {
        return position;
    }

}
