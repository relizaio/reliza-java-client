package reliza.java.client.responses;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;

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
	private ArtifactType artifactType;
	private String notes;
	private Map<String, String> tags;
	private List<TagRecord> tagRecords;
	private ZonedDateTime dateFrom;
	private ZonedDateTime dateTo;
	private Long buildDuration;
	private PackageType packageType;
	private String version;
	private String publisher;
	private String group;
	private List<UUID> dependencies;

	@Data
	public class ArtifactType {
		private String name;
		private List<String> aliases;
	}

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

	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class TagRecord {

		@JsonAlias("key")
		private String key;

		@JsonAlias("value")
		private String value;

	}
}