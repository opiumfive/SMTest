package com.opiumfive.smtest.email_choser_view;


import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class ContactSearchAdapter extends ArrayAdapter<Contact> {

    private List<Contact> originalData;
    private List<Contact> filteredData;
    private ItemFilter filter = new ItemFilter();

    public ContactSearchAdapter(Context context, int resourse, List<Contact> data) {
        super(context, resourse, data);
        this.filteredData = data;
        this.originalData = data;
    }

    @Override
    public int getCount() {
        return filteredData == null ? 0 : filteredData.size();
    }

    @Override
    public Contact getItem(int position) {
        return filteredData == null ? null : filteredData.get(position);
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
                if (getFuzzyDistance(filterableContact.getEmail(), filterString) > 1) {
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

        // Find the Fuzzy Distance which indicates the similarity score between two Strings.
        private int getFuzzyDistance(final CharSequence term, final CharSequence query) {
            final String termLowerCase = term.toString().toLowerCase(Locale.ENGLISH);
            final String queryLowerCase = query.toString().toLowerCase(Locale.ENGLISH);

            int score = 0;
            int termIndex = 0;

            int previousMatchingCharacterIndex = Integer.MIN_VALUE;

            for (int queryIndex = 0; queryIndex < queryLowerCase.length(); queryIndex++) {
                final char queryChar = queryLowerCase.charAt(queryIndex);

                boolean termCharacterMatchFound = false;
                for (; termIndex < termLowerCase.length() && !termCharacterMatchFound; termIndex++) {
                    final char termChar = termLowerCase.charAt(termIndex);

                    if (queryChar == termChar) {
                        score++;

                        if (previousMatchingCharacterIndex + 1 == termIndex) {
                            score += 2;
                        }

                        previousMatchingCharacterIndex = termIndex;
                        termCharacterMatchFound = true;
                    }
                }
            }

            return score;
        }

    }
}
