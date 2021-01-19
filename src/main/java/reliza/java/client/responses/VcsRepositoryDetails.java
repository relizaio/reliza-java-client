package reliza.java.client.responses;

import java.util.UUID;

import lombok.Getter;
import lombok.ToString;

/**
 * Response subclass for ReleaseMetadata
 */
@Getter @ToString
public class VcsRepositoryDetails {
    private UUID uuid;
    private String createdType;
    private String lastUpdatedBy;
    private String createdDate;
    private String name;
    private String org;
    private String uri;
    private String type;
}