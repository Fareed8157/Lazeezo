package com.example.fareed.lazeezo;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button gettingStarted;
    TextView textView;
    Typeface typeface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gettingStarted=(Button)findViewById(R.id.gettingStarted);
        textView=(TextView)findViewById(R.id.slogan);
        typeface=Typeface.createFromAsset(getAssets(),"fonts/tf.otf");
        textView.setTypeface(typeface);
        gettingStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SignLogin.class));
            }
        });
    }
}
