package reliza.java.client;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter @ToString
public class InstanceMetadata {
    private UUID uuid;
    private String createdType;
    private String lastUpdatedBy;
    private String createdDate;
    private String uri;
    private String org;
    private Map<String, String> properties;
    private List<Map<String, Object>> releases;
    private String agentData;
    private String environment;
    private List<Map<String, String>> products;
    private String type;
    private String notes;
}