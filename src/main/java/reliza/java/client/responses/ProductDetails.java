package reliza.java.client.responses;

import lombok.Getter;
import lombok.ToString;

/**
 * Response subclass for InstanceMetadata and ProjectMetadata
 */
@Getter @ToString
public class ProductDetails {
    private String featureSet;
    private String type;
    private String release;
    private String namespace;
    private String targetRelease;
    private String identifier;
}