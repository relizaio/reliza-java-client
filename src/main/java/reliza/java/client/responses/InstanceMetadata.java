package reliza.java.client.responses;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

/**
 * Response class of method instData in Library.java
 */
@Getter @ToString
public class InstanceMetadata {
    private UUID uuid;
    private String createdType;
    private String lastUpdatedBy;
    private String createdDate;
    private String uri;
    private String org;
    private Map<String, String> properties;
    private List<ReleaseDetails> releases;
    private Object agentData;
    private String environment;
    private List<ProductDetails> products;
    private String type;
    private String notes;
}