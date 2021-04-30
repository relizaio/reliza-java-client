package reliza.java.client.responses;

import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.Data;

/**
 * Timing details of release
 */
@Data
public class ReleaseTiming {
	private UUID releaseUuid;
	private ReleaseLifeCycle lifecycle;
	private ZonedDateTime dateFrom;
	private ZonedDateTime dateTo;
	private EnvironmentType environment;
	private UUID instance;
	private String event;
	private Long duration;
	
	public enum ReleaseLifeCycle {
		TASK_TO_COMMIT,
		COMMIT_TO_BUILD,
		BUILT,
		DRAFTED,
		COMPLETED,
		REJECTED,
		MODIFIED,
		APPROVED,
		DISAPPROVED,
		DEPLOYED,
		UNDEPLOYED
	}
	
	public enum EnvironmentType {
		DEV,
		BUILD,
		TEST,
		SIT,
		UAT,
		PAT,
		STAGING,
		PRODUCTION
	}
}