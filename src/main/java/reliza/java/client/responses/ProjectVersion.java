package reliza.java.client.responses;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Version of release
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectVersion {
	private String dockerTagSafeVersion;
	private String version;
}