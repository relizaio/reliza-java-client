package com.rearmhq.javaclient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.rearmhq.javaclient.interceptors.RearmBasicAuthInterceptor;
import com.rearmhq.javaclient.interceptors.RearmCsrfInterceptor;
import com.rearmhq.javaclient.responses.RearmGraphQLResponse;
import com.rearmhq.javaclient.responses.RearmRelease;
import com.rearmhq.javaclient.responses.RearmVersion;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

// retrofit2.Response is also in scope but referenced fully-qualified below to
// avoid clashing with okhttp3.Response (used by the multipart path).

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
	private final OkHttpClient httpClient;
	private final ObjectMapper om = new ObjectMapper();

	public RearmLibrary(RearmFlags flags) {
		this.flags = flags;
		this.om.registerModule(new JavaTimeModule());
		this.om.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
		this.httpClient = new OkHttpClient.Builder()
				.addInterceptor(new RearmBasicAuthInterceptor(flags.getApiKeyId(), flags.getApiKey()))
				.addInterceptor(new RearmCsrfInterceptor(flags.getBaseUrl()))
				.build();
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(flags.getBaseUrl())
				.addConverterFactory(JacksonConverterFactory.create(om))
				.client(this.httpClient)
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
		// SCE artifacts go *inside* the sourceCodeEntry map (the backend's
		// addReleaseProgrammatic only consumes `sourceCodeEntry.artifacts`,
		// not a top-level `sceArts` — the latter would be silently dropped
		// even though the schema declares it). If no SCE map was built yet
		// (no commit, no message) but we have sceArtifacts, create a stub.
		if (CollectionUtils.isNotEmpty(flags.getSceArtifacts())) {
			if (sce == null) sce = new LinkedHashMap<>();
			sce.put("artifacts", deepCopyList(flags.getSceArtifacts()));
		}
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

		// Free-form release artifacts (mirrors rearm-cli's --releasearts);
		// deliverable artifacts append to the single deliverable we built.
		if (CollectionUtils.isNotEmpty(flags.getReleaseArtifacts())) {
			List<Map<String, Object>> rels = (List<Map<String, Object>>) variables.getOrDefault("artifacts", new ArrayList<>());
			rels.addAll(deepCopyList(flags.getReleaseArtifacts()));
			variables.put("artifacts", rels);
		}
		if (CollectionUtils.isNotEmpty(flags.getDeliverableArtifacts()) && outbound != null && !outbound.isEmpty()) {
			List<Map<String, Object>> delArts = (List<Map<String, Object>>) outbound.get(0).getOrDefault("artifacts", new ArrayList<>());
			delArts.addAll(deepCopyList(flags.getDeliverableArtifacts()));
			outbound.get(0).put("artifacts", delArts);
		}

		String query = "mutation addReleaseProgrammatic($ReleaseInputProg: ReleaseInputProg!) {"
				+ " addReleaseProgrammatic(release: $ReleaseInputProg) { " + RELEASE_FIELDS + " }"
				+ "}";
		Map<String, Object> response = sendMutation("addReleaseProgrammatic", query, "ReleaseInputProg", variables);
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
		putIfPresent(sce, "commitAuthor", flags.getCommitAuthor());
		putIfPresent(sce, "commitEmail", flags.getCommitEmail());
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
	 * Sends a mutation, automatically switching to the Apollo
	 * graphql-multipart-request-spec transport if the variables tree carries
	 * any artifact maps with a {@code filePath} marker. Used by addRelease;
	 * other mutations can adopt the same path when they grow Upload inputs.
	 */
	private Map<String, Object> sendMutation(String operationName, String query,
			String inputName, Map<String, Object> variables) {
		Map<String, List<String>> locationMap = new LinkedHashMap<>();
		Map<String, FileUpload> filesMap = new LinkedHashMap<>();
		AtomicInteger counter = new AtomicInteger(0);
		extractUploads(variables, "variables." + inputName, counter, locationMap, filesMap);

		Map<String, Object> body = graphqlBody(query, inputName, variables);
		if (filesMap.isEmpty()) {
			return execute(service.graphql(body));
		}
		return executeMultipart(operationName, query, (Map<String, Object>) body.get("variables"),
				locationMap, filesMap);
	}

	/**
	 * Walks an artifact-bearing variables tree. Whenever it finds a Map with
	 * a non-empty {@code filePath} String entry, reads the file, registers it
	 * as an upload, swaps {@code filePath} for a {@code file: null} placeholder
	 * (the Apollo spec's marker that gets resolved from the multipart parts),
	 * and records the JSON path the part should fill in.
	 */
	private static void extractUploads(Object node, String path, AtomicInteger counter,
			Map<String, List<String>> locationMap, Map<String, FileUpload> filesMap) {
		if (node instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) node;
			Object rawPath = map.get("filePath");
			if (rawPath instanceof String && StringUtils.isNotEmpty((String) rawPath)) {
				String filePath = (String) rawPath;
				try {
					Path p = Paths.get(filePath);
					byte[] bytes = Files.readAllBytes(p);
					String key = String.valueOf(counter.incrementAndGet());
					String filename = p.getFileName().toString();
					filesMap.put(key, new FileUpload(filename, bytes));
					locationMap.put(key, List.of(path + ".file"));
					map.remove("filePath");
					map.put("file", null);
				} catch (IOException ex) {
					throw new RearmApiException("Failed to read artifact file " + filePath, ex);
				}
			}
			for (Map.Entry<String, Object> entry : new ArrayList<>(map.entrySet())) {
				extractUploads(entry.getValue(), path + "." + entry.getKey(), counter, locationMap, filesMap);
			}
		} else if (node instanceof List) {
			@SuppressWarnings("unchecked")
			List<Object> list = (List<Object>) node;
			for (int i = 0; i < list.size(); i++) {
				extractUploads(list.get(i), path + "." + i, counter, locationMap, filesMap);
			}
		}
	}

	private Map<String, Object> executeMultipart(String operationName, String query,
			Map<String, Object> variables,
			Map<String, List<String>> locationMap,
			Map<String, FileUpload> filesMap) {
		try {
			Map<String, Object> operations = new LinkedHashMap<>();
			operations.put("operationName", operationName);
			operations.put("variables", variables);
			operations.put("query", query);

			MultipartBody.Builder builder = new MultipartBody.Builder()
					.setType(MultipartBody.FORM)
					.addFormDataPart("operations", om.writeValueAsString(operations))
					.addFormDataPart("map", om.writeValueAsString(locationMap));
			MediaType octet = MediaType.parse("application/octet-stream");
			for (Map.Entry<String, FileUpload> e : filesMap.entrySet()) {
				FileUpload fu = e.getValue();
				builder.addFormDataPart(e.getKey(), fu.filename,
						RequestBody.create(fu.bytes, octet));
			}

			Request req = new Request.Builder()
					.url(flags.getBaseUrl() + "/graphql")
					.header("User-Agent", "ReARM Java Client")
					.header("Apollo-Require-Preflight", "true")
					.post(builder.build())
					.build();
			try (Response resp = httpClient.newCall(req).execute()) {
				String body = resp.body() != null ? resp.body().string() : "";
				if (!resp.isSuccessful()) {
					throw new RearmApiException("ReARM multipart HTTP " + resp.code() + ": " + body);
				}
				RearmGraphQLResponse parsed = om.readValue(body, RearmGraphQLResponse.class);
				List<RearmGraphQLResponse.Error> errors = parsed.getErrors();
				if (errors != null && !errors.isEmpty()) {
					String joined = errors.stream()
							.map(RearmGraphQLResponse.Error::getMessage)
							.collect(Collectors.joining("; "));
					throw new RearmApiException("ReARM GraphQL errors: " + joined);
				}
				return parsed.getData();
			}
		} catch (IOException e) {
			throw new RearmApiException("IO exception on multipart upload: " + e.getMessage(), e);
		}
	}

	/** Deep-copy a list of maps so callers' inputs aren't mutated when we walk
	 *  the tree to extract file uploads. */
	private static List<Map<String, Object>> deepCopyList(List<Map<String, Object>> in) {
		List<Map<String, Object>> out = new ArrayList<>(in.size());
		for (Map<String, Object> m : in) {
			out.add(deepCopyMap(m));
		}
		return out;
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> deepCopyMap(Map<String, Object> in) {
		Map<String, Object> out = new LinkedHashMap<>();
		for (Map.Entry<String, Object> e : in.entrySet()) {
			Object v = e.getValue();
			if (v instanceof Map) {
				out.put(e.getKey(), deepCopyMap((Map<String, Object>) v));
			} else if (v instanceof List) {
				List<Object> srcList = (List<Object>) v;
				List<Object> dstList = new ArrayList<>(srcList.size());
				for (Object item : srcList) {
					if (item instanceof Map) {
						dstList.add(deepCopyMap((Map<String, Object>) item));
					} else {
						dstList.add(item);
					}
				}
				out.put(e.getKey(), dstList);
			} else {
				out.put(e.getKey(), v);
			}
		}
		return out;
	}

	/** Container for one multipart file part. */
	private static final class FileUpload {
		final String filename;
		final byte[] bytes;
		FileUpload(String filename, byte[] bytes) { this.filename = filename; this.bytes = bytes; }
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
			retrofit2.Response<RearmGraphQLResponse> resp = call.execute();
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
