package org.orithoncore.versememory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
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
    TextView txtScore;
    TextView txtFeedBack;
    LinearLayout quizLayout;
    LinearLayout feedbackLayout;
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
        quizLayout = (LinearLayout) findViewById(R.id.QuizLayout);
        feedbackLayout = (LinearLayout) findViewById(R.id.FeedBackLayout);
        txtHeading = (TextView) findViewById(R.id.txtHeading);
        txtVerse = (TextView) findViewById(R.id.txtVerse);
        txtScore = (TextView) findViewById(R.id.txtScore);
        txtVerse.setMovementMethod(new ScrollingMovementMethod());
        txtFeedBack = (TextView) findViewById(R.id.txtFeedBack);
        currentIndex = 0;
        loadVerses(this);
        if (verses.size() != 0) {//if there are verses setup the quiz
            if (savedInstanceState != null) {
                verses = savedInstanceState.getParcelableArrayList("verses");
                currentIndex = savedInstanceState.getInt("currentIndex");
                if (currentIndex == verses.size() - 1) {
                    end();
                } else {
                    setUpQuestion();
                }
            } else {
                txtVerse.startAnimation(in);
                setUpQuestion();
            }
            btnPickScripture = (Button) findViewById(R.id.btnMakeGuess);
            btnPickScripture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(QuizManager.this, ScripturePickerPopup.class);
                    startActivityForResult(i, 90);
                }
            });
        }else{
            quizLayout.setVisibility(View.GONE);
            feedbackLayout.setVisibility(View.VISIBLE);
            txtFeedBack.setText(getString(R.string.noVerse));
            txtScore.setText(getString(R.string.noVerseInstruction));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("verses", verses);
        outState.putInt("currentIndex",currentIndex);
    }

    public void next(){
       if(currentIndex < verses.size() - 1){//if the guessed verse matches add a point
           if(guessedVerse.bookName.equals(verses.get(currentIndex).bookName) && guessedVerse.chapterNum == verses.get(currentIndex).chapterNum
                   && guessedVerse.verseNum.equals(verses.get(currentIndex).verseNum)){
               guessedCorrect ++;
           }
           currentIndex ++;//increment the current index
           txtVerse.startAnimation(out);//start animation
           setUpQuestion();
           txtVerse.startAnimation(in);
       }else{
           end();
       }
    }

    public void end(){
        quizLayout.setVisibility(View.GONE);//hide the quiz UI
        feedbackLayout.setVisibility(View.VISIBLE);//show the feedback
        if(guessedCorrect > (verses.size() / 2)){
            //feedback to user, they did good
            txtFeedBack.setText(getString(R.string.feedbackGood));
            txtScore.setText(getString(R.string.goodFeedback1) + guessedCorrect + getString(R.string.outOf) + verses.size() + getString(R.string.goodFeedback2));
        }else{
            //feedback to user, they could do better
            txtFeedBack.setText(getString(R.string.feedbackBad));
            txtScore.setText(getString(R.string.badFeedback1) + guessedCorrect + getString(R.string.outOf) + verses.size() + getString(R.string.badFeedback2));
        }
    }

    public void setUpQuestion() {
        dbHandler.open();
        if(verses.get(currentIndex).verseNum.contains("-")) {//set up a number range of verses if need be
            String[] verses2 = verses.get(currentIndex).verseNum.split("-");
            int verseRangeLow = Integer.parseInt(verses2[0]);
            int verseRangeHigh = Integer.parseInt(verses2[1]);
            int[] verseRange = new int[(verseRangeHigh - verseRangeLow) + 1];
            for (int i = 0; i < verseRange.length; i++) {
                verseRange[i] = verseRangeLow + i;
            }//set up verse with out the verseNum
            txtVerse.setText(dbHandler.getVerse(verses.get(currentIndex).bookName, verses.get(currentIndex).chapterNum, verseRange,false));
        }else{
            int[] verseRange = {Integer.parseInt(verses.get(currentIndex).verseNum)};
            txtVerse.setText(dbHandler.getVerse(verses.get(currentIndex).bookName, verses.get(currentIndex).chapterNum,verseRange,false));
        }
        dbHandler.close();
    }

    public void loadVerses(Context ctx){//load all the verses
        dbHandler.open();
        verses = dbHandler.loadVerses();
        int i = 0;
        Collections.shuffle(verses);//shuffle the verses
        dbHandler.close();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            guessedVerse = (Verse) data.getParcelableExtra("verse");
            next();
        }
    }
}
