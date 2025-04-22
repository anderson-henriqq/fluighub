package com.fluig.utils;

import com.fluig.configuration.ConfigurationHub;

public class ApiKeyUtil {

    private static final String API_KEY;

    static {
        ConfigurationHub config = ConfigurationHub.getInstance();
        API_KEY = config.getApi();
        if (API_KEY == null) {
            throw new RuntimeException("Chave API não encontrada no arquivo api.properties");
        }
    }

    private ApiKeyUtil() {
        // Evita instâncias
    }

    public static String getApiKey() {
        return API_KEY;
    }
}