package com.os.vee.data.notes;

import android.annotation.SuppressLint;
import android.arch.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.os.vee.data.db.NoteDao;
import com.os.vee.utils.resource.ResourceWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Omar on 15-Jul-18 8:02 PM.
 */

@SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored"})
@Singleton
public class NoteRepository {

    private FirebaseFirestore firestore;
    private NoteDao noteDao;

    @Inject
    public NoteRepository(FirebaseFirestore firestore, NoteDao noteDao) {
        this.firestore = firestore;
        this.noteDao = noteDao;
    }

    private CollectionReference users() {
        return firestore.collection("users");
    }

    private CollectionReference userNotes(String uid) {
        return users().document(uid).collection("notes");
    }

    public MutableLiveData<ResourceWrapper<Void>> insertNote(Note note) {
        MutableLiveData<ResourceWrapper<Void>> liveData = new MutableLiveData<>();
        liveData.setValue(new ResourceWrapper.Loading());

        String noteId = userNotes(note.uid).document().getId();
        note.id = noteId;

        userNotes(note.uid)
                .document(noteId)
                .set(note, SetOptions.merge())
                .addOnSuccessListener(v -> {
                    insertNoteIntoCache(note);
                    liveData.setValue(new ResourceWrapper.Success<>(v));
                })
                .addOnFailureListener(e -> {
                            Timber.e(e, "Error inserting note(%s)", note);
                            liveData.setValue(new ResourceWrapper.Error(e));
                        }
                );

        return liveData;
    }

    public MutableLiveData<ResourceWrapper<Void>> updateNote(Note note) {
        MutableLiveData<ResourceWrapper<Void>> liveData = new MutableLiveData<>();
        liveData.setValue(new ResourceWrapper.Loading());

        Map<String, Object> data = new HashMap<>();
        data.put("title", note.title);
        data.put("content", note.content);

        userNotes(note.uid)
                .document(note.id)
                .update(data)
                .addOnSuccessListener(v -> {
                    updateNoteCache(note);
                    liveData.setValue(new ResourceWrapper.Success<>(v));
                })
                .addOnFailureListener(e -> {
                            Timber.e(e, "Error inserting note(%s)", note);
                            liveData.setValue(new ResourceWrapper.Error(e));
                        }
                );

        return liveData;
    }

    @SuppressLint("CheckResult")
    public MutableLiveData<ResourceWrapper<List<Note>>> getUserNotes(String uid) {
        MutableLiveData<ResourceWrapper<List<Note>>> liveData = new MutableLiveData<>();
        liveData.setValue(new ResourceWrapper.Loading());

        Flowable.create((FlowableEmitter<List<Note>> emitter) ->
                userNotes(uid)
                        .addSnapshotListener((query, e) -> {
                            if (e != null) {
                                emitter.onError(e);
                                return;
                            }

                            List<Note> notes = new ArrayList<>();

                            if (query != null && !query.isEmpty()) {
                                notes = query.toObjects(Note.class);
                            }

                            emitter.onNext(notes);
                        }), BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.io())
                .flatMap(notes -> notes.isEmpty() ? getNotesCache(uid) : Flowable.just(notes))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(notes -> {
                    Timber.d("Got user notes for uid=%s, notes size=%d", uid, notes.size());
                    liveData.setValue(new ResourceWrapper.Success(notes));
                }, e -> {
                    Timber.e(e, "Error getting notes for uid=%s", uid);
                    liveData.setValue(new ResourceWrapper.Error(e));
                });

        return liveData;
    }

    public MutableLiveData<ResourceWrapper<List<Note>>> getUserNote(String uid, String id) {
        MutableLiveData<ResourceWrapper<List<Note>>> liveData = new MutableLiveData<>();
        liveData.setValue(new ResourceWrapper.Loading());

        userNotes(uid)
                .document(id)
                .get()
                .addOnSuccessListener(document -> {
                    Note note = document.toObject(Note.class);

                    Timber.d("Got user note for uid=%s, notes=%s", uid, note);
                    liveData.setValue(new ResourceWrapper.Success(note));
                })
                .addOnFailureListener(e -> {
                    Timber.e(e, "Error getting note (id=%s) for uid=%s", id, uid);
                    liveData.setValue(new ResourceWrapper.Error(e));
                });

        return liveData;
    }

    public MutableLiveData<ResourceWrapper<Void>> deleteUserNote(Note note) {
        MutableLiveData<ResourceWrapper<Void>> liveData = new MutableLiveData<>();
        liveData.setValue(new ResourceWrapper.Loading());

        userNotes(note.uid)
                .document(note.id)
                .delete()
                .addOnSuccessListener(v -> {
                    deleteNoteFromCache(note);
                    Timber.d("Deleted user note for note=%s", note);
                    liveData.setValue(new ResourceWrapper.Success(v));
                })
                .addOnFailureListener(e -> {
                    Timber.e(e, "Error deleting note=%s", note);
                    liveData.setValue(new ResourceWrapper.Error(e));
                });

        return liveData;
    }

    private Flowable<List<Note>> getNotesCache(String userId) {
        return noteDao.getUserNotes(userId);
    }

    @SuppressLint("CheckResult")
    private void insertNoteIntoCache(Note note) {
        Maybe.fromAction(() -> noteDao.insertNote(note)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        _void -> Timber.d("Inserted %s into cache", note),
                        throwable -> Timber.e(throwable, "Error inserting %s into cache", note),
                        () -> Timber.d("Inserted %s into cache", note)
                );
    }

    @SuppressLint("CheckResult")
    private void deleteNoteFromCache(Note note) {
        Maybe.fromAction(() -> noteDao.deleteNote(note)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        _void -> Timber.d("Deleted %s from cache", note),
                        e -> Timber.e(e, "Error deleting %s from cache", note),
                        () -> Timber.d("Deleted %s from cache", note)
                );
    }

    @SuppressLint("CheckResult")
    private void updateNoteCache(Note note) {
        Maybe.fromAction(() -> noteDao.insertNote(note)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        _void -> Timber.d("Update note %s cache", note),
                        throwable -> Timber.e(throwable, "Error updating note %s cache", note),
                        () -> Timber.d("Update note %s cache", note)
                );
    }
}
