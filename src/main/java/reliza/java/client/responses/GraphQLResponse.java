package reliza.java.client.responses;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * Response format of graphql api call
 */
@Data
public class GraphQLResponse {
	private Map<String, Object> data;
	private List<Error> errors;
	
	@Data @JsonIgnoreProperties(ignoreUnknown = true)
	public static class Error {
		private String message;
	}
}