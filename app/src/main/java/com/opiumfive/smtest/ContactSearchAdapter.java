package com.opiumfive.smtest;


import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import java.util.ArrayList;
import java.util.List;


public class ContactSearchAdapter extends ArrayAdapter<Contact> {

    private List<Contact> originalData = null;
    private List<Contact> filteredData = null;
    private ItemFilter filter = new ItemFilter();

    public ContactSearchAdapter(Context context, int resourse, List<Contact> data) {
        super(context, resourse, data);
        this.filteredData = data ;
        this.originalData = data ;
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public Contact getItem(int position) {
        return filteredData.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }



    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<Contact> list = originalData;

            int count = list.size();
            final List<Contact> nlist = new ArrayList<>(count);

            Contact filterableContact;

            for (int i = 0; i < count; i++) {
                filterableContact = list.get(i);
                if (filterableContact.getEmail().toLowerCase().contains(filterString)) {
                    nlist.add(filterableContact);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<Contact>) results.values;
            notifyDataSetChanged();
        }

    }
}
