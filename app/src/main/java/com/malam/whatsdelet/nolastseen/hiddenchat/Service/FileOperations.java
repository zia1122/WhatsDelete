package com.malam.whatsdelet.nolastseen.hiddenchat.Service;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

public  class   FileOperations {
    Context context;



    public static void deleteAndRefreshFiles(File file) {
        file.delete();
        FilesData.scrapSavedFiles();
    }


    public static void copyToAppDirectory (Context context, String in){


        File destFile = new File(context.getFilesDir()
                , "Saved");
        InputStream inputStream = null;

        try {
            inputStream = context.getContentResolver().openInputStream(Uri.parse(in));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (!destFile.exists()) {
            destFile.mkdirs();
        }
        File file = new File(in);
        File imageFile = new File(destFile, file.getName());
        if (!imageFile.exists()) {
            try {
                imageFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            OutputStream out = new FileOutputStream(imageFile);
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FilesData.scrapSavedFiles();

    }
    public static void saveAndRefreshFiles(File sourceFile, File destiniation) throws IOException {

        File destFile = destiniation;
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
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }

        FilesData.scrapSavedFiles();


    }
    public static void saveAndRefreshFiles(File sourceFile) throws IOException {

        File destFile = new File(Environment.getExternalStorageDirectory().toString()+ FilesData.getSavedFilesLocation(),sourceFile.getName());

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
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }

        FilesData.scrapSavedFiles();


    }



   /* public static  void shareFile(File file, Context c,char type){


        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        Uri uri = Uri.parse(file.getPath());

        if(type == 'i') {

            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            c.startActivity(Intent.createChooser(shareIntent, c.getString(R.string.share_using)));

        }else{

            shareIntent.setType("video/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            c.startActivity(Intent.createChooser(shareIntent,  c.getString(R.string.share_using)));
        }

    }*/


}
