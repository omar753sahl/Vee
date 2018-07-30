package com.os.vee.di.module;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.os.vee.base.BaseViewModel;
import com.os.vee.base.ViewModelFactory;
import com.os.vee.base.ViewModelKey;
import com.os.vee.ui.auth.LoginViewModel;
import com.os.vee.ui.auth.SignUpViewModel;
import com.os.vee.ui.home.HomeViewModel;
import com.os.vee.ui.note.AddNoteViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Created by Omar on 14-Jul-18 1:00 AM.
 */

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(viewModel = BaseViewModel.class)
    abstract ViewModel bindBaseViewModel(BaseViewModel baseViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(viewModel = LoginViewModel.class)
    abstract ViewModel bindLoginViewModel(LoginViewModel loginViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(viewModel = SignUpViewModel.class)
    abstract ViewModel bindSignUpViewModel(SignUpViewModel signUpViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(viewModel = HomeViewModel.class)
    abstract ViewModel bindHomeViewModel(HomeViewModel homeViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(viewModel = AddNoteViewModel.class)
    abstract ViewModel bindAddNoteViewModel(AddNoteViewModel addNoteViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}
