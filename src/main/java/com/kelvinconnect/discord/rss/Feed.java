package com.kelvinconnect.discord.rss;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Feed {

    private final List<FeedMessage> entries = new ArrayList<>();
    private final String title;
    private final String link;
    private final String description;
    private final String language;
    private final String copyright;

    public Feed(String title, String link, String description, String language, String copyright) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.language = language;
        this.copyright = copyright;
    }

    public List<FeedMessage> getMessages() {
        return entries;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public String getLanguage() {
        return language;
    }

    public String getCopyright() {
        return copyright;
    }

    @Override
    public String toString() {
        return "Feed{" + "title='" + title + '\'' + ", link='" + link + '\'' + ", description='" + description + '\''
                + ", language='" + language + '\'' + ", copyright='" + copyright + '\'' + ", entries=" + entries + '}';
    }

    public void sortMessages() {
        Collections.sort(entries);
    }
}