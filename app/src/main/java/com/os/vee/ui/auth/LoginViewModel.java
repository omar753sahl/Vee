package com.os.vee.ui.auth;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;
import com.os.vee.data.users.UserRepository;
import com.os.vee.utils.resource.ResourceWrapper;

import javax.inject.Inject;

/**
 * Created by Omar on 14-Jul-18 12:50 AM.
 */
public class LoginViewModel extends ViewModel {

    private UserRepository userRepository;

    @Inject
    public LoginViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<FirebaseUser> user() {
        return userRepository.user;
    }

    public MutableLiveData<ResourceWrapper<FirebaseUser>> signInWitEmail(String email, String password) {
        return userRepository.signInWithEmail(email, password);
    }

    public MutableLiveData<ResourceWrapper<FirebaseUser>> signInWitGoogle(GoogleSignInAccount account) {
        return userRepository.signInWithGoogle(account);
    }
}
