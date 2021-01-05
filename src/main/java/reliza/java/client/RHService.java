package reliza.java.client;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

// retrofit docs - https://square.github.io/retrofit/

public interface RHService {
	
	/**
	 * This is just to bootstrap the project - no use in real life
	 * @return
	 */
	@Headers({
	    "Accept: application/json",
	    "User-Agent: Reliza Go Client",
	    "Accept-Encoding: gzip, deflate"
	})
	
	@GET("{URL}")
	Call<Map<String, Object>> getVersion(@Path("URL") String URL);
	
	@POST("body")
	Call<String> setBody(@Body String body);

}
