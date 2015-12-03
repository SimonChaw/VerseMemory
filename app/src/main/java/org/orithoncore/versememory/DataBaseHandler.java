package org.orithoncore.versememory;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Simon on 12/3/2015.
 */
public class DataBaseHandler {
    // constants
    private final String DATABASE_NAME = "bibleDB";
    private final int DATABASE_VERSION = 1;

    // cursor object to navigate through records
    private Cursor cursor;
    // our database
    private SQLiteDatabase db;
    // the context of the application - needs it to do any database work
    private Context context;
    // our custom DBHelper object (inner class!)
    private DBHelper dbHelper;

    public DataBaseHandler(Context ctx){
        context = ctx;
    }

    public void open() {
        // construct the DBHelper object
        dbHelper = new DBHelper();
        // get reference to the SQLite DB for reading and writing
        // returns a SQLLiteDatabase object
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        db.close();
        db = null;
    }


    public ArrayList<String> getBooks(){
        ArrayList<String> books = new ArrayList<>();
        try {
            // initialization
            cursor = null;


            // do query on the database - parameterized query!
            cursor = db.rawQuery("SELECT book_name FROM tblBooks",null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    books.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.d("dbHandler","getBooks Exception: " + e.getMessage());
        }
        return books;
    }

    public int getChapters(String bookName){
        int chapters = 0;
        try {
            // initialization
            cursor = null;


            // do query on the database - parameterized query!
            cursor = db.rawQuery("SELECT numChapters FROM tblBooks where book_name='"+bookName+"'",null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    chapters = cursor.getInt(0);
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.d("dbHandler","getBooks Exception: " + e.getMessage());
        }
        return chapters;
    }

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper() {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
