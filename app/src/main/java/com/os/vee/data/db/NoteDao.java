package com.os.vee.data.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.os.vee.data.notes.Note;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by Omar on 30-Jul-18 3:17 PM.
 */

@Dao
public interface NoteDao {

    @Query("SELECT * FROM notes WHERE notes.uid = :userId")
    Flowable<List<Note>> getUserNotes(String userId);

    @Query("SELECT * FROM notes WHERE notes.id = :id LIMIT 1")
    Flowable<Note> getNoteById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertNote(Note note);

    @Insert
    void insertNotes(List<Note> notes);

    @Delete
    void deleteNote(Note note);
}
