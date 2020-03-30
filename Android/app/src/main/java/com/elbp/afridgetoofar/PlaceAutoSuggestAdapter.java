package com.elbp.afridgetoofar;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

public class PlaceAutoSuggestAdapter extends ArrayAdapter implements Filterable
{
    PlaceAutoSuggestAdapter(Context context, int resId)
    {
        super(context, resId);

        _placeApi = new PlaceApi();
    }

    @Override
    public int getCount()
    {
        return _resultsArrayList.size();
    }

    @Override
    public String getItem(int pos)
    {
        return _resultsArrayList.get(pos)[0];
    }

    @Override
    public Filter getFilter()
    {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence constraint)
            {
                FilterResults filterResults = new FilterResults();

                if (constraint != null)
                {
                    _resultsArrayList = _placeApi.autoComplete(constraint.toString());

                    filterResults.values = _resultsArrayList;
                    filterResults.count = _resultsArrayList.size();
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results)
            {
                if (results != null && results.count > 0)
                {
                    notifyDataSetChanged();
                }
                else
                {
                    notifyDataSetInvalidated();
                }
            }
        };

        return filter;
    }

    public String getMainText(int pos)
    {
        return _resultsArrayList.get(pos)[1];
    }

    public String getSecondaryText(int pos)
    {
        return _resultsArrayList.get(pos)[2];
    }


    private PlaceApi _placeApi;
    private ArrayList<String[]> _resultsArrayList;
}
