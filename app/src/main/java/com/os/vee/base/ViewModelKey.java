package com.os.vee.base;

import android.arch.lifecycle.ViewModel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import dagger.MapKey;

/**
 * Created by Omar on 14-Jul-18 1:02 AM.
 */

@Target(
        value = {
                ElementType.METHOD,
        }
)
@Retention(RetentionPolicy.RUNTIME)
@MapKey
public @interface ViewModelKey {
        Class<? extends ViewModel> viewModel();
}
