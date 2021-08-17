package com.malam.whatsdelet.nolastseen.hiddenchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Environment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

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
import com.malam.whatsdelet.nolastseen.hiddenchat.Activities.Contact_Chat_Act;
import com.malam.whatsdelet.nolastseen.hiddenchat.Activities.MainActivity;
import com.malam.whatsdelet.nolastseen.hiddenchat.Fragments.Whats_App_Frag;
import com.malam.whatsdelet.nolastseen.hiddenchat.Model.Note;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.Shared;
import com.whatsdelete.Test.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {

    private final Context context;
    public static List<Note> notesList;
    public static List<Note> notesList_new;
    public static List<Note> selected_list = new ArrayList<>();
    public static List<Integer> selected_pos = new ArrayList<>();

    private static final int CONTENT_TYPE = 0;

    private static final int AD_TYPE = 1;
    private static final int LIST_AD_DELTA = 5;
    Boolean ad_request_sent = false;
    int ad_request = 0;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView note, contact_number_textview, call_time_txtview, date_n_time;

        public TextView timestamp, dat_n_time_iteration;
        CircleImageView image_of_caller;

        ImageView selectedImage;
        FrameLayout frameLayout_1;
        LinearLayout num_of_unread_msgs_layout, main_layout;
        TextView num_of_unread_msgs_txtview;
        ProgressBar progress_bar;

        UnifiedNativeAdView adView;
        UnifiedNativeAd unifiedNativeAd = null;

        public MyViewHolder(View view) {
            super(view);
            note = view.findViewById(R.id.note);
            contact_number_textview = view.findViewById(R.id.contact_number_textview);
            call_time_txtview = view.findViewById(R.id.call_time_txtview);
            date_n_time = view.findViewById(R.id.date_n_time);

            timestamp = view.findViewById(R.id.timestamp);
            dat_n_time_iteration = view.findViewById(R.id.dat_n_time_iteration);
            image_of_caller = view.findViewById(R.id.image_of_caller);
            main_layout = view.findViewById(R.id.main_layout);
            progress_bar = view.findViewById(R.id.progress_bar);
            selectedImage = view.findViewById(R.id.selectedImage);
            frameLayout_1 = view.findViewById(R.id.frame_layout_adapter);
            num_of_unread_msgs_layout = view.findViewById(R.id.num_of_unread_msgs_layout);
            num_of_unread_msgs_txtview = view.findViewById(R.id.num_of_unread_msgs_txtview);
        }
    }


    public NotesAdapter(Context context, List<Note> notesList) {
        this.context = context;
        NotesAdapter.notesList = notesList;
        notesList_new = notesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    public static void de_select_all() {
        for (int i = 0; i < notesList.size(); i++) {
            notesList.get(i).setSelected(false);
        }

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
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        try {
            switch (getItemViewType(position)) {
                case AD_TYPE:
                    holder.progress_bar.setVisibility(View.VISIBLE);
                    if (Whats_App_Frag.unifiedNativeAd != null) {
                        try {
                            holder.adView = (UnifiedNativeAdView) LayoutInflater.from(context).inflate(R.layout.ad_unified_row_item, null);
                            populateUnifiedNativeAdView(Whats_App_Frag.unifiedNativeAd, holder.adView);
                            holder.frameLayout_1.removeAllViews();
                            holder.frameLayout_1.addView(holder.adView);
                            holder.frameLayout_1.setVisibility(View.VISIBLE);
                            holder.main_layout.setVisibility(View.GONE);
                            holder.progress_bar.setVisibility(View.GONE);
                            return;
                        } catch (NullPointerException asd) {
                        }
                    }
                    if (ad_request_sent) {
                        return;
                    }
                    AdLoader.Builder builder = new AdLoader.Builder(context, context.getResources().getString(R.string.nativeID_receyler_view));
                    builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                        // OnUnifiedNativeAdLoadedListener implementation.
                        @Override
                        public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                            /*UnifiedNativeAdView */
                            holder.adView = (UnifiedNativeAdView) LayoutInflater.from(context).inflate(R.layout.ad_unified_row_item, null);
                            holder.unifiedNativeAd = unifiedNativeAd;
                            Whats_App_Frag.unifiedNativeAd = unifiedNativeAd;
                            populateUnifiedNativeAdView(unifiedNativeAd, holder.adView);
                            try {
                                holder.frameLayout_1.removeAllViews();
                                holder.frameLayout_1.addView(holder.adView);
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
                            holder.main_layout.setVisibility(View.GONE);
                            holder.progress_bar.setVisibility(View.GONE);
                            ad_request_sent = false;
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            holder.frameLayout_1.setVisibility(View.VISIBLE);
                            holder.main_layout.setVisibility(View.GONE);
                            holder.progress_bar.setVisibility(View.GONE);
                        }
                    }).build();
                    adLoader.loadAd(new AdRequest.Builder().addTestDevice(context.getResources().getString(R.string.zegar_device)).addTestDevice(context.getResources().getString(R.string.test_device_j5)).addTestDevice(context.getResources().getString(R.string.vicky_s8)).addTestDevice(context.getResources().getString(R.string.test_device_white)).addTestDevice("A86A0D556F68465C49063589837FCF98").build());
                    ad_request_sent = true;
                    return;

            }
            final Note note = notesList.get(getRealPosition(position));
            holder.note.setText(note.getNote());
            holder.contact_number_textview.setText(note.getNumber());
            holder.call_time_txtview.setText(note.getTime());
            String asdasd = note.getTime_n_date_4_itration();
            // holder.dat_n_time_iteration.setText(asdasd);
            String date = note.getTimestamp();
            holder.date_n_time.setText(date);
            holder.timestamp.setText(date);
            if (!Shared.getInstance().getBooleanValueFromPreference(context.getResources().getString(R.string.long_pressd_pref), false, context)) {
                notesList.get(getRealPosition(position)).setSelected(false);
            }
            if (notesList.get(getRealPosition(position)).getSelected()) {
                holder.selectedImage.setVisibility(View.VISIBLE);
            } else holder.selectedImage.setVisibility(View.GONE);
            String input = note.getNote();
            input = input.replace(" ", "");
            input = input.trim();
            if (Shared.getInstance().getIntValueFromPreference(input + context.getResources().getString(R.string.num_of_unread_msgs_pref), 0, context) > 0) {
                holder.num_of_unread_msgs_layout.setVisibility(View.VISIBLE);
                holder.num_of_unread_msgs_txtview.setText("" + Shared.getInstance().getIntValueFromPreference(input + context.getResources().getString(R.string.num_of_unread_msgs_pref), 0, context));
            } else {
                holder.num_of_unread_msgs_layout.setVisibility(View.INVISIBLE);
            }
            if (Shared.getInstance().getBooleanValueFromPreference(input, false, context)) {
                holder.note.setTypeface(holder.note.getTypeface(), Typeface.BOLD);
                holder.contact_number_textview.setTypeface(holder.contact_number_textview.getTypeface(), Typeface.BOLD);

                holder.date_n_time.setTextColor(Color.BLACK);
                holder.timestamp.setTextColor(Color.BLACK);
                holder.call_time_txtview.setTextColor(Color.BLACK);
                if (note.isStatus()) {
                    holder.contact_number_textview.setTextColor(context.getResources().getColor(R.color.red));
                    holder.contact_number_textview.setTypeface(null, Typeface.ITALIC);
                    holder.contact_number_textview.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.deleted, 0, 0, 0);

                } else {
                    holder.contact_number_textview.setTextColor(Color.BLACK);
                    holder.contact_number_textview.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }

            } else {
                holder.note.setTypeface(null, Typeface.NORMAL);
                holder.date_n_time.setTypeface(null, Typeface.NORMAL);
                holder.timestamp.setTypeface(null, Typeface.NORMAL);
                holder.call_time_txtview.setTypeface(null, Typeface.NORMAL);

                holder.date_n_time.setTextColor(Color.GRAY);
                holder.timestamp.setTextColor(Color.GRAY);
                holder.call_time_txtview.setTextColor(Color.GRAY);
                if (note.isStatus()) {
                    holder.contact_number_textview.setTextColor(context.getResources().getColor(R.color.red));
                    holder.contact_number_textview.setTypeface(null, Typeface.ITALIC);
                    holder.contact_number_textview.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.deleted, 0, 0, 0);

                } else {
                    holder.contact_number_textview.setTextColor(Color.GRAY);
                    holder.contact_number_textview.setTypeface(null, Typeface.NORMAL);
                    holder.contact_number_textview.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }

            }

            String path = Environment.getExternalStorageDirectory() + "/.Profile Thumbnail/";
            Glide.with(context).load(path + "Image-" + input + ".png")
                    .skipMemoryCache(false).centerCrop()
                    /*.placeholder(R.drawable.ic_user)*/
                    .into(holder.image_of_caller);

            holder.main_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (Shared.getInstance().getBooleanValueFromPreference(context.getResources().getString(R.string.long_pressd_pref), false, context)) {
                            if (notesList.get(getRealPosition(holder.getAdapterPosition())).getSelected()) {
                                notesList.get(getRealPosition(holder.getAdapterPosition())).setSelected(false);
                                MainActivity.selected_items--;
                                try {
                                    selected_list.remove(notesList.get(getRealPosition(holder.getAdapterPosition())));
                                    for (int i = 0; i < selected_pos.size(); i++) {
                                        int asd = selected_pos.get(i);
                                        if (asd == getRealPosition(holder.getAdapterPosition())) {
                                            selected_pos.remove(i);
                                        }
                                    }
                                } catch (IndexOutOfBoundsException asd) {
                                } catch (Exception asd) {
                                }
                            } else {
                                notesList.get(getRealPosition(holder.getAdapterPosition())).setSelected(true);
                                MainActivity.selected_items++;
                                selected_list.add(notesList.get(getRealPosition(holder.getAdapterPosition())));
                                selected_pos.add(getRealPosition(holder.getAdapterPosition()));
                            }
                            MainActivity.check_selected_items();
                            notifyDataSetChanged();

                            return;
                        }
                        Shared.getInstance().saveStringToPreferences(context.getResources().getString(R.string.contact_image_pref), note.getPath(), context);
                        Shared.getInstance().saveStringToPreferences(context.getResources().getString(R.string.contact_name_pref), holder.note.getText().toString(), context);
                        Shared.getInstance().saveIntToPreferences(context.getResources().getString(R.string.chat_position_pref), getRealPosition(holder.getAdapterPosition()), context);
                        Intent i = new Intent(context, Contact_Chat_Act.class);
                        i.setFlags(FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
//                        System.out.println("chat_position_preference ==> " + Shared.getInstance().getIntValueFromPreference(context.getResources().getString(R.string.chat_position_pref), 0, context));
                        String input = notesList.get(getRealPosition(holder.getAdapterPosition())).getNote();
                        input = input.replace(" ", "");
                        input = input.trim();
                        Shared.getInstance().saveBooleanToPreferences(input, false, context);
                        int num_of_unread_msgs_specific_contact = Shared.getInstance().getIntValueFromPreference(input + context.getResources().getString(R.string.num_of_unread_msgs_pref), 0, context);
                        int total_unread_msgs = Shared.getInstance().getIntValueFromPreference(context.getResources().getString(R.string.total_unread_msgs_pref), 0, context);
                        total_unread_msgs = total_unread_msgs - num_of_unread_msgs_specific_contact;
                        if (total_unread_msgs >= 0) {
                            Shared.getInstance().saveIntToPreferences(context.getResources().getString(R.string.total_unread_msgs_pref), total_unread_msgs, context);
                        }
                        Shared.getInstance().saveIntToPreferences(input + context.getResources().getString(R.string.num_of_unread_msgs_pref), 0, context);
                        selected_pos.clear();
                        selected_list.clear();
                        notifyDataSetChanged();
                    } catch (IndexOutOfBoundsException qwe) {
                    } catch (Exception wsa) {
                    }
                }
            });
            holder.main_layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    try{
                    Shared.getInstance().saveStringToPreferences(context.getResources().getString(R.string.delete_diloug_title_pref), context.getResources().getString(R.string.delete_contact_title), context);
                    Shared.getInstance().saveStringToPreferences(context.getResources().getString(R.string.delete_dialoug_msg_pref), context.getResources().getString(R.string.delete_contact_msg), context);
                    Shared.getInstance().saveIntToPreferences(context.getResources().getString(R.string.delete_tabs_position_pref), 1, context);
                    if (Shared.getInstance().getBooleanValueFromPreference(context.getResources().getString(R.string.long_pressd_pref), false, context)) {
                        return false;
                    } else {
                        if (notesList.get(getRealPosition(holder.getAdapterPosition())).getSelected()) {
                            notesList.get(getRealPosition(holder.getAdapterPosition())).setSelected(false);
                            /*  MainActivity.selected_position.remove(getRealPosition(position));*/
                            selected_list.remove(notesList.get(getRealPosition(holder.getAdapterPosition())));

                        } else {
                            /* MainActivity.selected_position.add(getRealPosition(position));*/
                            notesList.get(getRealPosition(holder.getAdapterPosition())).setSelected(true);
                            MainActivity.selected_items++;
                            /* MainActivity.selected_position.add(getRealPosition(position));*/
                            selected_list.add(notesList.get(getRealPosition(holder.getAdapterPosition())));
                            selected_pos.add(getRealPosition(holder.getAdapterPosition()));
                            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
                            } else {
                                //deprecated in API 26
                                vibrator.vibrate(300);
                            }

                        }
                        MainActivity.check_selected_items();
                        Shared.getInstance().saveBooleanToPreferences(context.getResources().getString(R.string.long_pressd_pref), true, context);
                        System.out.println("selected list****************************************************");
                        for (int i = 0; i < selected_list.size(); i++) {
                            System.out.println("selected list " + selected_list.get(i).getNote());
                        }
                        notifyDataSetChanged();
                    }

                    } catch (IndexOutOfBoundsException qwe) {
                    } catch (Exception wsa) {
                    }
                    return false;
                }
            });
        } catch (IndexOutOfBoundsException ier) {
        } catch (NullPointerException ner) {
        } catch (Exception er) {
        }
    }

    private int getRealPosition(int position) {
        if (LIST_AD_DELTA == 0) {
            return position;
        } else {
            return position - position / LIST_AD_DELTA;
        }
    }

    @Override
    public int getItemCount() {
        int additionalContent = 0;
        if (notesList.size() > 0 && LIST_AD_DELTA > 0 && notesList.size() > LIST_AD_DELTA) {
            additionalContent = notesList.size() / LIST_AD_DELTA;
            additionalContent = (notesList.size() + (notesList.size() / LIST_AD_DELTA)) / LIST_AD_DELTA;
        }
        return notesList.size() + additionalContent;
    }


    @Override
    public int getItemViewType(int position) {
        /*if (notesList.get(position).getNote().equalsIgnoreCase("for_native_ad")) {
            return AD_TYPE;
        }*/
        if (position > 0 && position % LIST_AD_DELTA == 0) {
            return AD_TYPE;
        }
        return CONTENT_TYPE;
    }

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     */
    private String formatDate(String dateStr) {
        /*
                SimpleDateFormat fmt = new SimpleDateFormat("dd / MM / yyyy");
                Date date = fmt.parse(dateStr);
                SimpleDateFormat fmtOut = new SimpleDateFormat("dd m");

*/
        String output1 = null;
        try {

           /* Date date = new Date(location.getTime());
            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
            mTimeText.setText("Time: " + dateFormat.format(date));*/
            DateFormat inputFormatter1 = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = null;

            date1 = inputFormatter1.parse(dateStr);


            DateFormat outputFormatter1 = new SimpleDateFormat("dd-MMM-yyyy");
            output1 = outputFormatter1.format(date1); //
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return output1;
    }
}
