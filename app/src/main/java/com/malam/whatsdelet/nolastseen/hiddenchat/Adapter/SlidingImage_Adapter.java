package com.malam.whatsdelet.nolastseen.hiddenchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.malam.whatsdelet.nolastseen.hiddenchat.Activities.Video_View_Act;
import com.whatsdelete.Test.R;

import java.util.ArrayList;

public class SlidingImage_Adapter extends PagerAdapter {

    public static ArrayList<String> IMAGES;
    private LayoutInflater inflater;
    private Context context;
    int position;

    public SlidingImage_Adapter(Context context, ArrayList<String> IMAGES) {
        this.context = context;
        this.IMAGES = IMAGES;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public void remove(int position) {
        IMAGES.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return IMAGES.size();
    }


    @NonNull
    @Override
    public Object instantiateItem(ViewGroup view,  final int my_position) {
        View imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false);

        final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
        final LinearLayout play_btn = (LinearLayout) imageLayout.findViewById(R.id.play_btn);

/*
        imageView.setImageResource(IMAGES.get(position));*/
        if (IMAGES.get(my_position).endsWith(".mp4") || IMAGES.get(my_position).endsWith(".3gp") || IMAGES.get(my_position).endsWith(".avi") || IMAGES.get(my_position).endsWith(".wma") || IMAGES.get(my_position).endsWith(".wmv") || IMAGES.get(my_position).endsWith(".flv") || IMAGES.get(my_position).endsWith(".webm")) {
            play_btn.setVisibility(View.VISIBLE);
        } else {
            play_btn.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
           // Glide.with(context).load("file://" + IMAGES.get(position)).into(imageView);
        }
        Glide.with(context).load("file://" + IMAGES.get(my_position)).into(imageView);
        view.addView(imageLayout, 0);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IMAGES.get(my_position).endsWith(".mp4")|| IMAGES.get(my_position).endsWith(".3gp") || IMAGES.get(my_position).endsWith(".avi") || IMAGES.get(my_position).endsWith(".wma") || IMAGES.get(my_position).endsWith(".wmv") || IMAGES.get(my_position).endsWith(".flv") || IMAGES.get(my_position).endsWith(".webm")){
                    context.startActivity(new Intent(context, Video_View_Act.class).putExtra(context.getResources().getString(R.string.video_path),IMAGES.get(my_position)));
                }

            }
        });
        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }


}
