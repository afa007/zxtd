package com.cmeb.util;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class ConfigUtil {

    private static String propFileName = "ServerIPAddress.properties";

    private static Properties props;

    public static String getProp(String keyStr) {
        if (props == null) {
            try {
                Resource resource = new ClassPathResource(propFileName);
                props = PropertiesLoaderUtils.loadProperties(resource);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!props.containsKey(keyStr)) {
            return null;
        }
        return props.getProperty(keyStr);
    }

    public static String getPropFileName() {
        return propFileName;
    }

    public static void setPropFileName(String propFileName) {
        ConfigUtil.propFileName = propFileName;
    }

}
