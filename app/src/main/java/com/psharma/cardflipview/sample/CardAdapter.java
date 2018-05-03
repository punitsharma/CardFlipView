package com.psharma.cardflipview.sample;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.psharma.cardflipview.CardFlipView;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.MyViewHolder> {

    private List<FlipModel> list;

    class MyViewHolder extends RecyclerView.ViewHolder {
        CardFlipView cardFlipView;

        MyViewHolder(View view) {
            super(view);
            cardFlipView = view.findViewById(R.id.cardflipView);
        }
    }

    CardAdapter(
            List<FlipModel> list
    ) {
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        if (holder.cardFlipView.getCurrentFlipState() == CardFlipView.CardFlipState.CARD_FRONT_SIDE && list.get(
                position).isFlipped) {
            holder.cardFlipView.setCardFlipDuration(0);
            holder.cardFlipView.flipCard();
        } else if (holder.cardFlipView.getCurrentFlipState() == CardFlipView.CardFlipState.CARD_BACK_SIDE
                && !list.get(position).isFlipped) {
            holder.cardFlipView.setCardFlipDuration(0);
            holder.cardFlipView.flipCard();
        }
        holder.cardFlipView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.get(position).isFlipped) {
                    list.get(position).isFlipped = false;
                } else {
                    list.get(position).isFlipped = true;
                }
                holder.cardFlipView.setCardFlipDuration(700);
                holder.cardFlipView.flipCard();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}


