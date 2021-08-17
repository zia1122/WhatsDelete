package com.malam.whatsdelet.nolastseen.hiddenchat.Utilz;

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;


import androidx.core.content.ContextCompat;

import com.malam.whatsdelet.nolastseen.hiddenchat.Service.FilesData;
import com.whatsdelete.Test.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;


public class Shared
	{

		SharedPreferences sharedPreferences;
		SharedPreferences.Editor shareEditor;

		public final String[] SMS = {Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE};
		public final String[] PHONE = {Manifest.permission.CAMERA};
		public final String[] CAMERA = {Manifest.permission.CAMERA};
		public Drawable drawableG1, drawableG2, drawableG3;
		public String gridPackage1, gridPackage2, gridPackage3,title1,title2,title3;


		public static String saveAndRefreshFiles(File sourceFile) throws IOException {

			File destFile = new File(Environment.getExternalStorageDirectory().toString()+ FilesData.SAVED_FILES_LOCATION,sourceFile.getName());

			if (!destFile.getParentFile().exists())
				destFile.getParentFile().mkdirs();

			if (!destFile.exists()) {
				destFile.createNewFile();
			}

			FileChannel source = null;
			FileChannel destination = null;

			try {
				source = new FileInputStream(sourceFile).getChannel();
				destination = new FileOutputStream(destFile).getChannel();
				destination.transferFrom(source, 0, source.size());
				 return String.valueOf(destFile);
			} finally {
				if (source != null) {
					source.close();
				}
				if (destination != null) {
					destination.close();
				}
			}



		}


		public boolean hasPermissionsGranted(String[] permission, Context context)
			{
				for (int i = 0; i < permission.length; i++)
					{
						if (ContextCompat.checkSelfPermission(context, permission[i]) != PackageManager.PERMISSION_GRANTED)
							{
								return false;
							}
					}
				return true;
			}

		private static Shared ourInstance = new Shared();

		public static Shared getInstance()
			{
				return ourInstance;
			}

		private Shared()
			{
			}

		public void saveIntToPreferences(String key, int value, Context context)
			{
				if (sharedPreferences == null)
					{
						sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_pref_name), 0);
					}
				shareEditor = sharedPreferences.edit();
				shareEditor.putInt(key, value);
				shareEditor.commit();
			}

		public int getIntValueFromPreference(String key, int defaultValue, Context context)
			{
				if (sharedPreferences == null)
					{
						sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_pref_name), 0);
					}

				return sharedPreferences.getInt(key, defaultValue);

			}
		public boolean isAccessibilityEnabled(Context context, String id)
		{
			AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);

			List<AccessibilityServiceInfo> runningServices = null;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				runningServices = am.getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);

				for (AccessibilityServiceInfo service : runningServices)
				{
					if (id.equals(service.getId()))
					{
						return true;
					}
				}
			}


			return false;
		}

		public void saveStringToPreferences(String key, String value, Context context)
			{
				if (sharedPreferences == null)
					{
						sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_pref_name), 0);
					}

				shareEditor = sharedPreferences.edit();
				shareEditor.putString(key, value);
				shareEditor.commit();
			}

		public void saveBooleanToPreferences(String key, Boolean value, Context context)
			{
				if (sharedPreferences == null)
					{
						sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_pref_name), 0);
					}
				shareEditor = sharedPreferences.edit();
				shareEditor.putBoolean(key, value);
				shareEditor.commit();
			}

		public void saveLongToPreferences(String key, long value, Context context)
			{
				if (sharedPreferences == null)
					{
						sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_pref_name), 0);
					}
				shareEditor = sharedPreferences.edit();
				shareEditor.putLong(key, value);
				shareEditor.commit();
			}

		public boolean containsKey(String key, Context context)
			{
				if (sharedPreferences == null)
					{
						sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_pref_name), 0);
					}
				return sharedPreferences.contains(key);
			}

		public Boolean getBooleanValueFromPreference(String key, boolean defaultValue, Context context)
			{
				if (sharedPreferences == null)
					{
						sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_pref_name), 0);
					}

				return sharedPreferences.getBoolean(key, defaultValue);

			}

		public long getLongValueFromPreference(String key, long defaultValue, Context context)
			{
				if (sharedPreferences == null)
					{
						sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_pref_name), 0);
					}

				return sharedPreferences.getLong(key, defaultValue);
			}

		public String getStringValueFromPreference(String key, String defaultValue, Context context)
			{
				if (sharedPreferences == null)
					{
						sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_pref_name), 0);
					}

				return sharedPreferences.getString(key, defaultValue);
			}

		public void initPref(Context context)
			{
				sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_pref_name), 0);
			}



		public void startInstalledAppDetailsActivity(final Activity context)
			{
				if (context == null)
					{
						return;
					}
				final Intent i = new Intent();
				i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
				i.addCategory(Intent.CATEGORY_DEFAULT);
				i.setData(Uri.parse("package:" + context.getPackageName()));
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				context.startActivityForResult(i, 200);
			}

		public void shareApplication(Activity context)
			{

				Intent sendIntent = new Intent();
				sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
				sendIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_TEXT, "Please download this Flash on Call and SMS from \n" + "https://play.google.com/store/apps/details?id=" + getPackageName(context));
				sendIntent.setType("text/plain");
				context.startActivity(Intent.createChooser(sendIntent, "Share Link via"));
			}

		public void like(Activity context)
			{
				try
					{
						context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName(context))));
					}
				catch (android.content.ActivityNotFoundException anfe)
					{
						context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName(context))));
					}
			}

		public String getPackageName(Activity context)
			{
				return context.getPackageName().toString();
			}

		public ObjectAnimator setAnim(ImageView imageView)
			{
				ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(imageView, PropertyValuesHolder.ofFloat("scaleX", 1.1f), PropertyValuesHolder.ofFloat("scaleY", 1.1f));
				objectAnimator.setDuration(750);
				objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
				objectAnimator.setRepeatMode(ObjectAnimator.REVERSE);

				return objectAnimator;
			}






	}
