package com.os.vee.ui.home.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.os.vee.data.notes.Note;
import com.os.vee.ui.home.fragment.NotesFragment;
import com.os.vento.R;
import com.os.vento.databinding.NoteItemLayoutBinding;

import java.util.List;

/**
 * Created by Omar on 15-Jul-18 9:59 PM.
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private Context context;
    private List<Note> notes;
    private NotesFragment.OnNoteSelectedListener listener;
    private NoteActionListener noteActionListener;

    public NotesAdapter(Context context, NotesFragment.OnNoteSelectedListener listener, NoteActionListener noteActionListener) {
        this.context = context;
        this.listener = listener;
        this.noteActionListener = noteActionListener;
    }

    public void updateNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.note_item_layout, parent, false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.bind(notes.get(position));
    }

    @Override
    public int getItemCount() {
        return notes == null ? 0 : notes.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private final NoteItemLayoutBinding binding;

        public NoteViewHolder(NoteItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Note note) {
            binding.getRoot().setOnClickListener(this);
            binding.getRoot().setOnLongClickListener(this);
            binding.actionMenuButton.setOnClickListener(this::onLongClick);
            binding.setNote(note);
            binding.executePendingBindings();
        }

        @Override
        public void onClick(View view) {
            listener.onNoteSelected(notes.get(getAdapterPosition()));
        }

        @Override
        public boolean onLongClick(View view) {
            PopupMenu popup = new PopupMenu(context, binding.actionMenuButton);
            popup.inflate(R.menu.note_item_menu);
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.action_edit:
                        noteActionListener.onNoteActionClicked(notes.get(getAdapterPosition()), R.id.action_edit);
                        break;
                    case R.id.action_delete:
                        noteActionListener.onNoteActionClicked(notes.get(getAdapterPosition()), R.id.action_delete);
                        break;
                }
                return false;
            });

            popup.show();
            return true;
        }
    }

    public interface NoteActionListener {
        void onNoteActionClicked(Note note, int action);
    }
}
