package com.os.vee.di.module;

import com.os.vee.ui.home.fragment.NotesFragment;
import com.os.vee.ui.home.fragment.ProfileFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * Created by Omar on 15-Jul-18 9:52 PM.
 */

@Module
public abstract class FragmentModule {

    @ContributesAndroidInjector(modules = {AndroidSupportInjectionModule.class})
    abstract NotesFragment notesFragment();

    @ContributesAndroidInjector(modules = {AndroidSupportInjectionModule.class})
    abstract ProfileFragment profileFragment();
}
