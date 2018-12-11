package ru.voronezhtsev.contentproviderlesson;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import ru.voronezhtsev.contentproviderlesson.data.Note;

/**
 * Утилитный класс, позволяет конвертировать курсор БД {@link Cursor} в список заметок
 * и {@link ContentValues} в заметку
 */
public class ConvertUtils {
    private static final String ID = "id";
    private static final String NOTE = "note";

    static Note convertToNote(ContentValues values) {
        return new Note(values.getAsInteger(ID), values.getAsString(NOTE));
    }

    static List<Note> convertToNotes(Cursor cursor) {
        List<Note> notes = new ArrayList<>();
        while (cursor.moveToNext()) {
            Note note = new Note();
            note.setId(cursor.getInt(cursor.getColumnIndex(ID)));
            note.setNote(cursor.getString(cursor.getColumnIndex(NOTE)));
            notes.add(note);
        }
        cursor.close();
        return notes;
    }
}
