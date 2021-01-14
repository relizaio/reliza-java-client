package reliza.java.client;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter @ToString
public class ProjectMetadata {
    // TODO Most response types currently assumed to be Strings or Objects, todo applies to all response classes
    private UUID uuid;
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
    private UUID sourceCodeEntry;
    private List<UUID> artifacts;
    private String notes;
    private Map<String, Object> approvals;
    private List<Map<String, String>> timing;
    private String endpoint;
    private String uri;
    private Map<String, Object> properties;
    private List<Object> releases;
    private String agentData;
    private String environment;
    private List<String> products;
}