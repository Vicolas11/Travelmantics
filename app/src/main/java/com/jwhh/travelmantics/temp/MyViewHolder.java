package com.jwhh.travelmantics.temp;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jwhh.travelmantics.R;

class MyViewHolder extends RecyclerView.ViewHolder{
    TextView mTvDeal, mTvDesc, mTvPrice;
    ImageView mImageView;
    Context mContext;
    View view;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        mTvDeal = (TextView) itemView.findViewById(R.id.tvDeal);
        mTvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
        mTvDesc = (TextView) itemView.findViewById(R.id.tvDesc);
        mImageView = (ImageView) itemView.findViewById(R.id.imageView);
        view = itemView;
    }
}
