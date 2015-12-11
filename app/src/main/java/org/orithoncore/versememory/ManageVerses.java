package org.orithoncore.versememory;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ManageVerses extends AppCompatActivity {
    DataBaseHandler dbHandler;
    LinearLayout previewContainer;
    LinearLayout savedVerses;
    EditText txtVerse;
    TextView txtScripture;
    TextView txtHeading;
    LinearLayout verseContainer;
    LinearLayout noVerses;
    Button btnAdd;
    Button btnNew;
    LinearLayout listHeader;
    Verse currentVerse;
    View.OnClickListener deleter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        dbHandler = new DataBaseHandler(this);
        savedVerses = (LinearLayout) findViewById(R.id.savedVerses);
        previewContainer = (LinearLayout) findViewById(R.id.container3);
        verseContainer = (LinearLayout) findViewById(R.id.verseContainer);
        txtHeading = (TextView) findViewById(R.id.txtPreviewHeading);
        listHeader = (LinearLayout) findViewById(R.id.listHeader);
        txtScripture = (TextView) findViewById(R.id.txtPreviewScripture);
        noVerses = (LinearLayout) findViewById(R.id.noVerses);
        deleter = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteVerse(v.getId());
            }
        };
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveVerse();
            }
        });
        if(savedInstanceState != null){
            currentVerse = savedInstanceState.getParcelable("verse");
            verseContainer.setVisibility(View.VISIBLE);
            previewScripture();
        }else{
            verseContainer.setVisibility(View.GONE);
        }
        btnNew = (Button) findViewById(R.id.btnNew);
        txtScripture.setMovementMethod(new ScrollingMovementMethod());
        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ManageVerses.this, ScripturePickerPopup.class);
                startActivityForResult(i, 90);
            }
        });
        loadVerses(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("verse",currentVerse);
    }


    public void saveVerse(){
        //take the current verse and save it
        dbHandler.open();
        dbHandler.saveVerseForQuiz(currentVerse.bookName,currentVerse.chapterNum,currentVerse.verseNum);
        dbHandler.close();
        savedVerses.removeAllViews();
        loadVerses(this);
    }


    public void loadVerses(Context ctx){
        dbHandler.open();
        //get saved verses from the database
        ArrayList<Verse> verses = dbHandler.loadVerses();
        if(!verses.isEmpty()) {
            //show the related views
            listHeader.setVisibility(View.VISIBLE);
            savedVerses.setVisibility(View.VISIBLE);
            noVerses.setVisibility(View.GONE);
            for (Verse verse : verses) {
                //for each verses in the array list create a textview and a button and add it to the view
                String dasVerse = verse.bookName + " " + verse.chapterNum + ":" + verse.verseNum;
                TextView textView = new TextView(ctx);
                textView.setText(dasVerse);
                Button button = new Button(ctx);
                button.setId(verse.id);//set the button id to the db's verse id for later use
                button.setText("DELETE");
                button.setOnClickListener(deleter);
                savedVerses.addView(textView);
                savedVerses.addView(button);
            }
        }else{
            //If there are no verses show the imageview that lets the user know there are no verses
            listHeader.setVisibility(View.GONE);
            savedVerses.setVisibility(View.GONE);
            noVerses.setVisibility(View.VISIBLE);
        }
        dbHandler.close();
    }

    public void deleteVerse(int id){
        //take the id from the button and delete it from the database
        dbHandler.open();
        dbHandler.deleteVerseFromQuiz(id);
        dbHandler.close();
        savedVerses.removeAllViews();
        loadVerses(this);
    }


    public void previewScripture(){
        //Set up heading
        if(currentVerse!=null) {
            //if the string contains a hyphen it means there are a range of numbers
            if (currentVerse.verseNum.contains("-")) {
                String[] verses = currentVerse.verseNum.split("-");
                int verseRangeLow = Integer.parseInt(verses[0]);
                int verseRangeHigh = Integer.parseInt(verses[1]);
                int[] verseRange = new int[(verseRangeHigh - verseRangeLow) + 1];//set up range of numbers
                for (int i = 0; i < verseRange.length; i++) {
                    verseRange[i] = verseRangeLow + i;
                }
                dbHandler.open();
                txtHeading.setText(currentVerse.bookName + " " + currentVerse.chapterNum + ":" + currentVerse.verseNum);
                txtScripture.setText(dbHandler.getVerse(currentVerse.bookName, currentVerse.chapterNum, verseRange, true));
                dbHandler.close();
            } else {
                //there is only one verse so create a int array with the size of 1
                dbHandler.open();
                txtHeading.setText(currentVerse.bookName + " " + currentVerse.chapterNum + ":" + currentVerse.verseNum);
                int[] verse = {Integer.parseInt(currentVerse.verseNum)};
                txtScripture.setText(dbHandler.getVerse(currentVerse.bookName, currentVerse.chapterNum, verse, true));
                dbHandler.close();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {//get the verse from scripture picker
            currentVerse = (Verse) data.getParcelableExtra("verse");
            verseContainer.setVisibility(View.VISIBLE);
            previewScripture();
        }
    }
}
