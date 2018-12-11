package ru.voronezhtsev.contentproviderlesson.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class NotesDAO {
    public static final String NOTES_TABLE = "notes";
    public static final int NOT_INSERTED_ID = -1;
    private static final String TAG = "NotesDAO";
    private static final String ERR_GET_NOTES = "Error while getting notes from DB";

    public static final String ID_COLUMN = "id";
    private static final String NOTES_COLUMN = "note";
    private static final String NOTE_BY_ID_SQL = "select * from " + NOTES_TABLE +
            " where " + NOTES_COLUMN + "= ?";
    private static final String UPDATE_WHERE_CLAUSE = "id = ?";

    private static final String ERR_GET_NOTE = "Error while getting note by id";
    private static final String ERR_ADD_NOTE = "Error while adding new note ";
    private static final String ERR_UPDATE_NOTE = "Error while updating note ";
    private static final String ERR_DELETE_NOTE = "Error while delete note ";

    private NotesDBHelper mNotesDBHelper;

    public NotesDAO(NotesDBHelper notesDBHelper) {
        mNotesDBHelper = notesDBHelper;
    }

    /**
     * Получить список всех заметок из БД
     * @return список заметок {@link Note}
     */
    public List<Note> getNotes() {
        SQLiteDatabase database = null;
        List<Note> notes = new ArrayList<>();
        try {
            database = mNotesDBHelper.getReadableDatabase();
            Cursor cursor = database.query(NOTES_TABLE,
                    null, null, null,
                    null, null, null);

            if (cursor.moveToFirst()) {
                int idColIdx = cursor.getColumnIndex(ID_COLUMN);
                int noteColIdx = cursor.getColumnIndex(NOTES_COLUMN);

                while (!cursor.isAfterLast()) {
                    Note note = new Note(cursor.getInt(idColIdx), cursor.getString(noteColIdx));
                    notes.add(note);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        } catch (SQLException e) {
            Log.e(TAG, ERR_GET_NOTES, e);
        }
        finally {
            if(database != null) {
                database.close();
            }
        }
        return notes;
    }

    /**
     * Вернуть заметку по идентификатору
     * @param id идентификатор заметки {@link Note#id}
     * @return заметка
     */
    public Note getNote(int id) {
        SQLiteDatabase database = null;
        Note note = new Note();
        try {
            database = mNotesDBHelper.getReadableDatabase();
            Cursor cursor = database.rawQuery(NOTE_BY_ID_SQL, new String[]{String.valueOf(id)});
            if (cursor.moveToFirst()) {
                int idColIdx = cursor.getColumnIndex(ID_COLUMN);
                int noteColIdx = cursor.getColumnIndex(NOTES_COLUMN);
                note.setId(cursor.getInt(idColIdx));
                note.setNote(cursor.getString(noteColIdx));
            }
            cursor.close();
        } catch (SQLException e) {
            Log.e(TAG, ERR_GET_NOTE + id, e);
        } finally {
            if(database != null) {
                database.close();
            }
        }
        return note;
    }

    /**
     * Добавить новую заметку в БД
     * @param note заметка
     */
    public int addNote(Note note) {
        SQLiteDatabase database = null;
        int id = NOT_INSERTED_ID;
        try {
            database = mNotesDBHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(NOTES_COLUMN, note.getNote());
            id = (int) database.insert(NOTES_TABLE, null, contentValues);
        } catch (SQLException e) {
            Log.e(TAG, ERR_ADD_NOTE + note.getNote(), e);
        } finally {
            if (database != null) {
                database.close();
            }
        }
        return id;
    }
    public int updateNote(Note note) {
        SQLiteDatabase database = null;
        int rowsAffected = 0;
        try {
            database = mNotesDBHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(NOTES_COLUMN, note.getNote());
            rowsAffected = database.update(NOTES_TABLE,contentValues, UPDATE_WHERE_CLAUSE,
                    new String[]{String.valueOf(note.getId())});
        } catch (SQLException e) {
            Log.e(TAG, ERR_UPDATE_NOTE + note.getNote(), e);
        } finally {
            if(database != null) {
                database.close();
            }
        }
        return rowsAffected;
    }

    public int deleteNote(long id) {
        SQLiteDatabase database = null;
        int rowsAffected = 0;
        try {
            database = mNotesDBHelper.getWritableDatabase();
            rowsAffected = database.delete(NOTES_TABLE, ID_COLUMN + " = ?",
                    new String[]{String.valueOf(id)});
        } catch (SQLException e) {
            Log.e(TAG, ERR_DELETE_NOTE + id, e);
        } finally {
            if(database != null) {
                database.close();
            }
        }
        return rowsAffected;
    }
}
