package reliza.java.client.interceptors;

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
 * Class to automatically set up basic CSRF support
 */
public class CsrfInterceptor implements Interceptor {
    private String csrf = null;
    private String jSessionId = null;
    ObjectMapper OM = new ObjectMapper();

    public CsrfInterceptor(String baseUrl){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
            .url(baseUrl + "/api/manual/v1/fetchCsrf")
            .build();

        try {
            Response response = client.newCall(request).execute();
            JsonNode jsonNode = OM.readTree(response.body().string());
            this.csrf = jsonNode.get("token").asText(); 
            List<Cookie> cookies = Cookie.parseAll(request.url(), response.headers());
            for (Cookie cookie : cookies) {
                if (cookie.name().equals("JSESSIONID")) {
                    this.jSessionId = cookie.value();
                    break;
                }
            }
        } catch (Exception e) {
            // TODO: throw exception when CSRF is enforced
        }
    }

    /**
	 * Method automatically called when API call is made to intercept add CSRF headers
	 */
	@Override
	public Response intercept(Chain chain) throws IOException {
		Request request = chain.request();
        if(StringUtils.isNotBlank(this.csrf) && StringUtils.isNotBlank(this.jSessionId)){
            Request csrfRequest = request.newBuilder()
			.header("X-CSRF-Token", this.csrf)
			.header("Cookie", "JSESSIONID="+this.jSessionId)
            .build();
		    return chain.proceed(csrfRequest);
        }   
		return chain.proceed(request);
	}
}
