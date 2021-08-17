package com.malam.whatsdelet.nolastseen.hiddenchat.Utilz;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.malam.whatsdelet.nolastseen.hiddenchat.Service.ChatHead;
import com.whatsdelete.Test.R;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobSchedulaer extends JobService {
private static final String TAG = "SyncService";

@Override
public boolean onStartJob(JobParameters params) {

    if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.open_chat_head_service_pref),false,getApplicationContext())){

    }
    else {
        return false;
    }
    String title= params.getExtras().getString(Utils.EXTRA_TITLE);
    String message= params.getExtras().getString(Utils.EXTRA_MSG);
    Boolean IS_MESSENGER=params.getExtras().getBoolean(Utils.EXTRA_IS_MESSENGER);
    Boolean IS_INSTA=params.getExtras().getBoolean(Utils.EXTRA_IS_INSTA);
    Intent service = new Intent(getApplicationContext(), ChatHead.class).putExtra(Utils.EXTRA_TITLE,title)
            .putExtra(Utils.EXTRA_MSG,message).putExtra(Utils.EXTRA_IS_MESSENGER,IS_MESSENGER).putExtra(Utils.EXTRA_IS_INSTA,IS_INSTA);
    ContextCompat.startForegroundService(getApplicationContext(),service);
    Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.open_chat_head_service_pref),false,getApplicationContext());
    /*Utils.scheduleJob(getApplicationContext());*/ // reschedule the job
    return true;
}

@Override
public boolean onStopJob(JobParameters params) {
    return true;
}
}
