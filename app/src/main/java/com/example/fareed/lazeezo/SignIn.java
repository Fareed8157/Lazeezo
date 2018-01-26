package com.example.fareed.lazeezo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fareed.lazeezo.Common.Common;
import com.example.fareed.lazeezo.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class SignIn extends AppCompatActivity {

    AutoCompleteTextView phone,pass;
    CircularProgressButton signIn;
    Button forgotPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        phone=(AutoCompleteTextView)findViewById(R.id.phone);
        pass=(AutoCompleteTextView)findViewById(R.id.pass);

        signIn=(CircularProgressButton)findViewById(R.id.signIn);
        forgotPass=(Button) findViewById(R.id.forgotPass);

        //initiate firebase
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        final DatabaseReference userTable=firebaseDatabase.getReference("User");

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask<String,String,String> register=new AsyncTask<String, String, String>() {
                    @Override
                    protected String doInBackground(String... strings) {
                        try {
                            Thread.sleep(3000);
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                        return "Done";
                    }
                    @Override
                    protected void onPostExecute(String s) {
                        if(s.equals("Done")){
                            userTable.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //getUser Info here
                        if(dataSnapshot.child(phone.getText().toString()).exists()){
                            User user=dataSnapshot.child(phone.getText().toString()).getValue(User.class);
                            user.setPhone(phone.getText().toString());
                            if(user.getPassword().equals(pass.getText().toString())){
                                Intent homeIntent=new Intent(SignIn.this,Home.class);
                                Common.currentUser=user;
                                startActivity(homeIntent);
                                finish();

                            }else{
                                Toast.makeText(SignIn.this, "Sign In Failed!\nPlease Insert Correct Credentials", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(SignIn.this, "User Does not Exist!!\nRegister Your Self", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                            signIn.doneLoadingAnimation(Color.parseColor("#607D8B"), BitmapFactory.decodeResource(getResources(),R.drawable.ic_done_white_48dp));
                        }
                        super.onPostExecute(s);
                    }
                };
                signIn.startAnimation();
                register.execute();
//
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignIn.this,ForgotPass.class);
                intent.putExtra("no",phone.getText().toString());
                //startActivity(intent);
            }
        });
    }
}
