package com.malam.whatsdelet.nolastseen.hiddenchat.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.malam.whatsdelet.nolastseen.hiddenchat.Model.Note_new;
import com.malam.whatsdelet.nolastseen.hiddenchat.Model.Note_new_instagram;
import com.malam.whatsdelet.nolastseen.hiddenchat.Model.Note_new_messenger;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by vicky on 15/03/18.
 */

public class DatabaseHelper_new_instagram extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "notes_db_instagram_new";


    public DatabaseHelper_new_instagram(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Note_new_instagram.CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Note_new_instagram.TABLE_NAME);
        db.execSQL("ALTER TABLE foo ADD COLUMN "+ Note_new.COLUMN_STATUS+" INTEGER DEFAULT 0");
        // Create tables again
        onCreate(db);
    }
    public long insertNote(String table_name, String note, String number, String call_time, String image_path, boolean status) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Note_new_instagram.COLUMN_NOTE, note);
        values.put(Note_new_instagram.COLUMN_NUMBER, number);
        values.put(Note_new_instagram.COLUMN_TIME, call_time);
        values.put(Note_new_instagram.COLUMN_IMAGE_PATH, image_path);
        values.put(Note_new_instagram.COLUMN_STATUS, status);
        // insert row
        long id = db.insert(Note_new_instagram.TABLE_NAME, null, values);
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
        values.put(Note_new_instagram.COLUMN_NOTE, note);
        values.put(Note_new_instagram.COLUMN_NUMBER, number);
        values.put(Note_new_instagram.COLUMN_TIME, call_time);
        values.put(Note_new_instagram.COLUMN_IMAGE_PATH, image_path);
        values.put(Note_new_messenger.COLUMN_STATUS,false);
        // insert row
        long id = db.insert(table_name, null, values);
        // long id = db.insert(table_name, null, values);
        // close db connection
        db.close();
        // return newly inserted row id
        return id;
    }
    public Note_new_instagram getNote_new(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Note_new_instagram.TABLE_NAME,
                new String[]{Note_new_instagram.COLUMN_ID, Note_new_instagram.COLUMN_NOTE, Note_new_instagram.COLUMN_NUMBER, Note_new_instagram.COLUMN_TIME, Note_new_instagram.COLUMN_IMAGE_PATH, Note_new_instagram.COLUMN_TIMESTAMP},
                Note_new_instagram.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        // prepare note object
        Note_new_instagram note = new Note_new_instagram(
                cursor.getInt(cursor.getColumnIndex(Note_new_instagram.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Note_new_instagram.COLUMN_NOTE)),
                cursor.getString(cursor.getColumnIndex(Note_new_instagram.COLUMN_NUMBER)),
                cursor.getString(cursor.getColumnIndex(Note_new_instagram.COLUMN_TIME)),
                cursor.getString(cursor.getColumnIndex(Note_new_instagram.COLUMN_IMAGE_PATH)),
                cursor.getString(cursor.getColumnIndex(Note_new_instagram.COLUMN_TIMESTAMP)));
        // close the db connection
        cursor.close();
        return note;
    }
    public List<Note_new_instagram> getAllNotes() {
        List<Note_new_instagram> notes = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Note_new_instagram.TABLE_NAME + " ORDER BY " +
                Note_new_instagram.COLUMN_TIMESTAMP + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Note_new_instagram note = new Note_new_instagram();
                note.setId(cursor.getInt(cursor.getColumnIndex(Note_new_instagram.COLUMN_ID)));
                note.setNote(cursor.getString(cursor.getColumnIndex(Note_new_instagram.COLUMN_NOTE)));
                note.setNumber(cursor.getString(cursor.getColumnIndex(Note_new_instagram.COLUMN_NUMBER)));
                note.setTime(cursor.getString(cursor.getColumnIndex(Note_new_instagram.COLUMN_TIME)));
                note.setPath(cursor.getString(cursor.getColumnIndex(Note_new_instagram.COLUMN_IMAGE_PATH)));
                // note.setPath(Utils.path);
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(Note_new_instagram.COLUMN_TIMESTAMP)));
                 note.setStatus(false);

                notes.add(note);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notes;
    }

    public List<Note_new_instagram> getAllNotes_from_specfic_contact(String table_name) {
        List<Note_new_instagram> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + table_name + " ORDER BY " +
                Note_new_instagram.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Note_new_instagram note = new Note_new_instagram();
                note.setId(cursor.getInt(cursor.getColumnIndex(Note_new_instagram.COLUMN_ID)));
                note.setNote(cursor.getString(cursor.getColumnIndex(Note_new_instagram.COLUMN_NOTE)));
                note.setNumber(cursor.getString(cursor.getColumnIndex(Note_new_instagram.COLUMN_NUMBER)));
                note.setTime(cursor.getString(cursor.getColumnIndex(Note_new_instagram.COLUMN_TIME)));
                note.setPath(cursor.getString(cursor.getColumnIndex(Note_new_instagram.COLUMN_IMAGE_PATH)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(Note_new_instagram.COLUMN_TIMESTAMP)));

                notes.add(note);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notes;
    }

    public int getNotesCount() {
        String countQuery = "SELECT  * FROM " + Note_new_instagram.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public int updateNote(Note_new_instagram note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Note_new_instagram.COLUMN_NOTE, note.getNote());
        values.put(Note_new_instagram.COLUMN_NUMBER, note.getNumber());

        // updating row
        int my_note = note.getId();
        db.update(Note_new_instagram.TABLE_NAME, values, Note_new_instagram.COLUMN_ID + "=" + note.getId(), null);
        return db.update(Note_new_instagram.TABLE_NAME, values, Note_new_instagram.COLUMN_ID + " = ?",
                new String[]{String.valueOf(my_note)});

    }
    public void create_new_table(String create_table) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(create_table);


    }

    public void deleteNote(Note_new_instagram note) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Note_new_instagram.TABLE_NAME, Note_new_instagram.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
        /*
        db.delete(Note.TABLE_NAME, Note.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});*/

        db.close();
    }

    public boolean CheckIsDataAlreadyInDBorNot(String dbfield, String fieldValue) {
        SQLiteDatabase sqldb = getReadableDatabase();
        //String Query = "Select * from " + Note.TABLE_NAME + " where " + dbfield + " = " + fieldValue;
        String Query = "Select * from " + Note_new_instagram.TABLE_NAME + " where " + dbfield + " = \"" + fieldValue + "\";";
        Cursor cursor = sqldb.rawQuery(Query, null);

        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public void deleteallNotes( ) {

        SQLiteDatabase db = this.getWritableDatabase();
      /*  db.delete(Note.TABLE_NAME, Note.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});*/
        db.delete(Note_new_instagram.TABLE_NAME, null, null);
        db.close();
    }
}
