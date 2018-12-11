package ru.voronezhtsev.contentproviderlesson;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.voronezhtsev.contentproviderlesson.data.Note;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {

    private final List<Note> mNotes;
    public NotesAdapter(List<Note> notes) {
        mNotes = notes;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note, parent, false);

        return new NotesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        holder.mDataTextView.setText(mNotes.get(position).getNote());
        final int pos = position;
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public static class NotesViewHolder extends RecyclerView.ViewHolder {

        TextView mDataTextView;

        public NotesViewHolder(View itemView) {
            super(itemView);
            mDataTextView = itemView.findViewById(R.id.note_data_textView);
        }
    }
}
