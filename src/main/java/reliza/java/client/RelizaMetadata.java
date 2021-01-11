package reliza.java.client;

import java.util.Map;
import lombok.Getter;

@Getter
public class RelizaMetadata {
	private Object uuid;
	private Object branch;
	private Object project;
	private Object type;
	private Object createdType;
	private Object lastUpdatedBy;
	private Object createdDate;
	private Object version;
	private Object status;
	private Object org;
	private Object parentReleases;
	private Object optionalReleases;
	private Object sourceCodeEntry;
	private Object artifacts;
	private Object notes;
	private Object approvals;
	private Object timing;
	private Object endpoint;
	
	public RelizaMetadata(Map<String, Object> addReleaseOutput) {
		this.uuid = addReleaseOutput.get("uuid");
		this.branch = addReleaseOutput.get("branch");
		this.project = addReleaseOutput.get("project");
		this.type = addReleaseOutput.get("type");
		this.createdType = addReleaseOutput.get("createdType");
		this.lastUpdatedBy = addReleaseOutput.get("lastUpdatedBy");
		this.createdDate = addReleaseOutput.get("createdDate");
		this.version = addReleaseOutput.get("version");
		this.status = addReleaseOutput.get("status");
		this.org = addReleaseOutput.get("org");
		this.parentReleases = addReleaseOutput.get("parentReleases");
		this.optionalReleases = addReleaseOutput.get("optionalReleases");
		this.sourceCodeEntry = addReleaseOutput.get("sourceCodeEntry");
		this.artifacts = addReleaseOutput.get("artifacts");
		this.notes = addReleaseOutput.get("notes");
		this.approvals = addReleaseOutput.get("approvals");
		this.timing = addReleaseOutput.get("timing");
		this.endpoint = addReleaseOutput.get("endpoint");
	}
}