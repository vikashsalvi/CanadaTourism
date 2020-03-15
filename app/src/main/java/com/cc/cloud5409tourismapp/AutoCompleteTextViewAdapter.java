package com.cc.cloud5409tourismapp;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteTextViewAdapter extends ArrayAdapter<String> implements Filterable {

    private List<String> searchListData;

    public AutoCompleteTextViewAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        searchListData = new ArrayList<>();

    }

    public void setData(List<String> slist){
        searchListData.clear();
        searchListData.addAll(slist);
    }


    @Override
    public int getCount() {
        return searchListData.size();
    }

    @Nullable
    @Override
    public String getItem(int position){
        return searchListData.get(position);
    }


    public String getObject(int position){
        return searchListData.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Filter dataFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    filterResults.values = searchListData;
                    filterResults.count = searchListData.size();
                }
                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && (results.count > 0)) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return dataFilter;
    }
}

