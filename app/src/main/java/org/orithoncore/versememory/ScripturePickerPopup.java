package org.orithoncore.versememory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

public class ScripturePickerPopup extends AppCompatActivity {
    TextView txtStageInstructions;
    LinearLayout options;
    DataBaseHandler dbHandler;
    Verse pickedVerse;
    int numVerses;
    int stage;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scipturepicker);
        options = (LinearLayout) findViewById(R.id.options);
        txtStageInstructions = (TextView) findViewById(R.id.txtInstruction);
        pickedVerse = new Verse(null,0,null,0);//create an empty verse
        dbHandler = new DataBaseHandler(this);//set up dbHandler
        stage =1;//set stage to 1
        reStage();//stage scripture picker
    }

    public void reStage(){
        String instructions = "";
        if(stage==1){//the user is now picking bookName
            instructions = "What book?";
            populateBooks();
        }else if(stage==2){//user is picking chapter
            instructions = "What chapter?";
            populateChapters();
        }else if(stage==3){//user is picking a verse
            instructions = "What verse?";
            populateVerses();
        }else if(stage==4){//user is picking verse range
            populateVerses2();
            instructions = "Verse " + pickedVerse.verseNum + " to verse? (If you only want to select one verse just select " + pickedVerse.verseNum + " again).";
        }
        txtStageInstructions.setText(instructions);
    }


    public void populateVerses(){
        stage = 3;
        dbHandler.open();
        numVerses = dbHandler.getVerseCount(pickedVerse.bookName,pickedVerse.chapterNum);
        dbHandler.close();
        options.removeAllViews();//clear all button options
        ArrayList<Button> buttons = new ArrayList<>();
        for(int i =0; i<numVerses; i ++){
            final Button button = new Button(this);
            button.setText(Integer.toString(i + 1));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickedVerse.verseNum = button.getText().toString();
                    stage ++;
                    reStage();
                }
            });
            buttons.add(button);
        }
        populateCheck(options, buttons);//populate the new buttons
    }

    public void populateVerses2(){
        stage =4;
        options.removeAllViews();//clear view
        ArrayList<Button> buttons = new ArrayList<>();
        for(int i =(Integer.parseInt(pickedVerse.verseNum) - 1); i<numVerses; i ++){
            final Button button = new Button(this);
            button.setText(Integer.toString(i + 1));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!pickedVerse.verseNum.equals(button.getText())) {
                        pickedVerse.verseNum = pickedVerse.verseNum + "-" + button.getText();
                    }
                    packageResult();//final stage has been reached. package the result
                }
            });
            buttons.add(button);
        }
        populateCheck(options, buttons);//populate buttons
    }

    public void populateChapters(){
        stage =2;
        dbHandler.open();
        int numChapters = dbHandler.getChapters(pickedVerse.bookName);
        dbHandler.close();
        options.removeAllViews();
        ArrayList<Button> buttons = new ArrayList<>();
        Integer[] chapters = new Integer[numChapters];
        for(int i =0; i<numChapters; i ++){
            final Button button = new Button(this);
            button.setText(Integer.toString(i + 1));
            button.setWidth(100);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickedVerse.chapterNum = Integer.parseInt(button.getText().toString());
                    stage ++;
                    reStage();
                }
            });
            buttons.add(button);
        }
        populateCheck(options, buttons);
    }

    public void populateBooks(){
        stage = 1;
        options.removeAllViews();
        dbHandler.open();
        ArrayList<String> books = dbHandler.getBooks();
        dbHandler.close();
        ArrayList<Button> buttons = new ArrayList<>();
        for(String book:books){
            final Button button = new Button(this);
            button.setText(book);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickedVerse.bookName = button.getText().toString();
                    stage ++;
                    reStage();
                }
            });
            buttons.add(button);
        }
        populateCheck(options, buttons);
    }

    private void populateCheck(LinearLayout ll, ArrayList<Button> collection) {
        int width = this.getWindow().getDecorView().getWidth();//get the width of the window
        int maxWidth = width + 50;//allow for some extra space
        if (collection.size() > 0) {
            LinearLayout llAlso = new LinearLayout(this);//create a new layout
            llAlso.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            llAlso.setOrientation(LinearLayout.HORIZONTAL);
            int widthSoFar = 0;//set up the width so far
            for (Button button : collection) {
                button.measure(0, 0);//get the width of the button
                widthSoFar += button.getMeasuredWidth();//add to the width
                if (widthSoFar >= maxWidth) {//if the width has exceeded maxwidth make a new linearlayout
                    ll.addView(llAlso);
                    llAlso = new LinearLayout(this);
                    llAlso.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.FILL_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    llAlso.setOrientation(LinearLayout.HORIZONTAL);
                    llAlso.addView(button);
                    widthSoFar = button.getMeasuredWidth();
                } else {
                    llAlso.addView(button);//add the button
                }
            }
            ll.addView(llAlso);
        }
    }

    @Override
    public void onBackPressed() {
        //if the back button is pressed go back one stage in picking the verse
        if(stage>1){
            stage --;
            reStage();
        }else{
            finish();
        }
    }

    public void packageResult(){
        //package the picked verse
        Intent data = new Intent();
        data.putExtra("verse",pickedVerse);
        setResult(RESULT_OK, data);
        finish();
    }

}
