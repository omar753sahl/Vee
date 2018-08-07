package com.os.vee.ui.auth;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.os.vee.base.ViewModelFactory;
import com.os.vee.utils.Utils;
import com.os.vee.utils.resource.ResourceWrapper;
import com.os.vee.utils.scheduler.BaseSchedulerProvider;
import com.os.vento.R;
import com.os.vento.databinding.ActivitySignUpBinding;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

import static com.os.vee.utils.Constants.INPUT_DEBOUNCE_TIME_MILLISECONDS;

public class SignUpActivity extends DaggerAppCompatActivity {

    @Inject
    BaseSchedulerProvider schedulers;

    @Inject
    ViewModelFactory viewModelFactory;
    private SignUpViewModel viewModel;

    private ActivitySignUpBinding binding;

    private TextInputEditText emailEditText;
    private TextInputEditText nameEditText;
    private TextInputEditText passwordEditText;
    private TextInputLayout emailInputLayout;
    private TextInputLayout nameInputLayout;
    private TextInputLayout passwordInputLayout;

    private CompositeDisposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        binding.setValid(false);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        nameEditText = binding.nameEditText;
        nameInputLayout = binding.nameInputLayout;
        emailEditText = binding.emailEditText;
        emailInputLayout = binding.emailInputLayout;
        passwordEditText = binding.passwordEditText;
        passwordInputLayout = binding.passwordInputLayout;

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SignUpViewModel.class);

        disposable = new CompositeDisposable();

        setupFormValidation();

        binding.signUpButton.setOnClickListener((view -> firebaseSignUpWithEmail()));
    }

    private void firebaseSignUpWithEmail() {
        viewModel.signUpWithEmail(nameEditText.getText().toString(), emailEditText.getText().toString(), passwordEditText.getText().toString())
                .observe(this, resource -> {
                    if (resource instanceof ResourceWrapper.Loading) {
                        binding.setLoading(true);
                    } else if (resource instanceof ResourceWrapper.Success) {
                        binding.setLoading(false);
                        finish();
                    } else if (resource instanceof ResourceWrapper.Error) {
                        Throwable exception = ((ResourceWrapper.Error<FirebaseUser>) resource).throwable;
                        Timber.e(exception, "Error creating account");
                        binding.setLoading(false);

                        String message = getString(R.string.error_signup_unknown_error);

                        if (exception instanceof FirebaseAuthException) {
                            message = Utils.getMessageFromFirebaseException((FirebaseAuthException) exception, this);
                        }

                        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    private void setupFormValidation() {
        disposable.add(Flowable.combineLatest(
                RxTextView.textChanges(nameEditText)
                        .skipInitialValue()
                        .debounce(INPUT_DEBOUNCE_TIME_MILLISECONDS, TimeUnit.MILLISECONDS)
                        .observeOn(schedulers.main())
                        .map(Utils::isValidName)
                        .toFlowable(BackpressureStrategy.LATEST)
                        .distinctUntilChanged()
                        .doOnNext(valid -> {
                            if (valid)
                                nameInputLayout.setError(null);
                            else
                                nameInputLayout.setError(getString(R.string.invalid_name_error));

                        }),

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

                (b1, b2, b3) -> b1 && b2 && b3).subscribe((isValid -> binding.setValid(isValid)))
        );
    }

    @Override
    protected void onDestroy() {
        if (disposable != null) disposable.dispose();
        super.onDestroy();
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, SignUpActivity.class);
    }
}
