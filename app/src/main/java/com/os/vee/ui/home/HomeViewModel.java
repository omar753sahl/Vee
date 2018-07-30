package com.os.vee.ui.home;

import android.annotation.SuppressLint;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.os.vee.data.notes.Note;
import com.os.vee.data.notes.NoteRepository;
import com.os.vee.data.users.UserRepository;
import com.os.vee.utils.resource.ResourceWrapper;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Created by Omar on 15-Jul-18 9:33 PM.
 */
public class HomeViewModel extends ViewModel {

    private NoteRepository noteRepository;
    private UserRepository userRepository;
    private BehaviorSubject<State> state = BehaviorSubject.createDefault(State.INITIAL);

    @Inject
    public HomeViewModel(NoteRepository noteRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    public MutableLiveData<FirebaseUser> user() {
        return userRepository.user;
    }

    public MutableLiveData<ResourceWrapper<List<Note>>> getUserNotes(String uid) {
        return noteRepository.getUserNotes(uid);
    }

    public MutableLiveData<ResourceWrapper<Void>> deleteNote(Note note) {
        return noteRepository.deleteUserNote(note);
    }

    public MutableLiveData<ResourceWrapper<Void>> updateUserInfo(String name) {
        return userRepository.updateUserProfile(name);
    }

    public void logout() {
        userRepository.logout();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("CheckResult")
    public MutableLiveData<ResourceWrapper<State>> getState() {
        MutableLiveData<ResourceWrapper<State>> liveData = new MutableLiveData<>();
        liveData.setValue(new ResourceWrapper.Loading<>());

        state.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(state -> liveData.setValue(new ResourceWrapper.Success<>(state)));

        return liveData;
    }

    public void nextState() {
        switch (state.getValue()) {
            case INITIAL:
                state.onNext(State.EDITING);
                break;
            case EDITING:
                state.onNext(State.SAVING);
                break;
            case SAVING:
                state.onNext(State.DONE);
                break;
            case DONE:
                state.onNext(State.INITIAL);
        }
    }

    public enum State {
        INITIAL,
        EDITING,
        SAVING,
        DONE
    }
}
