package reliza.java.client;

import java.util.Map;
import lombok.Getter;

@Getter
public class RelizaVersion {
	private Object dockerVersion;
	private Object version;
	
	public RelizaVersion(Map<String, Object> getVersionOutput) {
		this.dockerVersion = getVersionOutput.get("dockerTagSafeVersion");
		this.version = getVersionOutput.get("version");
	}
}