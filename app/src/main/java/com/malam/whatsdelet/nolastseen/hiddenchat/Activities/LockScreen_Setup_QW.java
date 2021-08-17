package com.malam.whatsdelet.nolastseen.hiddenchat.Activities;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.Shared;
import com.whatsdelete.Test.R;


public class LockScreen_Setup_QW extends AppCompatActivity implements View.OnClickListener {
    RelativeLayout number1, number2, number3, number4, number5, number6, number7, number8, number9, number0, cross;
    private String enterPIN = "";
    ImageView imagePass1, imagePass2, imagePass3, imagePass4;

    private int numberSet = 1;
    private String correctPIN;
    private TextView textViewEnterPIN;
    Boolean wrongpin = false, wrongnewpin = false;
    private String tempPIN;
    private Handler handler;
    Boolean pin_correct = false, dark_mode;
    private FrameLayout adContainerView;
    private AdView adaptive_adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.dark_mode_pref), false, getApplicationContext())) {

            setContentView(R.layout.activity_lock_screen_dark);
            dark_mode = true;
        } else {
            setContentView(R.layout.activity_lock_screen);
            dark_mode = false;
        }
        load_adaptive_Banner();
        gettting_prefrence();
        initilize_componenets();
        if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.pin_set_pref), false, this)) {
            textViewEnterPIN.setText(getResources().getString(R.string.enter_old_pin));
        }

        handler = new Handler(new MyClass());
    }

    private void gettting_prefrence() {

        correctPIN = Shared.getInstance().getStringValueFromPreference(getResources().getString(R.string.pin_pref), "0000", LockScreen_Setup_QW.this);

    }

    class MyClass implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg != null) {
                switch (msg.what) {
                    case SwipeRefreshLayout.LARGE:
                        enterPIN = "";
                        imagePass1.setImageResource(R.mipmap.open_pass);
                        imagePass2.setImageResource(R.mipmap.open_pass);
                        imagePass3.setImageResource(R.mipmap.open_pass);
                        imagePass4.setImageResource(R.mipmap.open_pass);
                        if (dark_mode)
                            textViewEnterPIN.setTextColor(getResources().getColor(R.color.white));
                        else
                            textViewEnterPIN.setTextColor(getResources().getColor(R.color.black));
                        if (wrongpin)
                            textViewEnterPIN.setText(getResources().getString(R.string.enter_pin));
                        break;

                }
            }
            return false;
        }
    }

    private void load_adaptive_Banner() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        adContainerView = findViewById(R.id.ad_view_container);
        adaptive_adView = new AdView(this);
        adaptive_adView.setAdUnitId(getString(R.string.bannerAd));
        adContainerView.addView(adaptive_adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(getResources().getString(R.string.zegar_device)).addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        AdSize adSize = getAdSize();
        // Step 4 - Set the adaptive ad size on the ad view.
        adaptive_adView.setAdSize(adSize);
        // Step 5 - Start loading the ad in the background.
        adaptive_adView.loadAd(adRequest);
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
    private void initilize_componenets() {
        imagePass1 = (ImageView) findViewById(R.id.imagePass1);
        imagePass2 = (ImageView) findViewById(R.id.imagePass2);
        imagePass3 = (ImageView) findViewById(R.id.imagePass3);
        imagePass4 = (ImageView) findViewById(R.id.imagePass4);
        number1 = (RelativeLayout) findViewById(R.id.number1);
        number2 = (RelativeLayout) findViewById(R.id.number2);
        number3 = (RelativeLayout) findViewById(R.id.number3);
        number4 = (RelativeLayout) findViewById(R.id.number4);
        number5 = (RelativeLayout) findViewById(R.id.number5);
        number6 = (RelativeLayout) findViewById(R.id.number6);
        number7 = (RelativeLayout) findViewById(R.id.number7);
        number8 = (RelativeLayout) findViewById(R.id.number8);
        number9 = (RelativeLayout) findViewById(R.id.number9);
        number0 = (RelativeLayout) findViewById(R.id.number0);
        cross = (RelativeLayout) findViewById(R.id.cross);


        textViewEnterPIN = (TextView) findViewById(R.id.textViewEnterPin);
        imagePass1 = (ImageView) findViewById(R.id.imagePass1);
        imagePass2 = (ImageView) findViewById(R.id.imagePass2);
        imagePass3 = (ImageView) findViewById(R.id.imagePass3);
        imagePass4 = (ImageView) findViewById(R.id.imagePass4);

        number1.setOnClickListener(this);
        number2.setOnClickListener(this);
        number3.setOnClickListener(this);
        number4.setOnClickListener(this);
        number5.setOnClickListener(this);
        number6.setOnClickListener(this);
        number7.setOnClickListener(this);
        number8.setOnClickListener(this);
        number9.setOnClickListener(this);
        number0.setOnClickListener(this);
        cross.setOnClickListener(this);
    }

    private void verifyPINEnter(String number) {
        if (number != null && enterPIN != null) {
            enterPIN += number;
            int lengthEnterPin = enterPIN.length();
            switch (lengthEnterPin) {
                case 1:
                    imagePass1.setImageResource(R.mipmap.closed_pass);
                    cross.setVisibility(View.VISIBLE);
                    /*start_sound();*/
                    break;
                case 2:
                    imagePass1.setImageResource(R.mipmap.closed_pass);
                    imagePass2.setImageResource(R.mipmap.closed_pass);
                    /*start_sound();*/
                    break;
                case 3:
                    imagePass1.setImageResource(R.mipmap.closed_pass);
                    imagePass2.setImageResource(R.mipmap.closed_pass);
                    imagePass3.setImageResource(R.mipmap.closed_pass);
                    /*start_sound();*/
                    break;
                case 4:
                    imagePass1.setImageResource(R.mipmap.closed_pass);
                    imagePass2.setImageResource(R.mipmap.closed_pass);
                    imagePass3.setImageResource(R.mipmap.closed_pass);
                    imagePass4.setImageResource(R.mipmap.closed_pass);
                    /*start_sound();*/
                    break;
            }
            if (!Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.pin_set_pref), false, LockScreen_Setup_QW.this)) {
                if (lengthEnterPin == 4) {
                    if (numberSet == 1) {
                        tempPIN = enterPIN;
                        numberSet++;
                        wrongpin = false;
                        enterPIN = "";
                        handler.sendEmptyMessageDelayed(0, 500);
                        numberSet = 2;

                        textViewEnterPIN.setText(getResources().getString(R.string.confirm_pin_code));
                        cross.setVisibility(View.INVISIBLE);
                    } else if (numberSet == 2) {
                        if (tempPIN.equalsIgnoreCase(enterPIN)) {
                            Toast.makeText(LockScreen_Setup_QW.this, "PIN saved" , Toast.LENGTH_SHORT).show();
                            Shared.getInstance().saveStringToPreferences(getResources().getString(R.string.pin_pref), enterPIN, LockScreen_Setup_QW.this);
                            Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.pin_set_pref), true, LockScreen_Setup_QW.this);

                            finish();
                        } else {
                            enterPIN = "";
                            Toast.makeText(LockScreen_Setup_QW.this, getResources().getString(R.string.pin_didnot_match), Toast.LENGTH_SHORT).show();
                            handler.sendEmptyMessageDelayed(0, 500);
                        }

                        cross.setVisibility(View.INVISIBLE);
                    }
                }
            } else {
                if (pin_correct) {
                    if (lengthEnterPin == 4) {
                        if (numberSet == 1) {
                            tempPIN = enterPIN;
                            numberSet++;
                            wrongpin = false;
                            enterPIN = "";
                            handler.sendEmptyMessageDelayed(0, 500);
                            numberSet = 2;

                            textViewEnterPIN.setText(getResources().getString(R.string.confirm_pin_code));
                        } else if (numberSet == 2) {
                            if (tempPIN.equalsIgnoreCase(enterPIN)) {
                                Toast.makeText(LockScreen_Setup_QW.this, "PIN saved" , Toast.LENGTH_SHORT).show();
                                Shared.getInstance().saveStringToPreferences(getResources().getString(R.string.pin_pref), enterPIN, LockScreen_Setup_QW.this);
                                finish();
                            } else {
                                enterPIN = "";
                                Toast.makeText(LockScreen_Setup_QW.this, "PIN didnot match", Toast.LENGTH_SHORT).show();
                                handler.sendEmptyMessageDelayed(0, 500);
                            }
                        }
                    }
                } else if (lengthEnterPin == 4 && enterPIN.equals(correctPIN)) {
                    textViewEnterPIN.setText("Create New Pin Code");
                    if (numberSet == 1) {
                        numberSet = 2;
                        tempPIN = enterPIN;
                        correctPIN = enterPIN;

                        enterPIN = "";
                        wrongpin = false;
                        handler.sendEmptyMessageDelayed(0, 500);
                    }
                    pin_correct = true;
                    correctPIN = enterPIN;
                    numberSet = 1;
                } else if (lengthEnterPin >= 4) {
                    enterPIN = "";
                    wrongpin = true;
                    handler.sendEmptyMessageDelayed(0, 500);
                    textViewEnterPIN.setText(R.string.wrong_pin);
                    if (Build.VERSION.SDK_INT >= 23) {
                        textViewEnterPIN.setTextColor(getResources().getColor(R.color.pattern_wrong, getTheme()));
                    } else {
                        textViewEnterPIN.setTextColor(getResources().getColor(R.color.pattern_wrong));
                    }
                    cross.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.number1:
                verifyPINEnter("1");
                return;
            case R.id.number2:
                verifyPINEnter("2");
                return;
            case R.id.number3:
                verifyPINEnter("3");
                return;
            case R.id.number4:
                verifyPINEnter("4");
                return;
            case R.id.number5:
                verifyPINEnter("5");
                return;
            case R.id.number6:
                verifyPINEnter("6");
                return;
            case R.id.number7:
                verifyPINEnter("7");
                return;
            case R.id.number8:
                verifyPINEnter("8");
                return;
            case R.id.number9:
                verifyPINEnter("9");
                return;
            case R.id.number0:
                verifyPINEnter("0");
                return;
            case R.id.cross:
                enterPIN = method(enterPIN);
                number_deleted();
                return;
            default:
                return;
        }
    }

    public void number_deleted() {
        int lengthEnterPin = enterPIN.length();
        switch (lengthEnterPin) {
            case 0:
                imagePass1.setImageResource(R.mipmap.open_pass);
                imagePass2.setImageResource(R.mipmap.open_pass);
                imagePass3.setImageResource(R.mipmap.open_pass);
                imagePass4.setImageResource(R.mipmap.open_pass);
                cross.setVisibility(View.INVISIBLE);

                break;
            case 1:
                imagePass1.setImageResource(R.mipmap.closed_pass);
                imagePass2.setImageResource(R.mipmap.open_pass);
                imagePass3.setImageResource(R.mipmap.open_pass);
                imagePass4.setImageResource(R.mipmap.open_pass);
                break;
            case 2:
                imagePass1.setImageResource(R.mipmap.closed_pass);
                imagePass2.setImageResource(R.mipmap.closed_pass);
                imagePass3.setImageResource(R.mipmap.open_pass);
                imagePass4.setImageResource(R.mipmap.open_pass);
                break;
            case 3:
                imagePass1.setImageResource(R.mipmap.closed_pass);
                imagePass2.setImageResource(R.mipmap.closed_pass);
                imagePass3.setImageResource(R.mipmap.closed_pass);
                imagePass4.setImageResource(R.mipmap.open_pass);
                break;
            case 4:
                imagePass1.setImageResource(R.mipmap.closed_pass);
                imagePass2.setImageResource(R.mipmap.closed_pass);
                imagePass3.setImageResource(R.mipmap.closed_pass);
                imagePass4.setImageResource(R.mipmap.closed_pass);
                break;
        }
    }

    public String method(String str) {
        try {
            if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == 'x') {
            }
            str = str.substring(0, str.length() - 1);
        } catch (StringIndexOutOfBoundsException e) {
        } catch (Exception e) {
        }
        return str;
    }
}