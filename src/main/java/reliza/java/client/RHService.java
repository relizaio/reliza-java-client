package reliza.java.client;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

// retrofit docs - https://square.github.io/retrofit/

public interface RHService {
	
	/**
	 * This is just to bootstrap the project - no use in real life
	 * @return
	 */
	@GET("todos/{num}")
	Call<Map<String, Object>> todoTest(@Path("num") Integer num);
}
