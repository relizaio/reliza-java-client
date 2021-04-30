package reliza.java.client.responses;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Details of release artifact
 */
@Data @EqualsAndHashCode(callSuper=true)
public class ArtifactData extends RelizaDataParent {
	private UUID uuid;
	private String identifier;
	private String org;
	private UUID branch;
	private String buildId;
	private String buildUri;
	private String cicdMeta;
	private List<String> digests;
	private ArtifactBelonging isInternal;
	private String type;
	private String notes;
	private Map<String, String> tags;
	private ZonedDateTime dateFrom;
	private ZonedDateTime dateTo;
	private Long duration;
	private PackageType packageType;
	private String version;
	private String publisher;
	private String group;
	private List<UUID> dependencies;
	
	public enum ArtifactBelonging {
		INTERNAL,
		EXTERNAL
	}
	
	public enum PackageType {
		MAVEN,
		NPM,
		NUGET,
		GEM,
		PYPI,
		DOCKER
	}
}