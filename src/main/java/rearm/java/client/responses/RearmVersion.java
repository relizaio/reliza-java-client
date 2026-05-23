package rearm.java.client.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * Response of {@code getNewVersionProgrammatic}. The {@code lifecycle} field
 * reflects the release state AFTER any synchronous component-trigger; callers
 * should abort downstream build steps when it is a terminal state (REJECTED,
 * CANCELLED).
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RearmVersion {
	private String version;
	private String dockerTagSafeVersion;
	private String changelog;
	private Boolean releaseAlreadyExists;
	private String lifecycle;
}
