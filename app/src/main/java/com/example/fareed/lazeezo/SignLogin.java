package com.example.fareed.lazeezo;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SignLogin extends AppCompatActivity {

    Typeface typeface;
    Button signIn,signUp;
    TextView slogan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_login);
        signIn=(Button)findViewById(R.id.signIn);
        signUp=(Button)findViewById(R.id.signUp);
        slogan=(TextView)findViewById(R.id.slogan);
        typeface=Typeface.createFromAsset(getAssets(),"fonts/tf.otf");
        slogan.setTypeface(typeface);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignLogin.this,SignIn.class));
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignLogin.this,SignUp.class));
            }
        });
    }
}
