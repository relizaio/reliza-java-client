package reliza.java.client.responses;

import java.util.UUID;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Version control system details of release
 */
@Data @EqualsAndHashCode(callSuper=true)
public class VcsRepositoryData extends RelizaDataParent {
	private UUID uuid;
	private String name;
	private UUID org;
	private String uri;
	private String type;
}