package reliza.java.client.responses;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.Data;
import reliza.java.client.responses.RelizaDataParent.ProgrammaticType;

/**
 * Deployed release details
 */
@Data
public class DeployedRelease {
	private ZonedDateTime timeSent;
	private UUID release;
	private UUID artifact;
	private ProgrammaticType type;
	private String namespace;
	private Map<UUID, String> properties;
	private ServiceState state;
	private List<DeployedReplica> replicas;
	
	@Data
	public class DeployedReplica {
		private String id;
		private ServiceState state;
	}
	
	public enum ServiceState {
		COMPLETED,
		CRASHLOOPBACKOFF,
		ERROR,
		IMAGEPULLBACKOFF,
		PENDING,
		WAITING,
		RUNNING,
		SUCCEEDED,
		FAILED,
		TERMINATING,
		TERMINATED,
		UNKNOWN,
		UNSET
	}
}