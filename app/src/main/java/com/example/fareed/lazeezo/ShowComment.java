package com.example.fareed.lazeezo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fareed.lazeezo.Common.Common;
import com.example.fareed.lazeezo.Model.Rating;
import com.example.fareed.lazeezo.ViewHolder.ShowCommentViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ShowComment extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference ratingTable;

    SwipeRefreshLayout swipeRefreshLayout;

    FirebaseRecyclerAdapter<Rating,ShowCommentViewHolder> adapter;


    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null){
            adapter.stopListening();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));

    }


        String foodId="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_comment);


        database=FirebaseDatabase.getInstance();
        ratingTable=database.getReference("Rating");


        recyclerView=(RecyclerView)findViewById(R.id.recyclerComment);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeRefresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
              if(getIntent()!=null)
                  foodId=getIntent().getStringExtra(Common.INTENT_FOOD_ID);
                if(!foodId.isEmpty() && foodId!=null){
                      Query query=ratingTable.orderByChild("foodId").equalTo(foodId);

                      FirebaseRecyclerOptions<Rating> options =new FirebaseRecyclerOptions.Builder<Rating>()
                              .setQuery(query,Rating.class)
                              .build();

                      adapter=new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(options) {
                          @Override
                          protected void onBindViewHolder(@NonNull ShowCommentViewHolder holder, int position, @NonNull Rating model) {
                              holder.ratingBar.setRating(Float.parseFloat(model.getRateValue()));
                              holder.comment.setText(model.getComment());
                              holder.phoneNo.setText(model.getUserPhone());


                          }

                          @Override
                          public ShowCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                              View view= LayoutInflater.from(parent.getContext())
                                      .inflate(R.layout.show_comment_layout,parent,false);
                              return new ShowCommentViewHolder(view);
                          }
                      };
                      loadComment(foodId);
                  }

            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                if(getIntent()!=null){
                    foodId=getIntent().getStringExtra(Common.INTENT_FOOD_ID);
                    if(!foodId.isEmpty() && foodId!=null){
                        Query query=ratingTable.orderByChild("foodId").equalTo(foodId);

                        FirebaseRecyclerOptions<Rating> options =new FirebaseRecyclerOptions.Builder<Rating>()
                                .setQuery(query,Rating.class)
                                .build();

                        adapter=new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(options) {
                            @Override
                            protected void onBindViewHolder(@NonNull ShowCommentViewHolder holder, int position, @NonNull Rating model) {
                                holder.ratingBar.setRating(Float.parseFloat(model.getRateValue()));
                                holder.comment.setText(model.getComment());
                                holder.phoneNo.setText(Common.currentUser.getName());
                            }

                            @Override
                            public ShowCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                View view= LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.show_comment_layout,parent,false);
                                return new ShowCommentViewHolder(view);
                            }
                        };
                        loadComment(foodId);
                    }
                }
            }
        });
    }

    private void loadComment(String foodId) {
        adapter.startListening();

        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }
}
