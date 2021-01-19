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

// retrofit docs - https://square.github.io/retrofit/
/**
 * Class for making http requests using retrofit
 */
public interface RHService {   
    @Headers({
        "Content-Type: application/json",
        "User-Agent: Reliza Java Client",
        "Accept-Encoding: gzip, deflate"
    })

    @POST("/api/programmatic/v2/project/getNewVersion")
    Call<ProjectVersion> getVersion(@Body Map<String, Object> body);
    
    @POST("/api/programmatic/v1/release/create")
    Call<ProjectMetadata> addRelease(@Body Map<String, Object> body);
    
    @POST("/api/programmatic/v1/release/getByHash")
    Call<Map<String, ProjectMetadata>> checkHash(@Body Map<String, Object> body);
    
    @PUT("/api/programmatic/v1/instance/sendAgentData")
    Call<InstanceMetadata> instData(@Body Map<String, Object> body);
    
    @GET("/api/programmatic/v1/instance/getMyFollowReleases")
    Call<List<ReleaseMetadata>> getMyRelease(@Query("namespace") String namespace);
    
    @POST("/api/programmatic/v1/release/getLatestProjectRelease")
    Call<ReleaseMetadata> getLatestRelease(@Body Map<String, Object> body);
    
    @PUT("/api/programmatic/v1/release/approve")
    Call<ReleaseMetadata> approveRelease(@Body Map<String, Object> body);
}