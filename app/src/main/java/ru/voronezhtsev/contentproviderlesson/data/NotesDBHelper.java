package ru.voronezhtsev.contentproviderlesson.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.voronezhtsev.contentproviderlesson.NotesContentProvider;

public class NotesDBHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String NAME = "notes.db";
    private static final String CREATE_TABLE_DDL =
            "create table if not exists notes(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "note TEXT)";

    public NotesDBHelper(Context context) {
        this(context, NAME, null, VERSION);
    }

    public NotesDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_DDL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
