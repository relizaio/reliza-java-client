package reliza.java.client.interceptors;

import java.io.IOException;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Class to automatically set up basic authentication for calls
 */
public class BasicAuthInterceptor implements Interceptor {
	private String credentials;

	/**
	 * Uses apiKeyId and apiKey to create a basic authentication string
	 * @param apiKeyId - apiKeyId flag specified in builder
	 * @param apiKey - apiKey flag specified in builder
	 */
	public BasicAuthInterceptor(String apiKeyId, String apiKey) {
		this.credentials = Credentials.basic(apiKeyId, apiKey);
	}

	/**
	 * Method automatically called when API call is made to intercept it and put in the basic authentication string
	 */
	@Override
	public Response intercept(Chain chain) throws IOException {
		Request request = chain.request();
		Request authenticatedRequest = request.newBuilder()
			.header("Authorization", credentials).build();
		return chain.proceed(authenticatedRequest);
	}
}