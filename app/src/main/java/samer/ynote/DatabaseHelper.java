package samer.ynote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {
    //General:
    private static DatabaseHelper instance;
    private static final String TAG = "DatabaseHelper.java";
    //Database info:
    private static final String DATABASE_NAME = "Notes3.db";
    private static final int DATABASE_VERSION = 3;
    //Tables:
    private static final String TABLE_NOTES = "notes";
    //Notes::Columns
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_NOTE = "notes";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    //Create table:
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NOTES + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_TITLE + " TEXT, " + COLUMN_NOTE + " TEXT, " + COLUMN_TIMESTAMP + " TEXT)";


    //The static getInstance() method ensures that only one DatabaseHelper will ever exist at any given time.
    //If the instance object has not been initialized, one will be created. If one has already been created then it will simply be returned.
    public static synchronized DatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        Log.i(TAG, "Table has been created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            String sql = "DROP TABLE IF EXISTS " + TABLE_NOTES;
            db.execSQL(sql);
            onCreate(db);
        }
    }

    public long addItem(Note note) {
        SQLiteDatabase database = getWritableDatabase();
        long ID = -1;
        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        database.beginTransaction();
        try {
            // long noteID = addOrUpdateItem(note)
            ContentValues values = new ContentValues();
            values.put(COLUMN_TITLE, note.getTitle());
            values.put(COLUMN_NOTE, note.getNote());
            values.put(COLUMN_TIMESTAMP, note.getTimestamp());

            //Add values to database w/ error handling:
            ID = database.insertOrThrow(TABLE_NOTES, null, values);
            database.setTransactionSuccessful();
            Log.i(TAG, "NOTE ADDED");
        } catch (Exception e) {
            Log.e(TAG, "Unable to add item to database");
        } finally {
            Log.d(TAG, "New Item: " + note.toString());
            database.endTransaction();
        }
        return ID;
    }

    public void updateItem(Note note) {
        SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_TITLE, note.getTitle());
            values.put(COLUMN_NOTE, note.getNote());
            values.put(COLUMN_TIMESTAMP, note.getTimestamp());

            database.update(TABLE_NOTES, values, COLUMN_ID + " = ?",
                    new String[]{String.valueOf(note.getID())});
            database.setTransactionSuccessful();
            Log.i(TAG, "ITEM UPDATED");
        } catch (Exception e) {
            Log.e(TAG, "Unable to UPDATE item to database");
        } finally {
            Log.d(TAG, note.toString());
            database.endTransaction();
        }

    }
/*
    public void updateItem(Note note) {
        //Get writable database
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        //Add data to values
        values.put(COLUMN_TITLE, note.getTitle());
        values.put(COLUMN_NOTE, note.getNote());
        values.put(COLUMN_TIMESTAMP, note.getTimestamp());
        //Find note based on its ID && Update row
        database.update(TABLE_NOTES, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getID())});
        //Close database
        database.close();
        Log.i(TAG, "NOTE UPDATED");
        Log.d(TAG, note.toString());
    }
    */


    public void deleteItem(Note note) {
        long ID = note.getID();
        SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        try {
            database.delete(TABLE_NOTES, COLUMN_ID + "=?", new String[]{String.valueOf(ID)});
            database.setTransactionSuccessful();
            Log.i(TAG, "NOTE DELETED");
            Log.d(TAG, note.toString());
        } catch (Exception e) {
            Log.d(TAG, "Unable to delete note");
        } finally {
            database.endTransaction();
        }
    }


    public List<Note> getAllItemsFromDatabase() {
        List<Note> list_items = new ArrayList<>();
        String SELECT_QUERY = "SELECT * FROM " + TABLE_NOTES;
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Note note = new Note();
                    note.setID(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                    note.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                    note.setNote(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE)));
                    note.setTimestamp(cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP)));
                    list_items.add(note);
                    Log.i(TAG, note.toString());//print note to console
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.d(TAG, "Unable to get data from local database");
        } finally {
            Log.i(TAG, "Total rows (# of fields) = " + cursor.getCount());
            Log.d(TAG, list_items.toString());
            //check if cursor is closed, if not > close
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                Log.i(TAG, "Cursor is closed");
            }
        }
        database.close();
        Log.d(TAG, "VIP_DB: " + list_items.size());
        Log.d(TAG, "VIP_DB_ListFromDB: " + list_items.toString());
        return list_items;
    }

    public void deleteAllItemsFromDatabase() {
        SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        try {
            database.delete(TABLE_NOTES, null, null);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Unable to delete all notes from database");
        } finally {
            database.endTransaction();
        }
    }

    public String getDatabaseName() {
        return DATABASE_NAME;
    }
}
