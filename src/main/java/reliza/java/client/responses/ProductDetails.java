package reliza.java.client.responses;

import lombok.Getter;
import lombok.ToString;

@Getter @ToString
public class ProductDetails {
    private String featureSet;
    private String type;
    private String release;
    private String namespace;
    private String targetRelease;
}