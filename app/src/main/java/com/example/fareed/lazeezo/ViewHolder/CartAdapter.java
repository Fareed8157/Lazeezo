package com.example.fareed.lazeezo.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextDirectionHeuristics;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.fareed.lazeezo.Cart;
import com.example.fareed.lazeezo.Common.Common;
import com.example.fareed.lazeezo.Database.Database;
import com.example.fareed.lazeezo.Interface.ItemClickListener;
import com.example.fareed.lazeezo.Model.Order;
import com.example.fareed.lazeezo.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by fareed on 1/26/2018.
 */



public class CartAdapter extends RecyclerView.Adapter<CartViewHolder>{

    private List<Order> listData=new ArrayList<>();
    private Cart context;

    public CartAdapter(List<Order> listData, Cart context) {
        this.listData = listData;
        this.context = context;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View itemView=inflater.inflate(R.layout.cart_layout,parent,false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, final int position) {
//        TextDrawable drawable=TextDrawable.builder()
//                .buildRect(""+listData.get(position).getQuantity(), Color.RED);

        Picasso.with(context.getBaseContext())
                .load(listData.get(position).getImage())
                .resize(70,70)
                .centerCrop()
                .into(holder.cart_image);

        final Locale locale=new Locale("en","PK");
        NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);
        holder.numberButton.setNumber(listData.get(position).getQuantity());
        holder.numberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Order order=listData.get(position);
                order.setQuantity(String.valueOf(newValue));
                new Database(context).updateCart(order);

                int total=0;
                List<Order> orders=new Database(context).getCarts(Common.currentUser.getPhone());
                for (Order item:orders)
                    total+=(Integer.parseInt(order.getPrice())*Integer.parseInt(item.getQuantity()));
                NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);
                context.txtTotalPrice.setText(fmt.format(total));
            }
        });
        //holder.img_cart_count.setImageDrawable(drawable);

        int price=(Integer.parseInt(listData.get(position).getPrice())*Integer.parseInt(listData.get(position).getQuantity()));
        holder.txt_price.setText(fmt.format(price));
        holder.txt_cart_name.setText(listData.get(position).getProductName());

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public Order getItem(int position){
        return listData.get(position);
    }

    public void removeItem(int position){
        listData.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Order item,int position){
        listData.add(position,item);
        notifyItemInserted(position);
    }




}
