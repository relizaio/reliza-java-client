package rearm.java.client.interceptors;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.Cookie;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Bootstraps the ReARM CSRF token + cookie by calling
 * {@code /api/manual/v1/fetchCsrf} once at construction time, then attaches
 * both on every subsequent request. ReARM uses Spring Security's
 * {@code CookieCsrfTokenRepository} — the cookie name is {@code XSRF-TOKEN}
 * and the header name is whatever the server returns under {@code headerName}
 * (currently {@code X-XSRF-TOKEN}). Sending CSRF correctly is the
 * differentiator that lets HTTP-Basic callers reach {@code /graphql}.
 */
public class RearmCsrfInterceptor implements Interceptor {
	private String csrf;
	private String csrfHeaderName;
	private String csrfCookieName;
	private String csrfCookieValue;
	private final ObjectMapper om = new ObjectMapper();

	public RearmCsrfInterceptor(String baseUrl) {
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder()
				.url(baseUrl + "/api/manual/v1/fetchCsrf")
				.build();
		try (Response response = client.newCall(request).execute()) {
			JsonNode jsonNode = om.readTree(response.body().string());
			this.csrf = jsonNode.path("token").asText(null);
			this.csrfHeaderName = jsonNode.path("headerName").asText("X-XSRF-TOKEN");
			// Spring sets exactly one CSRF cookie on /fetchCsrf; capture whichever
			// cookie it actually emits so we stay correct if Spring renames it.
			List<Cookie> cookies = Cookie.parseAll(request.url(), response.headers());
			for (Cookie cookie : cookies) {
				String name = cookie.name();
				if ("XSRF-TOKEN".equalsIgnoreCase(name) || "JSESSIONID".equalsIgnoreCase(name)) {
					this.csrfCookieName = name;
					this.csrfCookieValue = cookie.value();
					break;
				}
			}
		} catch (Exception e) {
			// CSRF bootstrap failure surfaces later as a 401/403 on the first /graphql call;
			// nothing meaningful to do here without forcing every caller into a try/catch.
		}
	}

	@Override
	public Response intercept(Chain chain) throws IOException {
		Request request = chain.request();
		if (StringUtils.isNotBlank(csrf) && StringUtils.isNotBlank(csrfCookieName)
				&& StringUtils.isNotBlank(csrfCookieValue)) {
			Request withCsrf = request.newBuilder()
					.header(csrfHeaderName, csrf)
					.header("Cookie", csrfCookieName + "=" + csrfCookieValue)
					.build();
			return chain.proceed(withCsrf);
		}
		return chain.proceed(request);
	}
}
