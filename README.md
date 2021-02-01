# reliza-java-client
This tool is a Java client for [Reliza Hub at relizahub.com](https://app.relizahub.com). Particularly, this library can stream metadata about instances, releases, artifacts, and resolve bundles based on Reliza Hub data.

Video tutorial about key functionality of Reliza Hub is available on [YouTube](https://www.youtube.com/watch?v=yDlf5fMBGuI).

Community forum and support available at [r/Reliza](https://reddit.com/r/Reliza).

### Publishing to Maven Local
To build and publish to local Maven Repository, use:

```
gradle publishToMavenLocal
```

## 1. Use Case: Get Version Assignment From Reliza Hub

This use case requests Version from Reliza Hub for our project. Note that project schema must be preset on Reliza Hub prior to using this API. API key must also be generated for the project from Reliza Hub.

Sample method for semver version schema:

```bash
	Flags flags = Flags.builder().apiKeyId("project_or_organization_wide_rw_api_id")
		.apiKey("project_or_organization_wide_rw_api_key")
		.branch("master")
		.versionSchema("1.2.patch")
		.vcsType("git")
		.commitHash(CI_COMMIT_SHA)
		.vcsUri(CI_PROJECT_URL)
		.date("2021-02-01T13:27:02-05:00")
		.build();
	Library library = new Library(flags);
	ProjectVersion projectVersion = library.getVersion();
```

## 2. Use Case: Send Release Metadata to Reliza Hub

This use case is commonly used in the CI workflow to stream Release metadata to Reliza Hub. As in previous case, API key must be generated for the project on Reliza Hub prior to sending release details.

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
		.artId(Arrays.asList("relizaio/reliza-cli"))
		.artBuildId(Arrays.asList("1"))
		.artCiMeta(Arrays.asList("Github Actions"))
		.artType(Arrays.asList("Docker"))
		.artDigests(Arrays.asList("sha256:4e8b31b19ef16731a6f82410f9fb929da692aa97b71faeb1596c55fbf663dcdd"))
		.tagKeyArr(Arrays.asList("key1"))
		.tagValArr(Arrays.asList("val1"))
		.build();
	Library library = new Library(Flags);
	ProjectMetadata projectMetadata = addRelease(library);
```

## 3. Use Case: Check If Artifact Hash Already Present In Some Release

This is particularly useful for monorepos to see if there was a change in sub-project or not. We are using this technique in our sample [playground project](https://github.com/relizaio/reliza-hub-playground). We supply artifact hash to Reliza Hub - and if it's present already, we get release details; if not - we get empty json response {}. Search space is scoped to single project which is defined by API Id and API Key.

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