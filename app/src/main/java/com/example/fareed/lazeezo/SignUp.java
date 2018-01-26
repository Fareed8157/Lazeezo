package com.example.fareed.lazeezo;

import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.fareed.lazeezo.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.liuguangqiang.swipeback.SwipeBackActivity;
import com.liuguangqiang.swipeback.SwipeBackLayout;
import com.rengwuxian.materialedittext.MaterialEditText;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class SignUp extends SwipeBackActivity {

    AutoCompleteTextView phone,name,pass;
    CircularProgressButton signUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        setDragEdge(SwipeBackLayout.DragEdge.LEFT);
        signUp=(CircularProgressButton)findViewById(R.id.signUp);
        phone=(AutoCompleteTextView)findViewById(R.id.phone);
        name=(AutoCompleteTextView)findViewById(R.id.name);
        pass=(AutoCompleteTextView)findViewById(R.id.pass);
        //Initiate Database
        final FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        final DatabaseReference userTable=firebaseDatabase.getReference("User");

        signUp.setOnClickListener(new View.OnClickListener() {

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
                                    //check if phone already exist or not
                                    if(dataSnapshot.child(phone.getText().toString()).exists()){
                                        Toast.makeText(SignUp.this, "Number is Already Registered", Toast.LENGTH_SHORT).show();
                                    }else{
                                        User user=new User(name.getText().toString(),pass.getText().toString());
                                        userTable.child(phone.getText().toString()).setValue(user);
                                        Toast.makeText(SignUp.this, "Sign Up Successfull!!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            signUp.doneLoadingAnimation(Color.parseColor("#607D8B"), BitmapFactory.decodeResource(getResources(),R.drawable.ic_done_white_48dp));
                        }
                        super.onPostExecute(s);
                    }
                };
                signUp.startAnimation();
                register.execute();
            }
        });
    }
}
