package com.os.vee.ui.home.fragment;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.os.vee.ui.home.HomeViewModel;
import com.os.vee.utils.resource.ResourceObserver;
import com.os.vento.R;
import com.os.vento.databinding.FragmentProfileBinding;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;
import io.reactivex.BackpressureStrategy;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;


public class ProfileFragment extends DaggerFragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private HomeViewModel viewModel;
    private FragmentProfileBinding binding;

    @BindView(R.id.logoutButton)
    Button logoutButton;

    @BindView(R.id.editProfileFab)
    FloatingActionButton editProfileFab;

    @BindView(R.id.userDisplayName)
    EditText userDisplayNameInput;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private InputMethodManager imm = null;
    private CompositeDisposable disposable = new CompositeDisposable();

    public ProfileFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, binding.getRoot());
        setupUi();
        return binding.getRoot();
    }

    private void setupUi() {
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        logoutButton.setOnClickListener(view -> viewModel.logout());
        editProfileFab.setOnClickListener(view -> viewModel.nextState());
    }

    @Override
    public void onStart() {
        super.onStart();

        viewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(HomeViewModel.class);
        viewModel.user().observe(this, user -> binding.setUser(user));

        viewModel.getState().observe(this, new ResourceObserver<>(result -> {
            Timber.d("Current state = %s", result.name());
            switch (result) {
                case INITIAL:
                    // Do nothing!
                    break;
                case EDITING:
                    enableEditing();
                    break;
                case SAVING:
                    saveProfileInfo();
                    break;
                case DONE:
                    disableEditing();
            }
        }, () -> {
        }, (e, errorCode, errorMessage) -> {
            Timber.e(e, "Unexpected error during state transitions!");
        }));
    }

    private void enableEditing() {
        disposable.add(
                RxTextView.textChanges(userDisplayNameInput)
                        .skipInitialValue()
                        .map(name -> !TextUtils.isEmpty(name))
                        .toFlowable(BackpressureStrategy.LATEST)
                        .distinctUntilChanged()
                        .subscribe(valid -> {
                            editProfileFab.setEnabled(valid);
                        })
        );

        userDisplayNameInput.setEnabled(true);
        userDisplayNameInput.requestFocus();
        userDisplayNameInput.selectAll();
        imm.showSoftInput(userDisplayNameInput, InputMethodManager.SHOW_IMPLICIT);
        editProfileFab.setImageResource(R.drawable.ic_done);
    }

    private void disableEditing() {
        imm.hideSoftInputFromWindow(userDisplayNameInput.getWindowToken(), 0);
        userDisplayNameInput.clearFocus();
        userDisplayNameInput.setEnabled(false);
        editProfileFab.setImageResource(R.drawable.ic_edit);
    }

    private void saveProfileInfo() {
        viewModel.updateUserInfo(userDisplayNameInput.getText().toString())
                .observe(this, new ResourceObserver<>(result -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    editProfileFab.setEnabled(true);
                    viewModel.nextState(); // go to DONE to disable editing
                    viewModel.nextState(); // reset to INITIAL
                }, () -> {
                    progressBar.setVisibility(View.VISIBLE);
                    editProfileFab.setEnabled(false);
                }, (e, errorCode, errorMessage) -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    editProfileFab.setEnabled(true);
                    Timber.e(e, "Error updating user profile");
                    Toast.makeText(getActivity(), R.string.error_updating_profile, Toast.LENGTH_SHORT).show();
                }));
    }

    @Override
    public void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }
}
