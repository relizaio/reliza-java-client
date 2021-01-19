package reliza.java.client.responses;

import java.util.Map;
import lombok.Getter;
import lombok.ToString;

/**
 * Response subclass for InstanceMetadata and ProjectMetadata
 */
@Getter @ToString
public class ReleaseDetails {
    private String release;
    private String artifact;
    private String type;
    private String namespace;
    private Map<String, String> properties;
}