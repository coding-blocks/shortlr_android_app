package com.codingblocks.shortlr.models;

/**
 * Created by piyush0 on 11/04/17.
 */

public class Result {
    private String shortcode;
    private Boolean existed;
    private String longURL;

    public String getShortcode() {
        return shortcode;
    }

    public void setShortcode(String shortcode) {
        this.shortcode = shortcode;
    }

    public Boolean getExisted() {
        return existed;
    }

    public void setExisted(Boolean existed) {
        this.existed = existed;
    }

    public String getLongURL() {
        return longURL;
    }

    public void setLongURL(String longURL) {
        this.longURL = longURL;
    }
}

