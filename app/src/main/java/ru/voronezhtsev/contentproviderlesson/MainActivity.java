package ru.voronezhtsev.contentproviderlesson;

import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import ru.voronezhtsev.contentproviderlesson.data.NotesDAO;

public class MainActivity extends AppCompatActivity {

    private static final String URI = "content://" + NotesContentProvider.AUTHOTITY;
    private static final Uri NOTES_URI = Uri.parse(
            "content://" + NotesContentProvider.AUTHOTITY + "/" + NotesDAO.NOTES_TABLE
    );

    private RecyclerView mNotesRecyclerView;
    private ContentObserver mContentObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNotesRecyclerView = findViewById(R.id.notes_recycler_view);
        mContentObserver = new NotesContentObserver(
                new Handler(Looper.getMainLooper()));
        mContentObserver.onChange(true, NOTES_URI);
        mNotesRecyclerView.setHasFixedSize(true);
        mNotesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        getContentResolver().registerContentObserver(Uri.parse(URI), true,
                mContentObserver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getContentResolver().unregisterContentObserver(mContentObserver);
    }

    class NotesContentObserver extends ContentObserver {

        public NotesContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            Cursor cursor = getContentResolver().query(NOTES_URI, null, null, null);
            if (cursor != null) {
                mNotesRecyclerView.setAdapter(new NotesAdapter(ConvertUtils.convertToNotes(cursor)));
            }
        }
    }
}
