package com.os.vee.ui.splash;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.os.vee.ui.auth.LoginActivity;
import com.os.vento.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        startActivity(LoginActivity.createIntent(this));
        finish();
    }
}
