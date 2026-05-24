# Reliza / ReARM Java Client (SDK)
This artifact ships two parallel Java clients in one jar:

* **`reliza.java.client.*`** — the original client for
  [Reliza Hub](https://app.relizahub.com). Streams metadata about
  instances, releases, artifacts, and resolves bundles based on Reliza Hub
  data. See "Use cases" below.
* **`com.rearmhq.javaclient.*`** — sibling client for
  [ReARM](https://rearmhq.com). Speaks the ReARM GraphQL programmatic API
  (`getNewVersionProgrammatic`, `addReleaseProgrammatic`,
  `getLatestReleaseProgrammatic`, `getReleaseByHashProgrammatic`,
  `approveReleaseProgrammatic`). Auth is HTTP-Basic with a ReARM FREEFORM
  API key; the client also bootstraps the ReARM CSRF token + cookie on
  construction (sending CSRF is what lets HTTP-Basic callers reach
  `/graphql`).

The two packages share no code — they're kept apart so the Reliza-Hub
half can be removed cleanly once Reliza Hub is retired. Pick the one that
matches the backend you're talking to; they can coexist in the same
application if you need to double-publish during a migration.

---

## ReARM use cases (`com.rearmhq.javaclient.*`)

### Authentication + transport

Every call goes through a `RearmLibrary` constructed from a `RearmFlags`
holding the base URL plus a FREEFORM API key:

```java
RearmFlags flags = RearmFlags.builder()
    .baseUrl("https://app.rearmhq.com")
    .apiKeyId("FREEFORM__<orgUuid>__ord__<keyUuid>")
    .apiKey("<secret>")
    .build();
RearmLibrary rearm = new RearmLibrary(flags);
```

`RearmLibrary`'s constructor calls `/api/manual/v1/fetchCsrf` once, captures
the CSRF token + `XSRF-TOKEN` cookie, and attaches them to every subsequent
request — that's what lets HTTP-Basic callers reach `/graphql` (the API
gateway rejects raw HTTP-Basic without the CSRF flow).

All methods throw `RearmLibrary.RearmApiException` on backend / network
failure with the GraphQL error text in the message.

### 1. Mint a version + create a PENDING release

Canonical CI flow: mint a version at the start of the build, finalize the
release at the end. `getVersion()` creates the release in `PENDING`
lifecycle so it's visible in the UI immediately; `addRelease()` later
updates the same row in place and flips it to `ASSEMBLED`.

```java
RearmFlags mintFlags = flags.toBuilder()
    .branch("main")
    .vcsUri("https://github.com/acme/widget")          // alternative to componentId
    .repoPath("service")
    .createComponentIfMissing(true)                     // org-WRITE FREEFORM key required
    .createComponentName("acme widget service")
    .createComponentVersionSchema("semver")
    .createComponentFeatureBranchVersionSchema("Branch.Micro")
    .lifecycle("PENDING")                               // default; ASSEMBLED for one-shot
    .commitHash("deadbeef…")
    .commitMessage("first commit on main")
    .commitAuthor("alice")
    .commitEmail("alice@acme.example")
    .dateActual("2026-05-24T08:30:00Z")
    .build();
RearmVersion v = new RearmLibrary(mintFlags).getVersion();
// v.getVersion(), v.getDockerTagSafeVersion(), v.getLifecycle()
```

Pass `.onlyVersion(true)` instead to reserve only the version assignment
without creating a release row.

### 2. Finalize the release with build metadata + an outbound deliverable

A second call with the minted version + the build outputs. ReARM's
`addReleaseProgrammatic` finds the PENDING release on `(component, version)`
and updates it in place — no duplicate row.

```java
RearmFlags finalizeFlags = mintFlags.toBuilder()
    .version(v.getVersion())
    .lifecycle("ASSEMBLED")
    .deliverableId("registry.acme.example/widget:" + v.getDockerTagSafeVersion())
    .deliverableType("CONTAINER")                              // or FILE
    .deliverableDigest("sha256:abc…")                          // accepts sha256/SHA-256/SHA_256
    .deliverablePurl("pkg:oci/widget@sha256:abc…")             // optional
    .deliverableBuildId("ci-run-123")
    .deliverableBuildUri("https://ci.acme.example/runs/123")
    .deliverableCiMeta("Jenkins")
    .build();
RearmRelease r = new RearmLibrary(finalizeFlags).addRelease();
// r.getUuid(), r.getVersion(), r.getLifecycle()
```

### 3. Attach artifacts (BOMs, signatures, …) at any of three scopes

Each artifact is a `Map<String, Object>` shaped like
[`ArtifactInput`](https://github.com/relizaio/rearm-saas/blob/main/backend/src/main/resources/schema/schema.graphqls)
(`displayIdentifier`, `type`, `bomFormat`, `tags`, …). A special `filePath`
key — value is a local path — triggers an upload via the Apollo
[`graphql-multipart-request-spec`](https://github.com/jaydenseric/graphql-multipart-request-spec)
for that artifact; without it the entry is metadata-only. The library
walks the entire tree before sending so nested artifacts upload too.

| Builder method | Lands at |
|---|---|
| `.sceArtifact(map)` (or `.sceArtifacts(list)`) | `sourceCodeEntry.artifacts` on the release's SCE — e.g. fs-bom + its signature |
| `.releaseArtifact(map)` (or `.releaseArtifacts(list)`) | the release's own `artifacts` — release-level BOMs, VDRs, etc. |
| `.deliverableArtifact(map)` (or `.deliverableArtifacts(list)`) | nested under the outbound deliverable — image-scoped BOMs / attestations |

Artifact-of-artifact is just a nested `artifacts: [...]` inside any map —
canonical use is a `SIGNATURE` attached to a `BOM`:

```java
Map<String, Object> sigArt = Map.of(
    "displayIdentifier", "widget-image.cdx.json.sig",
    "type",              "SIGNATURE",
    "filePath",          "/tmp/widget-image.cdx.json.sig"
);
Map<String, Object> bomArt = Map.of(
    "displayIdentifier", "widget-image.cdx.json",
    "type",              "BOM",
    "bomFormat",         "CYCLONEDX",
    "filePath",          "/tmp/widget-image.cdx.json",
    "artifacts",         List.of(sigArt)                       // nested upload
);

RearmFlags fullFlags = finalizeFlags.toBuilder()
    .sceArtifact(Map.of(
        "displayIdentifier", "widget-fs.cdx.json",
        "type",              "BOM",
        "bomFormat",         "CYCLONEDX",
        "filePath",          "/tmp/widget-fs.cdx.json"))
    .releaseArtifact(Map.of(
        "displayIdentifier", "widget-release.vdr.json",
        "type",              "VDR",
        "filePath",          "/tmp/widget-release.vdr.json"))
    .deliverableArtifact(bomArt)
    .build();
new RearmLibrary(fullFlags).addRelease();
```

### 4. Read the latest release on a branch

For "what commit was last shipped?" / "have we built this before?" checks:

```java
RearmFlags latestFlags = flags.toBuilder()
    .vcsUri("https://github.com/acme/widget")
    .repoPath("service")
    .branch("main")
    .lifecycle("ASSEMBLED")                                    // optional filter
    .build();
RearmRelease latest = new RearmLibrary(latestFlags).getLatestRelease();
String previousCommit = latest != null && latest.getSourceCodeEntryDetails() != null
        ? latest.getSourceCodeEntryDetails().getCommit() : null;
```

### 5. Look up a release by an artifact / deliverable digest

Useful in monorepos to detect "did this artifact ship already?" before
spending CI time rebuilding:

```java
RearmFlags hashFlags = flags.toBuilder()
    .componentId(componentUuid)
    .hash("sha256:abc…")
    .build();
String releaseUuid = new RearmLibrary(hashFlags).getReleaseByHash();
if (releaseUuid != null) { /* already shipped */ }
```

### 6. Programmatic release approval

Approval mutations require a FREEFORM key whose permissions cover the
release's component and grant the requested approval type:

```java
RearmFlags approve = flags.toBuilder()
    .releaseId(releaseUuid)
    .approvalType("SECURITY_REVIEW")                            // matches your approval entry
    .disapprove(false)                                          // true for negative
    .build();
new RearmLibrary(approve).approveRelease();
```

---

## Reliza Hub use cases (`reliza.java.client.*`)


Video tutorial about key functionality of Reliza Hub is available on [YouTube](https://www.youtube.com/watch?v=yDlf5fMBGuI).

Community forum and support is available at [r/Reliza](https://reddit.com/r/Reliza).

### Publishing to Maven Local
To build and publish to local Maven Repository, use:

```
gradle publishToMavenLocal
```

### Publishing to Maven Central
To build and publish to Maven Central Repository, use:

```
gradle publishAllPublicationsToCentralRepository -PcentralUser=username -PcentralPassword=password
```

## 1. Use Case: Get Version Assignment From Reliza Hub

This use case requests Version from Reliza Hub for our project. Note that project schema must be preset on Reliza Hub prior to using this API. API key must also be generated for the project from Reliza Hub.

Sample method for semver version schema:

```bash
	Flags flags = Flags.builder().apiKeyId("project_or_organization_wide_rw_api_id")
		.apiKey("project_or_organization_wide_rw_api_key")
		.branch("master")
		.versionSchema("1.2.patch")
		.build();
	Library library = new Library(flags);
	ProjectVersion projectVersion = library.getVersion();
```

Sample command with commit details for a git commit:

```bash
	Flags flags = Flags.builder().apiKeyId("project_or_organization_wide_rw_api_id")
		.apiKey("project_or_organization_wide_rw_api_key")
		.branch("master")
		.vcsType("git")
		.commitHash(CI_COMMIT_SHA)
		.vcsUri(CI_PROJECT_URL)
		.date("2021-02-01T13:27:02-05:00")
		.build();
	Library library = new Library(flags);
	ProjectVersion projectVersion = library.getVersion();
```

Sample command to obtain only version info and skip creating the release:

```bash
	Flags flags = Flags.builder().apiKeyId("project_or_organization_wide_rw_api_id")
		.apiKey("project_or_organization_wide_rw_api_key")
		.branch("master")
		.onlyVersion(true)
		.build();
	Library library = new Library(flags);
	ProjectVersion projectVersion = library.getVersion();
```

## 2. Use Case: Send Release Metadata to Reliza Hub

This use case is commonly used in the CI workflow to stream Release metadata to Reliza Hub. As in previous case, API key must be generated for the project on Reliza Hub prior to sending release details. When pushing multiple artifacts, unused parameters should be left as null.

Sample command to send release details:

```bash
	Flags flags = Flags.builder().apiKeyId("project_or_organization_wide_rw_api_id")
		.apiKey("project_or_organization_wide_rw_api_key")
		.branch("master")
		.version("20.02.3")
		.vcsUri("github.com/relizaio/reliza-cli")
		.vcsType("git")
		.commitHash("7bfc5ce7b0da277d139f7993f90761223fa54442")
		.vcsTag("20.02.3")
		.artId("relizaio/reliza-cli")
		.artBuildId("1")
		.artBuildUri("https://github.com/relizaio/reliza-java-client/actions/runs/619086068")
		.artCiMeta("Github Actions")
		.artType("Docker")
		.artDigests("sha256:4e8b31b19ef16731a6f82410f9fb929da692aa97b71faeb1596c55fbf663dcdd")
		.tagKeyArr("key1")
		.tagValArr("val1")
		.build();
	Library library = new Library(Flags);
	ProjectMetadata projectMetadata = addRelease(library);
```

## 3. Use Case: Check If Artifact Hash Already Present In Some Release

This is particularly useful for monorepos to see if there was a change in sub-project or not. We are using this technique in our sample [playground project](https://github.com/relizaio/reliza-hub-playground). We supply an artifact hash to Reliza Hub - and if it's present already, we get release details; if not - we get an empty json response {}. Search space is scoped to a single project which is defined by API Id and API Key.

Sample command:

```bash
	Flags flags = Flags.builder().apiKeyId("project_api_id")
		.apiKey("project_api_key")
		.hash(sha256:hash)
		.build();
	Library library = new Library(Flags);
	ProjectMetadata projectMetadata = checkHash(library);
```


## 4. Use Case: Send Deployment Metadata From Instance To Reliza Hub

This use case is for sending digests of active deployments from instance to Reliza Hub. API key must also be generated for the instance from Reliza Hub. Sample script is also provided in our [playground project](https://github.com/relizaio/reliza-hub-playground/blob/master/sample-instance-agent-scripts/send_instance_data.sh).

Sample command:

```bash
    Flags flags = Flags.builder().apiKeyId("instance_api_id")
		.apiKey("instance_api_key")
		.imagesString("sha256:c10779b369c6f2638e4c7483a3ab06f13b3f57497154b092c87e1b15088027a5 sha256:e6c2bcd817beeb94f05eaca2ca2fce5c9a24dc29bde89fbf839b652824304703")
		.namespace("default")
		.sender("sender1")
		.build();
	Library library = new Library(Flags);
	InstanceMetadata instanceMetadata = instData(library);
```

## 5. Use Case: Request What Releases Must Be Deployed On This Instance From Reliza Hub

This use case is when instance queries Reliza Hub to receive infromation about what release versions and specific artifacts it needs to deploy. This would usually be used by either simple deployment scripts or full-scale CD systems. A sample use is presented in our [playground project script](https://github.com/relizaio/reliza-hub-playground/blob/master/sample-instance-agent-scripts/request_instance_target.sh).

Sample command:

```bash
	Flags flags = Flags.builder().apiKeyId("instance_api_id")
		.apiKey("instance_api_key")
		.namespace("default")
		.build();
	Library library = new Library(Flags);
	ReleaseMetadata releaseMetadata = getMyRelease(library);
```

## 6. Use Case: Request Latest Release Per Project Or Product

This use case is when Reliza Hub is queried either by CI or CD environment or by integration instance to check latest release version available per specific Project or Product.

Sample command:

```bash
	Flags flags = Flags.builder().apiKeyId("api_id")
		.apiKey("api_key")
		.projectId("b4534a29-3309-4074-8a3a-34c92e1a168b")
		.branch("master")
		.environment("TEST")
		.build();
	Library library = new Library(Flags);
	ReleaseMetadata releaseMetadata = getLatestRelease(library);
```

## 7. Use Case: Programmatic Approvals of Releases on Reliza Hub

This use case is for the case when we have configured an API key in Org settings which is allowed to perform programmatic approvals in releases.

Sample command:

```bash
	Flags flags = Flags.builder().apiKeyId("api_id")
		.apiKey("api_key")
		.releaseId("release_uuid")
		.approval("approval_type")
		.build();
	Library library = new Library(Flags);
	ReleaseMetadata releaseMetadata = approveRelease(library);
```

## 7. Use Case: Send Pull Request Data to Reliza Hub

This use case is used in the CI workflow to stream Pull Request metadata to Reliza Hub.

Sample command:

```bash
	Flags flags = Flags.builder().apiKeyId("api_id")
		.apiKey("api_key")
		.branch("base_branch")
		.targetBranch("targetBranch")
		.state("state")
		.endPoint("HTML endpoint of PR")
		.title("title")
		.number("1")
		.build();
	Library library = new Library(Flags);
	Boolean success = prData(library);
```