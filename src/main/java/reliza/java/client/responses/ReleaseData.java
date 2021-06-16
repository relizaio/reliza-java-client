package reliza.java.client.responses;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Release details
 */
@Data @EqualsAndHashCode(callSuper=true)
public class ReleaseData extends RelizaDataParent {
	private UUID uuid;
	private String version;
	private Status status;
	private UUID org;
	private UUID project;
	private UUID branch;
	private List<UUID> coreParentReleases;
	private List<DeployedRelease> parentReleases;
	private List<DeployedRelease> optionalReleases;
	private UUID sourceCodeEntry;
	private List<UUID> commits;
	private ZonedDateTime commitTime;
	private List<UUID> artifacts;
	private ReleaseType type;
	private String notes;
	private Map<ApprovalType, Boolean> approvals;
	private List<ReleaseTiming> timing;
	private String decoratedVersionString;
	private URI endpoint;
	
	public enum Status {
		ACTIVE,
		APPROVED,
		ARCHIVED,
		COMPLETE,
		DRAFT,
		IGNORED,
		OPTIONAL,
		PENDING,
		REJECTED,
		REQUIRED
	}
	
	public enum ReleaseType {
		PLACEHOLDER,
		REGULAR
	}
	
	public enum ApprovalType {
		DEV,
		QA,
		QA_MAN,
		QA_AUTO,
		QA_SIT,
		QA_UAT,
		QA_PAT,
		PM,
		OPS,
		IT,
		INFOSEC,
		CLIENT,
		MARKETING,
		EXEC
	}
}