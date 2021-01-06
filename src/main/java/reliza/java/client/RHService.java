package reliza.java.client;

import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

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
	
	@POST("/api/programmatic/v1/project/getNewVersion")
	Call<Map<String, Object>> getVersionCall(@Body Map<String, String> body, @Header("Authorization") String authorization);
}
