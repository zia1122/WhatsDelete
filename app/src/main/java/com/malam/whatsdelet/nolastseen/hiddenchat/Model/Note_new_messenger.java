package com.malam.whatsdelet.nolastseen.hiddenchat.Model;

/**
 * Created by vicky on 20/02/18.
 */

public class Note_new_messenger {
    public static  String TABLE_NAME = "notes";;

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_NUMBER= "number";
    public static final String COLUMN_STATUS= "status";
    public static final String COLUMN_TIME= "time";
    public static final String COLUMN_IMAGE_PATH= "img_path";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id;
    private String note;
    private String timestamp;
    private String number;
    private String time;
    private String path;
    private Boolean status;
    Boolean selected;


    public static  String TABLE_Contact_NAME = "contact_name";
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NOTE + " TEXT,"
                    + COLUMN_NUMBER + " TEXT,"
                    + COLUMN_TIME + " TEXT,"
                    + COLUMN_IMAGE_PATH+ " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                    + COLUMN_STATUS+" INTEGER DEFAULT 0)";
    // Create table SQL query
    public static final String CREATE_TABLE_FOREVERY_CONTACT =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NOTE + " TEXT,"
                    + COLUMN_NUMBER + " TEXT,"
                    + COLUMN_TIME + " TEXT,"
                    + COLUMN_IMAGE_PATH+ " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public Note_new_messenger() {
    }

    public Note_new_messenger(int id, String note, String number, String call_time, String image_path, String timestamp,Boolean status) {
        this.id = id;
        this.note = note;
        this.timestamp = timestamp;
        this.number = number;
        this.time = call_time;
        this.path = image_path;
        this.status = status;
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

    public void setPath(String imgpath) {
        this.path = imgpath;
    }


    public String getTime() {
        return time;
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

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

public Boolean getStatus() {
    return status;
}

public void setStatus(Boolean status) {
    this.status = status;
}
}
