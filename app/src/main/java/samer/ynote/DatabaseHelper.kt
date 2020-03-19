package samer.ynote

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.util.*

class DatabaseHelper(context: Context?) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(TABLE_CREATE)
        Log.i(TAG, "Table has been created.")
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        if (oldVersion != newVersion) {
            val sql = "DROP TABLE IF EXISTS $TABLE_NOTES"
            db.execSQL(sql)
            onCreate(db)
        }
    }

    fun addItem(note: Note): Long {
        val database = writableDatabase
        var ID: Long = -1
        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
// consistency of the database.
        database.beginTransaction()
        try { // long noteID = addOrUpdateItem(note)
            val values = ContentValues()
            values.put(COLUMN_TITLE, note.title)
            values.put(COLUMN_NOTE, note.note)
            values.put(COLUMN_TIMESTAMP, note.timestamp)
            //Add values to database w/ error handling:
            ID = database.insertOrThrow(TABLE_NOTES, null, values)
            database.setTransactionSuccessful()
            Log.i(TAG, "NOTE ADDED")
        } catch (e: Exception) {
            Log.e(TAG, "Unable to add item to database")
        } finally {
            Log.d(TAG, "New Item: $note")
            database.endTransaction()
        }
        return ID
    }

    fun updateItem(note: Note) {
        val database = writableDatabase
        database.beginTransaction()
        try {
            val values = ContentValues()
            values.put(COLUMN_TITLE, note.title)
            values.put(COLUMN_NOTE, note.note)
            values.put(COLUMN_TIMESTAMP, note.timestamp)
            database.update(
                TABLE_NOTES,
                values,
                "$COLUMN_ID = ?",
                arrayOf(note.id.toString())
            )
            database.setTransactionSuccessful()
            Log.i(TAG, "ITEM UPDATED")
        } catch (e: Exception) {
            Log.e(TAG, "Unable to UPDATE item to database")
        } finally {
            Log.d(TAG, note.toString())
            database.endTransaction()
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
    fun deleteItem(note: Note) {
        val ID = note.id
        val database = writableDatabase
        database.beginTransaction()
        try {
            database.delete(
                TABLE_NOTES,
                "$COLUMN_ID=?",
                arrayOf(ID.toString())
            )
            database.setTransactionSuccessful()
            Log.i(TAG, "NOTE DELETED")
            Log.d(TAG, note.toString())
        } catch (e: Exception) {
            Log.d(TAG, "Unable to delete note")
        } finally {
            database.endTransaction()
        }
    }//check if cursor is closed, if not > close

    //print note to console
    val allItemsFromDatabase: List<Note>
        get() {
            val list_items: MutableList<Note> =
                ArrayList()
            val SELECT_QUERY = "SELECT * FROM $TABLE_NOTES"
            val database = readableDatabase
            val cursor = database.rawQuery(SELECT_QUERY, null)
            try {
                if (cursor!!.moveToFirst()) {
                    do {
                        val note = Note()
                        note.id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID)).toLong()
                        note.title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
                        note.note = cursor.getString(cursor.getColumnIndex(COLUMN_NOTE))
                        note.timestamp = cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP))
                        list_items.add(note)
                        Log.i(
                            TAG,
                            note.toString()
                        ) //print note to console
                    } while (cursor.moveToNext())
                }
            } catch (e: Exception) {
                Log.d(
                    TAG,
                    "Unable to get data from local database"
                )
            } finally {
                Log.i(
                    TAG,
                    "Total rows (# of fields) = " + cursor!!.count
                )
                Log.d(TAG, list_items.toString())
                //check if cursor is closed, if not > close
                if (cursor != null && !cursor.isClosed) {
                    cursor.close()
                    Log.i(TAG, "Cursor is closed")
                }
            }
            database.close()
            Log.d(TAG, "VIP_DB: " + list_items.size)
            Log.d(
                TAG,
                "VIP_DB_ListFromDB: $list_items"
            )
            return list_items
        }

    fun deleteAllItemsFromDatabase() {
        val database = writableDatabase
        database.beginTransaction()
        try {
            database.delete(TABLE_NOTES, null, null)
            database.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.d(
                TAG,
                "Unable to delete all notes from database"
            )
        } finally {
            database.endTransaction()
        }
    }

    override fun getDatabaseName(): String {
        return DATABASE_NAME
    }

    companion object {
        //General:
        private var instance: DatabaseHelper? = null
        private const val TAG = "DatabaseHelper.java"
        //Database info:
        private const val DATABASE_NAME = "Notes3.db"
        private const val DATABASE_VERSION = 3
        //Tables:
        private const val TABLE_NOTES = "notes"
        //Notes::Columns
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_NOTE = "notes"
        private const val COLUMN_TIMESTAMP = "timestamp"
        //Create table:
        private const val TABLE_CREATE =
            ("CREATE TABLE " + TABLE_NOTES + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_TITLE + " TEXT, " + COLUMN_NOTE + " TEXT, " + COLUMN_TIMESTAMP + " TEXT)")

        //The static getInstance() method ensures that only one DatabaseHelper will ever exist at any given time.
//If the instance object has not been initialized, one will be created. If one has already been created then it will simply be returned.
        @Synchronized
        fun getInstance(context: Context): DatabaseHelper? { // Use the application context, which will ensure that you
// don't accidentally leak an Activity's context.
            if (instance == null) {
                instance = DatabaseHelper(context.applicationContext)
            }
            return instance
        }
    }
}