package reliza.java.client.responses;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

/**
 * Response class of methods addRelease and checkHash in Library.java
 */
@Getter @ToString
public class ProjectMetadata {
    private UUID uuid;
    private UUID branch;
    private UUID project;
    private String type;
    private String createdType;
    private String lastUpdatedBy;
    private String createdDate;
    private String version;
    private String status;
    private String org;
    private List<String> commits;
    private List<Object> parentReleases;
    private List<Object> optionalReleases;
    private String sourceCodeEntry;
    private List<UUID> artifacts;
    private String notes;
    private Map<String, Boolean> approvals;
    private List<TimingDetails> timing;
    private String endpoint;
    private String uri;
    private Map<String, String> properties;
    private List<ReleaseDetails> releases;
    private String agentData;
    private String environment;
    private List<ProductDetails> products;
}