package com.example.fareed.lazeezo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telecom.Call;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.example.fareed.lazeezo.Common.Common;
import com.example.fareed.lazeezo.Database.Database;
import com.example.fareed.lazeezo.Interface.ItemClickListener;
import com.example.fareed.lazeezo.Model.Food;
import com.example.fareed.lazeezo.Model.Order;
import com.example.fareed.lazeezo.Model.Rating;
import com.example.fareed.lazeezo.ViewHolder.FoodViewHolder;
import com.example.fareed.lazeezo.ViewHolder.ShowCommentViewHolder;
import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.liuguangqiang.swipeback.SwipeBackActivity;
import com.liuguangqiang.swipeback.SwipeBackLayout;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.stepstone.apprating.C;

import java.util.ArrayList;
import java.util.List;

public class FoodList extends AppCompatActivity {


    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList;

    SwipeRefreshLayout swipeRefreshLayout;

    FirebaseRecyclerAdapter<Food,FoodViewHolder> adapter;

    Database localDb;
    CallbackManager callbackManager;
    ShareDialog shareDialog;


    Target target=new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            SharePhoto photo=new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();
            if(ShareDialog.canShow(SharePhotoContent.class)){
                SharePhotoContent sharePhotoContent=new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();
                shareDialog.show(sharePhotoContent);

            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };


    //search
    FirebaseRecyclerAdapter<Food,FoodViewHolder> searchAdapter;
    List<String> suggestList=new ArrayList<>();
    MaterialSearchBar materialSearchBar;
    String categoryId="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        //setDragEdge(SwipeBackLayout.DragEdge.LEFT);
        localDb=new Database(this);

        callbackManager= CallbackManager.Factory.create();
        shareDialog=new ShareDialog(this);


        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeRefresh);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //get CategoryId here
                if(getIntent()!=null)
                    categoryId=getIntent().getStringExtra("CategoryId");
                if(!categoryId.isEmpty() && categoryId!=null){
                    if(Common.isInternet(getBaseContext()))
                        loadList(categoryId);
                    else{
                        Toast.makeText(FoodList.this, "Internet Connection Failed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                //get CategoryId here
                if(getIntent()!=null)
                    categoryId=getIntent().getStringExtra("CategoryId");
                if(!categoryId.isEmpty() && categoryId!=null){
                    if(Common.isInternet(getBaseContext()))
                        loadList(categoryId);
                    else{
                        Toast.makeText(FoodList.this, "Internet Connection Failed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                materialSearchBar=(MaterialSearchBar)findViewById(R.id.searchBar);
                materialSearchBar.setHint("Search Food");
                loadSuggest();
                materialSearchBar.setCardViewElevation(10);
                materialSearchBar.addTextChangeListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        //when user type text,then suggest list will be changed
                        List<String> suggest=new ArrayList<String>();
                        for (String search: suggestList){
                            if (search.toLowerCase().equals(materialSearchBar.getText().toLowerCase().toString()))
                                suggest.add(search);
                        }
                        materialSearchBar.setLastSuggestions(suggest);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener(){

                    @Override
                    public void onSearchStateChanged(boolean enabled) {
                        //when search bar is closed restore original adapter
                        if(!enabled)
                            recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onSearchConfirmed(CharSequence text) {
                        //when search finish and then show result of search
                        startSearch(text);
                    }

                    @Override
                    public void onButtonClicked(int buttonCode) {

                    }
                });
            }
        });

        //Initialize Database
        database=FirebaseDatabase.getInstance();
        foodList=database.getReference("Foods");

        recyclerView=(RecyclerView)findViewById(R.id.recycler_food);
        //recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        LayoutAnimationController controller= AnimationUtils.loadLayoutAnimation(recyclerView.getContext(),R.anim.layout_fall_down);
        recyclerView.setLayoutAnimation(controller);




    }


    protected void onResume(){
        super.onResume();
        if(adapter!=null){
            adapter.startListening();
        }
    }
    private void startSearch(CharSequence text) {
        Query query=foodList.orderByChild("name").equalTo(text.toString());

        FirebaseRecyclerOptions<Food> options =new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(query,Food.class)
                .build();
        searchAdapter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item,parent,false);
                return new FoodViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {

               // Log.d(""+model.getName(), "onBindViewHolder: ");
                holder.food_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(holder.food_image);
                final Food local=model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Starting new activity
                        Intent foodDetail=new Intent(FoodList.this,FoodDetail.class);
                        foodDetail.putExtra("FoodId",searchAdapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });
            }


        };

        searchAdapter.startListening();
        recyclerView.setAdapter(searchAdapter);
        recyclerView.getAdapter().notifyDataSetChanged();

    }

    private void loadSuggest() {
        foodList.orderByChild("menuId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                            Food food=postSnapshot.getValue(Food.class);
                            suggestList.add(food.getName());
                        }

                        materialSearchBar.setLastSuggestions(suggestList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    private void loadList(String categoryId) {
        Query query=foodList.orderByChild("menuId").equalTo(categoryId);

        FirebaseRecyclerOptions<Food> options =new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(query,Food.class)
                .build();
        adapter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item,parent,false);
                return new FoodViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final FoodViewHolder holder, final int position, @NonNull final Food model) {
                holder.food_name.setText(model.getName());
                holder.foodPrice.setText(model.getPrice());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(holder.food_image);

                    holder.quickCart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                             boolean isExist=new Database(getBaseContext()).checkFoodExists(adapter.getRef(position).getKey(),Common.currentUser.getPhone());
                            Log.i("onClick: ",String.valueOf(isExist));
                             if(!isExist) {
                                new Database(getBaseContext()).addToCart(new Order(
                                        Common.currentUser.getPhone(),
                                        adapter.getRef(position).getKey(),
                                        model.getName(),
                                        "1",
                                        model.getPrice(),
                                        model.getDiscount(),
                                        model.getImage()
                                ));

                            } else {
                                new Database(getBaseContext()).icreaseCart(Common.currentUser.getPhone(),adapter.getRef(position).getKey());
                            }
                            Toast.makeText(FoodList.this, "Submitted To Cart", Toast.LENGTH_SHORT).show();
                        }
                    });

                if(localDb.isFavFood(adapter.getRef(position).getKey(),Common.currentUser.getPhone()))
                    holder.favImage.setImageResource(R.drawable.ic_favorite_black_24dp);

                holder.shareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Picasso.with(getApplicationContext())
                                .load(model.getImage())
                                .into(target);
                    }
                });

//                holder.quickCart.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        new Database(getApplicationContext()).addToCart(new Order(
//                                Common.currentUser.getPhone(),
//                                adapter.getRef(position).getKey(),
//                                model.getName(),
//                                "1",
//                                model.getPrice(),
//                                model.getDiscount(),
//                                model.getImage()
//                        ));
//                        Toast.makeText(FoodList.this, "Submitted To Cart", Toast.LENGTH_SHORT).show();
//                    }
//                });
                holder.favImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!localDb.isFavFood(adapter.getRef(position).getKey(),Common.currentUser.getPhone())){
                            localDb.addToFavFood(adapter.getRef(position).getKey(),Common.currentUser.getPhone());
                            holder.favImage.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(FoodList.this, ""+model.getName()+" added to Favorites", Toast.LENGTH_SHORT).show();
                        }else{
                            localDb.removeFavFood(adapter.getRef(position).getKey(),Common.currentUser.getPhone());
                            holder.favImage.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(FoodList.this, ""+model.getName()+" removed From Favorites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                final Food local=model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Starting new activity
                        Intent foodDetail=new Intent(FoodList.this,FoodDetail.class);
                        foodDetail.putExtra("FoodId",adapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
