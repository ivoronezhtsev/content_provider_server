package ru.voronezhtsev.contentproviderlesson;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import ru.voronezhtsev.contentproviderlesson.data.NotesDAO;
import ru.voronezhtsev.contentproviderlesson.data.NotesDBHelper;

/**
 * Контент провайдер, провайдит заметки блокнота
 *
 * @author Воронежцев Игорь on 11.12.2018
 */
public class NotesContentProvider extends ContentProvider {
    public static final String AUTHOTITY = "ru.voronezhtsev.contentproviderlesson";
    private static final String ADD_NOTE_URI = "content://" + AUTHOTITY + "/" + NotesDAO.NOTES_TABLE
            + "/add";
    private static final String DELETE_NOTE = NotesDAO.NOTES_TABLE + "/delete";
    private static final String UPDATE_NOTE_PATH = NotesDAO.NOTES_TABLE
            + "/update/#";
    private static final String TAG = "NotesContentProvider";
    private static final String QUERY_ERR = "Error while query database";
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int NOTES = 1;
    private static final int NOTE_ID = 2;
    private static final int UPDATE_NOTE_CODE = 3;
    private static final int DELETE_NOTE_CODE = 4;
    private static final String UNKNOWN_URI = "Unknown URI: ";
    private static final String INIT_ERR = "Error while init content provider";
    private static final String ERR_NO_ID = "Unable to delete note, id not specified";
    private NotesDAO mNotesDao;

    @Override
    public boolean onCreate() {
        NotesDBHelper notesDBHelper = new NotesDBHelper(getContext());
        try {
            SQLiteDatabase database = notesDBHelper.getWritableDatabase();
            mNotesDao = new NotesDAO(notesDBHelper);
            database.close();
            return true;
        } catch (SQLException e) {
            Log.e(TAG, INIT_ERR, e);
            return false;
        }
    }

    static {
        URI_MATCHER.addURI(AUTHOTITY, NotesDAO.NOTES_TABLE, NOTES);
        URI_MATCHER.addURI(AUTHOTITY, NotesDAO.NOTES_TABLE + "/#", NOTE_ID);
        URI_MATCHER.addURI(AUTHOTITY, UPDATE_NOTE_PATH, UPDATE_NOTE_CODE);
        URI_MATCHER.addURI(AUTHOTITY, DELETE_NOTE, DELETE_NOTE_CODE);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int id = mNotesDao.addNote(ConvertUtils.convertToNote(values));
        getContext().getContentResolver().notifyChange(uri, null);
        return id != NotesDAO.NOT_INSERTED_ID ? Uri.parse(ADD_NOTE_URI + id) : null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(NotesDAO.NOTES_TABLE);
        NotesDBHelper notesDBHelper = new NotesDBHelper(getContext());
        int uriType = URI_MATCHER.match(uri);

        switch (uriType) {
            case NOTE_ID:
                queryBuilder.appendWhere(
                        String.format("%s = %s", NotesDAO.ID_COLUMN, uri.getLastPathSegment()));
                break;
            case NOTES:
                break;
            default:
                throw new IllegalArgumentException(UNKNOWN_URI + uri);
        }
        try {
            Cursor cursor = queryBuilder.query(notesDBHelper.getReadableDatabase(), projection,
                    selection, selectionArgs, null, null, sortOrder);
            return cursor;
        } catch (SQLException e) {
            Log.e(TAG, QUERY_ERR, e);
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsDeleted = 0;
        int uriType = URI_MATCHER.match(uri);
        switch (uriType) {
            case DELETE_NOTE_CODE:
                if (selectionArgs == null || selectionArgs.length == 0) {
                    throw new IllegalArgumentException(ERR_NO_ID);
                }
                break;
            default:
                throw new IllegalArgumentException(UNKNOWN_URI + uri);
        }
        rowsDeleted = mNotesDao.deleteNote(Long.parseLong(selectionArgs[0]));
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsUpdated = 0;
        int uriType = URI_MATCHER.match(uri);
        switch (uriType) {
            case UPDATE_NOTE_CODE:
                break;
            default:
                throw new IllegalArgumentException(UNKNOWN_URI + uri);
        }
        rowsUpdated = mNotesDao.updateNote(ConvertUtils.convertToNote(values));
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
