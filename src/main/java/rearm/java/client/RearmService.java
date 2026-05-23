package rearm.java.client;

import java.util.Map;

import rearm.java.client.responses.RearmGraphQLResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Retrofit binding for the ReARM GraphQL endpoint. The {@code Apollo-Require-Preflight}
 * header is what ReARM's WAF inspects to distinguish first-party clients from
 * generic HTTP-Basic posters; CSRF token + JSESSIONID are attached by
 * {@link rearm.java.client.interceptors.RearmCsrfInterceptor}.
 */
public interface RearmService {
	// NOTE: do NOT add Accept-Encoding here. Setting it explicitly turns off
	// OkHttp's transparent gzip handling, leaving the caller with raw gzip
	// bytes that Jackson can't parse. ReARM's backend gzip-encodes larger
	// GraphQL responses, so the symptom is "Illegal character (CTRL-CHAR,
	// code 31)" on the first response big enough to compress (e.g.
	// getLatestRelease). Without an explicit header, OkHttp adds
	// `Accept-Encoding: gzip` and inflates the response transparently.
	@Headers({
		"Content-Type: application/json",
		"User-Agent: ReARM Java Client",
		"Apollo-Require-Preflight: true"
	})
	@POST("/graphql")
	Call<RearmGraphQLResponse> graphql(@Body Map<String, Object> body);
}
