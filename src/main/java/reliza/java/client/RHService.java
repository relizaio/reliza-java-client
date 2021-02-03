package reliza.java.client;

import java.util.List;
import java.util.Map;
import reliza.java.client.responses.InstanceMetadata;
import reliza.java.client.responses.ProjectMetadata;
import reliza.java.client.responses.ProjectVersion;
import reliza.java.client.responses.ReleaseMetadata;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

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
     * @return requested ProjectVersion call
     */
    @POST("/api/programmatic/v2/project/getNewVersion")
    Call<ProjectVersion> getVersion(@Body Map<String, Object> body);
    
    /**
     * POST request corresponding to addRelease method.
     * @param body - request payload.
     * @return requested ProjectMetadata call
     */
    @POST("/api/programmatic/v1/release/create")
    Call<ProjectMetadata> addRelease(@Body Map<String, Object> body);
    
    /**
     * POST request corresponding to checkHash method.
     * @param body - request payload.
     * @return requested ProjectMetadata call
     */
    @POST("/api/programmatic/v1/release/getByHash")
    Call<Map<String, ProjectMetadata>> checkHash(@Body Map<String, Object> body);
    
    /**
     * POST request corresponding to instData method.
     * @param body - request payload.
     * @return requested projectmetadata call
     */
    @PUT("/api/programmatic/v1/instance/sendAgentData")
    Call<InstanceMetadata> instData(@Body Map<String, Object> body);
    
    /**
     * GET request corresponding to getMyRelease method.
     * @param namespace - URL parameter.
     * @return requested InstanceMetadata call
     */
    @GET("/api/programmatic/v1/instance/getMyFollowReleases")
    Call<List<ReleaseMetadata>> getMyRelease(@Query("namespace") String namespace);
    
    /**
     * POST request corresponding to getLatestRelease method.
     * @param body - request payload.
     * @return requested ReleaseMetadata call
     */
    @POST("/api/programmatic/v1/release/getLatestProjectRelease")
    Call<ReleaseMetadata> getLatestRelease(@Body Map<String, Object> body);
    
    /**
     * PUT request corresponding to approveRelease method.
     * @param body - request payload.
     * @return requested ReleaseMetadata call
     */
    @PUT("/api/programmatic/v1/release/approve")
    Call<ReleaseMetadata> approveRelease(@Body Map<String, Object> body);
}