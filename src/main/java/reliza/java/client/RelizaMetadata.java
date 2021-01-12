package reliza.java.client;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter @ToString
public class RelizaMetadata {
    // TODO Unknown response types: branch, org, parentReleases, optionalReleases, approvals, timing
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
    private List<String> parentReleases;
    private List<String> optionalReleases;
    private UUID sourceCodeEntry;
    private List<UUID> artifacts;
    private String notes;
    private Map<String, String> approvals;
    private List<Map<String, String>> timing;
    private String endpoint;
}