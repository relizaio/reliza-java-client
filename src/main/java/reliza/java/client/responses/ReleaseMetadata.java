package reliza.java.client.responses;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

/**
 * Response class for methods getMyRelease, getLatestRelease, and approveRelease in Library.java
 */
@Getter @ToString
public class ReleaseMetadata {
    //TODO Unknown response types parentReleases and optionalReleases currently substituted by type Object
    //All other unknown response types are either Date's or UUID's substituted by type String
    private UUID uuid;
    private String projectName;
    private String namespace;
    private UUID branch;
    private UUID project;
    private String type;
    private String createdType;
    private String lastUpdatedBy;
    private String createdDate;
    private String version;
    private String status;
    private String org;
    private List<Object> parentReleases;
    private List<Object> optionalReleases;
    private String sourceCodeEntry;
    private List<UUID> artifacts;
    private String notes;
    private Map<String, Boolean> approvals;
    private List<TimingDetails> timing;
    private String endpoint;
    private SourceCodeEntryDetails sourceCodeEntryDetails;
    private VcsRepositoryDetails vcsRepository;
    private List<ArtifactDetails> artifactDetails;
}