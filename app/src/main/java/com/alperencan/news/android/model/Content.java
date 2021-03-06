package com.alperencan.news.android.model;

/**
 * A {@link Content} object contains information related to a single news item.
 */

public class Content {

    /**
     * Title of the news item
     */
    private String title;

    /**
     * Section name of the news item
     */
    private String section;

    /**
     * Url of the news item
     */
    private String url;

    /**
     * Author of the news item
     */
    private String author;

    /**
     * Date of the news item
     */
    private String date;

    /**
     * Constructs a new {@link Content} object.
     *
     * @param title   is the title of the news item
     * @param section is the section the news item belongs
     * @param url     is the url of the news item
     * @param author  is the author of the news item if provided
     * @param date    is the publish date of the news item if provided
     */
    public Content(String title, String section, String url, String author, String date) {
        this.title = title;
        this.section = section;
        this.url = url;
        this.author = author;
        this.date = date;
    }

    /**
     * @return title of the news item
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return section the news item belongs
     */
    public String getSection() {
        return section;
    }

    /**
     * @return url of the news item
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return the author of the news item
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @return the publish date of the news item
     */
    public String getDate() {
        return date;
    }
}