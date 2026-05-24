package com.rearmhq.javaclient.interceptors;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Adds HTTP-Basic credentials to every outbound request. ReARM FREEFORM keys
 * are sent as {@code Basic <base64(id:secret)>}.
 */
public class RearmBasicAuthInterceptor implements Interceptor {
	private final String credentials;

	public RearmBasicAuthInterceptor(String apiKeyId, String apiKey) {
		this.credentials = Credentials.basic(apiKeyId, apiKey);
	}

	@Override
	public Response intercept(Chain chain) throws IOException {
		Request authenticatedRequest = chain.request().newBuilder()
				.header("Authorization", credentials)
				.build();
		return chain.proceed(authenticatedRequest);
	}
}
