package rearm.java.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import rearm.java.client.interceptors.RearmBasicAuthInterceptor;
import rearm.java.client.interceptors.RearmCsrfInterceptor;
import rearm.java.client.responses.RearmGraphQLResponse;
import rearm.java.client.responses.RearmRelease;
import rearm.java.client.responses.RearmVersion;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Java client for the ReARM GraphQL programmatic API. Sibling of
 * {@code reliza.java.client.Library}; the two are kept apart so that the
 * Reliza Hub package can be removed cleanly once Reliza Hub is retired.
 *
 * <p>Auth is HTTP-Basic with a FREEFORM API key. The library bootstraps the
 * ReARM CSRF token at construction time and attaches it on every call.
 */
@Slf4j
public class RearmLibrary {
	private static final String RELEASE_FIELDS =
			"uuid version lifecycle org component branch sourceCodeEntry endpoint";
	private static final String FULL_RELEASE_FIELDS = RELEASE_FIELDS + " "
			+ "sourceCodeEntryDetails { uuid branch vcsUuid vcsBranch commit commits commitMessage "
			+ "commitAuthor commitEmail vcsTag notes org dateActual }";

	private final RearmFlags flags;
	private final RearmService service;
	private final ObjectMapper om = new ObjectMapper();

	public RearmLibrary(RearmFlags flags) {
		this.flags = flags;
		this.om.registerModule(new JavaTimeModule());
		this.om.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
		OkHttpClient client = new OkHttpClient.Builder()
				.addInterceptor(new RearmBasicAuthInterceptor(flags.getApiKeyId(), flags.getApiKey()))
				.addInterceptor(new RearmCsrfInterceptor(flags.getBaseUrl()))
				.build();
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(flags.getBaseUrl())
				.addConverterFactory(JacksonConverterFactory.create(om))
				.client(client)
				.build();
		this.service = retrofit.create(RearmService.class);
	}

	/**
	 * Negotiates a new release version on ReARM.
	 *
	 * <p>Component resolution: either {@code componentId} on the flags, or
	 * {@code vcsUri} + {@code repoPath} (requires the FREEFORM key to carry
	 * org-WRITE scope). When {@code createComponentIfMissing} is true and the
	 * repo doesn't yet map to a component, ReARM creates one.
	 */
	public RearmVersion getVersion() {
		Map<String, Object> variables = new LinkedHashMap<>();
		putIfPresent(variables, "component", flags.getComponentId());
		putIfPresent(variables, "branch", flags.getBranch());
		putIfPresent(variables, "modifier", flags.getModifier());
		putIfPresent(variables, "action", flags.getAction());
		putIfPresent(variables, "metadata", flags.getMetadata());
		putIfPresent(variables, "versionSchema", flags.getVersionSchema());
		putIfPresent(variables, "lifecycle", flags.getLifecycle());
		variables.put("onlyVersion", flags.getOnlyVersion());
		putIfPresent(variables, "vcsUri", flags.getVcsUri());
		putIfPresent(variables, "repoPath", flags.getRepoPath());
		putIfPresent(variables, "vcsDisplayName", flags.getVcsDisplayName());
		if (Boolean.TRUE.equals(flags.getCreateComponentIfMissing())) {
			variables.put("createComponentIfMissing", true);
			putIfPresent(variables, "createComponentName", flags.getCreateComponentName());
			putIfPresent(variables, "createComponentVersionSchema", flags.getCreateComponentVersionSchema());
			putIfPresent(variables, "createComponentFeatureBranchVersionSchema",
					flags.getCreateComponentFeatureBranchVersionSchema());
			putIfPresent(variables, "perspective", flags.getPerspective());
		}
		variables.put("rebuild", flags.getRebuild());

		Map<String, Object> sce = buildSourceCodeEntryMap();
		if (sce != null) {
			variables.put("sourceCodeEntry", sce);
		}
		List<Map<String, Object>> commits = buildCommitList();
		if (commits != null) {
			variables.put("commits", commits);
		}

		String query = "mutation ($GetNewVersionInput: GetNewVersionInput!) {"
				+ " getNewVersionProgrammatic(newVersionInput: $GetNewVersionInput) {"
				+ "   version dockerTagSafeVersion changelog releaseAlreadyExists lifecycle"
				+ " } }";

		Map<String, Object> body = graphqlBody(query, "GetNewVersionInput", variables);
		Map<String, Object> response = execute(service.graphql(body));
		return response == null ? null
				: om.convertValue(response.get("getNewVersionProgrammatic"), RearmVersion.class);
	}

	/**
	 * Records a release on ReARM.
	 *
	 * <p>Component resolution follows the same rules as {@link #getVersion()}.
	 * {@code version} on the flags is required.
	 */
	public RearmRelease addRelease() {
		Map<String, Object> variables = new LinkedHashMap<>();
		variables.put("version", flags.getVersion());
		putIfPresent(variables, "lifecycle", flags.getLifecycle());
		putIfPresent(variables, "component", flags.getComponentId());
		putIfPresent(variables, "branch", flags.getBranch());
		putIfPresent(variables, "endpoint", flags.getEndpoint());
		putIfPresent(variables, "vcsUri", flags.getVcsUri());
		putIfPresent(variables, "repoPath", flags.getRepoPath());
		putIfPresent(variables, "vcsDisplayName", flags.getVcsDisplayName());
		if (Boolean.TRUE.equals(flags.getCreateComponentIfMissing())) {
			variables.put("createComponentIfMissing", true);
			putIfPresent(variables, "createComponentName", flags.getCreateComponentName());
			putIfPresent(variables, "createComponentVersionSchema", flags.getCreateComponentVersionSchema());
			putIfPresent(variables, "createComponentFeatureBranchVersionSchema",
					flags.getCreateComponentFeatureBranchVersionSchema());
			putIfPresent(variables, "perspective", flags.getPerspective());
		}
		variables.put("rebuildRelease", flags.getRebuild());

		Map<String, Object> sce = buildSourceCodeEntryMap();
		if (sce != null) {
			variables.put("sourceCodeEntry", sce);
		}
		List<Map<String, Object>> commits = buildCommitList();
		if (commits != null) {
			variables.put("commits", commits);
		}

		List<Map<String, Object>> artifacts = buildArtifacts();
		if (artifacts != null) {
			variables.put("artifacts", artifacts);
		}

		List<Map<String, Object>> outbound = buildOutboundDeliverables();
		if (outbound != null) {
			variables.put("outboundDeliverables", outbound);
		}

		String query = "mutation ($ReleaseInputProg: ReleaseInputProg!) {"
				+ " addReleaseProgrammatic(release: $ReleaseInputProg) { " + RELEASE_FIELDS + " }"
				+ "}";
		Map<String, Object> body = graphqlBody(query, "ReleaseInputProg", variables);
		Map<String, Object> response = execute(service.graphql(body));
		return response == null ? null
				: om.convertValue(response.get("addReleaseProgrammatic"), RearmRelease.class);
	}

	/**
	 * Returns the latest release matching the supplied filters.
	 */
	public RearmRelease getLatestRelease() {
		Map<String, Object> variables = new LinkedHashMap<>();
		putIfPresent(variables, "component", flags.getComponentId());
		putIfPresent(variables, "product", flags.getProduct());
		putIfPresent(variables, "branch", flags.getBranch());
		putIfPresent(variables, "vcsUri", flags.getVcsUri());
		putIfPresent(variables, "repoPath", flags.getRepoPath());
		putIfPresent(variables, "lifecycle", flags.getLifecycle());
		if (CollectionUtils.isNotEmpty(flags.getTagKeys()) && CollectionUtils.isNotEmpty(flags.getTagVals())) {
			List<Map<String, String>> tags = new ArrayList<>();
			Map<String, String> tag = new LinkedHashMap<>();
			tag.put("key", flags.getTagKeys().get(0));
			tag.put("value", flags.getTagVals().get(0));
			tags.add(tag);
			variables.put("tags", tags);
		}

		String query = "query ($GetLatestReleaseInput: GetLatestReleaseInput!) {"
				+ " getLatestReleaseProgrammatic(release: $GetLatestReleaseInput) { " + FULL_RELEASE_FIELDS + " }"
				+ "}";
		Map<String, Object> body = graphqlBody(query, "GetLatestReleaseInput", variables);
		Map<String, Object> response = execute(service.graphql(body));
		return response == null ? null
				: om.convertValue(response.get("getLatestReleaseProgrammatic"), RearmRelease.class);
	}

	/**
	 * Looks up a release by an artifact digest. Returns the release UUID as a
	 * string, or null if no release carries the hash.
	 */
	public String getReleaseByHash() {
		String query = "query ($hash: String!, $componentId: ID) {"
				+ " getReleaseByHashProgrammatic(hash: $hash, componentId: $componentId)"
				+ "}";
		Map<String, Object> variables = new LinkedHashMap<>();
		variables.put("hash", flags.getHash());
		putIfPresent(variables, "componentId", flags.getComponentId());
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("query", query);
		body.put("variables", variables);
		Map<String, Object> response = execute(service.graphql(body));
		return response == null ? null : (String) response.get("getReleaseByHashProgrammatic");
	}

	/**
	 * Approves (or disapproves, when {@code disapprove=true}) a release for the
	 * configured approval type.
	 */
	public RearmRelease approveRelease() {
		Map<String, Object> approval = new LinkedHashMap<>();
		approval.put("type", flags.getApprovalType());
		approval.put("approved", !flags.getDisapprove());

		Map<String, Object> variables = new LinkedHashMap<>();
		variables.put("approvals", List.of(approval));
		putIfPresent(variables, "release", flags.getReleaseId());
		putIfPresent(variables, "version", flags.getVersion());
		putIfPresent(variables, "component", flags.getComponentId());

		String query = "mutation ($ReleaseApprovalProgrammaticInput: ReleaseApprovalProgrammaticInput!) {"
				+ " approveReleaseProgrammatic(releaseApprovals: $ReleaseApprovalProgrammaticInput) { "
				+ RELEASE_FIELDS + " }"
				+ "}";
		Map<String, Object> body = graphqlBody(query, "ReleaseApprovalProgrammaticInput", variables);
		Map<String, Object> response = execute(service.graphql(body));
		return response == null ? null
				: om.convertValue(response.get("approveReleaseProgrammatic"), RearmRelease.class);
	}

	private Map<String, Object> buildSourceCodeEntryMap() {
		if (StringUtils.isEmpty(flags.getCommitHash()) && StringUtils.isEmpty(flags.getCommitMessage())) {
			return null;
		}
		Map<String, Object> sce = new LinkedHashMap<>();
		putIfPresent(sce, "commit", flags.getCommitHash());
		putIfPresent(sce, "commitMessage", flags.getCommitMessage());
		putIfPresent(sce, "uri", flags.getVcsUri());
		putIfPresent(sce, "type", flags.getVcsType());
		putIfPresent(sce, "vcsTag", flags.getVcsTag());
		putIfPresent(sce, "dateActual", flags.getDateActual());
		return sce;
	}

	private List<Map<String, Object>> buildCommitList() {
		if (StringUtils.isEmpty(flags.getCommitList())) {
			return null;
		}
		String commits = new String(Base64.getDecoder().decode(flags.getCommitList()));
		List<String> commitLines = Arrays.asList(StringUtils.split(commits, System.lineSeparator()));
		List<Map<String, Object>> body = new ArrayList<>();
		for (String line : commitLines) {
			List<String> parts = Arrays.asList(StringUtils.split(line, "|||"));
			if (parts.isEmpty()) {
				continue;
			}
			Map<String, Object> commit = new LinkedHashMap<>();
			commit.put("commit", parts.get(0));
			if (parts.size() > 1) commit.put("dateActual", parts.get(1));
			if (parts.size() > 2) commit.put("commitMessage", parts.get(2));
			if (parts.size() > 4) {
				commit.put("commitAuthor", parts.get(3));
				commit.put("commitEmail", parts.get(4));
			}
			body.add(commit);
		}
		return body;
	}

	private List<Map<String, Object>> buildArtifacts() {
		if (CollectionUtils.isEmpty(flags.getArtId())) {
			return null;
		}
		List<Map<String, Object>> artifacts = new ArrayList<>();
		for (String artId : flags.getArtId()) {
			if (StringUtils.isEmpty(artId)) {
				log.error("artId entry cannot be empty");
				return null;
			}
			Map<String, Object> artifact = new LinkedHashMap<>();
			artifact.put("displayIdentifier", artId);
			artifacts.add(artifact);
		}

		Map<String, List<String>> perArt = new LinkedHashMap<>();
		perArt.put("buildId", flags.getArtBuildId());
		perArt.put("buildUri", flags.getArtBuildUri());
		perArt.put("cicdMeta", flags.getArtCiMeta());
		perArt.put("type", upperCaseAll(flags.getArtType()));
		perArt.put("version", flags.getArtVersion());
		perArt.put("group", flags.getArtGroup());
		perArt.put("dateFrom", flags.getDateStart());
		perArt.put("dateTo", flags.getDateEnd());

		for (Map.Entry<String, List<String>> entry : perArt.entrySet()) {
			List<String> values = entry.getValue();
			if (CollectionUtils.isEmpty(values)) {
				continue;
			}
			if (values.size() != flags.getArtId().size()) {
				log.error("number of {} entries must match number of artId entries", entry.getKey());
				return null;
			}
			for (int i = 0; i < flags.getArtId().size(); i++) {
				if (StringUtils.isNotEmpty(values.get(i))) {
					artifacts.get(i).put(entry.getKey(), values.get(i));
				}
			}
		}

		if (CollectionUtils.isNotEmpty(flags.getArtDigests())) {
			if (flags.getArtDigests().size() != flags.getArtId().size()) {
				log.error("number of artDigests entries must match number of artId entries");
				return null;
			}
			for (int i = 0; i < flags.getArtId().size(); i++) {
				String csv = flags.getArtDigests().get(i);
				if (StringUtils.isEmpty(csv)) {
					continue;
				}
				List<Map<String, String>> digestRecords = new ArrayList<>();
				for (String digest : StringUtils.split(csv, ",")) {
					String[] parts = StringUtils.split(digest, ":", 2);
					if (parts.length != 2) {
						log.error("digest {} must be in <algorithm>:<value> form", digest);
						return null;
					}
					Map<String, String> record = new LinkedHashMap<>();
					record.put("algo", normalizeChecksumAlgo(parts[0]));
					record.put("digest", parts[1]);
					record.put("scope", "ORIGINAL_FILE");
					digestRecords.add(record);
				}
				artifacts.get(i).put("digestRecords", digestRecords);
			}
		}

		if (CollectionUtils.isNotEmpty(flags.getTagKeys())) {
			if (flags.getTagKeys().size() != flags.getArtId().size()
					|| (CollectionUtils.isNotEmpty(flags.getTagVals())
							&& flags.getTagVals().size() != flags.getTagKeys().size())) {
				log.error("tagKeys / tagVals entries must match number of artId entries");
				return null;
			}
			for (int i = 0; i < flags.getTagKeys().size(); i++) {
				String keyCsv = flags.getTagKeys().get(i);
				String valCsv = flags.getTagVals().get(i);
				if (StringUtils.isEmpty(keyCsv) || StringUtils.isEmpty(valCsv)) {
					continue;
				}
				List<String> keys = Arrays.asList(StringUtils.split(keyCsv, ","));
				List<String> vals = Arrays.asList(StringUtils.split(valCsv, ","));
				if (keys.size() != vals.size()) {
					log.error("keys and values per artifact must have the same count");
					return null;
				}
				List<Map<String, String>> tags = new ArrayList<>();
				for (int j = 0; j < keys.size(); j++) {
					Map<String, String> rec = new LinkedHashMap<>();
					rec.put("key", keys.get(j));
					rec.put("value", vals.get(j));
					tags.add(rec);
				}
				artifacts.get(i).put("tags", tags);
			}
		}

		return artifacts;
	}

	/**
	 * Builds the single-entry {@code outboundDeliverables} list when the
	 * caller has supplied at least a {@code deliverableId}. Mirrors what
	 * {@code rearm-cli}'s {@code --odel*} flags produce: a Deliverable row
	 * attached to the release, typed (CONTAINER/FILE/...) with a
	 * softwareMetadata block carrying build trail + digest + an optional
	 * PURL identifier.
	 *
	 * <p>Keep the surface single-deliverable for now — N-deliverable support
	 * mirrors the artifact list pattern (parallel @Singular lists) if it
	 * becomes useful.
	 */
	private List<Map<String, Object>> buildOutboundDeliverables() {
		if (StringUtils.isEmpty(flags.getDeliverableId())) {
			return null;
		}
		Map<String, Object> deliv = new LinkedHashMap<>();
		deliv.put("displayIdentifier", flags.getDeliverableId());
		if (StringUtils.isNotEmpty(flags.getDeliverableType())) {
			deliv.put("type", flags.getDeliverableType().toUpperCase());
		}

		Map<String, Object> swMeta = new LinkedHashMap<>();
		putIfPresent(swMeta, "buildId", flags.getDeliverableBuildId());
		putIfPresent(swMeta, "buildUri", flags.getDeliverableBuildUri());
		putIfPresent(swMeta, "cicdMeta", flags.getDeliverableCiMeta());
		if (StringUtils.isNotEmpty(flags.getDeliverableDigest())) {
			// `digests: [String]` is deprecated server-side — DeliverableService
			// has a one-time migrator that copies it into `digestRecords` with
			// scope=ORIGINAL_FILE. Send the structured form directly so the
			// UI's per-deliverable view (which reads digestRecords) actually
			// shows the hashes.
			List<Map<String, String>> digestRecords = new ArrayList<>();
			for (String entry : StringUtils.split(flags.getDeliverableDigest(), ",")) {
				String[] parts = StringUtils.split(entry, ":", 2);
				if (parts.length != 2) {
					log.error("deliverable digest {} must be in <algo>:<value> form", entry);
					return null;
				}
				Map<String, String> rec = new LinkedHashMap<>();
				rec.put("algo", normalizeChecksumAlgo(parts[0]));
				rec.put("digest", parts[1]);
				rec.put("scope", "ORIGINAL_FILE");
				digestRecords.add(rec);
			}
			swMeta.put("digestRecords", digestRecords);
		}
		if (!swMeta.isEmpty()) {
			deliv.put("softwareMetadata", swMeta);
		}

		if (StringUtils.isNotEmpty(flags.getDeliverablePurl())) {
			Map<String, String> idRec = new LinkedHashMap<>();
			idRec.put("idType", "PURL");
			idRec.put("idValue", flags.getDeliverablePurl());
			deliv.put("identifiers", List.of(idRec));
		}

		return List.of(deliv);
	}

	/**
	 * Maps user-friendly checksum algo spellings to ReARM's
	 * {@code TeaArtifactChecksumType} enum values. Accepts the OCI/Docker form
	 * (`sha256`), the CycloneDX/IANA hyphenated form (`SHA-256`), and the raw
	 * enum form (`SHA_256`). Anything else is passed through upper-cased and
	 * underscore-normalized as a best-effort guess.
	 */
	private static String normalizeChecksumAlgo(String raw) {
		if (raw == null) return null;
		String upper = raw.trim().toUpperCase();
		// Common Docker/OCI: sha256 / sha512 -> SHA_256 / SHA_512
		if (upper.matches("SHA(1|256|384|512)")) {
			return "SHA_" + upper.substring(3);
		}
		// Hyphenated -> underscored
		return upper.replace('-', '_');
	}

	private static List<String> upperCaseAll(List<String> in) {
		if (CollectionUtils.isEmpty(in)) {
			return in;
		}
		return in.stream().map(s -> StringUtils.isNotEmpty(s) ? s.toUpperCase() : s).collect(Collectors.toList());
	}

	private static void putIfPresent(Map<String, Object> map, String key, Object value) {
		if (value == null) {
			return;
		}
		if (value instanceof String && StringUtils.isEmpty((String) value)) {
			return;
		}
		map.put(key, value);
	}

	private static Map<String, Object> graphqlBody(String query, String inputName, Map<String, Object> variables) {
		Map<String, Object> input = new HashMap<>();
		input.put(inputName, variables);
		Map<String, Object> body = new HashMap<>();
		body.put("query", query);
		body.put("variables", input);
		return body;
	}

	private static Map<String, Object> execute(Call<RearmGraphQLResponse> call) {
		try {
			Response<RearmGraphQLResponse> resp = call.execute();
			if (resp.body() == null) {
				String err = resp.errorBody() != null ? resp.errorBody().string() : "(no body)";
				throw new RearmApiException("ReARM HTTP " + resp.code() + ": " + err);
			}
			List<RearmGraphQLResponse.Error> errors = resp.body().getErrors();
			if (errors != null && !errors.isEmpty()) {
				String joined = errors.stream()
						.map(RearmGraphQLResponse.Error::getMessage)
						.collect(Collectors.joining("; "));
				throw new RearmApiException("ReARM GraphQL errors: " + joined);
			}
			return resp.body().getData();
		} catch (IOException e) {
			throw new RearmApiException("IO exception calling ReARM: " + e.getMessage(), e);
		}
	}

	/** Surfaces ReARM call failures up the stack with detail; callers (e.g. the
	 * Jenkins plugin) catch and convert to user-visible build errors instead of
	 * silently dropping null. */
	public static class RearmApiException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public RearmApiException(String msg) { super(msg); }
		public RearmApiException(String msg, Throwable cause) { super(msg, cause); }
	}
}
