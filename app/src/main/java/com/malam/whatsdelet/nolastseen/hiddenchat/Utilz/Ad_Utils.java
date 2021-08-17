package com.malam.whatsdelet.nolastseen.hiddenchat.Utilz;

import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;

public class Ad_Utils {

    public static String gridPackage1, gridPackage2, gridPackage3;
    public  static String title1, title2, title3;
   public static Drawable drawableG1, drawableG2, drawableG3;
public void counter(int millisecs){
    new CountDownTimer(millisecs, 1000) {

        public void onTick(long millisUntilFinished) {
            //here you can have your logic to set text to edittext
        }

        public void onFinish() {
        }

    }.start();
}


}
