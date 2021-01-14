package reliza.java.client;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter @ToString
public class ReleaseMetadata {
    private UUID uuid;
    private String projectName;
    private String namespace;
    private String branch;
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
    private Map<String, Object> approvals;
    private List<Map<String, String>> timing;
    private String endpoint;
    private String sourceCodeEntryDetails;
    private String vcsRepository;
    private List<Map<String, Object>> artifactDetails;
}