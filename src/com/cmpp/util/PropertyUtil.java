package com.cmpp.util;

import java.util.ResourceBundle;

public class PropertyUtil {
    private String propertyName = "ServerIPAddress.properties";

    private ResourceBundle res = null;

    private void init() {
        res = ResourceBundle.getBundle(propertyName);
    }

    public PropertyUtil(String propertyName) {
        this.propertyName = propertyName;
        init();
    }

    public String getValue(String title) {
        return res.getString(title);
    }
}
