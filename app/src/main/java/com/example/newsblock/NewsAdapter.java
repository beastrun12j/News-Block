package com.example.newsblock;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class NewsAdapter extends ArrayAdapter<News>
{

    public NewsAdapter(Activity context, ArrayList<News> news)
    {
        super(context,0,news);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View listItemView = convertView;
        if(listItemView == null)
        {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        News currentPosition = getItem(position);

        TextView HeadlineTextView = listItemView.findViewById(R.id.headline);
        HeadlineTextView.setText(currentPosition.getHeadline());

        TextView TopicTextView = listItemView.findViewById(R.id.topic);
        TopicTextView.setText(currentPosition.getTopic());

        TextView TypeTextView = listItemView.findViewById(R.id.type);
        String type = currentPosition.getType();
        type = type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase();
        TypeTextView.setText(type);

        TextView newsTimeTextView = listItemView.findViewById(R.id.time);
        newsTimeTextView.setText(covertTimeToText(currentPosition.getTimeInAgoFormat()));

        return listItemView;

    }

    public String covertTimeToText(String dataDate) {

        String convTime = null;

        String suffix = "Ago";

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date pasTime = dateFormat.parse(dataDate);

            Date nowTime = new Date();

            long dateDiff = nowTime.getTime() - pasTime.getTime();

            long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
            long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
            long hour   = TimeUnit.MILLISECONDS.toHours(dateDiff);
            long day  = TimeUnit.MILLISECONDS.toDays(dateDiff);

            if (second < 60) {
                convTime = second + " Seconds " + suffix;
            } else if (minute < 60) {
                convTime = minute + " Minutes "+suffix;
            } else if (hour < 24) {
                convTime = hour + " Hours "+suffix;
            } else if (day >= 7) {
                if (day > 360) {
                    convTime = (day / 360) + " Years " + suffix;
                } else if (day > 30) {
                    convTime = (day / 30) + " Months " + suffix;
                } else {
                    convTime = (day / 7) + " Week " + suffix;
                }
            } else if (day < 7) {
                convTime = day+" Days "+suffix;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("ConvTimeE", e.getMessage());
        }

        return convTime;
    }


}

