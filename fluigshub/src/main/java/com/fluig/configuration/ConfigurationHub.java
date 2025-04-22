package com.fluig.configuration;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationHub {
    private final Properties configProp = new Properties();
    private final Properties versionProp = new Properties();
    private final Properties apiProp = new Properties();

    public ConfigurationHub() {
//        ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
//        ServletContext request = factory.getContextData(ServletContext.class);
        ServletContext request = ResteasyProviderFactory.getContextData(ServletContext.class);

        InputStream is = null;
        InputStream is2, is3;

        try {
            is = request.getResourceAsStream("resources/" + "config.properties");
            is2 = request.getResourceAsStream("resources/" + "version.properties");
            is3 = request.getResourceAsStream("resources/" + "api.properties");
            versionProp.load(is2);
            configProp.load(is);
            apiProp.load(is3);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert is != null;
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static ConfigurationHub getInstance() {
        return PropertiesLazyHolder.INSTANCE;
    }

    public String getProperty(String key) {
        return configProp.getProperty(key);
    }

    public String getVersion() {
        return versionProp.getProperty("build.timestamp");
    }

    public String getApi() {return apiProp.getProperty("API_KEY");}

    private static class PropertiesLazyHolder {
        private static final ConfigurationHub INSTANCE = new ConfigurationHub();
    }
}