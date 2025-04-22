package com.fluig.oauthhub;

import com.fluig.configuration.ConfigurationHub;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.oauth.OAuth10aService;

public class OAuthClientInstance {

    private final OAuth10aService service;
    private final OAuth1AccessToken accessToken;

    public OAuthClientInstance() {
        // Configura o serviço OAuth1.0a
        service = new ServiceBuilder(ConfigurationHub.getInstance().getProperty("CONSUMER_KEY")).apiSecret(ConfigurationHub.getInstance().getProperty("CONSUMER_SECRET")).build(new DefaultApi10a() {
            @Override
            public String getRequestTokenEndpoint() {
                return null;
            }

            @Override
            public String getAccessTokenEndpoint() {
                return null;
            }

            @Override
            protected String getAuthorizationBaseUrl() {
                return null;
            }
        });

        // Cria o token de acesso
        accessToken = new OAuth1AccessToken(ConfigurationHub.getInstance().getProperty("ACCESS_TOKEN"), ConfigurationHub.getInstance().getProperty("TOKEN_SECRET"));
    }

    public static OAuthClientInstance getInstance() {
        return OAuthClientLazyHolder.INSTANCE;
    }

    public OAuth10aService getService() {
        return service;
    }

    public OAuth1AccessToken getAccessToken() {
        return accessToken;
    }

    private static class OAuthClientLazyHolder {
        private static final OAuthClientInstance INSTANCE = new OAuthClientInstance();
    }
}
