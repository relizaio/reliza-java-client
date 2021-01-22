package reliza.java.client.responses;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

/**
 * Response subclass for ReleaseMetadata
 */
@Getter @ToString
public class ArtifactDetails {
    private UUID uuid;
    private UUID branch;
    private String createdType;
    private String lastUpdatedBy;
    private String createdDate;
    private String identifier;
    private String org;
    private String buildId;
    private String cicdMeta;
    private List<String> digests;
    private String isInternal;
    private String type;
    private String notes;
    private Map<String, String> tags;
    private String dateFrom;
    private String dateTo;
    private String duration;
    private String packageType;
    private String version;
    private String publisher;
    private String group;
    private List<Object> dependencies;
}