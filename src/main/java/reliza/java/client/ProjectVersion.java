package reliza.java.client;

import lombok.Getter;
import lombok.ToString;

@Getter @ToString
public class ProjectVersion {
    private String dockerTagSafeVersion;
    private String version;
}