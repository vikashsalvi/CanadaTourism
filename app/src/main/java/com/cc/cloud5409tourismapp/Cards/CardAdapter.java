package com.cc.cloud5409tourismapp.Cards;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cc.cloud5409tourismapp.LandmarkInfo.LandmarkInfoActivity;
import com.cc.cloud5409tourismapp.R;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardViewHolder> {

    private Context context;
    private List<Card> cardData;
    String s3bucketUrl = "https://cloud-5409-tourism-app-resources.s3.amazonaws.com/";

    public CardAdapter(Context context, List<Card> cardData) {
        this.context = context;
        this.cardData = cardData;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_cardview, parent, false);
        return new CardViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, final int position) {
        Glide.with(context).load(s3bucketUrl + cardData.get(position).getPlaceId() + ".jpeg").into(holder.cardImage);
        holder.cardPlace.setText(cardData.get(position).getPlaceName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Toast.makeText(context, cardData.get(position).getPlaceId(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, LandmarkInfoActivity.class);
                intent.putExtra("place_id", cardData.get(position).getPlaceId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardData.size();
    }
}
class CardViewHolder extends RecyclerView.ViewHolder {
    ImageView cardImage;
    TextView cardPlace;
    public CardViewHolder(@NonNull final View itemView) {
        super(itemView);
        cardImage = itemView.findViewById(R.id.card_imageView);
        cardPlace = itemView.findViewById(R.id.place_cardView);
    }
}
