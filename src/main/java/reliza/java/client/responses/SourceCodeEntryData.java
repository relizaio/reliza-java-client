package reliza.java.client.responses;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Source code entry details of release
 */
@Data @EqualsAndHashCode(callSuper=true)
public class SourceCodeEntryData extends RelizaDataParent {
	private UUID uuid;
	private UUID branchUuid;
	private UUID vcsUuid;
	private String vcsBranch;
	private String commit;
	private List<String> commits;
	private String commitMessage;
	private String vcsTag;
	private String notes;
	private UUID org;
	private ZonedDateTime dateActual;
}