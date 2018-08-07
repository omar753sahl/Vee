package com.os.vee;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.stetho.Stetho;
import com.os.vee.di.component.DaggerAppComponent;
import com.os.vento.BuildConfig;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import dagger.android.HasActivityInjector;
import timber.log.Timber;

/**
 * Created by Omar on 13-Jul-18 11:54 PM.
 */
public class VeeApp extends DaggerApplication implements HasActivityInjector {

    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initializeWithDefaults(this);
        if (BuildConfig.DEBUG) Timber.plant(new Timber.DebugTree());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().application(this).build();
    }
}
