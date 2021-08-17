package com.malam.whatsdelet.nolastseen.hiddenchat.Helper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.malam.whatsdelet.nolastseen.hiddenchat.Model.Note;
import com.malam.whatsdelet.nolastseen.hiddenchat.Model.Note_new;

import java.util.ArrayList;
import java.util.List;

import static com.malam.whatsdelet.nolastseen.hiddenchat.Model.Note.COLUMN_ID;
import static com.malam.whatsdelet.nolastseen.hiddenchat.Model.Note.COLUMN_IMAGE_PATH;
import static com.malam.whatsdelet.nolastseen.hiddenchat.Model.Note.COLUMN_KEY;
import static com.malam.whatsdelet.nolastseen.hiddenchat.Model.Note.COLUMN_NOTE;
import static com.malam.whatsdelet.nolastseen.hiddenchat.Model.Note.COLUMN_NUMBER;
import static com.malam.whatsdelet.nolastseen.hiddenchat.Model.Note.COLUMN_STATUS;
import static com.malam.whatsdelet.nolastseen.hiddenchat.Model.Note.COLUMN_TIME;
import static com.malam.whatsdelet.nolastseen.hiddenchat.Model.Note.COLUMN_TIMESTAMP;
import static com.malam.whatsdelet.nolastseen.hiddenchat.Model.Note.COLUMN_TIME_4_ITERATION;


/**
 * Created by vicky on 15/03/18.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

// Database Version
private static final int DATABASE_VERSION = 1;

// Database Name
private static final String DATABASE_NAME = "notes_db";


public DatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
}

// Creating Tables
@Override
public void onCreate(SQLiteDatabase db) {
    
    // create notes table
    db.execSQL(Note.CREATE_TABLE);
}


// Upgrading database
@SuppressLint("SQLiteString")
@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // Drop older table if existed
    db.execSQL("DROP TABLE IF EXISTS " + Note.TABLE_NAME);
    db.execSQL("ALTER TABLE foo ADD COLUMN" + Note_new.COLUMN_STATUS + " INTEGER DEFAULT 0");
    
    // Create tables again
    onCreate(db);
}

public void delte_table_if_exist(String table_name) {
    SQLiteDatabase db = this.getWritableDatabase();
    db.execSQL("DROP TABLE IF EXISTS " + table_name);
    db.close();
}

public long insertNote(String table_name, String note, String number, String call_time, String date, String date_n_time_4iteration, String image_path, int key, boolean status) {
    // get writable database as we want to write data
    SQLiteDatabase db = this.getWritableDatabase();
    
    ContentValues values = new ContentValues();
    // `id` and `timestamp` will be inserted automatically.
    // no need to add them
    values.put(COLUMN_NOTE, note);
    values.put(COLUMN_NUMBER, number);
    values.put(COLUMN_TIME, call_time);
    values.put(COLUMN_TIMESTAMP, date);
    values.put(COLUMN_TIME_4_ITERATION, date_n_time_4iteration);
    if (status) {
        values.put(COLUMN_STATUS, 0);
    } else
        values.put(COLUMN_STATUS, 1);
    
    values.put(COLUMN_IMAGE_PATH, image_path);
    values.put(COLUMN_KEY, key);
    
    // insert row
    long id = db.insert(Note.TABLE_NAME, null, values);
    // long id = db.insert(table_name, null, values);
    
    // close db connection
    db.close();
    
    // return newly inserted row id
    return id;
}

public Note getNote(long id) {
    // get readable database as we are not inserting anything
    SQLiteDatabase db = this.getReadableDatabase();
    
    Cursor cursor = db.query(Note.TABLE_NAME,
            new String[]{COLUMN_ID, COLUMN_NOTE, COLUMN_NUMBER, COLUMN_TIME, COLUMN_IMAGE_PATH, COLUMN_TIMESTAMP, COLUMN_KEY, COLUMN_STATUS},
            COLUMN_ID + "=?",
            new String[]{String.valueOf(id)}, null, null, null, null);
    
    if (cursor != null)
        cursor.moveToFirst();
    
    // prepare note object
    Note note = new Note(
            cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
            cursor.getString(cursor.getColumnIndex(COLUMN_NOTE)),
            cursor.getString(cursor.getColumnIndex(COLUMN_NUMBER)),
            cursor.getString(cursor.getColumnIndex(COLUMN_TIME)),
            cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_PATH)),
            cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP)),
            cursor.getInt(cursor.getColumnIndex(COLUMN_KEY)),
            cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS)));
    
    // close the db connection
    cursor.close();
    
    return note;
}

public List<Note> getAllNotes() {
    List<Note> notes = new ArrayList<>();
    try {
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Note.TABLE_NAME + " ORDER BY " +
                                     COLUMN_TIMESTAMP + " DESC";
        
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int i = 1;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                /*if (i%2==0){
                    Note note = new Note();
                    note.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                    note.setNote("for_native_ad");
                    note.setNumber("for_native_ad");
                    note.setTime("for_native_ad");
                    note.setPath("for_native_ad");
                    // note.setPath(Utils.path);
                    note.setTimestamp("for_native_ad");
                    note.setTime_n_date_4_itration(cursor.getString(cursor.getColumnIndex(COLUMN_TIME_4_ITERATION)));
                    note.setSelected(false);
                    notes.add(note);
                    System.out.println("native_ad added whatsapp");
                }*/
                Note note = new Note();
                note.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                note.setNote(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE)));
                note.setNumber(cursor.getString(cursor.getColumnIndex(COLUMN_NUMBER)));
                note.setTime(cursor.getString(cursor.getColumnIndex(COLUMN_TIME)));
                note.setPath(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_PATH)));
                // note.setPath(Utils.path);
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP)));
                note.setTime_n_date_4_itration(cursor.getString(cursor.getColumnIndex(COLUMN_TIME_4_ITERATION)));
                note.setKey(cursor.getInt(cursor.getColumnIndex(COLUMN_KEY)));
                if (cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS)) == 0) {
                    note.setStatus(true);
                } else
                    note.setStatus(false);
                notes.add(note);
                i++;
            } while (cursor.moveToNext());
        }
        
        // close db connection
        db.close();
    } catch (IllegalStateException asd) {
    } catch (Exception er) {
    }
    // return notes list
    return notes;
}


public int getNotesCount() {
    String countQuery = "SELECT  * FROM " + Note.TABLE_NAME;
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery(countQuery, null);
    
    int count = cursor.getCount();
    cursor.close();
    
    
    // return count
    return count;
}

public int updateNote(final Note note) {
    final SQLiteDatabase db = this.getWritableDatabase();
    
    final ContentValues values = new ContentValues();
    values.put(COLUMN_NOTE, note.getNote());
    values.put(COLUMN_NUMBER, note.getNumber());
    values.put(COLUMN_TIME, note.getTime());
    values.put(COLUMN_TIMESTAMP, note.getTimestamp());
    values.put(COLUMN_TIME_4_ITERATION, note.getTime_n_date_4_itration());
    if (note.isStatus()) {
        values.put(COLUMN_STATUS, 0);
    } else
        values.put(COLUMN_STATUS, 1);
    
     values.put(COLUMN_KEY, note.getKey());
    
    
    Log.e("db_value",""+values);
    db.update(Note.TABLE_NAME, values, COLUMN_ID + "=" + note.getId(), null);
    return      db.update(Note.TABLE_NAME, values, COLUMN_ID + " = ?",
                    new String[]{String.valueOf(note.getId())});
      
     
    
 }

public void create_new_table(String create_table) {
    SQLiteDatabase db = this.getWritableDatabase();
    db.execSQL(create_table);
    
    
}

public void deleteNote(Note note) {
    
    SQLiteDatabase db = this.getWritableDatabase();
    db.delete(Note.TABLE_NAME, COLUMN_ID + " = ?",
            new String[]{String.valueOf(note.getId())});
    
    db.close();
}

public int CheckIsDataAlreadyInDBorNot(String dbfield, String fieldValue) {
    SQLiteDatabase sqldb = getReadableDatabase();
    //String Query = "Select * from " + Note.TABLE_NAME + " where " + dbfield + " = " + fieldValue;
    String Query = "Select * from " + Note.TABLE_NAME + " where " + dbfield + " = \"" + fieldValue + "\";";
    Cursor cursor = sqldb.rawQuery(Query, null);
    
    if (cursor.getCount() <= 0) {
        cursor.close();
        return 0;
    }
  int id=  (cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
    
    cursor.close();
    return id;
}

public void deleteallNotes() {
    
    SQLiteDatabase db = this.getWritableDatabase();
      /*  db.delete(Note.TABLE_NAME, Note.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});*/
    db.delete(Note.TABLE_NAME, null, null);
    db.close();
}
}
