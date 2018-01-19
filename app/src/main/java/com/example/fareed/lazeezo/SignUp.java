package com.example.fareed.lazeezo;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.fareed.lazeezo.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignUp extends AppCompatActivity {

    MaterialEditText phone,name,pass;
    Button signUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signUp=(Button)findViewById(R.id.signUp);
        phone=(MaterialEditText)findViewById(R.id.phone);
        name=(MaterialEditText)findViewById(R.id.name);
        pass=(MaterialEditText)findViewById(R.id.pass);
        //Initiate Database
        final FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        final DatabaseReference userTable=firebaseDatabase.getReference("User");

        signUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final ProgressDialog pd=new ProgressDialog(SignUp.this);
                pd.setMessage("Please Wait...");
                pd.show();

                userTable.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //check if phone already exist or not
                        if(dataSnapshot.child(phone.getText().toString()).exists()){
                            pd.dismiss();
                            Toast.makeText(SignUp.this, "Number is Already Registered", Toast.LENGTH_SHORT).show();
                        }else{
                            pd.dismiss();
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
            }
        });
    }
}
