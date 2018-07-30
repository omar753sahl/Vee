package com.os.vee.ui.note;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.os.vee.data.notes.Note;
import com.os.vee.data.notes.NoteRepository;
import com.os.vee.data.users.UserRepository;
import com.os.vee.utils.resource.ResourceWrapper;

import javax.inject.Inject;

/**
 * Created by Omar on 16-Jul-18 11:25 AM.
 */
public class AddNoteViewModel extends ViewModel {

    private NoteRepository noteRepository;
    private UserRepository userRepository;

    @Inject
    public AddNoteViewModel(NoteRepository noteRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    public FirebaseUser getCurrentUser() {
        return userRepository.getCurrentUser();
    }

    public MutableLiveData<ResourceWrapper<Void>> addNote(Note note) {
        return noteRepository.insertNote(note);
    }

    public MutableLiveData<ResourceWrapper<Void>> updateNote(Note note) {
        return noteRepository.updateNote(note);
    }
}
