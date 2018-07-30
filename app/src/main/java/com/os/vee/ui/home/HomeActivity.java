package com.os.vee.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;

import com.os.vee.base.BaseActivity;
import com.os.vee.data.notes.Note;
import com.os.vee.ui.home.fragment.NotesFragment;
import com.os.vee.ui.home.fragment.ProfileFragment;
import com.os.vee.ui.note.NoteActivity;
import com.os.vento.R;

public class HomeActivity extends BaseActivity implements NotesFragment.OnNoteSelectedListener {

    private final String SELECTED_TAB = "SELECTED_TAB";
    private String NOTES_FRAGMENT_TAG = NotesFragment.class.getName();
    private String PROFILE_FRAGMENT_TAG = ProfileFragment.class.getName();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = item -> {
        switch (item.getItemId()) {
            case R.id.nav_notes:
                Fragment fragmentNotes = getSupportFragmentManager().findFragmentByTag(NOTES_FRAGMENT_TAG);
                if (fragmentNotes == null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer,
                                    new NotesFragment(),
                                    NOTES_FRAGMENT_TAG)
                            .commit();
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer,
                                    fragmentNotes,
                                    NOTES_FRAGMENT_TAG)
                            .commit();
                }
                return true;
            case R.id.nav_profile:
                Fragment fragmentProfile = getSupportFragmentManager().findFragmentByTag(PROFILE_FRAGMENT_TAG);
                if (fragmentProfile == null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer,
                                    new ProfileFragment(),
                                    PROFILE_FRAGMENT_TAG)
                            .commit();
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer,
                                    fragmentProfile,
                                    PROFILE_FRAGMENT_TAG)
                            .commit();
                }
                return true;
        }
        return false;
    };

    private BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        navigation = findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        int id = R.id.nav_notes;
        if (savedInstanceState != null) {
            id = savedInstanceState.getInt(SELECTED_TAB, R.id.nav_notes);
        }
        navigation.setSelectedItemId(id);
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, HomeActivity.class);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_TAB, navigation.getSelectedItemId());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onNoteSelected(Note note) {
        startActivity(NoteActivity.createIntent(this, note));
    }
}
