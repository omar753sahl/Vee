package com.os.vee.data.users;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.os.vee.utils.resource.ResourceWrapper;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

/**
 * Created by Omar on 13-Jul-18 11:36 PM.
 */

@SuppressWarnings("unchecked")
@Singleton
public class UserRepository implements FirebaseAuth.AuthStateListener {

    private FirebaseAuth auth;
    public MutableLiveData<FirebaseUser> user;

    @Inject
    public UserRepository(FirebaseAuth auth) {
        this.auth = auth;
        this.auth.addAuthStateListener(this);
        this.user = new MutableLiveData<>();
    }

    public MutableLiveData<ResourceWrapper<FirebaseUser>> signInWithGoogle(GoogleSignInAccount account) {
        final MutableLiveData<ResourceWrapper<FirebaseUser>> liveData = new MutableLiveData<>();
        liveData.setValue(new ResourceWrapper.Loading());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Timber.d("signInWithGoogle:success");
                liveData.setValue(new ResourceWrapper.Success(auth.getCurrentUser()));
            } else {
                Timber.e("signInWithCredential:failure, %s", task.getException());
                liveData.setValue(new ResourceWrapper.Error(task.getException()));
            }
        });

        return liveData;
    }

    public MutableLiveData<ResourceWrapper<FirebaseUser>> signInWithEmail(String email, String password) {
        final MutableLiveData<ResourceWrapper<FirebaseUser>> liveData = new MutableLiveData<>();
        liveData.setValue(new ResourceWrapper.Loading());

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Timber.d("signInWithEmail:success");
                liveData.setValue(new ResourceWrapper.Success(auth.getCurrentUser()));
            } else {
                Timber.e("signInWithEmail:failure, %s", task.getException());
                liveData.setValue(new ResourceWrapper.Error(task.getException()));
            }
        });

        return liveData;
    }

    public MutableLiveData<ResourceWrapper<FirebaseUser>> signUpWithEmail(final String name, String email, String password) {
        final MutableLiveData<ResourceWrapper<FirebaseUser>> liveData = new MutableLiveData<>();
        liveData.setValue(new ResourceWrapper.Loading());

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Timber.d("signUpWithEmail:success");

                if (auth.getCurrentUser() != null) {
                    auth.getCurrentUser().updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(name).build())
                            .addOnCompleteListener(task1 -> liveData.setValue(new ResourceWrapper.Success(auth.getCurrentUser())));
                }

            } else {
                Timber.e("signUpWithEmail:failure, %s", task.getException());
                liveData.setValue(new ResourceWrapper.Error(task.getException()));
            }
        });

        return liveData;
    }

    public MutableLiveData<ResourceWrapper<Void>> updateUserProfile(String name) {
        final MutableLiveData<ResourceWrapper<Void>> liveData = new MutableLiveData<>();
        liveData.setValue(new ResourceWrapper.Loading());

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        auth.getCurrentUser().updateProfile(profileUpdates)
                .addOnSuccessListener(v -> {
                    Timber.d("User profile updated.");
                    liveData.setValue(new ResourceWrapper.Success<>(v));
                })
                .addOnFailureListener(e -> {
                    Timber.e(e, "userPorfileUpdate:failure");
                    liveData.setValue(new ResourceWrapper.Error(e));
                });

        return liveData;
    }

    public void logout() {
        auth.signOut();
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            Timber.d("Current User(displayName=%s, email=%s, photoUrl=%s)", user.getDisplayName(), user.getEmail(), user.getPhotoUrl());
        } else {
            Timber.d("Current User=(null)");
        }
        this.user.setValue(user);
    }
}
