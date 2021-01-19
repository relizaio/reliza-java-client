package reliza.java.client.responses;

import lombok.Getter;
import lombok.ToString;

/**
 * Response class of method getVersion from Library.java
 */
@Getter @ToString
public class ProjectVersion {
    private String dockerTagSafeVersion;
    private String version;
}