package com.malam.whatsdelet.nolastseen.hiddenchat.Activities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.malam.whatsdelet.nolastseen.hiddenchat.Fragments.DownloadImages;
import com.malam.whatsdelet.nolastseen.hiddenchat.Fragments.DownloadVideos;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.malam.whatsdelet.nolastseen.hiddenchat.Adapter.SlidingImage_Adapter;
import com.malam.whatsdelet.nolastseen.hiddenchat.Fragments.Deleted_images_frag;
import com.malam.whatsdelet.nolastseen.hiddenchat.Fragments.Deleted_videos_frag;
import com.malam.whatsdelet.nolastseen.hiddenchat.Fragments.ImagesFragment;
import com.malam.whatsdelet.nolastseen.hiddenchat.Fragments.VideosFragment;
import com.malam.whatsdelet.nolastseen.hiddenchat.Model.Music_model;
import com.malam.whatsdelet.nolastseen.hiddenchat.Service.FileOperations;
import com.malam.whatsdelet.nolastseen.hiddenchat.Service.FilesData;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.Shared;
import com.whatsdelete.Test.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Image_Viewer_Act extends AppCompatActivity implements View.OnClickListener {
    String path;
    ImageView main_image, backArrow,openBox,closeBox;
    private FrameLayout frameLayout_1;
    private AdView adaptive_adView;
    Boolean is_net_connected;
    ImageView options_layout, saveLayout, deletelayout, shareLayout;
    private ViewPager mPager;
    int position = 0;
    List<Music_model> muList;
    int position_call = 1;
    VideoView video_view;
    private ArrayList<String> ImagesArray = new ArrayList<String>();
    SlidingImage_Adapter slidingImage_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        setContentView(R.layout.activity_image__viewer_);
        path = getIntent().getStringExtra("path");
        is_net_connected = getIntent().getBooleanExtra(getResources().getString(R.string.is_net_connected_intent), false);
        position = Shared.getInstance().getIntValueFromPreference(getResources().getString(R.string.image_position_pref), 0, this);
        position_call = Shared.getInstance().getIntValueFromPreference(getResources().getString(R.string.from_status_pref), 1, this);
        intilioze_components();
        load_adaptive_Banner();

        muList = new ArrayList<>();

        if (position_call == 1) {
            if (position > 2) {
                position = position - 1;
            }

            File f = new File(path);

            if (f.getName().endsWith(".mp4") || f.getName().endsWith(".3gp") || f.getName().endsWith(".avi") || f.getName().endsWith(".3gp") || f.getName().endsWith(".M4V") || f.getName().endsWith(".flv")) {
                /*for (int i = 0; i < VideosFragment.muList.size(); i++) {
                    String path = VideosFragment.muList.get(i).getFile_path();
                    ImagesArray.add(path);
                    video_view.setVideoURI(Uri.parse(path));
                    video_view.requestFocus();
                    video_view.start();
                    MediaController mediaController = new MediaController(this);
                    mediaController.setAnchorView(video_view);
                    video_view.setVisibility(View.VISIBLE);
                    video_view.setMediaController(mediaController);
                    *//*set_as.setVisibility(View.GONE);*//*
                }*/
                video_view.setVideoURI(Uri.parse(path));
                video_view.requestFocus();
                video_view.start();
                MediaController mediaController = new MediaController(this);
                mediaController.setAnchorView(video_view);
                video_view.setVisibility(View.VISIBLE);
                video_view.setMediaController(mediaController);
                    //*set_as.setVisibility(View.GONE);*//*
            }
            else{
                Glide.with(this).load(path).into(main_image);
                /*for (int i = 0; i < ImagesFragment.muList.size(); i++) {
                    String path = ImagesFragment.muList.get(i).getFile_path();
                   // ImagesArray.add(path);
                    *//*if (is_net_connected) {
                        if (i != 3) {
                            Glide.with(this).load(path).into(main_image);
                        }
                    } else {
                        Glide.with(this).load(path).into(main_image);
                    }*//*
                    Glide.with(this).load(path).into(main_image);
                    Log.e("path", " " + path);
                }*/
            }

            openBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveLayout.setVisibility(View.VISIBLE);
                    shareLayout.setVisibility(View.VISIBLE);
                    closeBox.setVisibility(View.VISIBLE);
                }
            });
            closeBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveLayout.setVisibility(View.GONE);
                    shareLayout.setVisibility(View.GONE);
                    closeBox.setVisibility(View.GONE);
                }
            });



        }
        else if (position_call == 2) {
            if (position > 2) {
                /* position = position - 1;*/
            }

            File f = new File(path);
            if (f.getName().endsWith(".mp4") || f.getName().endsWith(".3gp") || f.getName().endsWith(".avi") || f.getName().endsWith(".3gp") || f.getName().endsWith(".M4V") || f.getName().endsWith(".flv"))
            {
                for (int i = 0; i < DownloadVideos.muList.size(); i++) {
                    String path = DownloadVideos.muList.get(i).getFile_path();
                    video_view.setVideoURI(Uri.parse(path));
                    video_view.requestFocus();
                    video_view.start();
                    MediaController mediaController = new MediaController(this);
                    mediaController.setAnchorView(video_view);
                    video_view.setVisibility(View.VISIBLE);
                    video_view.setMediaController(mediaController);
                }
                openBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveLayout.setVisibility(View.VISIBLE);
                        shareLayout.setVisibility(View.VISIBLE);
                        closeBox.setVisibility(View.VISIBLE);
                    }
                });
                closeBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveLayout.setVisibility(View.GONE);
                        shareLayout.setVisibility(View.GONE);
                        closeBox.setVisibility(View.GONE);
                    }
                });
            }


            else {
                for (int i = 0; i < DownloadImages.imageModel.size(); i++) {
                    String path = DownloadImages.imageModel.get(i).getFile_path();
                    Glide.with(this).load(path).into(main_image);
                    Log.e("path", " " + path);
                }

            }
            openBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deletelayout.setVisibility(View.VISIBLE);
                    shareLayout.setVisibility(View.VISIBLE);
                    closeBox.setVisibility(View.VISIBLE);
                }
            });
            closeBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deletelayout.setVisibility(View.GONE);
                    shareLayout.setVisibility(View.GONE);
                    closeBox.setVisibility(View.GONE);
                }
            });

        } else if (position_call == 4) {
            if (position > 2) {
                position = position - 1;
            }
            for (int i = 0; i < Deleted_videos_frag.muList.size(); i++) {
                String path = Deleted_videos_frag.muList.get(i).getFile_path();
                //ImagesArray.add(path);
                if (is_net_connected) {
                    if (i != 2) {
                        ImagesArray.add(path);
                    }
                } else {
                    ImagesArray.add(path);
                }
                Log.e("path", " " + path);
            }
        } else {
            if (position > 2) {
                position = position - 1;
            }
            for (int i = 0; i < Deleted_images_frag.muList.size(); i++) {
                String path = Deleted_images_frag.muList.get(i).getFile_path();
                //ImagesArray.add(path);
                if (is_net_connected) {
                    if (i != 3) {
                        ImagesArray.add(path);
                    }
                } else {
                    ImagesArray.add(path);
                }
                Log.e("path", " " + path);
            }

        }


        mPager = (ViewPager) findViewById(R.id.pager);
        slidingImage_adapter = new SlidingImage_Adapter(Image_Viewer_Act.this, ImagesArray);
        mPager.setAdapter(slidingImage_adapter);
        mPager.setCurrentItem(position, true);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int current_position) {
                path = ImagesArray.get(current_position);
                position = current_position;
                slidingImage_adapter.notifyDataSetChanged();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    private int getRealPosition(int position) {
        return position;
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
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                frameLayout_1.setVisibility(View.VISIBLE);
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


    private void intilioze_components() {
        main_image = (ImageView) findViewById(R.id.main_image);
        backArrow = (ImageView) findViewById(R.id.backArrow);
        video_view = (VideoView) findViewById(R.id.video_view);


        openBox = (ImageView) findViewById(R.id.openBox);
        saveLayout = (ImageView) findViewById(R.id.saveLayout);
        deletelayout = (ImageView) findViewById(R.id.deletelayout);
        shareLayout = (ImageView) findViewById(R.id.shareLayout);
        closeBox = (ImageView) findViewById(R.id.closeBox);


        backArrow.setOnClickListener(this);

        saveLayout.setOnClickListener(this);
        deletelayout.setOnClickListener(this);
        shareLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backArrow:
                finish();
                break;

            case R.id.saveLayout:
               /* try {

                    String newpath = Shared.getInstance().saveAndRefreshFiles(new File(path));
                    File file = new File(newpath);
                    File destFile = new File(Environment.getExternalStorageDirectory().toString() + FilesData.getSavedFilesLocation(), file.getName());
                    FileOperations.saveAndRefreshFiles(file, destFile);
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                    Toast.makeText(this, "File Saved in Gallery", Toast.LENGTH_LONG).show();
                    Intent saved_intent = new Intent(getResources().getString(R.string.intent_file_saved_images));
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(saved_intent);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Opps Something went wrong please try again", Toast.LENGTH_LONG).show();
                }
*/
                try {
                    if (Build.VERSION.SDK_INT >= 29) {

                        if (path.endsWith(".png") || path.endsWith(".jpg")) {
                            OutputStream fos;
                            Bitmap bitmap = null;
                            Uri imageUri = Uri.parse(path);
                            File file = new File(path);
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ContentResolver resolver = getContentResolver();
                            ContentValues values = new ContentValues();

                            values.put(MediaStore.MediaColumns.DISPLAY_NAME, getString(R.string.app_name) + file.getName());
                            if (path.endsWith(".png"))
                                values.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                            else
                                values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");

                            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM + File.separator + "DownloadedStatuses");

                            Uri imageUri1 = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                            Log.d("Android11Problem", imageUri1.toString());
                            try {
                                fos = resolver.openOutputStream(imageUri1);
                                assert bitmap != null;

                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                Objects.requireNonNull(fos);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        } else {
                            File destFile = new File(Image_Viewer_Act.this.getExternalFilesDir(Environment.DIRECTORY_DCIM)
                                    + "/DownloadedStatuses");
                            InputStream inputStream = null;

                            try {
                                inputStream = Image_Viewer_Act.this.getContentResolver().openInputStream(Uri.parse(path));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                            if (!destFile.exists()) {
                                destFile.mkdirs();
                            }
                            File file = new File(path);
                            File imageFile = new File(destFile, file.getName());
                            if (!imageFile.exists()) {
                                try {
                                    imageFile.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            try {
                                OutputStream out = new FileOutputStream(imageFile);
                                byte[] buf = new byte[1024];
                                int len;
                                while ((len = inputStream.read(buf)) > 0) {
                                    out.write(buf, 0, len);
                                }
                                out.close();
                                inputStream.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                    else
                    {
                        FileOperations.saveAndRefreshFiles(new File(path));
                        Toast.makeText(this, getResources().getString(R.string.image_in_gallery), Toast.LENGTH_LONG).show();
                        /*sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(path))));*/

                        File imageFile = new File(path);
                        File destFile = new File(Environment.getExternalStorageDirectory().toString() + FilesData.getSavedFilesLocation(Image_Viewer_Act.this), imageFile.getName());

                        MediaScannerConnection.scanFile(this,
                                new String[]{destFile.toString()}, null,
                                new MediaScannerConnection.OnScanCompletedListener() {
                                    public void onScanCompleted(String path, Uri uri) {
                                        Log.i("ExternalStorage", "Scanned " + path + ":");
                                        Log.i("ExternalStorage", "-> uri=" + uri);
                                    }
                                });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent saved_intent = new Intent(getResources().getString(R.string.intent_file_saved_images));
                Intent saved_intent_video = new Intent(getResources().getString(R.string.intent_file_saved_videos));
                LocalBroadcastManager.getInstance(this).sendBroadcast(saved_intent);
                LocalBroadcastManager.getInstance(this).sendBroadcast(saved_intent_video);
                Toast.makeText(this, getResources().getString(R.string.image_in_gallery), Toast.LENGTH_LONG).show();
                break;
            case R.id.deletelayout:
                final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(Image_Viewer_Act.this).create();
                View layout1 = LayoutInflater.from(Image_Viewer_Act.this).inflate(R.layout.delet_dialog, (ViewGroup) findViewById(R.id.your_dialog_root_element));
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
                        try {
                            File file = new File(ImagesArray.get(mPager.getCurrentItem()));
                            if (file.exists()) {
                                file.delete();
                            }
                            alertDialog.dismiss();
                            Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.file_deleted_pref), true, getApplicationContext());
                            SlidingImage_Adapter.IMAGES.remove(mPager.getCurrentItem());
                            slidingImage_adapter.notifyDataSetChanged();
                            int current_poistion = mPager.getCurrentItem();
                            current_poistion++;
                            mPager.setAdapter(slidingImage_adapter);
                            mPager.setCurrentItem(position, true);

                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                            if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.from_saved_files_pref), false, Image_Viewer_Act.this)) {
                                Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.delete_file_saved_files_pref), true, getApplicationContext());

                            } else if (position_call == 1) {
                                Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.delete_file_status_pref), true, getApplicationContext());
                            } else if (position_call == 2) {
                                Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.delete_file_images_pref), true, getApplicationContext());

                            } else {
                                Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.delete_file_deletedimages_pref), true, getApplicationContext());

                            }
                            if (position_call == 2) {
                                Intent saved_intent = new Intent("refresh_download");
                                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(saved_intent);
                            }
                            Intent saved_intent = new Intent("ref_download");
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(saved_intent);
                            if (ImagesArray.size() < 1)
                                finish();

                        } catch (IndexOutOfBoundsException qwe) {
                            Toast.makeText(Image_Viewer_Act.this, getResources().getString(R.string.opps_somthing_went_wrong), Toast.LENGTH_LONG).show();
                        } catch (Exception aqqwe) {
                            Toast.makeText(Image_Viewer_Act.this, getResources().getString(R.string.opps_somthing_went_wrong), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                cancel_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();

                    }
                });
                break;
            case R.id.shareLayout:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                share.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
                startActivity(Intent.createChooser(share, "Share Image via"));
                //shareImage();


                if (path.endsWith(".mp4") || path.endsWith(".3gp") || path.endsWith(".avi") || path.endsWith(".flv") || path.endsWith(".wmv")) {
                    Uri photoURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", new File(path));

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("video/*");
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    intent.putExtra(Intent.EXTRA_STREAM, photoURI);
                    startActivity(Intent.createChooser(intent, "Share Video via"));
                } else {
                    Uri photoURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", new File(path));
                    Uri myUri = Uri.parse(path);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/*");
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    intent.putExtra(Intent.EXTRA_STREAM, photoURI);
                    startActivity(Intent.createChooser(intent, "Share image via"));
                }
                break;
            default:
                break;
        }
    }

    public void shareImage() {


/*
            Uri myUri = Uri.parse(path);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, myUri);
            startActivity(Intent.createChooser(intent, "Share image using"));*/
    }
}
