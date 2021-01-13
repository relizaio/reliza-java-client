package reliza.java.client;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter @ToString
public class InstanceMetadata {
    // TODO Unknown response types: lastUpdatedBy, org, approvals, timing, properties, releases, agentData, products
    private UUID uuid;
    private String createdType;
    private String lastUpdatedBy;
    private ZonedDateTime createdDate;
    private String uri;
    private String org;
    private Map<String, String> properties;
    private List<String> releases;
    private String agentData;
    private String environment;
    private List<String> products;
    private String type;
    private String notes;
}