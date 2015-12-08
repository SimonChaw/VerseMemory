package org.orithoncore.versememory;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
    Button btnAdd;
    Button btnNew;
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
        txtScripture = (TextView) findViewById(R.id.txtPreviewScripture);
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
        btnNew = (Button) findViewById(R.id.btnNew);
        verseContainer.setVisibility(View.GONE);
        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ManageVerses.this, ScripturePickerPopup.class);
                startActivityForResult(i, 90);
            }
        });
        loadVerses(this);
    }

    public void saveVerse(){
        dbHandler.open();
            dbHandler.saveVerseForQuiz(currentVerse.bookName,currentVerse.chapterNum,currentVerse.verseNum);
        dbHandler.close();
        savedVerses.removeAllViews();
        loadVerses(this);
    }

    public void loadVerses(Context ctx){
        dbHandler.open();
        ArrayList<Verse> verses = dbHandler.loadVerses();
        int i = 0;
        if(!verses.isEmpty()) {
            for (Verse verse : verses) {
                String dasVerse = verse.bookName + " " + verse.chapterNum + ":" + verse.verseNum;
                TextView textView = new TextView(ctx);
                textView.setText(dasVerse);
                Button button = new Button(ctx);
                button.setId(verse.id);
                button.setText("DELETE");
                button.setOnClickListener(deleter);
                savedVerses.addView(textView);
                savedVerses.addView(button);
                Log.d("repeater", "done " + i);
                i++;
            }
        }else{

        }
        dbHandler.close();
    }

    public void deleteVerse(int id){
        dbHandler.open();
        dbHandler.deleteVerseFromQuiz(id);
        dbHandler.close();
        savedVerses.removeAllViews();
        loadVerses(this);
    }


    public void previewScripture(){
        //Set up heading
        if(currentVerse!=null) {
            if (currentVerse.verseNum.contains("-")) {
                String[] verses = currentVerse.verseNum.split("-");
                int verseRangeLow = Integer.parseInt(verses[0]);
                int verseRangeHigh = Integer.parseInt(verses[1]);
                int[] verseRange = new int[(verseRangeHigh - verseRangeLow) + 1];
                for (int i = 0; i < verseRange.length; i++) {
                    verseRange[i] = verseRangeLow + i;
                }
                dbHandler.open();
                txtHeading.setText(currentVerse.bookName + " " + currentVerse.chapterNum + ":" + currentVerse.verseNum);
                txtScripture.setText(dbHandler.getVerse(currentVerse.bookName, currentVerse.chapterNum, verseRange, true));
                dbHandler.close();
            } else {
                dbHandler.open();
                txtHeading.setText(currentVerse.bookName + " " + currentVerse.chapterNum + ":" + currentVerse.verseNum);
                int[] verse = {Integer.parseInt(currentVerse.verseNum)};
                txtScripture.setText(dbHandler.getVerse(currentVerse.bookName, currentVerse.chapterNum, verse, true));
                dbHandler.close();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            currentVerse = (Verse) data.getParcelableExtra("verse");
            verseContainer.setVisibility(View.VISIBLE);
            previewScripture();
        }
    }
}
