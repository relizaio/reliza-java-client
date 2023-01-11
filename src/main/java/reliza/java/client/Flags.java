package reliza.java.client;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;

/**
 * Class for storing all flags for use in Library.java methods. Base url for API calls is "https://app.relizahub.com" and can be modified in builder. <p>
 * Class is initialized using builder pattern, see <a href="https://projectlombok.org/features/all" target="_top">https://projectlombok.org/features/all</a>.
 */
@Builder @Setter @Getter @ToString
public class Flags{
	@Builder.Default @NonNull private String baseUrl = "https://app.relizahub.com";
	private String apiKeyId;
	private String apiKey;
	private String branch;
	private String versionSchema;
	@Builder.Default @NonNull private Boolean onlyVersion = false;
	private String version;
	private String status;
	private String endPoint;
	private UUID projectId;
	private String action;
	private String metadata;
	private String modifier;
	private String commitMessage;
	private String commitHash;
	private String commitList;
	private String vcsType;
	private String vcsUri;
	private String vcsTag;
	private String targetBranch;
	private String state;
	private String title;
	private String number;
	private String commits;
	private String createdDate;
	private String closedDate;
	private String mergedDate;
	@Builder.Default @NonNull private Boolean manual = false;
	private String dateActual;
	@Singular("artId") private List<String> artId;
	@Singular("artBuildId") private List<String> artBuildId;
	@Singular("artBuildUri") private List<String> artBuildUri;
	@Singular("artCiMeta") private List<String> artCiMeta;
	@Singular("artType") private List<String> artType;
	@Singular("artVersion") private List<String> artVersion;
	@Singular("artPublisher") private List<String> artPublisher;
	@Singular("artPackage") private List<String> artPackage;
	@Singular("artGroup") private List<String> artGroup;
	@Singular("dateStart") private List<String> dateStart;
	@Singular("dateEnd") private List<String> dateEnd;
	@Singular("artDigests") private List<String> artDigests;
	@Singular("tagKeys") private List<String> tagKeys;
	@Singular("tagVals") private List<String> tagVals;
	private String hash;
	private String imagesString;
	private InputStream imageInputStream;
	@Builder.Default @NonNull private String namespace = "default";
	@Builder.Default @NonNull private String senderId = "default";
	private UUID product;
	private String environment;
	private String instance;
	private UUID releaseId;
	private String approvalType;
	@Builder.Default @NonNull private Boolean disapprove = false;
}