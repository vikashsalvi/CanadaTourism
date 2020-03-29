package com.cc.cloud5409tourismapp.Cards;

import android.widget.ImageView;

public class Card {
    public String placeName;
    public String placeId;

    public Card(String placeName, String placeId) {
        this.placeName = placeName;
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getPlaceId() {
        return placeId;
    }

}
