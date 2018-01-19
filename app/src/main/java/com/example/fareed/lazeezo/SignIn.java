package com.example.fareed.lazeezo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

public class SignIn extends AppCompatActivity {

    EditText phone,pass;
    Button signIn,forgotPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        phone=(MaterialEditText)findViewById(R.id.phone);
        pass=(MaterialEditText)findViewById(R.id.pass);

        signIn=(Button)findViewById(R.id.signIn);
        forgotPass=(Button)findViewById(R.id.forgotPass);

        //initiate firebase
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        final DatabaseReference userTable=firebaseDatabase.getReference("User");

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pd=new ProgressDialog(SignIn.this);
                pd.setMessage("Loading...");
                pd.show();
                userTable.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //getUser Info here
                        if(dataSnapshot.child(phone.getText().toString()).exists()){
                            pd.dismiss();
                            User user=dataSnapshot.child(phone.getText().toString()).getValue(User.class);
                            if(user.getPassword().equals(pass.getText().toString())){
                                Intent homeIntent=new Intent(SignIn.this,Home.class);
                                Common.currentUser=user;
                                startActivity(homeIntent);
                                finish();

                            }else{
                                Toast.makeText(SignIn.this, "Sign In Failed!\nPlease Insert Correct Credentials", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            pd.dismiss();
                            Toast.makeText(SignIn.this, "User Does not Exist!!\nRegister Your Self", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignIn.this,ForgotPass.class);
                intent.putExtra("no",phone.getText().toString());
                startActivity(intent);
            }
        });
    }
}
