package com.os.vee.di.component;

import android.app.Application;

import com.os.vee.VeeApp;
import com.os.vee.di.module.ActivityModule;
import com.os.vee.di.module.AppModule;
import com.os.vee.di.module.FragmentModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * Created by Omar on 13-Jul-18 11:52 PM.
 */

@Singleton
@Component(
        modules = {
                AndroidSupportInjectionModule.class,
                AppModule.class,
                ActivityModule.class,
                FragmentModule.class
        }
)
public interface AppComponent extends AndroidInjector<DaggerApplication> {

    public void inject(VeeApp app);

    @Component.Builder
    interface Builder {

        AppComponent build();

        @BindsInstance
        Builder application(Application app);
    }
}
