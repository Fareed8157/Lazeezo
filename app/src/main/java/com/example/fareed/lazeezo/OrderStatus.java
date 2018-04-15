package com.example.fareed.lazeezo;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fareed.lazeezo.Common.Common;
import com.example.fareed.lazeezo.Model.Rating;
import com.example.fareed.lazeezo.Model.Request;
import com.example.fareed.lazeezo.ViewHolder.OrderViewHolder;
import com.example.fareed.lazeezo.ViewHolder.ShowCommentViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class OrderStatus extends AppCompatActivity {


    FirebaseRecyclerAdapter<Request,OrderViewHolder> adapter;
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        database=FirebaseDatabase.getInstance();
        requests=database.getReference("Requests");

        recyclerView=(RecyclerView)findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //if(getIntent()==null)
            loadOrders(Common.currentUser.getPhone());
        //else
          //  loadOrders(getIntent().getStringExtra("userPhone"));
    }

    private void loadOrders(String phone) {

        Query query=requests.orderByChild("phone").equalTo(Common.currentUser.getPhone());


        FirebaseRecyclerOptions<Request> options =new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(query,Request.class)
                .build();

        adapter=new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull Request model) {
                holder.txtOrderId.setText(adapter.getRef(position).getKey());
                holder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                holder.txtOrderPhone.setText(model.getPhone());
                holder.txtOrderAddress.setText(model.getAddress());
                holder.txtOrderDate.setText(Common.getDate(Long.parseLong(adapter.getRef(position).getKey())));
                Log.i("populateViewHolder: ", model.getPhone());
                Log.i("populateViewHolder: ", model.getName());
                Log.i("populateViewHolder: ", model.getStatus());
                Log.i("populateViewHolder: ", model.getComment());
            }

            @Override
            public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate( R.layout.order_layout, parent, false);
                return new OrderViewHolder(view);
            }
        };
        /*
        adapter=new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests.orderByChild("phone").equalTo(phone)
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.txtOrderAddress.setText(model.getAddress());
                Log.i( "populateViewHolder: ",model.getPhone());
                Log.i( "populateViewHolder: ",model.getName());
                Log.i( "populateViewHolder: ",model.getStatus());
                Log.i( "populateViewHolder: ",model.getComment());
            }
        };*/
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
