package org.orithoncore.versememory;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

public class ManageVerses extends AppCompatActivity {
    Spinner spnBooks;
    Spinner spnChapters;
    DataBaseHandler dbHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        dbHandler = new DataBaseHandler(this);
        spnBooks = (Spinner) findViewById(R.id.spnBook);
        spnChapters = (Spinner) findViewById(R.id.spnChapter);
        spnBooks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                populateChapters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        populateBooks();
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
        Log.d("numChapters",Integer.toString(numChapters));
        spnChapters.setAdapter(arrayAdapter);
    }
}
