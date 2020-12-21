package com.lexor.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigProperties {

    public  static Properties loadConfig() throws IOException {
        Properties props =  new Properties();
        InputStream inputStream = new FileInputStream(System.getProperty("config"));
        props.load(inputStream);
        return props;
    }

}
