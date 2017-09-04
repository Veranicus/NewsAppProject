package com.example.android.newsappproject;

/**
 * Created by Polacek on 16.7.2017.
 */

public class ListOfNews {
    private String mWebTitle;
    private String mSectionName;
    private String mWebPublicationDate;
    private String mWebUrl;
    private String mAuthor;
   /**
     * @param webtitle           news title
     * @param sectionName        section name
     * @param webPublicationDate date of publication
     * @param weburl             url of given news
     * @param author             name of author
     */
    public ListOfNews(String webtitle, String sectionName, String webPublicationDate, String weburl, String author) {
        mWebTitle = webtitle;
        mSectionName = sectionName;
        mWebPublicationDate = webPublicationDate;
        mWebUrl = weburl;
        mAuthor = author;
    }

    //getter methods
    public String getWebTitle() {
        return mWebTitle;
    }

    public String getSectionName() {
        return mSectionName;
    }

    public String getWebPublicationDate() {
        return mWebPublicationDate;
    }

    public String getWebUrl() {
        return mWebUrl;
    }

    public String getAuthor() {
        return mAuthor;
    }


}
