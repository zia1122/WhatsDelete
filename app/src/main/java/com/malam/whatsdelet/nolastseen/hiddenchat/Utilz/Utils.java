package com.malam.whatsdelet.nolastseen.hiddenchat.Utilz;


import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.PersistableBundle;
import android.provider.Settings;

import androidx.annotation.RequiresApi;

public class Utils {
	public static String LogTag = "henrytest";
	public static String EXTRA_MSG = "extra_msg";
	public static String EXTRA_TITLE = "extra_title";
	public static String EXTRA_IS_MESSENGER = "extra_is_messenger";
	public static String EXTRA_IS_INSTA = "extra_is_insta";

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public static void scheduleJob(Context context,String title,String message,Boolean is_messenger,Boolean is_insta) {
	ComponentName serviceComponent = new ComponentName(context, JobSchedulaer.class);
	PersistableBundle bundle = new PersistableBundle();
	bundle.putString(Utils.EXTRA_TITLE, title);
	bundle.putString(Utils.EXTRA_MSG, message);
	bundle.putBoolean(Utils.EXTRA_IS_MESSENGER, is_messenger);
	bundle.putBoolean(Utils.EXTRA_IS_INSTA, is_insta);
	JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent).setExtras(bundle);
	builder.setMinimumLatency(1000); // wait at least
	builder.setOverrideDeadline(5 * 1000); // maximum delay
	//builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
	//builder.setRequiresDeviceIdle(true); // device should be idle
	//builder.setRequiresCharging(false); // we don't care if the device is charging or not
	JobScheduler jobScheduler = null;
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
		jobScheduler = context.getSystemService(JobScheduler.class);
	}
	if (jobScheduler != null) {
		jobScheduler.schedule(builder.build());
	}
}




}
