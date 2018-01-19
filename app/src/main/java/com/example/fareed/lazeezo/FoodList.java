package com.example.fareed.lazeezo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.fareed.lazeezo.Interface.ItemClickListener;
import com.example.fareed.lazeezo.Model.Food;
import com.example.fareed.lazeezo.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class FoodList extends AppCompatActivity {


    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList;

    FirebaseRecyclerAdapter<Food,FoodViewHolder> adapter;
    String categoryId="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);


        //Initialize Database
        database=FirebaseDatabase.getInstance();
        foodList=database.getReference("Foods");

        recyclerView=(RecyclerView)findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        //get CategoryId here
        if(getIntent()!=null)
            categoryId=getIntent().getStringExtra("CategoryId");
        if(!categoryId.isEmpty() && categoryId!=null){
            loadList(categoryId);
        }

    }

    private void loadList(String categoryId) {
        adapter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("MenuId").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                viewHolder.food_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.food_image);
                final Food local=model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(FoodList.this, ""+local.getName(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }
}
