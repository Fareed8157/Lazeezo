package com.example.fareed.lazeezo;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.fareed.lazeezo.Common.Common;
import com.example.fareed.lazeezo.Database.Database;
import com.example.fareed.lazeezo.Model.Food;
import com.example.fareed.lazeezo.Model.Order;
import com.example.fareed.lazeezo.Model.Rating;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.liuguangqiang.swipeback.SwipeBackActivity;
import com.liuguangqiang.swipeback.SwipeBackLayout;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class FoodDetail extends AppCompatActivity implements RatingDialogListener{

    TextView food_name,food_price,food_description;
    ImageView food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton ratingButton;
    CounterFab btnCart;
    ElegantNumberButton numberButton;

    Button showComment;
    RatingBar ratingBar;

    String foodId="";

    Food currentFood;
    DatabaseReference ratingTable;
    FirebaseDatabase database;
    DatabaseReference foods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);
        //setDragEdge(SwipeBackLayout.DragEdge.LEFT);
        showComment=(Button)findViewById(R.id.btnShowComment);
        database=FirebaseDatabase.getInstance();
        foods=database.getReference("Foods");
        ratingTable=database.getReference("Rating");

        ratingButton=(FloatingActionButton)findViewById(R.id.btnRating) ;
        ratingBar=(RatingBar)findViewById(R.id.ratingBar);

        ratingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRatingDialog();
            }
        });

        numberButton=(ElegantNumberButton)findViewById(R.id.number_button);
        btnCart=(CounterFab) findViewById(R.id.btnCart);

        showComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent comment=new Intent(FoodDetail.this,ShowComment.class);
                comment.putExtra(Common.INTENT_FOOD_ID,foodId);
                startActivity(comment);
            }
        });
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Database(getApplicationContext()).addToCart(new Order(
                        Common.currentUser.getPhone(),
                        foodId,
                        currentFood.getName(),
                        numberButton.getNumber(),
                        currentFood.getPrice(),
                        currentFood.getDiscount(),
                        currentFood.getImage()
                        ));
                Toast.makeText(FoodDetail.this, "Submitted To Cart", Toast.LENGTH_SHORT).show();
            }
        });

        btnCart.setCount(new Database(this).getCountCart(Common.currentUser.getPhone()));
        food_description=(TextView)findViewById(R.id.food_description);
        food_price=(TextView)findViewById(R.id.food_price);
        food_name=(TextView)findViewById(R.id.food_name);

        food_image=(ImageView)findViewById(R.id.img_food);

        collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);


        if(getIntent()!=null)
            foodId=getIntent().getStringExtra("FoodId");
        if(!foodId.isEmpty()){
            if(Common.isInternet(getBaseContext())) {
                getDetailFood(foodId);
                getRatingOfFood(foodId);
            }else{
                Toast.makeText(FoodDetail.this, "Internet Connection Failed", Toast.LENGTH_SHORT).show();
                return;
            }
        }

    }

    private void getRatingOfFood(String foodId) {
        Query foodRating=ratingTable.orderByChild("foodId").equalTo(foodId);

        foodRating.addValueEventListener(new ValueEventListener() {
            int count=0,sum=0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postDataSnapshot: dataSnapshot.getChildren()){
                    Rating item=postDataSnapshot.getValue(Rating.class);
                    sum+=Integer.parseInt(item.getRateValue());
                    count++;
                }
                if (count!=0){
                    float average=sum/count;
                    ratingBar.setRating(average);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Good","Not Good","Just Good","Very bad","Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate Food")
                .setDescription("Your Feedback")
                .setTitleTextColor(R.color.colorPrimaryDark)
                .setDescriptionTextColor(R.color.signInActive)
                .setHint("Your comment here")
                .setHintTextColor(R.color.white)
                .setCommentTextColor(R.color.white)
                .setCommentBackgroundColor(R.color.signInActive)
                .setWindowAnimation(R.style.MyDialogSlideVerticalAnimation)
                .create(FoodDetail.this)
                .show();
    }

    private void getDetailFood(String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentFood=dataSnapshot.getValue(Food.class);
                Picasso.with(getBaseContext()).load(currentFood.getImage()).into(food_image);
                collapsingToolbarLayout.setTitle(currentFood.getName());
                food_price.setText(currentFood.getPrice());
                food_name.setText(currentFood.getName());
                food_description.setText(currentFood.getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onPositiveButtonClicked(int i, @NotNull String s) {
        final Rating rating =new Rating(Common.currentUser.getPhone(),foodId,String.valueOf(i),s);

        Log.i("onPositive",rating.getUserPhone());
        ratingTable.push()
                .setValue(rating)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(FoodDetail.this, "Thank You for Rating", Toast.LENGTH_SHORT).show();
                    }
                });
//        ratingTable.child(Common.currentUser.getPhone()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if(dataSnapshot.child(Common.currentUser.getPhone()).exists()) {
//                    ratingTable.child(Common.currentUser.getPhone()).removeValue();
//                    ratingTable.child(Common.currentUser.getPhone()).setValue(rating);
//                }else
//                {
//                    ratingTable.child(Common.currentUser.getPhone()).setValue(rating);
//                }
//                Toast.makeText(FoodDetail.this, "Thank You for Rating", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    @Override
    public void onNegativeButtonClicked() {

    }
}
