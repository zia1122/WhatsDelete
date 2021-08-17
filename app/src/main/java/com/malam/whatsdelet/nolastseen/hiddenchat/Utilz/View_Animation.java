package com.malam.whatsdelet.nolastseen.hiddenchat.Utilz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

public class View_Animation {
    public static boolean rotateFab(final View v, boolean rotate) {
        v.animate().setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                })
                .rotation(rotate ? 135f : 0f);
        return rotate;
}
}
