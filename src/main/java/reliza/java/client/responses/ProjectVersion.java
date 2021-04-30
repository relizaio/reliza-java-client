package reliza.java.client.responses;

import lombok.Data;

/**
 * Version of release
 */
@Data
public class ProjectVersion {
	private String dockerTagSafeVersion;
	private String version;
}