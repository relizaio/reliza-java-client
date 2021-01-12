/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package reliza.java.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Slf4j
public class Library {
    Flags flags;
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("https://test.relizahub.com")
        .addConverterFactory(JacksonConverterFactory.create())
        .build();
    public Library(Flags flags) {
        this.flags = flags;
    }
    
    
    public RelizaMetadata checkHash() {
        RHService rhs = retrofit.create(RHService.class);
        Map<String, Object> body = new HashMap<>();
        body.put("hash", flags.getHash());
        
        String basicAuth = Credentials.basic(flags.getApiKeyId(), flags.getApiKey());
        Call<Map<String,RelizaMetadata>> homeResp = rhs.checkHash(body, basicAuth);
        
        try {
            Response<Map<String,RelizaMetadata>> resp = homeResp.execute();
            if (resp.isSuccessful()) {
                log.info(resp.body().toString());
                return resp.body().get("release");
            } else {
                log.error(resp.errorBody().string());
                return null;
            }
        } catch (IOException e) {
            log.error("IO exception", e);
            return null;
        }
    }
    
    
    public RelizaMetadata addRelease() {
        RHService rhs = retrofit.create(RHService.class);
        Map<String, Object> body = new HashMap<>();
        if (StringUtils.isNotEmpty(flags.getBranch())) {body.put("branch", flags.getBranch());}
        if (StringUtils.isNotEmpty(flags.getVersion())) {body.put("version", flags.getVersion());}
        if (StringUtils.isNotEmpty(flags.getStatus())) {body.put("status", flags.getStatus());}
        if (StringUtils.isNotEmpty(flags.getEndPoint())) {body.put("endpoint", flags.getEndPoint());}
        if (flags.getProjectId() != null) {body.put("project", flags.getProjectId());}
        
        if (StringUtils.isNotEmpty(flags.getCommitHash())) {
            Map<String, String> commitMap = new HashMap<>();
            commitMap.put("commit", flags.getCommitHash());
            commitMap.put("uri", flags.getVcsUri());
            commitMap.put("type", flags.getVcsType());
            if (StringUtils.isNotEmpty(flags.getBranch())) {commitMap.put("vcsTag", flags.getVcsTag());}
            if (StringUtils.isNotEmpty(flags.getBranch())) {commitMap.put("dateActual", flags.getDateActual());}
            body.put("sourceCodeEntry", commitMap);
        }
        
        if (isNotEmpty(flags.getArtId())) {
            List<Map<String, Object>> artifacts = new ArrayList<Map<String, Object>>();
            
            for (int i = 0; i < flags.getArtId().size(); i++) {
                Map<String, Object> artifact = new HashMap<>();
                artifact.put("identifier", flags.getArtId().get(i));
                artifacts.add(i, artifact);
            }
            
            Map<String, List<String>> artFlags = new HashMap<String, List<String>>();
            artFlags.put("buildId", flags.getArtBuildId());
            artFlags.put("cicdMeta", flags.getArtCiMeta());
            artFlags.put("type", flags.getArtType());
            artFlags.put("artifactVersion", flags.getArtVersion());
            artFlags.put("publisher", flags.getArtPublisher());
            artFlags.put("packageType", flags.getArtPackage());
            artFlags.put("group", flags.getArtGroup());
            artFlags.put("dateFrom", flags.getDateStart());
            artFlags.put("dateTo", flags.getDateEnd());

            for (String key : artFlags.keySet()) {
                if (isNotEmpty(artFlags.get(key)) && artFlags.get(key).size() != flags.getArtId().size()) {
                    log.error("number of art" + key.toLowerCase() + " flags must be either zero or match number of artid flags");
                    return null;
                } else if (isNotEmpty(artFlags.get(key))) {
                    for (int j = 0; j < flags.getArtId().size(); j++) {
                        artifacts.get(j).put(key, artFlags.get(key).get(j));
                    }
                }
            }
            
            if (isNotEmpty(flags.getArtDigests()) && flags.getArtDigests().size() != flags.getArtId().size()) {
                log.error("number of artdigests flags must be either zero or match number of artid flags");
                return null;
            } else if (isNotEmpty(flags.getArtDigests())) {
                for (int i = 0; i < flags.getArtId().size(); i++) {
                    artifacts.get(i).put("digests", Arrays.asList(flags.getArtDigests().get(i).split(",")));
                }
            }
            
            if (isNotEmpty(flags.getTagKeyArr()) && flags.getTagKeyArr().size() != flags.getArtId().size()) {
                log.error("number of tagkey flags must be either zero or match number of artid flags");
                return null;
            } else if (isNotEmpty(flags.getTagValArr()) && flags.getTagValArr().size() != flags.getArtId().size()) {
                log.error("number of tagval flags must be either zero or match number of artid flags");
                return null;
            } else if (isNotEmpty(flags.getTagKeyArr()) && !isNotEmpty(flags.getTagValArr())) {
                log.error("number of tagval and tagkey flags must be the same and must match number of artid flags");
                return null;
            } else if (isNotEmpty(flags.getTagKeyArr())) {
                for (int i = 0; i < flags.getTagKeyArr().size(); i++) {
                    List<String> tagKeys = List.of(flags.getTagKeyArr().get(i).split(","));
                    List<String> tagVals = List.of(flags.getTagValArr().get(i).split(","));
                    if (isNotEmpty(tagKeys) && isNotEmpty(tagVals) && tagKeys.size() != tagVals.size()) {
                        log.error("number of keys and values per each tagval and tagkey flag must be the same");
                        return null;
                    }
                    
                    Map<String, String> tagKeyToVal = new HashMap<>();
                    for (int j = 0; j < tagKeys.size(); j++) {
                        tagKeyToVal.put(tagKeys.get(j), tagVals.get(j));
                    }
                   
                    artifacts.get(i).put("tags", tagKeyToVal);        
                }
                
            }
            body.put("artifacts", artifacts);
        }
        
        String basicAuth = Credentials.basic(flags.getApiKeyId(), flags.getApiKey());
        Call<RelizaMetadata> homeResp = rhs.addRelease(body, basicAuth);
        
        try {
            Response<RelizaMetadata> resp = homeResp.execute();
            if (resp.isSuccessful()) {
                log.info(resp.body().toString());
                return resp.body();
            } else {
                log.error(resp.errorBody().string());
                return null;
            }
        } catch (IOException e) {
            log.error("IO exception", e);
            return null;
        }
    }
        
    
    public RelizaVersion getVersion() {
        RHService rhs = retrofit.create(RHService.class);
        Map<String, Object> body = new HashMap<>();  
        if (StringUtils.isNotEmpty(flags.getBranch())) {body.put("branch", flags.getBranch());}
        if (StringUtils.isNotEmpty(flags.getVersionSchema())) {body.put("versionSchema", flags.getVersionSchema());}
        if (flags.getProjectId() != null) {body.put("project", flags.getProjectId());}

        String basicAuth = Credentials.basic(flags.getApiKeyId(), flags.getApiKey());
        Call<RelizaVersion> homeResp = rhs.getVersion(body, basicAuth);
        
        try {
            Response<RelizaVersion> resp = homeResp.execute();
            if (resp.isSuccessful()) {
                log.info(resp.body().toString());
                return resp.body();
            } else {
                log.error(resp.errorBody().string());
                return null;
            }
        } catch (IOException e) {
            log.error("IO exception", e);
            return null;
        }
    }
    
    private boolean isNotEmpty(List<String> flag) {
        if (flag == null || flag.size() == 0) {
            return false;
        }
        return true;
    }
}