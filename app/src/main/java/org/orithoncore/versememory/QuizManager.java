package org.orithoncore.versememory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


/**
 * Created by Simon on 12/4/2015.
 */
public class QuizManager extends AppCompatActivity {
    Button btnPickScripture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        btnPickScripture = (Button) findViewById(R.id.btnMakeGuess);
        btnPickScripture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(QuizManager.this, ScripturePickerPopup.class);
                startActivity(i);
            }
        });
    }


}
