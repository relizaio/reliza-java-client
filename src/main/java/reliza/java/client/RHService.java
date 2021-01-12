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
	Call<RelizaVersion> getVersion(@Body Map<String, Object> body, @Header("Authorization") String authorization);
	
	@POST("/api/programmatic/v1/release/create")
	Call<RelizaMetadata> addRelease(@Body Map<String, Object> body, @Header("Authorization") String authorization);
	
	@POST("/api/programmatic/v1/release/getByHash")
	Call<Map<String, RelizaMetadata>> checkHash(@Body Map<String, Object> body, @Header("Authorization") String authorization);
}
