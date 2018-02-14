package com.example.fareed.lazeezo;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fareed.lazeezo.Common.Common;
import com.example.fareed.lazeezo.Model.User;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dmax.dialog.SpotsDialog;

public class SignLogin extends AppCompatActivity {

    Typeface typeface;
    Button signIn,signUp;
    Button cntnu;
    TextView slogan;
    private static final int  reqstCode=1000;

    FirebaseDatabase database;
    DatabaseReference user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_login);
        AccountKit.initialize(this);
       // signIn=(Button)findViewById(R.id.signIn);
       // signUp=(Button)findViewById(R.id.signUp);
        cntnu=(Button)findViewById(R.id.cont);
        slogan=(TextView)findViewById(R.id.slogan);
        typeface=Typeface.createFromAsset(getAssets(),"fonts/tf.otf");
        slogan.setTypeface(typeface);
        database=FirebaseDatabase.getInstance();
        user=database.getReference("User");

        cntnu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // startLogin();
            }
        });


//        if (AccountKit.getCurrentAccessToken()!=null){
//            final android.app.AlertDialog waitingDialog=new SpotsDialog(this);
//            waitingDialog.show();
//            waitingDialog.setMessage("Please Wait...");
//            waitingDialog.setCancelable(false);
//
//            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
//                @Override
//                public void onSuccess(Account account) {
//                    user.child(account.getPhoneNumber().toString())
//                            .addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    User localUser=dataSnapshot.getValue(User.class);
//                                    Intent homeIntent = new Intent(SignLogin.this, Home.class);
//                                    Common.currentUser = localUser;
//                                    startActivity(homeIntent);
//                                    waitingDialog.dismiss();
//                                    finish();
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//                }
//
//                @Override
//                public void onError(AccountKitError accountKitError) {
//
//                }
//            });
//        }

//        signIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(SignLogin.this,SignIn.class));
//            }
//        });
//
//        signUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(SignLogin.this,SignUp.class));
//            }
//        });
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode==reqstCode){
//            AccountKitLoginResult result=data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
//            if (result.getError()!=null){
//                Toast.makeText(this, ""+result.getError().getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
//                return;
//            }
//            else if(result.wasCancelled()){
//                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            else {
//                if(result.getAccessToken()!=null){
//                    final android.app.AlertDialog waitingDialog=new SpotsDialog(this);
//                    waitingDialog.show();
//                    waitingDialog.setMessage("Please Wait...");
//                    waitingDialog.setCancelable(false);
//
//                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
//                        @Override
//                        public void onSuccess(Account account) {
//                            final String userPhone=account.getPhoneNumber().toString();
//                            user.orderByKey().equalTo(userPhone)
//                                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                            if(!dataSnapshot.child(userPhone).exists()){
//                                                User newUser=new User();
//                                                newUser.setPhone(userPhone);
//                                                newUser.setName("");
//
//
//                                                user.child(userPhone)
//                                                        .setValue(newUser)
//                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task<Void> task) {
//                                                                if(task.isSuccessful())
//                                                                    Toast.makeText(SignLogin.this, "Registration Done", Toast.LENGTH_SHORT).show();
//                                                                user.child(userPhone)
//                                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
//                                                                            @Override
//                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                                                                User localUser=dataSnapshot.getValue(User.class);
//                                                                                Intent homeIntent = new Intent(SignLogin.this, Home.class);
//                                                                                Common.currentUser = localUser;
//                                                                                startActivity(homeIntent);
//                                                                                waitingDialog.dismiss();
//                                                                                finish();
//                                                                            }
//
//                                                                            @Override
//                                                                            public void onCancelled(DatabaseError databaseError) {
//
//                                                                            }
//                                                                        });
//                                                            }
//                                                        });
//                                            }else { //if exits
//                                                user.child(userPhone)
//                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
//                                                            @Override
//                                                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                                                User localUser=dataSnapshot.getValue(User.class);
//                                                                Intent homeIntent = new Intent(SignLogin.this, Home.class);
//                                                                Common.currentUser = localUser;
//                                                                startActivity(homeIntent);
//                                                                waitingDialog.dismiss();
//                                                                finish();
//                                                            }
//
//                                                            @Override
//                                                            public void onCancelled(DatabaseError databaseError) {
//
//                                                            }
//                                                        });
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//
//                                        }
//                                    });
//                        }
//
//                        @Override
//                        public void onError(AccountKitError accountKitError) {
//                            Toast.makeText(SignLogin.this, ""+accountKitError.getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            }
//        }
//    }
//
//    private void startLogin() {
//        Intent intent=new Intent(SignLogin.this,AccountKit.class);
//        AccountKitConfiguration.AccountKitConfigurationBuilder accountKitConfigurationBuilder=new AccountKitConfiguration.AccountKitConfigurationBuilder(
//                LoginType.PHONE,
//                AccountKitActivity.ResponseType.TOKEN
//        );
//        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,accountKitConfigurationBuilder.build());
//        startActivityForResult(intent,reqstCode);
//    }

}
