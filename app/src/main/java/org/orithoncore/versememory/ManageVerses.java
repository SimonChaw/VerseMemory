package org.orithoncore.versememory;

import android.content.Context;
import android.content.DialogInterface;
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
    Spinner spnBooks;
    Spinner spnChapters;
    Spinner spnVerses;
    Spinner spnVerses2;
    DataBaseHandler dbHandler;
    LinearLayout previewContainer;
    EditText txtVerse;
    TextView txtScripture;
    TextView txtHeading;
    LinearLayout verseContainer;
    Button btnAdd;
    View.OnClickListener deleter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        dbHandler = new DataBaseHandler(this);
        previewContainer = (LinearLayout) findViewById(R.id.container3);
        spnBooks = (Spinner) findViewById(R.id.spnBook);
        verseContainer = (LinearLayout) findViewById(R.id.verseContainer);
        spnChapters = (Spinner) findViewById(R.id.spnChapter);
        spnVerses = (Spinner) findViewById(R.id.spnVerse);
        spnVerses2 = (Spinner) findViewById(R.id.spnVerse2);
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
        spnBooks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                populateChapters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnChapters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                populateVerses();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnVerses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spnVerses2.setSelection(spnVerses.getSelectedItemPosition());
                previewScripture();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnVerses2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if((Integer)spnVerses2.getSelectedItem() < (Integer)spnVerses.getSelectedItem()){
                    spnVerses2.setSelection(spnVerses.getSelectedItemPosition());
                }
                previewScripture();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        populateBooks();
        loadVerses(this);
    }

    public void saveVerse(){
        dbHandler.open();
        if((Integer)spnVerses.getSelectedItem() == (Integer) spnVerses2.getSelectedItem()) {
            dbHandler.saveVerseForQuiz(spnBooks.getSelectedItem().toString(), (Integer) spnChapters.getSelectedItem(), spnVerses.getSelectedItem().toString() );
        }else{
            String verse = (String) spnVerses.getSelectedItem() + "-" + (String) spnVerses2.getSelectedItem();
            dbHandler.saveVerseForQuiz(spnBooks.getSelectedItem().toString(), (Integer) spnChapters.getSelectedItem(),verse);
        }
        dbHandler.close();
        verseContainer.removeAllViews();
        loadVerses(this);
    }

    public void loadVerses(Context ctx){
        dbHandler.open();
        ArrayList<DataBaseHandler.Verse> verses = dbHandler.loadVerses();
        ArrayList<Button> buttons = new ArrayList<Button>();
        ArrayList<TextView> textViews = new ArrayList<>();

        int i = 0;
        for(DataBaseHandler.Verse verse:verses){
            String dasVerse = verse.bookName + " " + verse.chapterNum + ":" + verse.verseNum;
            TextView textView = new TextView(ctx);
            textView.setText(dasVerse);
            textViews.add(textView);
            Button button = new Button(ctx);
            button.setId(verse.id);
            button.setText("DELETE");
            button.setOnClickListener(deleter);
            buttons.add(button);
            verseContainer.addView(textView);
            verseContainer.addView(button);
            Log.d("repeater","done " + i);
            i++;
        }
        dbHandler.close();
    }

    public void deleteVerse(int id){
        dbHandler.open();
        dbHandler.deleteVerseFromQuiz(id);
        dbHandler.close();
        verseContainer.removeAllViews();
        loadVerses(this);
    }

    public void populateBooks(){
        dbHandler.open();
        ArrayList<String> books = dbHandler.getBooks();
        dbHandler.close();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, books);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnBooks.setAdapter(arrayAdapter);
    }

    public void populateChapters(){
        dbHandler.open();
        int numChapters = dbHandler.getChapters(spnBooks.getSelectedItem().toString());
        dbHandler.close();
        Integer[] chapters = new Integer[numChapters];
        for(int i =0; i<numChapters; i ++) chapters[i] = i + 1;
        ArrayAdapter<Integer> arrayAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, chapters);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Log.d("numChapters", Integer.toString(numChapters));
        spnChapters.setAdapter(arrayAdapter);
    }

    public void populateVerses(){
        dbHandler.open();
        int numVerses = dbHandler.getVerseCount(spnBooks.getSelectedItem().toString(), (Integer) spnChapters.getSelectedItem());
        dbHandler.close();
        Integer[] chapters = new Integer[numVerses];
        for(int i =0; i<numVerses; i ++) chapters[i] = i + 1;
        ArrayAdapter<Integer> arrayAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, chapters);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Log.d("numChapters", Integer.toString(numVerses));
        spnVerses.setAdapter(arrayAdapter);
        spnVerses2.setAdapter(arrayAdapter);
    }



    public void previewScripture(){
        //Set up heading
        int verseRangeLow = Integer.parseInt(spnVerses.getSelectedItem().toString());
        int verseRangeHigh = Integer.parseInt(spnVerses2.getSelectedItem().toString());
        if(verseRangeLow != verseRangeHigh){
            int[] verses = new int[(verseRangeHigh - verseRangeLow) + 1];
            for(int i=0; i < verses.length; i++){
                verses[i] = verseRangeLow + i;
            }
            dbHandler.open();
            txtHeading.setText((String)spnBooks.getSelectedItem() + " " + spnChapters.getSelectedItem() + ":" + spnVerses.getSelectedItem());
            txtScripture.setText(dbHandler.getVerse((String)spnBooks.getSelectedItem(),(Integer)spnChapters.getSelectedItem(),verses));
            dbHandler.close();
        }else{
            dbHandler.open();
            txtHeading.setText((String)spnBooks.getSelectedItem() + " " + spnChapters.getSelectedItem() + ":" + spnVerses.getSelectedItem());
            int[] verses = {verseRangeLow};
            txtScripture.setText(dbHandler.getVerse((String)spnBooks.getSelectedItem(),(Integer)spnChapters.getSelectedItem(),verses));
            dbHandler.close();
        }
    }
}
