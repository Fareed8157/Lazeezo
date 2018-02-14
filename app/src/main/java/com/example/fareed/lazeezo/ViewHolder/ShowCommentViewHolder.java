package com.example.fareed.lazeezo.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.fareed.lazeezo.R;

/**
 * Created by fareed on 2/8/2018.
 */

public class ShowCommentViewHolder extends RecyclerView.ViewHolder {

    public TextView phoneNo,comment;
    public RatingBar ratingBar;
    public ShowCommentViewHolder(View itemView) {
        super(itemView);

        phoneNo=(TextView)itemView.findViewById(R.id.txtUserPhone);
        comment=(TextView)itemView.findViewById(R.id.txtComment);
        ratingBar=(RatingBar)itemView.findViewById(R.id.rating_Bar);

    }
}
