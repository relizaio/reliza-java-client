package reliza.java.client;
import java.util.Map;

public class API {
	String apiKeyId;
	String apiKey;
	String branch;
	
	API(Map<String, Object> args) {
		if (args.get("apiKeyId") instanceof String) {
			this.apiKeyId = (String) args.get("apiKeyId");
		}
		if (args.get("apiKey") instanceof String) {
			this.apiKey = (String) args.get("apiKey");
		}
		if (args.get("branch") instanceof String) {
			this.branch = (String) args.get("branch");
		}
	}
}