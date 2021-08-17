package com.malam.whatsdelet.nolastseen.hiddenchat.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.hbb20.CountryCodePicker;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class DirectChatAct extends AppCompatActivity implements View.OnClickListener {
    CountryCodePicker ccp;
    EditText number, meesage;
    String validNUmber;
    private FrameLayout frameLayout_1;
    private AdView adaptive_adView;
    private ShimmerFrameLayout shimmer_animation;
    String HomeAct;
    //
    String gridPackage1, gridPackage2, gridPackage3;
    ImageView g1, g2, g3;
    Animation animation;
    LinearLayout g1_layout, g2_layout, g3_layout, local_ads_layout;
    TextView title1_txtview, title2_txtview, title3_txtview, more_from_dev_txtview;
    private String title2, title3, title1;
    private Drawable drawable;
    private String packageName;
    private Drawable drawableG1, drawableG2, drawableG3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_chat);
        shimmer_animation = findViewById(R.id.ad_loader);
        shimmer_animation.startShimmerAnimation();
        load_adaptive_Banner();
        ccp = findViewById(R.id.ccp);
        number = findViewById(R.id.nmber);
        meesage = findViewById(R.id.message);
        ccp.registerCarrierNumberEditText(number);
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


    public void onCountryPickerClick(View view) {
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                //Alert.showMessage(RegistrationActivity.this, ccp.getSelectedCountryCodeWithPlus());
                String selected_country_code = ccp.getSelectedCountryCodeWithPlus();
            }
        });
    }
    public void selecPhone(){
        ccp.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
                validNUmber = ccp.getFormattedFullNumber();
            }
        });
    }
    public void onSendClick(View view){
        selecPhone();
        String txt = meesage.getText().toString();
        if (TextUtils.isEmpty(validNUmber)) {
            number.setError("Please enter valid number");
            return;
        }
        else {
            PackageManager packageManager = getPackageManager();
            Intent i = new Intent(Intent.ACTION_VIEW);
            try {
                String url = "https://api.whatsapp.com/send?phone=" + validNUmber + "&text=" + URLEncoder.encode(txt, "UTF-8");
                i.setPackage("com.whatsapp");
                i.setData(Uri.parse(url));
                if (i.resolveActivity(packageManager) != null) {
                    startActivity(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void onBackClick(View view){
        finish();
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
                frameLayout_1.setVisibility(View.VISIBLE);
                shimmer_animation.setVisibility(View.GONE);
                shimmer_animation.stopShimmerAnimation();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                frameLayout_1.setVisibility(View.VISIBLE);
                shimmer_animation.setVisibility(View.GONE);
                shimmer_animation.stopShimmerAnimation();
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
}