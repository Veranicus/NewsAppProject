package com.example.android.newsappproject;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Polacek on 16.7.2017.
 */

public class ListOfNewsAdapter extends ArrayAdapter<ListOfNews> {
    public static final String LOG_TAG = ListOfNewsAdapter.class.getName();

    public ListOfNewsAdapter(Activity context, ArrayList<ListOfNews> items) {
        super(context, 0, items);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listView = convertView;
        if (listView == null) {
            listView = LayoutInflater.from(getContext()).inflate(R.layout.news_list, parent, false);
        }
        //geting the item's postion
        if (position < getCount()) {
            ListOfNews currentItem = getItem(position);
            //displaying title news to a lsit
            TextView tw_title = (TextView) listView.findViewById(R.id.TitleOfArticle);
            tw_title.setText(currentItem.getWebTitle());
            //display name of the author to a list
            TextView tw_authors = (TextView) listView.findViewById(R.id.AuthorsName);
            tw_authors.setText(currentItem.getAuthor());
            //displaying Publishion Date to a list
            TextView tw_date = (TextView) listView.findViewById(R.id.DatePublished);
            tw_date.setText(formatDate(currentItem.getWebPublicationDate()));
            //Displaying Given section to a list
            TextView tw_section = (TextView) listView.findViewById((R.id.Section));
            tw_section.setText(currentItem.getSectionName());
        }
        return listView;
    }

    public String formatDate(String date) {
        String newFormatData = "";
        if (date.length() >= 10) {
            // Formating date after 10 characters
            CharSequence splittedDate = date.subSequence(0, 10);
            try {
                Date formatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(splittedDate.toString());
                newFormatData = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(formatDate);
            } catch (ParseException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        } else {
            newFormatData = date;
        }
        return newFormatData;
    }
}
