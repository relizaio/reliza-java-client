package reliza.java.client.responses;

import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.Data;

/**
 * Timing details of release
 */
@Data
public class ReleaseTiming {
	private String event;
	private ReleaseLifeCycle lifecycle;
	private ZonedDateTime dateFrom;
	private ZonedDateTime dateTo;
	private String environment;
	private UUID instanceUuid;
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
}