package org.orithoncore.versememory;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
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
    DataBaseHandler.Verse verse;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scipturepicker);
        options = (LinearLayout) findViewById(R.id.options);
        dbHandler = new DataBaseHandler(this);
        populateBooks();
    }

    public void populateChapters(String bookName){
        dbHandler.open();
        int numChapters = dbHandler.getChapters(bookName);
        dbHandler.close();
        options.removeAllViews();
        ArrayList<Button> buttons = new ArrayList<>();
        Integer[] chapters = new Integer[numChapters];
        for(int i =0; i<numChapters; i ++){
            Button button = new Button(this);
            button.setText(Integer.toString(i + 1));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            buttons.add(button);
        }
        populateCheck(options, buttons);
    }

    public void populateBooks(){
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
                    populateChapters(button.getText().toString());
                }
            });
            buttons.add(button);
        }
        populateCheck(options, buttons);
    }

    private void populateCheck(LinearLayout ll, ArrayList<Button> collection) {

        Display display = getWindowManager().getDefaultDisplay();
        int maxWidth = display.getWidth() - 50;

        if (collection.size() > 0) {
            LinearLayout llAlso = new LinearLayout(this);
            llAlso.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            llAlso.setOrientation(LinearLayout.HORIZONTAL);
            int widthSoFar = 0;
            for (Button button : collection) {
                button.measure(0, 0);
                widthSoFar += button.getMeasuredWidth();

                if (widthSoFar >= maxWidth) {
                    Log.d("button", "Button created: in new layout");
                    ll.addView(llAlso);

                    llAlso = new LinearLayout(this);
                    llAlso.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.FILL_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    llAlso.setOrientation(LinearLayout.HORIZONTAL);

                    llAlso.addView(button);
                    widthSoFar = button.getMeasuredWidth();
                } else {
                    Log.d("button", "Button created: sameLayout");
                    llAlso.addView(button);
                }
            }

            ll.addView(llAlso);
        }
    }

}
