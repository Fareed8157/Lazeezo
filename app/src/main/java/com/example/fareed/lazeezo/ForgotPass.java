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

public class ForgotPass extends AppCompatActivity {

    Button proceed;
    MaterialEditText phone,pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        phone=(MaterialEditText)findViewById(R.id.phone);
        pass=(MaterialEditText)findViewById(R.id.pass);
        proceed=(Button)findViewById(R.id.proceed);
        phone.setText(getIntent().getExtras().getString("no"));
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        final DatabaseReference userTable=firebaseDatabase.getReference("User");
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pd=new ProgressDialog(ForgotPass.this);
                pd.setMessage("Loading...");
                pd.show();
                userTable.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(phone.getText().toString()).exists()){
                            pd.dismiss();
                            User user=dataSnapshot.child(phone.getText().toString()).getValue(User.class);
                            pass.setText(user.getPassword().toString());

                        }else{
                            pd.dismiss();
                            Toast.makeText(ForgotPass.this, "Please Enter Valid Number", Toast.LENGTH_SHORT).show();
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
