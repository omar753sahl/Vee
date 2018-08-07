package com.os.vee.ui.auth;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.os.vee.base.ViewModelFactory;
import com.os.vee.ui.home.HomeActivity;
import com.os.vee.utils.Utils;
import com.os.vee.utils.resource.ResourceWrapper;
import com.os.vee.utils.scheduler.BaseSchedulerProvider;
import com.os.vento.R;
import com.os.vento.databinding.ActivityLoginBinding;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

import static com.os.vee.utils.Constants.INPUT_DEBOUNCE_TIME_MILLISECONDS;

public class LoginActivity extends DaggerAppCompatActivity {

    private final static int RC_GOOGLE_SIGN_IN = 101;

    @Inject
    BaseSchedulerProvider schedulers;

    @Inject
    ViewModelFactory viewModelFactory;
    private LoginViewModel viewModel;

    private ActivityLoginBinding binding;

    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextInputLayout emailInputLayout;
    private TextInputLayout passwordInputLayout;

    private CompositeDisposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setValid(false);

        emailEditText = binding.emailEditText;
        emailInputLayout = binding.emailInputLayout;
        passwordEditText = binding.passwordEditText;
        passwordInputLayout = binding.passwordInputLayout;

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel.class);
        viewModel.user().observe(this, user -> {
            if (user != null) {
                startActivity(HomeActivity.createIntent(this));
                finish();
            }
        });

        disposable = new CompositeDisposable();

        setupFormValidation();

        binding.createAccountButton.setOnClickListener(view -> startActivity(SignUpActivity.createIntent(this)));
        binding.signInButton.setOnClickListener((view -> firebaseAuthWithEmail()));
        binding.googleSignInButton.setOnClickListener(view -> triggerGoogleAuth());
    }

    private void triggerGoogleAuth() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();

        startActivityForResult(GoogleSignIn.getClient(this, gso).getSignInIntent(), RC_GOOGLE_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RC_GOOGLE_SIGN_IN: {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account);
                } catch (ApiException e) {
                    Timber.e(e, "Error signing in with google");
                    Snackbar.make(binding.getRoot(), R.string.google_login_error, Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Timber.d("firebaseAuthWithGoogle: %s", account.getId());

        viewModel.signInWitGoogle(account).observe(this, resource -> {
            if (resource instanceof ResourceWrapper.Loading) {
                binding.setLoading(true);
            } else if (resource instanceof ResourceWrapper.Success) {
                binding.setLoading(false);
            } else if (resource instanceof ResourceWrapper.Error) {
                Throwable exception = ((ResourceWrapper.Error<FirebaseUser>) resource).throwable;
                Timber.e(exception, "Error authenticating with google");
                binding.setLoading(false);

                String message = getString(R.string.error_login_unknown_error);

                if (exception instanceof FirebaseAuthException) {
                    message = Utils.getMessageFromFirebaseException((FirebaseAuthException) exception, this);
                }

                Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void firebaseAuthWithEmail() {
        viewModel.signInWitEmail(emailEditText.getText().toString(), passwordEditText.getText().toString())
                .observe(this, resource -> {
                    if (resource instanceof ResourceWrapper.Loading) {
                        binding.setLoading(true);
                    } else if (resource instanceof ResourceWrapper.Success) {
                        binding.setLoading(false);
                    } else if (resource instanceof ResourceWrapper.Error) {
                        Throwable exception = ((ResourceWrapper.Error<FirebaseUser>) resource).throwable;
                        Timber.e(exception, "Error authenticating with email");
                        binding.setLoading(false);

                        String message = getString(R.string.error_login_unknown_error);

                        if (exception instanceof FirebaseAuthException) {
                            message = Utils.getMessageFromFirebaseException((FirebaseAuthException) exception, this);
                        }

                        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    private void setupFormValidation() {
        disposable.add(Flowable.combineLatest(
                RxTextView.textChanges(emailEditText)
                        .skipInitialValue()
                        .debounce(INPUT_DEBOUNCE_TIME_MILLISECONDS, TimeUnit.MILLISECONDS)
                        .observeOn(schedulers.main())
                        .map(Utils::isValidEmail)
                        .toFlowable(BackpressureStrategy.LATEST)
                        .distinctUntilChanged()
                        .doOnNext(valid -> {
                            if (valid)
                                emailInputLayout.setError(null);
                            else
                                emailInputLayout.setError(getString(R.string.invalid_email_error));

                        }),

                RxTextView.textChanges(passwordEditText)
                        .skipInitialValue()
                        .debounce(INPUT_DEBOUNCE_TIME_MILLISECONDS, TimeUnit.MILLISECONDS)
                        .observeOn(schedulers.main())
                        .map(Utils::isValidPassword)
                        .toFlowable(BackpressureStrategy.LATEST)
                        .distinctUntilChanged()
                        .doOnNext(valid -> {
                            if (valid)
                                passwordInputLayout.setError(null);
                            else
                                passwordInputLayout.setError(getString(R.string.invalid_password_error, 6));

                        }),

                (b1, b2) -> b1 && b2).subscribe((isValid -> binding.setValid(isValid)))
        );
    }

    @Override
    protected void onDestroy() {
        if (disposable != null) disposable.dispose();
        super.onDestroy();
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }
}