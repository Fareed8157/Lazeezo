package com.example.fareed.lazeezo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fareed.lazeezo.Common.Common;
import com.example.fareed.lazeezo.Model.User;
import com.facebook.FacebookSdk;
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    Button gettingStarted;
    TextView textView;
    Typeface typeface;
    Animation uptodown,downtopup;
    LinearLayout l1,l2;
    FirebaseDatabase database;
    DatabaseReference user;
    private static final int reqstCode=1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        AccountKit.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database= FirebaseDatabase.getInstance();
        user=database.getReference("User");
        l1=(LinearLayout)findViewById(R.id.linearLayout);
        l2=(LinearLayout)findViewById(R.id.linearLayout2);
        uptodown = AnimationUtils.loadAnimation(this,R.anim.uptodown);
        downtopup = AnimationUtils.loadAnimation(this,R.anim.downtopup);
        l1.setAnimation(uptodown);
        l2.setAnimation(downtopup);
        gettingStarted=(Button)findViewById(R.id.gettingStarted);
        textView=(TextView)findViewById(R.id.slogan);
        typeface=Typeface.createFromAsset(getAssets(),"fonts/tf.otf");
        textView.setTypeface(typeface);
        gettingStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this,Home.class));
                startLogin();
            }
        });

        if (AccountKit.getCurrentAccessToken()!=null){
            final android.app.AlertDialog waitingDialog=new SpotsDialog(this);
            waitingDialog.show();
            waitingDialog.setMessage("Please Wait...");
            waitingDialog.setCancelable(false);

            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(Account account) {
                    user.child(account.getPhoneNumber().toString())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User localUser=dataSnapshot.getValue(User.class);
                                    Intent homeIntent = new Intent(MainActivity.this, Home.class);
                                    Common.currentUser = localUser;
                                    startActivity(homeIntent);
                                    waitingDialog.dismiss();
                                    finish();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }

                @Override
                public void onError(AccountKitError accountKitError) {

                }
            });
        }

        //printKeyHash();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==reqstCode){
            AccountKitLoginResult result=data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (result.getError()!=null){
                Toast.makeText(this, ""+result.getError().getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            else if(result.wasCancelled()){
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                if(result.getAccessToken()!=null){
                    final android.app.AlertDialog waitingDialog=new SpotsDialog(this);
                    waitingDialog.show();
                    waitingDialog.setMessage("Please Wait...");
                    waitingDialog.setCancelable(false);

                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(Account account) {
                            final String userPhone=account.getPhoneNumber().toString();
                            user.orderByKey().equalTo(userPhone)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(!dataSnapshot.child(userPhone).exists()){
                                                User newUser=new User();
                                                newUser.setPhone(userPhone);
                                                newUser.setName("");
                                                newUser.setBalance(0.0);


                                                user.child(userPhone)
                                                        .setValue(newUser)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful())
                                                                    Toast.makeText(MainActivity.this, "Registration Done", Toast.LENGTH_SHORT).show();
                                                                user.child(userPhone)
                                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                User localUser=dataSnapshot.getValue(User.class);
                                                                                Intent homeIntent = new Intent(MainActivity.this, Home.class);
                                                                                Common.currentUser = localUser;
                                                                                startActivity(homeIntent);
                                                                                waitingDialog.dismiss();
                                                                                finish();
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(DatabaseError databaseError) {

                                                                            }
                                                                        });
                                                            }
                                                        });
                                            }else { //if exits
                                                user.child(userPhone)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                User localUser=dataSnapshot.getValue(User.class);
                                                                Intent homeIntent = new Intent(MainActivity.this, Home.class);
                                                                Common.currentUser = localUser;
                                                                startActivity(homeIntent);
                                                                waitingDialog.dismiss();
                                                                finish();
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }

                        @Override
                        public void onError(AccountKitError accountKitError) {
                            Toast.makeText(MainActivity.this, ""+accountKitError.getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }

    private void startLogin() {
        Intent intent=new Intent(MainActivity.this,AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder accountKitConfigurationBuilder=new AccountKitConfiguration.AccountKitConfigurationBuilder(
                LoginType.PHONE,
                AccountKitActivity.ResponseType.TOKEN
        );
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,accountKitConfigurationBuilder.build());
        startActivityForResult(intent,reqstCode);
    }
    private void printKeyHash() {
        try {
            PackageInfo info=getPackageManager().getPackageInfo("com.example.fareed.lazeezo",
                    PackageManager.GET_SIGNATURES);
            for(Signature sign: info.signatures){
                MessageDigest md=MessageDigest.getInstance("SHA");
                md.update(sign.toByteArray());
                Log.i("KeyHash", Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }
}
