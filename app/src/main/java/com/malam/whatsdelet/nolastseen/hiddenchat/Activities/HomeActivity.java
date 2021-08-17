package com.malam.whatsdelet.nolastseen.hiddenchat.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.os.storage.StorageManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;
import com.malam.whatsdelet.nolastseen.hiddenchat.Fragments.DownloadedStatus_frag;
import com.malam.whatsdelet.nolastseen.hiddenchat.Fragments.Status_frag;
import com.malam.whatsdelet.nolastseen.hiddenchat.Model.AdsModel;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.Ad_Utils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.whatsdelete.Test.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    InterstitialAd mInterstitialAd;
    TextView Whatsdelete, StatusSaver, directMessage, settings, app_Lang, share, rateUs, feedback, moreApps, ppolicy;
    DrawerLayout drawer;
    NavigationView navigationView;
    public static ImageView menu_btn;
    private ShimmerFrameLayout shimmer_animation;
    UnifiedNativeAd nativeAd;
    private FrameLayout adContainerView;
    private AdView adaptive_adView;
    ////
    String gridPackage1, gridPackage2, gridPackage3;
    ImageView g1, g2, g3;
    Animation animation;
    LinearLayout g1_layout, g2_layout, g3_layout, local_ads_layout,deleteChat,newStatus;
    TextView title1_txtview, title2_txtview, title3_txtview, more_from_dev_txtview;
    private String title2, title3, title1;
    private Drawable drawable;
    private String packageName;
    private Drawable drawableG1, drawableG2, drawableG3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
        initlize_adz();
        locaAds();

    }



    private void locaAds() {

        title1_txtview = (TextView) findViewById(R.id.title1);
        title2_txtview = (TextView) findViewById(R.id.title2);
        title3_txtview = (TextView) findViewById(R.id.title3);
        g1 = (ImageView) findViewById(R.id.g1);
        g2 = (ImageView) findViewById(R.id.g2);
        g3 = (ImageView) findViewById(R.id.g3);

        g1_layout = (LinearLayout) findViewById(R.id.g1_layout);
        g2_layout = (LinearLayout) findViewById(R.id.g2_layout);
        g3_layout = (LinearLayout) findViewById(R.id.g3_layout);
        local_ads_layout = (LinearLayout) findViewById(R.id.local_ads_layout);
        deleteChat = (LinearLayout) findViewById(R.id.deleteChat);
        newStatus = (LinearLayout) findViewById(R.id.newStatus);

        g1_layout.setOnClickListener(this);
        g2_layout.setOnClickListener(this);
        g3_layout.setOnClickListener(this);
        before_loading_ads();
        new GridAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getResources().getString(R.string.main_grid_images_link));
    }
    private void before_loading_ads() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                .displayer(new FadeInBitmapDisplayer(500)).build();
        File cacheDir = StorageUtils.getCacheDirectory(this);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .memoryCacheSizePercentage(13) // default
                .diskCache(new UnlimitedDiskCache(cacheDir)) // default
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .threadPoolSize(3)
                .discCacheSize(100 * 1024 * 1024)
                .discCacheExtraOptions(480, 800, null)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(this)) // default
                .imageDecoder(new BaseImageDecoder(true)) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .writeDebugLogs().build();
        ImageLoader.getInstance().init(config); // Do it on Application start
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.g1_layout:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + gridPackage1)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + gridPackage1)));
                }
                break;
            case R.id.g2_layout:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + gridPackage2)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + gridPackage2)));
                }
                break;
            case R.id.g3_layout:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + gridPackage3)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + gridPackage3)));
                }
                break;
            default:
                break;
        }
    }

    private class GridAsyncTask extends AsyncTask<String, String, List<AdsModel>> {
        private final List<AdsModel> adsModelList;
        private final Context mContext;

        public GridAsyncTask(Context context) {
            this.mContext = context;
            adsModelList = new ArrayList<AdsModel>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<AdsModel> doInBackground(String... params) {
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(getResources().getString(R.string.main_grid_images_link));
                HttpResponse response = client.execute(post);
                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent()));
                StringBuffer stringBuffer = new StringBuffer();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    stringBuffer.append(line);
                }
                //Show Complete JSON
                String jsonString = stringBuffer.toString().replace("\\/", "/");//.substring(0, stringBuffer.toString().length()-1);
                //Parsing JSON
                JSONObject parentJSONObj = new JSONObject(jsonString);
                JSONArray jsonArray = parentJSONObj.getJSONArray("application");
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject mainObjects = jsonArray.getJSONObject(i);
                    AdsModel modelGrid = new AdsModel();
                    modelGrid.setUrlApp(mainObjects.getString("url"));
                    modelGrid.setImageUrl(mainObjects.getString("image"));
                    modelGrid.setAppTitle(mainObjects.getString("title"));
                    buffer.append(modelGrid);
                    System.out.println("URLS: " + mainObjects.getString("url") + " Image: " + mainObjects.getString("image"));
                    adsModelList.add(modelGrid);
                }
                return adsModelList;

            } catch (MalformedURLException mfe) {
                mfe.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                try {
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(final List<AdsModel> adsList) {
            super.onPostExecute(adsList);

            if (adsList == null) {
            } else {
                final DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .resetViewBeforeLoading(true)
                        .resetViewBeforeLoading(false)  // default
                        .delayBeforeLoading(500)
                        .cacheInMemory(true) // default
                        .cacheOnDisk(true) // default
                        .considerExifParams(false) // default
                        .imageScaleType(ImageScaleType.NONE)// default
                        .bitmapConfig(Bitmap.Config.RGB_565) // default
                        .displayer(new SimpleBitmapDisplayer()) // default
                        .handler(new Handler()) // default
                        .build();
                ImageLoader imageLoader = ImageLoader.getInstance();
                ImageSize targetSize = new ImageSize(100, 100);
                for (int i = 0; i < adsList.size(); i++) {
                    AdsModel ads;
                    if (i == 0) {
                        ads = adsList.get(0);
                        String imageFirst = ads.getImageUrl();
                        gridPackage1 = ads.getUrlApp();
                        Ad_Utils.gridPackage1 = gridPackage1;
                        title1 = ads.getAppTitle();
                        Ad_Utils.title1 = title1;

                        imageLoader.loadImage(imageFirst, targetSize, options, new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view, loadedImage);
                                try {
                                    drawableG1 = new BitmapDrawable(getResources(), loadedImage);
                                    Ad_Utils.drawableG1 = drawableG1;
                                    g1.setImageDrawable(drawableG1);
                                    g1.setVisibility(View.VISIBLE);
                                    title1_txtview.setText(title1);
                                    more_from_dev_txtview.setText("More from developer");

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    if (i == 1) {
                        ads = adsList.get(1);
                        String imageSecond = ads.getImageUrl();
                        gridPackage2 = ads.getUrlApp();
                        Ad_Utils.gridPackage2 = gridPackage2;
                        title2 = ads.getAppTitle();
                        Ad_Utils.title2 = title2;

                        imageLoader.loadImage(imageSecond, targetSize, options, new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view, loadedImage);
                                try {
                                    drawableG2 = new BitmapDrawable(getResources(), loadedImage);
                                    Ad_Utils.drawableG2 = drawableG2;
                                    g2.setImageDrawable(drawableG2);
                                    title2_txtview.setText(title2);
                                    more_from_dev_txtview.setText("More from developer");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    if (i == 2) {
                        ads = adsList.get(2);
                        String imageThird = ads.getImageUrl();
                        gridPackage3 = ads.getUrlApp();
                        Ad_Utils.gridPackage3 = gridPackage3;
                        title3 = ads.getAppTitle();
                        Ad_Utils.title3 = title3;

                        imageLoader.loadImage(imageThird, targetSize, options, new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view, loadedImage);
                                try {
                                    drawableG3 = new BitmapDrawable(getResources(), loadedImage);
                                    Ad_Utils.drawableG3 = drawableG3;
                                    g3.setImageDrawable(drawableG3);

                                    g3.setVisibility(View.VISIBLE);
                                    title3_txtview.setText(title3);
                                    g3.startAnimation(animation);
                                    more_from_dev_txtview.setText("More from developer");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }

            }
        }
    }


    private void initlize_adz() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.intrestialAd));
        requestNewInterstitial();
        load_adaptive_Banner();
        loading_native_advance_ad();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }

    private void load_adaptive_Banner() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        adContainerView = findViewById(R.id.ad_view_container);
        // Step 1 - Create an AdView and set the ad unit ID on it.
        adaptive_adView = new AdView(this);
        adaptive_adView.setAdUnitId(getString(R.string.bannerAd));
        adContainerView.addView(adaptive_adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(getResources().getString(R.string.vicky_s8)).addTestDevice(getResources().getString(R.string.zegar_device)).addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        AdSize adSize = getAdSize();
        // Step 4 - Set the adaptive ad size on the ad view.
        adaptive_adView.setAdSize(adSize);

        // Step 5 - Start loading the ad in the background.
        adaptive_adView.loadAd(adRequest);
        adaptive_adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                shimmer_animation.setVisibility(View.GONE);
                shimmer_animation.stopShimmerAnimation();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                shimmer_animation.stopShimmerAnimation();
                shimmer_animation.setVisibility(View.GONE);
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

    public void init() {

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        menu_btn = findViewById(R.id.menu_btn);
        shimmer_animation = findViewById(R.id.ad_loader);
        shimmer_animation.startShimmerAnimation();

        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        findViewById(R.id.statusSaver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, DownloadedStatuses.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.whatsdelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.deleteChat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, DeleteWhatsappMessages.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.newStatus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, NewWhatsappStatus.class);
                startActivity(intent);
            }
        });

        /*findViewById(R.id.newStatus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd.isLoaded()) mInterstitialAd.show();
                else {
                    Intent intent = new Intent(HomeActivity.this, NewWhatsappStatus.class);
                    intent.putExtra("status", "messaging");
                    startActivity(intent);
                    requestNewInterstitial();
                }
                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        requestNewInterstitial();
                        Intent intent = new Intent(HomeActivity.this, DirectChatAct.class);
                        intent.putExtra("status", "messaging");
                        startActivity(intent);
                    }
                });
            }
        });*/
        findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Intent intent = new Intent(HomeActivity.this, Settings.class);
                    startActivity(intent);
                    requestNewInterstitial();
                }
                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        requestNewInterstitial();
                        Intent intent = new Intent(HomeActivity.this, Settings.class);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
           /* case R.id.nav_del_chat:
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.del_status:
                startActivity(new Intent(this, Downloaded_status.class));
                break;
            case R.id.nav_direct_chat:
                if (mInterstitialAd.isLoaded()) mInterstitialAd.show();
                else {
                    startActivity(new Intent(this, DirectChatAct.class));
                    requestNewInterstitial();
                }
                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        requestNewInterstitial();
                        startActivity(new Intent(getApplicationContext(), DirectChatAct.class));
                    }
                });
                break;
            case R.id.del_settings:
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    startActivity(new Intent(this, Settings.class));
                    requestNewInterstitial();
                }
                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        requestNewInterstitial();
                        Intent intent = new Intent(HomeActivity.this, Settings.class);
                        startActivity(intent);
                    }
                });
                break;
            case R.id.del_help:
                startActivity(new Intent(this, SliderActivity.class).putExtra("HomeAct", "HomeAct"));
                break;
            case R.id.del_rate_us:
                show_rating_dialoug();
                break;
            case R.id.del_moreapps:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri
                            .parse("market://search?q=pub:" + getResources().getString(R.string.account_name))));
                } catch (ActivityNotFoundException anfe) {
                    startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/developer?id=" + getResources().getString(R.string.account_name))));
                }
                break;*/
            case R.id.del_share:
                final String appPackajeName = getPackageName();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Please download this Awesome app " + getResources().getString(R.string.app_name_share) + " \n" + ": " + "https://play.google.com/store/apps/details?id=" + appPackajeName);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Share Link via"));
                break;
            case R.id.del_privacy:
                privacy_policy();
                break;
            case R.id.exit:
                onBackPressed();
                break;
            default:
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onBackPressed() {
        if (mInterstitialAd.isLoaded()) mInterstitialAd.show();
        else {
            exite_dialog();
        }
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                exite_dialog();
            }
        });
    }

    private void show_rating_dialoug() {
        final Dialog dialog;
        dialog = new Dialog(HomeActivity.this);
        try {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (NullPointerException ne) {
        } catch (Exception e) {
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.rating_dialoug);
        Button submit = dialog.findViewById(R.id.submit);
        final RatingBar rating_bar = dialog.findViewById(R.id.rating_bar);
        rating_bar.setMax(5);
        rating_bar.setNumStars(5);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rating_bar.getRating() <= 3) {
                    sendFeedback();
                    dialog.dismiss();
                } else {
                    final String appPackageName = getPackageName();
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                .parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id="
                                        + appPackageName)));
                    }
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void sendFeedback() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("*/*");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.account_email)});
        i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name) + " FeedBack");
        startActivity(createEmailOnlyChooserIntent(i, "Send via email"));
    }

    public Intent createEmailOnlyChooserIntent(Intent source, CharSequence chooserTitle) {
        Stack<Intent> intents = new Stack<Intent>();
        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
                "", null));
        List<ResolveInfo> activities = getPackageManager()
                .queryIntentActivities(i, 0);

        for (ResolveInfo ri : activities) {
            Intent target = new Intent(source);
            target.setPackage(ri.activityInfo.packageName);
            intents.add(target);
        }
        if (!intents.isEmpty()) {
            Intent chooserIntent = Intent.createChooser(intents.remove(0),
                    chooserTitle);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                    intents.toArray(new Parcelable[intents.size()]));
            return chooserIntent;
        } else {
            return Intent.createChooser(source, chooserTitle);
        }
    }

    private void privacy_policy() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCancelable(true);
        LayoutInflater inflater = getLayoutInflater();

        View view = LayoutInflater.from(this).inflate(R.layout.privacy_policy, null);

        alertDialog.setView(view);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        TextView textView = view.findViewById(R.id.google_link);
        TextView textView2 = view.findViewById(R.id.google_link2);
        TextView textView3 = view.findViewById(R.id.email_link);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://policies.google.com/privacy"));
                startActivity(browserIntent);

            }
        });
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://policies.google.com/privacy"));
                startActivity(browserIntent);

            }
        });
        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("*/*");
                // i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(crashLogFile));
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.privacy_policy_email_us_link)});
                i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name) + " Feedback");

                startActivity(createEmailOnlyChooserIntent(i, "Send via email"));


            }
        });
        alertDialog.show();
    }

    private void exite_dialog() {
        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
        View layout1 = LayoutInflater.from(this).inflate(R.layout.exit_dialog, findViewById(R.id.your_dialog_root_element));
        layout1.setMinimumWidth(100);
        alertDialog.setView(layout1);

        if (nativeAd != null) {
            FrameLayout frameLayout = layout1.findViewById(R.id.adview_fram);
            UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater().inflate(R.layout.ad_unified_new, null);
            populateUnifiedNativeAdView(nativeAd, adView);

            frameLayout.addView(adView);
        }

        alertDialog.show();
        try {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (NullPointerException ne) {
        } catch (Exception e) {
        }
        Button cancel_btn = layout1.findViewById(R.id.no_btn);
        Button ok_btn = layout1.findViewById(R.id.ok_btn);
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                alertDialog.dismiss();
            }

        });
        Button rate_btn = layout1.findViewById(R.id.rate_btn);
        rate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_rating_dialoug();
                alertDialog.dismiss();
            }

        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void loading_native_advance_ad() {
        AdLoader.Builder builder = new AdLoader.Builder(this, getResources().getString(R.string.nativeID));

        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            // OnUnifiedNativeAdLoadedListener implementation.
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                // You must call destroy on old ads when you are done with them,
                // otherwise you will have a memory leak.
                if (nativeAd != null) {
                    nativeAd.destroy();
                }
                nativeAd = unifiedNativeAd;
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
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();

            }
        }).build();
        adLoader.loadAd(new AdRequest.Builder().addTestDevice(getResources().getString(R.string.vicky_s8)).addTestDevice(getResources().getString(R.string.zegar_device)).addTestDevice(getResources().getString(R.string.test_device_j5)).addTestDevice(getResources().getString(R.string.vicky_s8)).addTestDevice(getResources().getString(R.string.test_device_white)).addTestDevice("A86A0D556F68465C49063589837FCF98").build());
    }

    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
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
    protected void onDestroy() {
        super.onDestroy();

        DownloadedStatus_frag.my_unifiedNativeAd=null;
        Status_frag.my_unifiedNativeAd=null;
        DownloadedStatus_frag.net_is_connected=false;
        Status_frag.net_is_connected=false;
    }
}