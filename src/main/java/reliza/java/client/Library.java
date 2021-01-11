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
import org.apache.commons.lang3.ArrayUtils;
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
        Call<Map<String, Object>> homeResp = rhs.checkHash(body, basicAuth);
        System.out.println(body);
        
        try {
			Response<Map<String, Object>> resp = homeResp.execute();
			if (resp.body().isEmpty()) {
				log.info(resp.body().toString());
				return new RelizaMetadata(resp.body());
			} else {
				log.info(resp.body().get("release").toString());
				return new RelizaMetadata((Map<String, Object>) resp.body().get("release"));
			}
		} catch (IOException e) {
			log.error("IO exception", e);
			return null;
		} catch (NullPointerException e) {
			log.error("NullPointerException", e);
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
        
        if (ArrayUtils.isNotEmpty(flags.getArtId())) {
			ArrayList<Map<String, Object>> artifacts = new ArrayList<Map<String, Object>>();
        	
        	for (int i = 0; i < flags.getArtId().length; i++) {
        		Map<String, Object> artifact = new HashMap<>();
        		artifact.put("identifier", flags.getArtId()[i]);
        		artifacts.add(i, artifact);
        	}
        	
        	List<String[]> artFlags = Arrays.asList(flags.getArtBuildId(), flags.getArtCiMeta(), flags.getArtType(), flags.getArtVersion(),
        			flags.getArtPublisher(), flags.getArtPackage(), flags.getArtGroup(), flags.getDateStart(), flags.getDateEnd());
        	
        	List<String> artMessages = Arrays.asList("buildId", "cicdMeta", "type", "artifactVersion",
        			"publisher", "packageType", "group", "dateFrom", "dateTo");
        	
        	for (int i = 0; i < artFlags.size(); i++) {
	        	if (ArrayUtils.isNotEmpty(artFlags.get(i)) && artFlags.get(i).length != flags.getArtId().length) {
	        		log.error("number of art" + artMessages.get(i).toLowerCase() + " flags must be either zero or match number of artid flags");
	        		return null;
	        	} else if (ArrayUtils.isNotEmpty(artFlags.get(i))) {
	        		for (int j = 0; j < flags.getArtId().length; j++) {
	        			artifacts.get(j).put(artMessages.get(i), artFlags.get(i)[j]);
	        		}
	        	}
        	}
        	
        	if (ArrayUtils.isNotEmpty(flags.getArtDigests()) && flags.getArtDigests().length != flags.getArtId().length) {
        		log.error("number of artdigests flags must be either zero or match number of artid flags");
        		return null;
        	} else if (ArrayUtils.isNotEmpty(flags.getArtDigests())) {
        		for (int i = 0; i < flags.getArtId().length; i++) {
        			artifacts.get(i).put("digests", Arrays.asList(flags.getArtDigests()[i].split(",")));
        		}
        	}
        	
        	if (ArrayUtils.isNotEmpty(flags.getTagKeyArr()) && flags.getTagKeyArr().length != flags.getArtId().length) {
        		log.error("number of tagkey flags must be either zero or match number of artid flags");
        		return null;
        	} else if (ArrayUtils.isNotEmpty(flags.getTagValArr()) && flags.getTagValArr().length != flags.getArtId().length) {
        		log.error("number of tagval flags must be either zero or match number of artid flags");
        		return null;
        	} else if (ArrayUtils.isNotEmpty(flags.getTagKeyArr()) && ArrayUtils.isEmpty(flags.getTagValArr())) {
        		log.error("number of tagval and tagkey flags must be the same and must match number of artid flags");
        		return null;
        	} else if (ArrayUtils.isNotEmpty(flags.getTagKeyArr())) {
        		for (int i = 0; i < flags.getTagKeyArr().length; i++) {
        			String[] tagKeys = flags.getTagKeyArr()[i].split(",");
        			String[] tagVals = flags.getTagValArr()[i].split(",");
            		if (ArrayUtils.isNotEmpty(tagKeys) && ArrayUtils.isNotEmpty(tagVals) && tagKeys.length != tagVals.length) {
            			log.error("number of keys and values per each tagval and tagkey flag must be the same");
            			return null;
            		}
            		
            		Map<String, String> tagKeyToVal = new HashMap<>();
            		for (int j = 0; j < tagKeys.length; j++) {
            			tagKeyToVal.put(tagKeys[j], tagVals[j]);
            		}
           		
            		artifacts.get(i).put("tags", tagKeyToVal);		
        		}
        		
        	}
        	body.put("artifacts", artifacts);
        }
        
    	String basicAuth = Credentials.basic(flags.getApiKeyId(), flags.getApiKey());
        Call<Map<String, Object>> homeResp = rhs.addRelease(body, basicAuth);
        System.out.println(body);
        
        try {
			Response<Map<String, Object>> resp = homeResp.execute();
			log.info(resp.body().toString());
			return new RelizaMetadata(resp.body());
		} catch (IOException e) {
			log.error("IO exception", e);
			return null;
		} catch (NullPointerException e) {
			log.error("NullPointerException", e);
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
        Call<Map<String, Object>> homeResp = rhs.getVersion(body, basicAuth);
    	
        try {
			Response<Map<String, Object>> resp = homeResp.execute();
			log.info(resp.body().toString());
			return new RelizaVersion(resp.body());
		} catch (IOException e) {
			log.error("IO exception", e);
			return null;
		} catch (NullPointerException e) {
			log.error("NullPointerException", e);
			return null;
		}
    }
}
