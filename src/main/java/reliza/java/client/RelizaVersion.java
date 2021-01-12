package reliza.java.client;

import lombok.Getter;
import lombok.ToString;

@Getter @ToString
public class RelizaVersion {
	private String dockerTagSafeVersion;
	private String version;
}