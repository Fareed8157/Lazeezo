package com.example.fareed.lazeezo.ViewHolder;

import android.content.ClipData;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.fareed.lazeezo.Interface.ItemClickListener;
import com.example.fareed.lazeezo.R;

/**
 * Created by fareed on 1/27/2018.
 */

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


    public TextView txtOrderId,txtOrderStatus,txtOrderPhone,txtOrderAddress,txtOrderDate;
    private ItemClickListener itemClickListener;

    public OrderViewHolder(View itemView) {
        super(itemView);

        txtOrderId=(TextView)itemView.findViewById(R.id.order_id);
        txtOrderStatus=(TextView)itemView.findViewById(R.id.order_status);
        txtOrderPhone=(TextView)itemView.findViewById(R.id.order_phone);
        txtOrderAddress=(TextView)itemView.findViewById(R.id.order_address);
        txtOrderDate=(TextView)itemView.findViewById(R.id.order_date);


        itemView.setOnClickListener(this);
    }


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}
