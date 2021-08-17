package com.malam.whatsdelet.nolastseen.hiddenchat.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.Shared;
import com.whatsdelete.Test.R;


public class Lock_Screen_QW extends AppCompatActivity implements View.OnClickListener {

ImageView imagePass1, imagePass2, imagePass3, imagePass4;
RelativeLayout number1, number2, number3, number4, number5, number6, number7, number8, number9, number0, cross;

private String correctPIN,title;
private String enterPIN="";
private Handler handler;
private TextView textViewEnterPIN,date;
private static final int MESSAGE_DESTROY_SERVICE = 1;
Boolean  dark_mode, open_whatsapp_act = false, open_main_act = false,open_messenger_act = false,open_insta_act = false;
int position=0;
int intent_position=0;
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
    iniitlize_components();
    getting_preference();
    try {
        intent_position = getIntent().getIntExtra(getResources().getString(R.string.is_image_intent),
                0);
    } catch (Exception asd) {
    }
    try {
        open_whatsapp_act = getIntent().getBooleanExtra(getResources().getString(R.string.open_whatsapp_act), false);
    } catch (Exception asd) {
    }    try {
        open_main_act = getIntent().getBooleanExtra(getResources().getString(R.string.open_mainactivity), false);
    } catch (Exception asd) {
    }
    try {
        open_messenger_act = getIntent().getBooleanExtra(getResources().getString(R.string.open_messenger_act), false);
    } catch (Exception asd) {
    }
    try {
        open_insta_act = getIntent().getBooleanExtra(getResources().getString(R.string.open_insta_act), false);
    } catch (Exception asd) {
    }
    try {
        title = getIntent().getStringExtra(getResources().getString(R.string.contact_name_pref));
    } catch (Exception asd) {
    }
    try {
        position = getIntent().getIntExtra(getResources().getString(R.string.chat_position_from_chathead),0);
    } catch (Exception asd) {
    }
    handler = new Handler(new MyClass());
}

private void getting_preference() {
    correctPIN= Shared.getInstance().getStringValueFromPreference(getResources().getString(R.string.pin_pref),"0000", Lock_Screen_QW.this);
}

private void iniitlize_components() {
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
                
                break;
            case 2:
                imagePass1.setImageResource(R.mipmap.closed_pass);
                imagePass2.setImageResource(R.mipmap.closed_pass);
                
                break;
            case 3:
                imagePass1.setImageResource(R.mipmap.closed_pass);
                imagePass2.setImageResource(R.mipmap.closed_pass);
                imagePass3.setImageResource(R.mipmap.closed_pass);
                
                break;
            case 4:
                imagePass1.setImageResource(R.mipmap.closed_pass);
                imagePass2.setImageResource(R.mipmap.closed_pass);
                imagePass3.setImageResource(R.mipmap.closed_pass);
                imagePass4.setImageResource(R.mipmap.closed_pass);
                
                break;
        }
        if (lengthEnterPin == 4 && enterPIN.equals(correctPIN)) {
            if (open_main_act){
                startActivity(new Intent(Lock_Screen_QW.this, MainActivity.class).
                                                                                         putExtra(getResources().getString(R.string.is_image_intent),intent_position));
                finish();
            }else if (open_whatsapp_act){
                startActivity(new Intent(Lock_Screen_QW.this, Contact_Chat_Act_fro_Notification.class).putExtra(getResources().getString(R.string.contact_name_pref),title));
                finish();
            }else if (open_messenger_act){
                Shared.getInstance().saveStringToPreferences(getResources().getString(R.string.contact_name_pref),title,getApplicationContext());
                startActivity(new Intent(Lock_Screen_QW.this, Contact_Chat_Act_Messenger_for_notification.class).putExtra(getResources().getString(R.string.contact_name_pref),title));
                finish();
            }
            else if (open_insta_act){
                
                Shared.getInstance().saveStringToPreferences(getResources().getString(R.string.contact_name_pref),title,getApplicationContext());
                startActivity(new Intent(Lock_Screen_QW.this, Contact_Chat_Act_Instagram.class).putExtra(getResources().getString(R.string.contact_name_pref),title));
                finish();
            }
            else {
                startActivity(new Intent(Lock_Screen_QW.this, MainActivity.class).putExtra(getResources().getString(R.string.contact_name_pref),title).putExtra(getResources().getString(R.string.chat_position_from_chathead),position));
                finish();
                
            }
        } else if (lengthEnterPin >= 4) {
            enterPIN = "";
            handler.sendEmptyMessageDelayed(0, 500);
            textViewEnterPIN.setText(R.string.wrong_pin);
            if (Build.VERSION.SDK_INT >= 23) {
                textViewEnterPIN.setTextColor(getResources().getColor(R.color.pattern_wrong, getTheme()));
            } else {
                textViewEnterPIN.setTextColor(getResources().getColor(R.color.pattern_wrong));
            }
            cross.setVisibility(View.INVISIBLE);
            
            try {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (v != null) {
                    v.vibrate(300);
                }
            }catch (NullPointerException e){}
            
        }
    }
}
@Override
public void onClick(View view) {
    if (view !=null){
        switch (view.getId()) {
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
                enterPIN= method(enterPIN);
                number_deleted();
                return;
            default:
                return;
        }
        
    }
    
}
public String method(String str) {
    try {
        if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == 'x') {}
        
        str = str.substring(0, str.length() - 1);
        
    }catch (StringIndexOutOfBoundsException e){}
    catch (Exception e){}
    
    
    return str;
}
public void number_deleted(){
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

@Override
public void onBackPressed() {
    super.onBackPressed();
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
                    textViewEnterPIN.setText(R.string.enter_pin);
                    if (dark_mode)
                        textViewEnterPIN.setTextColor(getResources().getColor(R.color.white));
                    else
                        textViewEnterPIN.setTextColor(getResources().getColor(R.color.black));
                    break;
                case MESSAGE_DESTROY_SERVICE:
                    startActivity(new Intent(Lock_Screen_QW.this,MainActivity.class));
                    finish();
                    break;
            }
        }
        return false;
    }
}
}