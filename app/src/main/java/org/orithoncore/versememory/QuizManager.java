package org.orithoncore.versememory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextSwitcher;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;


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
    Animation in;
    Animation out;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(3000);
        out = new AlphaAnimation(1.0f, 0.0f);
        out.setDuration(3000);
        setContentView(R.layout.activity_quiz);
        dbHandler = new DataBaseHandler(this);
        txtHeading = (TextView) findViewById(R.id.txtHeading);
        txtVerse = (TextView) findViewById(R.id.txtVerse);
        currentIndex =0;
        loadVerses(this);
        setUpQuestion();
        btnPickScripture = (Button) findViewById(R.id.btnMakeGuess);
        btnPickScripture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(QuizManager.this, ScripturePickerPopup.class);
                startActivityForResult(i, 90);
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
           setUpQuestion();
       }else{
           if(guessedCorrect > (verses.size() / 2)){
               //feedback to user, they did good
           }else{
               //feedback to user, they could do better
           }
       }

    }

    public void setUpQuestion(){
        txtHeading.setText("Where is the following scripture from?");
        txtVerse.startAnimation(out);
        dbHandler.open();
        if(verses.get(currentIndex).verseNum.contains("-")) {
            String[] verses2 = verses.get(currentIndex).verseNum.split("-");
            int verseRangeLow = Integer.parseInt(verses2[0]);
            int verseRangeHigh = Integer.parseInt(verses2[1]);
            int[] verseRange = new int[(verseRangeHigh - verseRangeLow) + 1];
            for (int i = 0; i < verseRange.length; i++) {
                verseRange[i] = verseRangeLow + i;
            }
            txtVerse.setText(dbHandler.getVerse(verses.get(currentIndex).bookName, verses.get(currentIndex).chapterNum,verseRange,false));
            txtVerse.startAnimation(in);
        }else{
            int[] verseRange = {Integer.parseInt(verses.get(currentIndex).verseNum)};
            txtVerse.setText(dbHandler.getVerse(verses.get(currentIndex).bookName, verses.get(currentIndex).chapterNum,verseRange,false));
            txtVerse.startAnimation(in);
        }
        dbHandler.close();
    }

    public void loadVerses(Context ctx){
        dbHandler.open();
        verses = dbHandler.loadVerses();
        int i = 0;
        if(verses.isEmpty()) {

        }
        Collections.shuffle(verses);
        dbHandler.close();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            guessedVerse = (Verse) data.getParcelableExtra("verse");
            next();
        }
    }
}
