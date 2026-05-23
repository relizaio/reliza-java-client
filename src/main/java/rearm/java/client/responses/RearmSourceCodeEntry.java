package rearm.java.client.responses;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RearmSourceCodeEntry {
	private UUID uuid;
	private UUID branch;
	private UUID vcsUuid;
	private String vcsBranch;
	private String commit;
	private List<String> commits;
	private String commitMessage;
	private String commitAuthor;
	private String commitEmail;
	private String vcsTag;
	private String notes;
	private UUID org;
	private String dateActual;
}
