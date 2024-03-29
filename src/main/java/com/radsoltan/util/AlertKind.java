package com.radsoltan.util;

import com.radsoltan.constants.UIText;

/**
 * Enum for types of popup alerts in the app. The enum includes a url to the icon image and a text that is displayed in alert title bar.
 * The enum supports three different alert kinds: info, warning and error.
 */
public enum AlertKind {
    INFO("/images/alert/info.png", UIText.INFO),
    WARNING("/images/alert/warning.png", UIText.WARNING),
    ERROR("/images/alert/error.png", UIText.ERROR);

    private final String url;
    private final String title;

    /**
     * Enum constructor.
     *
     * @param url   The url of the image icon.
     * @param title Title to be displayed in the window.
     */
    AlertKind(String url, String title) {
        this.url = url;
        this.title = title;
    }

    /**
     * Getter for the url.
     *
     * @return url of the image icon.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Getter for the title.
     *
     * @return title of the alert
     */
    public String getTitle() {
        return title;
    }
}
