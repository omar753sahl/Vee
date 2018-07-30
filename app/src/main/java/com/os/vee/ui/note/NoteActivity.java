package com.os.vee.ui.note;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.os.vee.base.BaseActivity;
import com.os.vee.data.notes.Note;
import com.os.vee.utils.resource.ResourceObserver;
import com.os.vento.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.mthli.knife.KnifeText;
import io.reactivex.BackpressureStrategy;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class NoteActivity extends BaseActivity {

    private final static String NOTE_EXTRA_KEY = "arg:optionalNote";


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.editor)
    KnifeText editor;

    @BindView(R.id.noteTitleInput)
    EditText noteTitleInput;

    @BindView(R.id.editor_bold)
    ImageView editorBold;

    @BindView(R.id.editor_bullet)
    ImageView editorBullet;

    @BindView(R.id.editor_clear)
    ImageView editorClear;

    @BindView(R.id.editor_italic)
    ImageView editorItalic;

    @BindView(R.id.editor_link)
    ImageView editorLink;

    @BindView(R.id.editor_quote)
    ImageView editorQuote;

    @BindView(R.id.editor_strikethrough)
    ImageView editorStrikthrough;

    @BindView(R.id.editor_underline)
    ImageView editorUnderline;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    MenuItem saveItem;

    Disposable disposable = null;

    private AddNoteViewModel viewModel;
    private Note note = null;
    private boolean newNote = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        ButterKnife.bind(this);

        getWindow().getAttributes().windowAnimations = R.style.AddNoteAnimation;

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(AddNoteViewModel.class);

        Parcelable extra = getIntent().getParcelableExtra(NOTE_EXTRA_KEY);
        if (getIntent().hasExtra(NOTE_EXTRA_KEY) && extra != null) {
            note = (Note) extra;
            newNote = false;
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        setupEditor();

        progressBar.setVisibility(View.INVISIBLE);

        setupEditorButtons();
    }

    private void setupEditor() {
        if (newNote) {
            editor.setHint(getResources().getText(R.string.note_editor_hint));
        } else {
            editor.fromHtml(note.content);
            noteTitleInput.setText(note.title);
        }
    }

    private void setupEditorButtons() {
        editorBold.setOnClickListener(v -> editor.bold(!editor.contains(KnifeText.FORMAT_BOLD)));
        editorBold.setOnLongClickListener(v -> {
            Toast.makeText(this, R.string.toast_bold, Toast.LENGTH_SHORT).show();
            return true;
        });

        editorItalic.setOnClickListener(v -> editor.italic(!editor.contains(KnifeText.FORMAT_ITALIC)));
        editorItalic.setOnLongClickListener(v -> {
            Toast.makeText(this, R.string.toast_italic, Toast.LENGTH_SHORT).show();
            return true;
        });

        editorUnderline.setOnClickListener(v -> editor.underline(!editor.contains(KnifeText.FORMAT_UNDERLINED)));
        editorUnderline.setOnLongClickListener(v -> {
            Toast.makeText(this, R.string.toast_underline, Toast.LENGTH_SHORT).show();
            return true;
        });

        editorStrikthrough.setOnClickListener(v -> editor.strikethrough(!editor.contains(KnifeText.FORMAT_STRIKETHROUGH)));
        editorStrikthrough.setOnLongClickListener(v -> {
            Toast.makeText(this, R.string.toast_strikethrough, Toast.LENGTH_SHORT).show();
            return true;
        });

        editorBullet.setOnClickListener(v -> editor.bullet(!editor.contains(KnifeText.FORMAT_BULLET)));
        editorBullet.setOnLongClickListener(v -> {
            Toast.makeText(this, R.string.toast_bullet, Toast.LENGTH_SHORT).show();
            return true;
        });

        editorQuote.setOnClickListener(v -> editor.quote(!editor.contains(KnifeText.FORMAT_QUOTE)));
        editorQuote.setOnLongClickListener(v -> {
            Toast.makeText(this, R.string.toast_quote, Toast.LENGTH_SHORT).show();
            return true;
        });

        editorLink.setOnClickListener(v -> showLinkDialog());
        editorLink.setOnLongClickListener(v -> {
            Toast.makeText(this, R.string.toast_insert_link, Toast.LENGTH_SHORT).show();
            return true;
        });

        editorClear.setOnClickListener(v -> editor.clearFormats());
        editorClear.setOnLongClickListener(v -> {
            Toast.makeText(this, R.string.toast_format_clear, Toast.LENGTH_SHORT).show();
            return true;
        });

    }

    private void showLinkDialog() {
        final int start = editor.getSelectionStart();
        final int end = editor.getSelectionEnd();

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.EditorLinkDialog);
        builder.setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.dialog_link, null, false);
        final EditText editText = view.findViewById(R.id.edit);
        builder.setView(view);
        builder.setTitle(R.string.link_dialog_title);

        builder.setPositiveButton(R.string.dialog_button_ok, (dialog, which) -> {
            String link = editText.getText().toString().trim();
            if (TextUtils.isEmpty(link)) {
                return;
            }

            editor.link(link, start, end);
        });

        builder.setNegativeButton(R.string.dialog_button_cancel, null);

        builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_note_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        saveItem = menu.findItem(R.id.action_save_note);

        disposable = RxTextView.textChanges(noteTitleInput)
                .map(text -> !TextUtils.isEmpty(text))
                .toFlowable(BackpressureStrategy.LATEST)
                .distinctUntilChanged()
                .subscribe(valid -> saveItem.setEnabled(valid));

        return true;
    }

    @Override
    protected void onDestroy() {
        if (disposable != null) disposable.dispose();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
            case R.id.action_save_note: {
                saveNote();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public void onBackPressed() {
        checkChanges();
    }

    private void checkChanges() {
        if (newNote && (!TextUtils.isEmpty(editor.getText()) || !TextUtils.isEmpty(noteTitleInput.getText()))) {
            showDiscardConfirmationDialog();
        } else if (note != null && (!editor.toHtml().equals(note.content) || !noteTitleInput.getText().toString().equals(note.title))) {
            showDiscardConfirmationDialog();
        } else {
            finish();
        }
    }

    private void showDiscardConfirmationDialog() {
        new AlertDialog.Builder(this, R.style.EditorLinkDialog)
                .setTitle("Discard changes?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialogInterface, i) -> finish())
                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }

    private void saveNote() {
        Timber.d("Editor contents=%s", editor.toHtml());
        String content = TextUtils.isEmpty(editor.toHtml()) ? getText(R.string.note_editor_hint).toString() : editor.toHtml();
        String title = noteTitleInput.getText().toString();

        saveItem.setEnabled(false);
        progressBar.setVisibility(View.INVISIBLE);

        if (newNote) {
            note = new Note("", viewModel.getCurrentUser().getUid(), title, content);
            viewModel.addNote(note)
                    .observe(this, new ResourceObserver<>(result -> {
                        saveItem.setEnabled(false);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(this, R.string.note_saved, Toast.LENGTH_SHORT).show();
                        finish();
                    }, () -> {
                        saveItem.setEnabled(false);
                        progressBar.setVisibility(View.VISIBLE);
                    }, (e, errorCode, errorMessage) -> {
                        Timber.e(e, "Error saving note");
                        Toast.makeText(this, R.string.error_saving_note, Toast.LENGTH_SHORT).show();
                        saveItem.setEnabled(true);
                        progressBar.setVisibility(View.INVISIBLE);
                    }));
        } else {
            note.title = title;
            note.content = content;
            viewModel.updateNote(note)
                    .observe(this, new ResourceObserver<>(result -> {
                        saveItem.setEnabled(false);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(this, R.string.note_saved, Toast.LENGTH_SHORT).show();
                        finish();
                    }, () -> {
                        saveItem.setEnabled(false);
                        progressBar.setVisibility(View.VISIBLE);
                    }, (e, errorCode, errorMessage) -> {
                        Timber.e(e, "Error updating note");
                        Toast.makeText(this, R.string.error_saving_note, Toast.LENGTH_SHORT).show();
                        saveItem.setEnabled(true);
                        progressBar.setVisibility(View.INVISIBLE);
                    }));
        }

    }

    public static Intent createIntent(Context context, Note note) {
        Intent intent = new Intent(context, NoteActivity.class);
        intent.putExtra(NOTE_EXTRA_KEY, note);
        return intent;
    }
}
