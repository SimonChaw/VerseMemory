package org.orithoncore.versememory;

import android.content.ContentValues;
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



    public String getVerse(String bookName,int chapter, int[] verses){
        String verse = "";
        try {
            // initialization
            cursor = null;


            // do query on the database - parameterized query!
            for(int i =0; i < verses.length; i ++) {
                cursor = db.rawQuery("SELECT content FROM tblVerses WHERE chapterNum=" + chapter + " and verseNum=" + verses[i] + " and bookName='" + bookName + "'", null);

                // looping through all rows and adding to list
                if (cursor.moveToFirst()) {
                    do {
                        verse += verses[i] + " " + cursor.getString(0);
/*                        if(verse.charAt(verse.length()) != ' '){
                            verse = verse + " ";
                        }*/
                    } while (cursor.moveToNext());
                }
            }

        } catch (Exception e) {
            Log.d("dbHandler","getBooks Exception: " + e.getMessage());
        }
        return verse;
    }

    public void saveVerseForQuiz(String bookName, int chapter, String verses){
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("bookName", bookName);
            contentValues.put("chapterNum", chapter);
            contentValues.put("verseNum", verses);
            db.insert("tblUserVerses", null, contentValues);
        }catch(Exception e){
            Log.d("saveError",e.getMessage());
        }
    }

    public void deleteVerseFromQuiz(int id){
        try {
            db.execSQL("DELETE FROM tblUserVerses WHERE verseID="+id);
        } catch (Exception e) {
            Log.d("dbHandler","getBooks Exception: " + e.getMessage());
        }
    }
    public ArrayList<Verse> loadVerses(){
        ArrayList<Verse> verses = new ArrayList<>();
        try {
            // initialization
            cursor = null;


            // do query on the database - parameterized query!
            cursor = db.rawQuery("SELECT * FROM tblUserVerses",null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Verse verse = new Verse(cursor.getString(1),cursor.getInt(2),cursor.getString(3), cursor.getInt(0));
                    verses.add(verse);
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.d("dbHandler","getBooks Exception: " + e.getMessage());
        }
        return verses;
    }

    public int getVerseCount(String bookName, int chapter){
        int verses = 0;
        try {
            // initialization
            cursor = null;


            // do query on the database - parameterized query!
            cursor = db.rawQuery("SELECT count(*) FROM tblVerses where bookName='"+bookName+"' and chapterNum=" + chapter,null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    verses = cursor.getInt(0);
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.d("dbHandler","getBooks Exception: " + e.getMessage());
        }
        return verses;
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

    public class Verse{
        String bookName;
        int chapterNum;
        String verseNum;
        int id;
        public Verse(String bookName, int chapterNum, String verseNum, int id){
            this.bookName = bookName;
            this.chapterNum = chapterNum;
            this.verseNum = verseNum;
            this.id = id;
        }
    }
}
