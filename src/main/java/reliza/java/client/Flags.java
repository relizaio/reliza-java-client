package reliza.java.client;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Flags {
	private String apiKeyId;
	private String apiKey;
	private String branch;
	private UUID projectId;
	
	public Flags(String apiKeyId, String apiKey, String branch, String projectId) {
		this.apiKeyId = apiKeyId;
		this.apiKey = apiKey;
		this.branch = branch;
		if (StringUtils.isNotEmpty(projectId)) {
	        try {
	            this.projectId = UUID.fromString(projectId);
	        } catch (IllegalArgumentException e) {
	            log.error("IllegalArgumentException", e);
	        }
		}
	}
	
	public String getApiKeyId() {
		return apiKeyId;
	}
	public String getApiKey() {
		return apiKey;
	}
	public String getBranch() {
		return branch;
	}
	public UUID getProjectId() {
		return projectId;
	}
}