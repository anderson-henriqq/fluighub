package com.fluig.utils;

import com.fluig.oauthhub.OAuthClientInstance;
import com.fluig.model.ResponseGeneralModel;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class HttpRequestUtil {
	public static HttpPost createHttpPost(URI uri, Map<String, String> headers, StringEntity params) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
		HttpPost httpPost = new HttpPost(uri);



		if (params != null) {
			httpPost.setEntity(params);
		}

		addCustomHeaders(httpPost, headers);

		addAuthorizationHeader(httpPost, uri.toString(), Verb.POST);
		return httpPost;
	}

	private static void addCustomHeaders(HttpRequestBase request, Map<String, String> customHeaders) {
		if (customHeaders != null) {
			for (Map.Entry<String, String> entry : customHeaders.entrySet()) {
				request.setHeader(entry.getKey(), entry.getValue());
			}
		}
	}

	public static HttpGet createHttpGet(URI uri, Map<String, String> headers) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
		HttpGet httpGet = new HttpGet(uri);
		addAuthorizationHeader(httpGet, uri.toString(), Verb.GET);
		addCustomHeaders(httpGet, headers);
		return httpGet;
	}

	private static void addAuthorizationHeader(HttpRequestBase request, String uri, Verb verb) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
		OAuthClientInstance oAuthClient = OAuthClientInstance.getInstance();
		OAuth10aService service = oAuthClient.getService();
		OAuth1AccessToken accessToken = oAuthClient.getAccessToken();

		OAuthRequest oAuthRequest = new OAuthRequest(verb, uri);
		service.signRequest(accessToken, oAuthRequest);

		String authorizationHeader = oAuthRequest.getHeaders().get("Authorization");
		if (authorizationHeader != null) {
			request.setHeader("Authorization", authorizationHeader);
		}
	}

	public static CloseableHttpClient createHttpClient() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

		return HttpClients.custom().build();
	}

	public static ResponseGeneralModel executeHttpRequest(HttpUriRequest request) {
		try (CloseableHttpClient httpClient = createHttpClient()) {
			HttpResponse response = httpClient.execute(request);
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity);

			return new ResponseGeneralModel(result, false, response.getStatusLine().getStatusCode());
		} catch (IOException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
			throw new RuntimeException(e);
		}
	}
}
