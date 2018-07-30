package com.os.vee.utils.resource;

/**
 * Created by Omar on 15-Jul-18 8:39 PM.
 */

@FunctionalInterface
public interface OnError {

    void onError(Throwable e, int errorCode, String errorMessage);
}
