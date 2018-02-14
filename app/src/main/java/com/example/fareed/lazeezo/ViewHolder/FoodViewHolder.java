package com.example.fareed.lazeezo.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fareed.lazeezo.Interface.ItemClickListener;
import com.example.fareed.lazeezo.R;

/**
 * Created by fareed on 1/20/2018.
 */

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView food_name,foodPrice;
    public ImageView food_image,favImage,quickCart,shareButton;

    private ItemClickListener itemClickListener;


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public FoodViewHolder(View itemView) {
        super(itemView);

        food_name=(TextView)itemView.findViewById(R.id.food_name);
        foodPrice=(TextView)itemView.findViewById(R.id.food_price);
        food_image=(ImageView)itemView.findViewById(R.id.food_image);
        favImage=(ImageView)itemView.findViewById(R.id.fav);

        quickCart=(ImageView)itemView.findViewById(R.id.btnQuickCart);
        shareButton=(ImageView)itemView.findViewById(R.id.btnShare);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}
