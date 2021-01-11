package reliza.java.client;

import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.Getter;

@Slf4j
@Getter
public class Flags {
	private String apiKeyId;
	private String apiKey;
	private String branch;
	private String versionSchema;
	private String version;
	private String status;
	private String endPoint;
	private UUID projectId;
	private String commitHash;
	private String vcsType;
	private String vcsUri;
	private String vcsTag;
	private String dateActual;
	private String[] artId;
	private String[] artBuildId;
	private String[] artCiMeta;
	private String[] artType;
	private String[] artVersion;
	private String[] artPublisher;
	private String[] artPackage;
	private String[] artGroup;
	private String[] dateStart;
	private String[] dateEnd;
	private String[] artDigests;
	private String[] tagKeyArr;
	private String[] tagValArr;
	private String hash;
	
	public Flags(String apiKeyId, String apiKey, String branch, String versionSchema, String version,
			String status, String endPoint, String projectId, String commitHash,
			 String vcsUri, String vcsType, String vcsTag, String dateActual, String[] artId,
			String[] artBuildId, String[] artCiMeta, String[] artType, String[] artVersion,
			String[] artPublisher, String[] artPackage,String[] artGroup, String[] dateStart,
			String[] dateEnd, String[] artDigests, String[] tagKeyArr, String[] tagValArr, String hash) {
		this.apiKeyId = apiKeyId;
		this.apiKey = apiKey;
		this.branch = branch;
		this.versionSchema = versionSchema;
		this.version = version;
		this.status = status;
		this.endPoint = endPoint;
		if (StringUtils.isNotEmpty(projectId)) {
	        try {
	            this.projectId = UUID.fromString(projectId);
	        } catch (IllegalArgumentException e) {
	            log.error("IllegalArgumentException", e);
	        }
		}
		this.commitHash = commitHash;
		this.vcsUri = vcsUri;
		this.vcsType = vcsType;
		this.vcsTag = vcsTag;
		this.dateActual = dateActual;
		this.artId = artId;
		this.artBuildId = artBuildId;
		this.artCiMeta = artCiMeta;
		this.artType = artType;
		this.artVersion = artVersion;
		this.artPublisher = artPublisher;
		this.artPackage = artPackage;
		this.artGroup = artGroup;
		this.dateStart = dateStart;
		this.dateEnd = dateEnd;
		this.artDigests = artDigests;
		this.tagKeyArr = tagKeyArr;
		this.tagValArr = tagValArr;
		this.hash = hash;
	}
}