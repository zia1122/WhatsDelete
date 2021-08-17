package com.malam.whatsdelet.nolastseen.hiddenchat.Model;

/**
 * Created by vicky on 20/02/18.
 */

public class Note_messenger implements Comparable<Note_messenger>{
    public static  String TABLE_NAME = "notes_messenger";;

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_NUMBER= "number";
    public static final String COLUMN_TIME= "time";
    public static final String COLUMN_IMAGE_PATH= "img_path";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_KEY = "key";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_TIME_4_ITERATION= "COLUMN_TIME_4_ITERATION";

    private int id;
    private int key;
    private String note;
    private String timestamp;
    private String time_n_date_4_itration;
    private String number;
    private String time;
    private String path;
    private Boolean deleted_status;
    Boolean selected;
    Boolean bold;


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NOTE + " TEXT,"
                    + COLUMN_NUMBER + " TEXT,"
                    + COLUMN_TIME + " TEXT,"
                    + COLUMN_IMAGE_PATH+ " TEXT,"
                    + COLUMN_TIME_4_ITERATION+ " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                    + COLUMN_STATUS+" INTEGER DEFAULT 0,"+
                    COLUMN_KEY+" INTEGER DEFAULT 0)";
    public static final String CREATE_TABLE_FOREVERY_CONTACT =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NOTE + " TEXT,"
                    + COLUMN_NUMBER + " TEXT,"
                    + COLUMN_TIME + " TEXT,"
                    + COLUMN_IMAGE_PATH+ " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public Note_messenger() {
    }

    public Note_messenger(int id, String note, String number, String call_time, String image_path, String timestamp,Boolean deleted_status,int key) {
        this.id = id;
        this.note = note;
        this.key = key;
        this.timestamp = timestamp;
        this.number = number;
        this.time = call_time;
        this.deleted_status = deleted_status;
        this.path = image_path;
    }

    public int getId() {
        return id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
    public String getNumber() {
        return number;
    }

    public void setNumber(String contact_number) {
        this.number = contact_number;
    }

    public String getPath() {
        return path;
    }
   public Boolean getDeleted_status() {
        return deleted_status;
    }

    public void setPath(String imgpath) {
        this.path = imgpath;
    }


    public void setTime(String call_time) {
        this.time = call_time;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public Boolean getBold() {
        return bold;
    }

    public void setBold(Boolean bold_txt) {
        this.bold = bold_txt;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public String getTime() {
        return time;
    }

    public void setTime_n_date_4_itration(String time_n_date) {
        this.time_n_date_4_itration= time_n_date;
    } public void setDeleted_status(Boolean status) {
        this.deleted_status= status;
    }
    public String getTime_n_date_4_itration() {
        return time_n_date_4_itration;
    }


    @Override
    public int compareTo(Note_messenger other) {
        return this.time_n_date_4_itration.compareTo(other.time_n_date_4_itration);
    }

public int getKey() {
    return key;
}

public void setKey(int key) {
    this.key = key;
}
}
