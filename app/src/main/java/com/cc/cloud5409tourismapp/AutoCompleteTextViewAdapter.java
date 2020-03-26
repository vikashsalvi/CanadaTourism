package com.cc.cloud5409tourismapp;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AutoCompleteTextViewAdapter extends ArrayAdapter<String> implements Filterable {

    private List<String> searchListData;
    ArrayList<HashMap<String,String>> autoCompleteList;

    public AutoCompleteTextViewAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        searchListData = new ArrayList<>();
        autoCompleteList = new ArrayList<>();

    }

    public void setData(ArrayList<HashMap<String,String>> slist){
        autoCompleteList.clear();
        autoCompleteList.addAll(slist);
    }


    @Override
    public int getCount() {
        return autoCompleteList.size();
    }

    @Nullable
    @Override
    public String getItem(int position){
        return autoCompleteList.get(position).get("location");
    }


    public HashMap<String, String> getObject(int position){
        return autoCompleteList.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Filter dataFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    filterResults.values = autoCompleteList;
                    filterResults.count = autoCompleteList.size();
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

