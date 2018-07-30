package com.os.vee.utils.resource;

/**
 * Created by Omar on 15-Jul-18 8:42 PM.
 */
@FunctionalInterface
public interface OnSuccess<T> {

    void onSuccess(T result);
}
