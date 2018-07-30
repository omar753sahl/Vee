package com.os.vee.utils.resource;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Omar on 15-Jul-18 8:28 PM.
 */
public class ResourceObserver<T> implements Observer<ResourceWrapper<T>> {

    private OnSuccess<T> success;
    private OnLoading loading;
    private OnError error;

    public ResourceObserver(OnSuccess<T> success, OnLoading loading, OnError error) {
        this.success = success;
        this.loading = loading;
        this.error = error;
    }

    @Override
    public void onChanged(@Nullable ResourceWrapper<T> resource) {
        if (resource instanceof ResourceWrapper.Loading) {
            loading.onLoading();
        } else if (resource instanceof ResourceWrapper.Success) {
            success.onSuccess(((ResourceWrapper.Success<T>) resource).data);
        } else if (resource instanceof ResourceWrapper.Error) {
            Throwable exception = ((ResourceWrapper.Error<FirebaseUser>) resource).throwable;
            String errorMessage = ((ResourceWrapper.Error<FirebaseUser>) resource).errorMessage;
            int errorCode = ((ResourceWrapper.Error<FirebaseUser>) resource).errorCode;
            error.onError(exception, errorCode, errorMessage);
        }
    }
}
