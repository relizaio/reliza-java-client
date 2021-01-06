package reliza.java.client;

public class Flags {
	private String apiKeyId;
	private String apiKey;
	private String branch;
	private String project_Id;
	
	public Flags(String apiKeyId, String apiKey, String branch, String project_Id) {
		this.apiKeyId = apiKeyId;
		this.apiKey = apiKey;
		this.branch = branch;
		this.project_Id = project_Id;
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
	public String getProjectId() {
		return project_Id;
	}
}