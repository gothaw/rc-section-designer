package com.radsoltan.controllers;

public enum AlertKind {
    INFO("/images/alert/info.png", "Info"),
    WARNING("/images/alert/warning.png", "Warning!"),
    ERROR("/images/alert/error.png", "Error!");

    private final String url;
    private final String title;

    AlertKind(String url, String title){
        this.url = url;
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }
}
