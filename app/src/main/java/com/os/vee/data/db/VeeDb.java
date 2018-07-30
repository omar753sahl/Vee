package com.os.vee.data.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.os.vee.data.notes.Note;

/**
 * Created by Omar on 30-Jul-18 3:23 PM.
 */

@Database(entities = {Note.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class VeeDb extends RoomDatabase {
    public abstract NoteDao noteDao();
}
