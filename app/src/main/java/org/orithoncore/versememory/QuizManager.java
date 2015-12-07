package org.orithoncore.versememory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextSwitcher;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by Simon on 12/4/2015.
 */
public class QuizManager extends AppCompatActivity {
    Button btnPickScripture;
    DataBaseHandler dbHandler;
    ArrayList<Verse> verses;
    TextView txtHeading;
    TextView txtVerse;
    int currentIndex;
    int guessedCorrect;
    Verse guessedVerse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        dbHandler = new DataBaseHandler(this);
        txtHeading = (TextView) findViewById(R.id.txtHeading);
        txtVerse = (TextView) findViewById(R.id.txtVerse);
        currentIndex =0;
        btnPickScripture = (Button) findViewById(R.id.btnMakeGuess);
        btnPickScripture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(QuizManager.this, ScripturePickerPopup.class);
                startActivityForResult(i,90);
            }
        });
    }

    public void next(){
       if(currentIndex < verses.size()){
           if(guessedVerse.bookName.equals(verses.get(currentIndex).bookName) && guessedVerse.chapterNum == verses.get(currentIndex).chapterNum
                   && guessedVerse.verseNum.equals(verses.get(currentIndex).verseNum)){
               guessedCorrect ++;
           }
           currentIndex ++;
       }
    }

    public void loadVerses(Context ctx){
        dbHandler.open();
        verses = dbHandler.loadVerses();
        int i = 0;
        if(verses.isEmpty()) {

        }
        dbHandler.close();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            guessedVerse = (Verse) data.getParcelableExtra("verse");
            next();
        }
    }
}
