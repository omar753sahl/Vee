package com.os.vee.base;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.os.vee.data.users.UserRepository;

import javax.inject.Inject;

/**
 * Created by Omar on 14-Jul-18 4:01 AM.
 */
public class BaseViewModel extends ViewModel {

    private UserRepository userRepository;

    @Inject
    public BaseViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public MutableLiveData<FirebaseUser> user() {
        return userRepository.user;
    }
}
