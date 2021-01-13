package reliza.java.client;

import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;

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
    Call<ProjectVersion> getVersion(@Body Map<String, Object> body, @Header("Authorization") String authorization);
    
    @POST("/api/programmatic/v1/release/create")
    Call<ProjectMetadata> addRelease(@Body Map<String, Object> body, @Header("Authorization") String authorization);
    
    @POST("/api/programmatic/v1/release/getByHash")
    Call<Map<String, ProjectMetadata>> checkHash(@Body Map<String, Object> body, @Header("Authorization") String authorization);
    
    @PUT("/api/programmatic/v1/instance/sendAgentData")
    Call<InstanceMetadata> instData(@Body Map<String, Object> body, @Header("Authorization") String authorization);   
}