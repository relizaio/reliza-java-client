package reliza.java.client.responses;

import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Source code entry details of release
 */
@Data @EqualsAndHashCode(callSuper=true)
public class SourceCodeEntryData extends RelizaDataParent {
	private UUID uuid;
	private UUID branch;
	private UUID vcsRepositoryUuid;
	private String vcsBranch;
	private String commit;
	private String commitMessage;
	private String vcsTag;
	private String notes;
	private UUID org;
	private ZonedDateTime dateActual;
}