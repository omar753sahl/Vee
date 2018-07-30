package com.os.vee.di.module;

import com.os.vee.ui.auth.LoginActivity;
import com.os.vee.ui.auth.SignUpActivity;
import com.os.vee.ui.home.HomeActivity;
import com.os.vee.ui.note.NoteActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * Created by Omar on 14-Jul-18 1:53 AM.
 */

@Module
public abstract class ActivityModule {

    @ContributesAndroidInjector(modules = {AndroidSupportInjectionModule.class})
    abstract HomeActivity homeActivity();

    @ContributesAndroidInjector(modules = {AndroidSupportInjectionModule.class})
    abstract LoginActivity loginActivity();

    @ContributesAndroidInjector(modules = {AndroidSupportInjectionModule.class})
    abstract SignUpActivity signUpActivity();

    @ContributesAndroidInjector(modules = {AndroidSupportInjectionModule.class})
    abstract NoteActivity addNoteActivity();
}
