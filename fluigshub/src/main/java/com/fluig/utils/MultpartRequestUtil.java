package com.fluig.utils;


import com.fluig.model.ResponseGeneralModel;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;


import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
public class MultpartRequestUtil {
	public static ResponseGeneralModel uploadFile(URI uri, MultipartEntityBuilder builder, InputStream inputStream, String fileName)
			throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException, IOException {

		builder.addBinaryBody("file", inputStream, ContentType.APPLICATION_OCTET_STREAM, fileName);

		HttpPost httpPost = HttpRequestUtil.createHttpPost(uri, null, null);
		httpPost.setEntity(builder.build());

		return HttpRequestUtil.executeHttpRequest(httpPost);
	}
}
