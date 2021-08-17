package com.malam.whatsdelet.nolastseen.hiddenchat.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.Shared;
import com.whatsdelete.Test.R;

public class Promotion_act extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (NullPointerException asd) {
        }
        setContentView(R.layout.activity_promotion_act);
        String packg_name = getIntent().getStringExtra("package_name");

        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri
                    .parse("market://details?id=" + packg_name)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id="
                            + packg_name)));
        }
        Shared.getInstance().saveStringToPreferences(getResources().getString(R.string.promotion_package_name_pref),null,getApplicationContext());
        finish();
    }
}