package reliza.java.client.responses;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Contains more details about release
 */
@Data @EqualsAndHashCode(callSuper=true)
public class FullRelease extends ReleaseData {
	private SourceCodeEntryData sourceCodeEntryDetails;
	private VcsRepositoryData vcsRepository;
	private List<ArtifactData> artifactDetails;
	private String projectName;
	private String namespace;
}