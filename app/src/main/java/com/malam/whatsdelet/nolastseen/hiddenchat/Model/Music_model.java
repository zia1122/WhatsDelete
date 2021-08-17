package com.malam.whatsdelet.nolastseen.hiddenchat.Model;

/**
 * Created by vicky on 20/02/18.
 */

public class Music_model {
    String file_name,file_path;
    long file_size;
    Boolean is_selected;

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public long getFile_size() {
        return file_size;
    }

    public void setFile_size(long file_size) {
        this.file_size = file_size;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public void setIs_selected(Boolean is_selected) {
        this.is_selected = is_selected;
    }

    public Boolean getIs_selected() {
        return is_selected;
    }
}
