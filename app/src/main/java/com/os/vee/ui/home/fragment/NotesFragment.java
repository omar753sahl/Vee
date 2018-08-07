package com.os.vee.ui.home.fragment;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.os.vee.data.notes.Note;
import com.os.vee.ui.home.HomeViewModel;
import com.os.vee.ui.home.adapter.NotesAdapter;
import com.os.vee.ui.note.NoteActivity;
import com.os.vee.utils.resource.ResourceObserver;
import com.os.vento.R;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerFragment;
import timber.log.Timber;


public class NotesFragment extends DaggerFragment implements NotesAdapter.NoteActionListener {

    private static final String LIST_STATE_KEY = "notesList.state";

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private HomeViewModel viewModel;

    private OnNoteSelectedListener listener;

    @BindView(R.id.noteList)
    RecyclerView notesList;
    private NotesAdapter notesAdapter;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.emptyView)
    LinearLayout emptyView;

    @BindView(R.id.addNoteFab)
    FloatingActionButton addNoteFab;

    private Bundle listState;

    public NotesFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notes, container, false);
        ButterKnife.bind(this, root);

        progressBar.setVisibility(View.GONE);
        notesAdapter = new NotesAdapter(getActivity(), listener, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        notesList.setLayoutManager(layoutManager);
        notesList.addItemDecoration(new DividerItemDecoration(notesList.getContext(), layoutManager.getOrientation()));
        notesList.setAdapter(notesAdapter);

        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(HomeViewModel.class);
        viewModel.user().observe(this, user -> {
            if (user != null)
                loadUserNotes(user.getUid());
        });

        if (savedInstanceState != null) {
            listState = savedInstanceState.getParcelable(LIST_STATE_KEY);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(LIST_STATE_KEY, notesList.getLayoutManager().onSaveInstanceState());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            listState = savedInstanceState.getParcelable(LIST_STATE_KEY);
        }
    }

    private void loadUserNotes(String uid) {
        progressBar.setVisibility(View.VISIBLE);

        viewModel.getUserNotes(uid)
                .observe(this, new ResourceObserver<>(notes -> {
                    progressBar.setVisibility(View.GONE);
                    setEmptyViewState(notes.size() == 0);
                    notesAdapter.updateNotes(notes);

                    if (listState != null) {
                        Timber.d("Restoring list position");
                        notesList.getLayoutManager().onRestoreInstanceState(listState);
                    } else {
                        Timber.d("List state is null!!");
                    }
                }, () -> {
                    progressBar.setVisibility(View.VISIBLE);
                }, (e, errorCode, errorMessage) -> {
                    progressBar.setVisibility(View.GONE);
                    Timber.e(e, "Error loading notes");
                    Snackbar.make(notesList, R.string.error_loading_notes, Snackbar.LENGTH_SHORT).show();
                }));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNoteSelectedListener) {
            listener = (OnNoteSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnNoteSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @OnClick(R.id.addNoteFab)
    public void addNote(View view) {
        startActivity(NoteActivity.createIntent(getActivity(), null));
    }

    private void setEmptyViewState(boolean enabled) {
        emptyView.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onNoteActionClicked(Note note, int action) {
        switch (action) {
            case R.id.action_edit: {
                listener.onNoteSelected(note);
                break;
            }
            case R.id.action_delete: {
                viewModel.deleteNote(note)
                        .observe(this, new ResourceObserver<>(result -> {
                            Toast.makeText(getActivity(), R.string.note_deleted, Toast.LENGTH_SHORT).show();
                        }, () -> {
                        }, (e, errorCode, errorMessage) -> {
                            Timber.e(e, "Error deleting note");
                            Toast.makeText(getActivity(), R.string.error_deleting_note, Toast.LENGTH_SHORT).show();
                        }));
                break;
            }
        }
    }

    public interface OnNoteSelectedListener {
        void onNoteSelected(Note note);
    }
}
