package com.os.vee.ui.auth;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.os.vee.data.users.UserRepository;
import com.os.vee.utils.resource.ResourceWrapper;

import javax.inject.Inject;

/**
 * Created by Omar on 14-Jul-18 4:07 AM.
 */
public class SignUpViewModel extends ViewModel {

    private UserRepository userRepository;

    @Inject
    public SignUpViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<FirebaseUser> user() {
        return userRepository.user;
    }

    public MutableLiveData<ResourceWrapper<FirebaseUser>> signUpWithEmail(String name, String email, String password) {
        return userRepository.signUpWithEmail(name, email, password);
    }
}
