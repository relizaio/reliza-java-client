package rearm.java.client;

import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;

/**
 * Parameter holder for {@link RearmLibrary}. ReARM-specific sibling of
 * {@code reliza.java.client.Flags}.
 *
 * <p>Authentication is HTTP-Basic with a FREEFORM key (id + secret). Component
 * resolution can be either by explicit {@code componentId} or by
 * {@code vcsUri} + {@code repoPath} when the API key has organization-WRITE
 * scope.
 */
@Builder @Setter @Getter @ToString
public class RearmFlags {
	@Builder.Default @NonNull private String baseUrl = "https://app.rearmhq.com";
	private String apiKeyId;
	private String apiKey;

	// Component resolution: either componentId, or (vcsUri + repoPath)
	private UUID componentId;
	private String vcsUri;
	private String repoPath;
	private String vcsDisplayName;

	// Auto-create the component when (vcsUri, repoPath) doesn't resolve.
	// Requires the FREEFORM key to carry org-WRITE scope.
	@Builder.Default @NonNull private Boolean createComponentIfMissing = false;
	private String createComponentName;
	private String createComponentVersionSchema;
	private String createComponentFeatureBranchVersionSchema;
	private UUID perspective;

	// Branch / version negotiation
	private String branch;
	private String versionSchema;
	@Builder.Default @NonNull private Boolean onlyVersion = false;
	private String version;
	private String action;
	private String metadata;
	private String modifier;
	private String lifecycle;
	@Builder.Default @NonNull private Boolean rebuild = false;

	// Source code entry
	private String commitMessage;
	private String commitHash;
	private String commitList;
	private String vcsType;
	private String vcsTag;
	private String dateActual;

	// Release
	private String endpoint;
	private String status;

	// Artifacts (parallel lists, one entry per artifact)
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

	// Outbound deliverable (one per release; matches rearm-cli's --odel* flags).
	// Identifier + type are the minimum required to land a Deliverable row; the
	// rest populate softwareMetadata.
	private String deliverableId;
	private String deliverableType;
	private String deliverableDigest;
	private String deliverableBuildId;
	private String deliverableBuildUri;
	private String deliverableCiMeta;
	private String deliverablePurl;

	// Hash lookup
	private String hash;

	// Latest release lookup
	private UUID product;

	// Approval
	private UUID releaseId;
	private String approvalType;
	@Builder.Default @NonNull private Boolean disapprove = false;
}
