package com.os.vee.data.notes;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Created by Omar on 15-Jul-18 8:05 PM.
 */

@Entity(tableName = "notes")
public class Note implements Parcelable {

    @PrimaryKey
    @NonNull
    public String id;
    public String uid;
    public String title;
    public String content;
    public Date timestamp;

    public Note() { }

    public Note(@NonNull String id, String uid, String title, String content) {
        this.id = id;
        this.uid = uid;
        this.title = title;
        this.content = content;
        this.timestamp = new Date();
    }

    protected Note(Parcel in) {
        id = in.readString();
        uid = in.readString();
        title = in.readString();
        content = in.readString();
        timestamp = (Date) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(uid);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeSerializable(timestamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", uid='" + uid + '\'' +
                ", title='" + title + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
