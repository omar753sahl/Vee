package com.os.vee.base;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.os.vee.ui.auth.LoginActivity;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

/**
 * Created by Omar on 14-Jul-18 12:52 AM.
 */
public abstract class BaseActivity extends DaggerAppCompatActivity {

    @Inject
    protected ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BaseViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(BaseViewModel.class);
        viewModel.user().observe(this, user -> {
            if (user == null) {
                startActivity(LoginActivity.createIntent(this).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        });
    }
}
