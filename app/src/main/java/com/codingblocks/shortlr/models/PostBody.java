package com.codingblocks.shortlr.models;

/**
 * Created by piyush0 on 11/04/17.
 */

public class PostBody {
    private String url;
    private String secret;
    private String code;

    public PostBody(String url, String secret, String code) {
        this.url = url;
        this.secret = secret;
        this.code = code;
    }
}

