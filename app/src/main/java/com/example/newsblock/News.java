package com.example.newsblock;

public class News {

    String mHeadline;
    String mTopic;
    String mType;
    String mTimeInAgoFormat;
    String mUrl;

    public News(String Headline,String Topic,String Type,String TimeInAgoFormat,String Url)
    {
        mHeadline = Headline;
        mTopic = Topic;
        mType = Type;
        mTimeInAgoFormat = TimeInAgoFormat;
        mUrl = Url;
    }

    public String getHeadline() { return mHeadline;}

    public String getTopic() { return mTopic;}

    public String getType() { return mType; }

    public String getTimeInAgoFormat() { return mTimeInAgoFormat;}

    public String getUrl() { return mUrl;}

}
