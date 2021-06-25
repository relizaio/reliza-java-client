package reliza.java.client;

import java.util.Map;

import reliza.java.client.responses.GraphQLResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Class for making API calls using retrofit, see <a href="https://square.github.io/retrofit" target="_top">https://square.github.io/retrofit</a> <p>
 * All request payloads are taken as Map&lt;String,Object&gt; and converted to JSON using JacksonConverterFactory.
 */
public interface RHService {   
	@Headers({
		"Content-Type: application/json",
		"User-Agent: Reliza Java Client",
		"Accept-Encoding: gzip, deflate"
	})
	
	/**
	 * POST request corresponding to getVersion method.
	 * @param body - request payload.
	 * @return graphql response from API.
	 */
	@POST("/graphql")
	Call<GraphQLResponse> getVersion(@Body Map<String, Object> body);
	
	/**
	 * POST request corresponding to addRelease method.
	 * @param body - request payload.
	 * @return graphql response from API.
	 */
	@POST("/graphql")
	Call<GraphQLResponse> addRelease(@Body Map<String, Object> body);
	
	/**
	 * POST request corresponding to checkHash method.
	 * @param body - request payload.
	 * @return graphql response from API.
	 */
	@POST("/graphql")
	Call<GraphQLResponse> checkHash(@Body Map<String, Object> body);
	
	/**
	 * POST request corresponding to instData method.
	 * @param body - request payload.
	 * @return graphql response from API.
	 */
	@POST("/graphl")
	Call<GraphQLResponse> instData(@Body Map<String, Object> body);
	
	/**
	 * POST request corresponding to getMyRelease method.
	 * @param body - request payload.
	 * @return graphql response from API.
	 */
	@POST("/graphql")
	Call<GraphQLResponse> getMyRelease(@Body Map<String, Object> body);
	
	/**
	 * POST request corresponding to getLatestRelease method.
	 * @param body - request payload.
	 * @return graphql response from API.
	 */
	@POST("/graphql")
	Call<GraphQLResponse> getLatestRelease(@Body Map<String, Object> body);
	
	/**
	 * PUT request corresponding to approveRelease method.
	 * @param body - request payload.
	 * @return graphql response from API.
	 */
	@POST("/graphql")
	Call<GraphQLResponse> approveRelease(@Body Map<String, Object> body);
}