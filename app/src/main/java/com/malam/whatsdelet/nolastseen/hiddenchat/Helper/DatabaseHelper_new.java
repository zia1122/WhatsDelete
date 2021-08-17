package com.malam.whatsdelet.nolastseen.hiddenchat.Helper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.malam.whatsdelet.nolastseen.hiddenchat.Model.Note;
import com.malam.whatsdelet.nolastseen.hiddenchat.Model.Note_new;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by vicky on 15/03/18.
 */

public class DatabaseHelper_new extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "notes_db_new";


    public DatabaseHelper_new(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Note_new.CREATE_TABLE);
    }
    @SuppressLint("SQLiteString")
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Note_new.TABLE_NAME);
        if (newVersion > oldVersion) {
            db.execSQL("ALTER TABLE foo ADD COLUMN "+Note_new.COLUMN_STATUS+" INTEGER DEFAULT 0");
        }
        if (newVersion > oldVersion) {
            db.execSQL("ALTER TABLE foo ADD COLUMN "+Note_new.COLUMN_KEY+" INTEGER DEFAULT 0");
        }  // Create tables again
        onCreate(db);
    }
    public long insertNote(String table_name, String note, String number, String call_time, String image_path,Boolean deleted_status,int key) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Note.COLUMN_NOTE, note);
        values.put(Note.COLUMN_NUMBER, number);
        values.put(Note.COLUMN_TIME, call_time);
        values.put(Note.COLUMN_IMAGE_PATH, image_path);
        if (deleted_status)
        values.put(Note.COLUMN_STATUS, 1);
        else  values.put(Note.COLUMN_STATUS, 0);
    
        values.put(Note.COLUMN_KEY, key);
         // insert row
        long id = db.insert(Note_new.TABLE_NAME, null, values);
        // long id = db.insert(table_name, null, values);
        // close db connection
        db.close();
        // return newly inserted row id
        return id;
    }
    public long insert_on_existed_table(String table_name, String note, String number, String call_time, String image_path) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Note.COLUMN_NOTE, note);
        values.put(Note.COLUMN_NUMBER, number);
        values.put(Note.COLUMN_TIME, call_time);
        values.put(Note.COLUMN_IMAGE_PATH, image_path);
         // insert row
        long id = db.insert(table_name, null, values);
        // long id = db.insert(table_name, null, values);
        // close db connection
        db.close();
        // return newly inserted row id
        return id;
    }
    public Note_new getNote_new(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Note_new.TABLE_NAME,
                new String[]{Note.COLUMN_ID, Note.COLUMN_NOTE, Note.COLUMN_NUMBER, Note.COLUMN_TIME, Note.COLUMN_IMAGE_PATH, Note.COLUMN_TIMESTAMP},
                Note.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        // prepare note object
        Note_new note = new Note_new(
                cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_NUMBER)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIME)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_IMAGE_PATH)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)),
                cursor.getInt(cursor.getColumnIndex(Note.COLUMN_KEY))
                ,false);
        // close the db connection
        cursor.close();
        return note;
    }
    public List<Note_new> getAllNotes() {

        List<Note_new> notes = new ArrayList<>();
        try{
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Note_new.TABLE_NAME + " ORDER BY " +
                Note.COLUMN_TIMESTAMP + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Note_new note = new Note_new();
                note.setId(cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)));
                note.setNote(cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE)));
                note.setNumber(cursor.getString(cursor.getColumnIndex(Note.COLUMN_NUMBER)));
                note.setTime(cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIME)));
                note.setPath(cursor.getString(cursor.getColumnIndex(Note.COLUMN_IMAGE_PATH)));
                System.out.print("path store in  database "+cursor.getString(cursor.getColumnIndex(Note.COLUMN_IMAGE_PATH)));
                // note.setPath(Utils.path);
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)));
                note.setKey(cursor.getInt(cursor.getColumnIndex(Note.COLUMN_KEY)));
                int  dytstud=cursor.getInt(cursor.getColumnIndex(Note.COLUMN_STATUS));
               if (dytstud== 0)
                   note.setstatus(false);
               else if (dytstud== 1)
                note.setstatus(true);

                notes.add(note);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();
        }catch (IllegalStateException asd){}catch (Exception asd){}
        // return notes list
        return notes;
    }

    public List<Note_new> getAllNotes_from_specfic_contact(String table_name) {
        List<Note_new> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + table_name + " ORDER BY " +
                Note.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Note_new note = new Note_new();
                note.setId(cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)));
                note.setNote(cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE)));
                note.setNumber(cursor.getString(cursor.getColumnIndex(Note.COLUMN_NUMBER)));
                note.setTime(cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIME)));
                note.setPath(cursor.getString(cursor.getColumnIndex(Note.COLUMN_IMAGE_PATH)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)));

                notes.add(note);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notes;
    }

    public int getNotesCount() {
        String countQuery = "SELECT  * FROM " + Note_new.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public int updateNote(Note_new note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
         values.put(Note.COLUMN_NOTE, note.getNote());
        values.put(Note.COLUMN_NUMBER, note.getNumber());
        values.put(Note.COLUMN_TIME, note.getTime());
        values.put(Note.COLUMN_IMAGE_PATH, note.getPath());
        if (note.getstatus())
            values.put(Note.COLUMN_STATUS, 1);
        else  values.put(Note.COLUMN_STATUS, 0);
    
        values.put(Note.COLUMN_KEY, note.getKey());
        // updating row
        int my_note = note.getId();
        db.update(Note_new.TABLE_NAME, values, Note.COLUMN_KEY + "=" + note.getKey(), null);
        return db.update(Note_new.TABLE_NAME, values, Note.COLUMN_ID + " = ?",
                new String[]{String.valueOf(my_note)});

    }
    public void create_new_table(String create_table) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(create_table);


    }

    public void deleteNote(Note_new note) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Note_new.TABLE_NAME, Note.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
        /*
        db.delete(Note.TABLE_NAME, Note.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});*/

        db.close();
    }

    public Note_new CheckIsDataAlreadyInDBorNot( int fieldValue) {
        SQLiteDatabase sqldb = getReadableDatabase();
        //String Query = "Select * from " + Note.TABLE_NAME + " where " + dbfield + " = " + fieldValue;
        String Query = "Select * from " + Note_new.TABLE_NAME + " where " + Note_new.COLUMN_KEY + " = \"" + fieldValue + "\";";
        Cursor cursor = sqldb.rawQuery(Query, null);
        Note_new note = new Note_new();
    
        if (cursor.moveToFirst()) {
            note.setId(cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)));
            note.setNote(cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE)));
            note.setNumber(cursor.getString(cursor.getColumnIndex(Note.COLUMN_NUMBER)));
            note.setTime(cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIME)));
            note.setPath(cursor.getString(cursor.getColumnIndex(Note.COLUMN_IMAGE_PATH)));
            System.out.print("path store in  database "+cursor.getString(cursor.getColumnIndex(Note.COLUMN_IMAGE_PATH)));
            // note.setPath(Utils.path);
            note.setTimestamp(cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)));
            note.setKey(cursor.getInt(cursor.getColumnIndex(Note.COLUMN_KEY)));
            int  dytstud=cursor.getInt(cursor.getColumnIndex(Note.COLUMN_STATUS));
            if (dytstud== 0)
                note.setstatus(false);
            else if (dytstud== 1)
                note.setstatus(true);
            
            
            cursor.close();
            return note;
        }
        cursor.close();
        return note;
    }

    public void deleteallNotes() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Note_new.TABLE_NAME, null, null);
        db.close();
    }
}
