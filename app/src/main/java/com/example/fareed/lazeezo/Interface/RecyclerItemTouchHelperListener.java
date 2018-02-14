package com.example.fareed.lazeezo.Interface;

import android.support.v7.widget.RecyclerView;

/**
 * Created by fareed on 2/12/2018.
 */

public interface RecyclerItemTouchHelperListener {
    void onSwiped(RecyclerView.ViewHolder viewHolder,int direction,int position);
}
