package com.malam.whatsdelet.nolastseen.hiddenchat.Service;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.whatsdelete.Test.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class FilesData {

    public static final String WHATSAPP_IMAGES_LOCATION = "/WhatsApp/Media/WhatsApp Images/";
    public static final String WHATSAPP_IMAGES_LOCATION_ANDROID_11 = "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Images";

    public static final String WHATSAPP_VIDEOS_LOCATION = "/WhatsApp/Media/WhatsApp Video/";
    public static final String WHATSAPP_VIDEOS_LOCATION_ANDROID_11 = "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Video";

    public static final String WHATSAPP_AUDIOS_LOCATION = "/WhatsApp/Media/WhatsApp Audio/";
    public static final String WHATSAPP_AUDIOS_LOCATION_ANDROID_11 = "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Audio";

    public static final String WHATSAPP_DOCUMENTS_LOCATION = "/WhatsApp/Media/WhatsApp Documents/";
    public static final String WHATSAPP_DOCUMENTS_LOCATION_ANDROID_11 = "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Documents/";

    public static final String WHATSAPP_VOICES_LOCATION = "/WhatsApp/Media/WhatsApp Voice Notes/";
    public static final String WHATSAPP_VOICES_LOCATION_ANDROID_11 = "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Voice Notes/";

    public static final String SAVED_FILES_LOCATION = "/.whatsdelet Media";
    public static final String SAVED_Status_LOCATION = "/.whatsdelet Media/whatsdelet Statuses";
    public static final String DELETED_MEDIA = "/.DELETED_MEDIA";
    public static final String DELETED_MEDIA_Android_11 = "/DELETED_MEDIA";

    public static final String WHATSAPP_STATUSES_LOCATION = "/WhatsApp/Media/.Statuses/";
    public static final String WHATSAPP_STATUSES_LOCATION_ANDROID_11 = "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses";
    public static final String HAS_PERMISSIONS_ANDROID_11 = "android 11 permissions";
    public static final String STATUSES_FOLDER_PATH = "statuses folder path";

    public static final String WHATSAPP_LOCATION = "/WhatsApp/";

    private static final ArrayList<File> whatsAppFilesImages = new ArrayList<>();
    private static final ArrayList<File> whatsAppFilesVideos = new ArrayList<>();
    private static final ArrayList<File> whatsAppFilesAudios = new ArrayList<>();
    private static final ArrayList<File> whatsAppFilesDocuments = new ArrayList<>();
    private static final ArrayList<File> whatsAppFilesVoices = new ArrayList<>();
    private static final ArrayList<File> savedFilesImages = new ArrayList<>();
    private static final ArrayList<File> savedFilesVideos = new ArrayList<>();
    private static final ArrayList<File> savedFilesVideosAudios = new ArrayList<>();
    private static final ArrayList<File> deleted_media = new ArrayList<>();
    private static String recentOrSaved;
    static boolean founded = false;
    public static Context context;


    public static void scrapSavedFiles() {
        savedFilesImages.clear();
        File[] files;
        File parentDir = new File(Environment.getExternalStorageDirectory().toString() + SAVED_FILES_LOCATION);
        if (!parentDir.exists())
            parentDir.mkdirs();
        files = parentDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".jpg")) {
                    if (!savedFilesImages.contains(file))
                        savedFilesImages.add(file);
                } else if (file.getName().endsWith(".gif") ||
                        file.getName().endsWith(".mp4")) {
                    if (!savedFilesVideos.contains(file))
                        savedFilesVideos.add(file);
                }
            }
        }
    }

    public static ArrayList<File> getSavedFilesImages() {
        return savedFilesImages;
    }

    public static ArrayList<File> getSavedFilesVideos() {
        return savedFilesVideos;
    }

    public static ArrayList<File> getWhatsAppFilesImages() {
        return whatsAppFilesImages;
    }

    public static ArrayList<File> getWhatsAppFilesVideos() {
        return whatsAppFilesVideos;
    }

    public static String getRecentOrSaved() {
        return recentOrSaved;
    }

    public static void setRecentOrSaved(String a) {
        recentOrSaved = a;
    }

    public static String getSavedFilesLocation(Context context) {
        return SAVED_FILES_LOCATION;
    }

    public static String getDeletedFilesLocation() {
        return DELETED_MEDIA;
    }


    public static void getDeletedMedia(String file) {
        File deletedpDir = new File(Environment.getExternalStorageDirectory().toString() + DELETED_MEDIA);
        deletedpDir = new File(context.getExternalFilesDir(FilesData.DELETED_MEDIA).toString());
        if (!deletedpDir.exists())
            deletedpDir.mkdirs();
        try {
            File imageFile = new File(Environment.getExternalStorageDirectory().toString() + FilesData.getSavedFilesLocation(context), file);
            File destFile = new File(Environment.getExternalStorageDirectory().toString() + FilesData.getDeletedFilesLocation(), file);
            FileOperations.saveAndRefreshFiles(imageFile, destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent saved_intent = new Intent(context.getString(R.string.intent_file_saved_images));
        LocalBroadcastManager.getInstance(context).sendBroadcast(saved_intent);
    }


    public static void getDeletedMediaVideio(String file) {
        File deletedpDir = new File(Environment.getExternalStorageDirectory().toString() + DELETED_MEDIA);
        if (!deletedpDir.exists())
            deletedpDir.mkdirs();
        try {
            File imageFile = new File(Environment.getExternalStorageDirectory().toString() + FilesData.getSavedFilesLocation(context), file);
            File destFile = new File(Environment.getExternalStorageDirectory().toString() + FilesData.getDeletedFilesLocation(), file);
            FileOperations.saveAndRefreshFiles(imageFile, destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent saved_intent = new Intent(context.getString(R.string.intent_file_saved_videos));
        LocalBroadcastManager.getInstance(context).sendBroadcast(saved_intent);
    }

    public static void getDeletedVideio_whatsapp_folder(String file) {

        File deletedpDir = new File(Environment.getExternalStorageDirectory().toString() + DELETED_MEDIA);
        if (!deletedpDir.exists())
            deletedpDir.mkdirs();


        try {
            File imageFile = new File(Environment.getExternalStorageDirectory().toString() + FilesData.WHATSAPP_VIDEOS_LOCATION, file);
            File destFile = new File(Environment.getExternalStorageDirectory().toString() + FilesData.SAVED_FILES_LOCATION, file);
            FileOperations.saveAndRefreshFiles(imageFile, destFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent saved_intent = new Intent(context.getString(R.string.intent_file_saved_videos));
        LocalBroadcastManager.getInstance(context).sendBroadcast(saved_intent);

    }

    public static void getDeletedMediaAudio(String file) {
        File deletedpDir = new File(Environment.getExternalStorageDirectory().toString() + DELETED_MEDIA);
        if (!deletedpDir.exists())
            deletedpDir.mkdirs();
        try {
            File imageFile = new File(Environment.getExternalStorageDirectory().toString() + FilesData.getSavedFilesLocation(context), file);
            File destFile = new File(Environment.getExternalStorageDirectory().toString() + FilesData.getDeletedFilesLocation(), imageFile.getName());
            FileOperations.saveAndRefreshFiles(imageFile, destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent saved_intent = new Intent(context.getString(R.string.intent_file_saved_images));
        LocalBroadcastManager.getInstance(context).sendBroadcast(saved_intent);
    }

    public static void getDeletedMediaStatus(String file) {
        File deletedpDir = new File(Environment.getExternalStorageDirectory().toString() + WHATSAPP_STATUSES_LOCATION);
        if (!deletedpDir.exists())
            deletedpDir.mkdirs();
        try {
            File imageFile = new File(Environment.getExternalStorageDirectory().toString() + FilesData.getSavedFilesLocation(context), file);
            File destFile = new File(Environment.getExternalStorageDirectory().toString() + FilesData.getDeletedFilesLocation(), imageFile.getName());
            FileOperations.saveAndRefreshFiles(imageFile, destFile);


        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent saved_intent = new Intent(context.getString(R.string.intent_file_saved_images));
        LocalBroadcastManager.getInstance(context).sendBroadcast(saved_intent);

    }

    public static void getDeletedMediaVoice(String file) {
        File deletedpDir = new File(Environment.getExternalStorageDirectory().toString() + DELETED_MEDIA);
        if (!deletedpDir.exists())
            deletedpDir.mkdirs();
        try {
            File imageFile = new File(Environment.getExternalStorageDirectory().toString()
                    + FilesData.getSavedFilesLocation(context), file);
            File destFile = new File(Environment.getExternalStorageDirectory().toString()
                    + FilesData.getDeletedFilesLocation(), imageFile.getName());
        /* String[] result=destFile.toString().split("/");
         String[] result_dot=result[result.length-1].toString().split("\\.");
          String newname="/"+result_dot[0]+".ogg";
        destFile=new File(deletedpDir+newname);*/
            FileOperations.saveAndRefreshFiles(imageFile, destFile);
            //redeclaration of file here is not needed, but added for clarity
            File from = new File(destFile.getAbsolutePath());
            //what you are renaming the file to
            File to = new File(deletedpDir, file.replace(".opus", ".ogg"));
            //now rename
            Boolean success = from.renameTo(to);
            if (success) {
                Log.e("success", "rename");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent saved_intent = new Intent(context.getString(R.string.intent_file_saved_images));
        LocalBroadcastManager.getInstance(context).sendBroadcast(saved_intent);

    }

    private static String getfolder(File dir) {
        int name = 0;
        File[] listFile = dir.listFiles();
        String returnr = listFile[0].toString();
        for (int i = 0; i < listFile.length; i++) {
            String name_string = listFile[i].toString();
            String[] index = name_string.split("/");
            if (((index[index.length - 1])).matches("\\d+(?:\\.\\d+)?")) {
                if (name < Integer.parseInt(index[index.length - 1])) {
                    name = Integer.parseInt(index[index.length - 1]);
                    returnr = listFile[i].toString();
                }
            }
        }
        return returnr;
    }


    public static void scrapWhatsAppImages(String path, String file, Context my_context) {
        String patht = Environment.getExternalStorageDirectory().toString() + path;
        File di = new File(patht);
        File destFile = new File(Environment.getExternalStorageDirectory().toString() + FilesData.getSavedFilesLocation(context), file);
        destFile = new File(my_context.getExternalFilesDir("").toString(), file);
        File asd = new File(my_context.getExternalFilesDir("").toString());
        {
            if (asd.exists()) {
                Log.e("Directory","Already  exit Folder");
            } else {
                asd.mkdir();
                Log.e("Directory","Creating  Folder");
            }
        }


        try {
            FileOperations.saveAndRefreshFiles(new File(di + "/" + file), destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent saved_intent = new Intent(context.getString(R.string.intent_file_saved_images));
        LocalBroadcastManager.getInstance(my_context).sendBroadcast(saved_intent);
    }


    public static void scrapWhatsAppVoice(String file) {
        String patht;
        if (Build.VERSION.SDK_INT >= 29) {
            patht = Environment.getExternalStorageDirectory().toString() + FilesData.WHATSAPP_VOICES_LOCATION_ANDROID_11;
        } else {
            patht = Environment.getExternalStorageDirectory().toString() + FilesData.WHATSAPP_VOICES_LOCATION;
        }
        File di = new File(patht);
        String VoicesinternalPath = getfolder(di) + "/";
        File imageFile = new File(VoicesinternalPath, file);
        File destFile = new File(Environment.getExternalStorageDirectory().toString() + FilesData.getSavedFilesLocation(context), imageFile.getName());
        try {
            FileOperations.saveAndRefreshFiles(imageFile, destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent saved_intent_voice = new Intent(context.getString(R.string.intent_file_saved_voice));
        LocalBroadcastManager.getInstance(context).sendBroadcast(saved_intent_voice);

    }


    public static String getSavedFilesLocation() {
        return SAVED_Status_LOCATION;
    }
}



