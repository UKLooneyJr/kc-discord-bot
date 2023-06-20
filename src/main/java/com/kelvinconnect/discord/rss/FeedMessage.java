package com.kelvinconnect.discord.rss;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FeedMessage implements Comparable<FeedMessage> {
    private static final Logger logger = LogManager.getLogger(FeedMessage.class);

    private String title;
    private String description;
    private String link;
    private String author;
    private String guid;
    private Date pubDate;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDateAsString) {
        SimpleDateFormat parser = new SimpleDateFormat("EEE, dd MMM YYYY HH:mm:ss zzz");
        try {
            this.pubDate = parser.parse(pubDateAsString);
        } catch (ParseException e) {
            logger.error("Failed to parse date " + pubDateAsString, e);
            this.pubDate = new Date(253402300799000L);
        }
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    @Override
    public String toString() {
        return "FeedMessage{"
                + "title='"
                + title
                + '\''
                + ", description='"
                + description
                + '\''
                + ", link='"
                + link
                + '\''
                + ", author='"
                + author
                + '\''
                + ", guid='"
                + guid
                + '\''
                + ", pubDate="
                + pubDate
                + '}';
    }

    @Override
    public int compareTo(FeedMessage o) {
        return getPubDate().compareTo(o.getPubDate());
    }
}
