package reliza.java.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import reliza.java.client.interceptors.BasicAuthInterceptor;
import reliza.java.client.responses.InstanceMetadata;
import reliza.java.client.responses.ProjectMetadata;
import reliza.java.client.responses.ProjectVersion;
import reliza.java.client.responses.ReleaseMetadata;

/**
 * Defines a library which holds all the parameters from Flags and uses it to make API calls
 */
@Slf4j
public class Library {
    Flags flags;
    RHService rhs;
    
    /**
     * Initializing Flags and RHService to call API. Default endpoint is set to https://app.relizahub.com, however can be modified
     * by setting baseUrl in flags builder.
     * @param flags - Class Flags which contains all possible parameters, flags must be created using builder pattern.
     */   
    public Library(Flags flags) {
        this.flags = flags;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new BasicAuthInterceptor(flags.getApiKeyId(), flags.getApiKey()))
            .build();
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(flags.getBaseUrl())
            .addConverterFactory(JacksonConverterFactory.create(objectMapper))
            .client(client)
            .build();
        this.rhs = retrofit.create(RHService.class);
    }
    
    /**
     * Method that denotes we are obtaining the next available release version for the branch.
     * Note that if the call succeeds, the version assignment will be recorded and will not be given again by Reliza Hub, even if it is not consumed. It will create the release in pending status. <p>
     * Method itself does not require parameters but requires that the Flags class passed during library initialization contains these parameters. <p>
     * - apiKeyId (required) - flag for project api id or organization-wide read-write api id. <br> 
     * - apiKey (required) - flag for project api key or organization-wide read-write api key. <br>
     * - branch (required) - flag to denote branch. If the branch is not recorded yet, Reliza Hub will attempt to create it. <br>
     * - projectId (optional) - flag to denote project uuid. Required if organization-wide read-write key is used, ignored if project specific api key is used. <br>
     * - versionSchema (optional for existing branches, required for new branches) - flag to denote branch versionSchema. If supplied for an existing branch and versionSchema is different from current, it will override current versionSchema. <br>
     * - vcsUri (optional) - flag to denote vcs uri. Currently this flag is needed if we want to set a commit for the release. However, soon it will be needed only if the vcs uri is not yet set for the project. <br>
     * - vcsType (optional) - flag to denote vcs type. Supported values: git, svn, mercurial. As with vcsuri, this flag is needed if we want to set a commit for the release. However, soon it will be needed only if the vcs uri is not yet set for the project. <br>
     * - commitHash (optional) - flag to denote vcs commit id or hash. This is needed to provide source code entry metadata into the release. <br>
     * - dateActual (optional) - flag to denote date time with timezone when commit was made, iso strict formatting with timezone is required, i.e. for git use git log --date=iso-strict. <br>
     * - vcsTag (optional) - flag to denote vcs tag. This is needed to include vcs tag into commit, if present. <br>
     * - manual (optional) - flag to indicate a manual release. Sets status as "draft", otherwise "pending" status is used.
     * - onlyVersion (optional) - boolean flag to skip creation of the release. Default is false.
     * @return returns class ProjectVersion if successful API call and null otherwise.
     */
    public ProjectVersion getVersion() {
        Map<String, Object> body = new HashMap<>();  
        body.put("branch", flags.getBranch());
        body.put("versionSchema", flags.getVersionSchema());
        body.put("project", flags.getProjectId());
        body.put("onlyVersion", flags.getOnlyVersion());
        
        if (StringUtils.isNotEmpty(flags.getCommitHash())) {
            Map<String, String> commitMap = new HashMap<>();
            commitMap.put("commit", flags.getCommitHash());
            commitMap.put("uri", flags.getVcsUri());
            commitMap.put("type", flags.getVcsType());
            commitMap.put("vcsTag", flags.getVcsTag());
            commitMap.put("dateActual", flags.getDateActual());
            body.put("sourceCodeEntry", commitMap);
        }
        if (flags.getManual()) {body.put("status", "draft");}
        Call<ProjectVersion> call = rhs.getVersion(body);
        return execute(call);
    }
    
    /**
     * Method that denotes we are sending Release Metadata of a Project to Reliza Hub.
     * Note that Reliza Hub will only allow you to send release data once per project version. <p>
     * Multiple artifacts per release are supported. In which case artifact specific flags (artid, artbuildid, artbuilduri, artcimeta, arttype, artdigests, tagkey and tagval must be repeated for each artifact). <p>
     * For sample of how to use workflow in CI, refer to the GitHub Actions build yaml of this project here
     * <a href="https://github.com/relizaio/reliza-cli/blob/master/.github/workflows/dockerimage.yml" target="_top">https://github.com/relizaio/reliza-cli/blob/master/.github/workflows/dockerimage.yml</a> <p>
     * Method itself does not require parameters but requires that the Flags class passed during library initialization contains these parameters. <p>
     * - apiKeyId (required) - flag for project api id or organization-wide read-write api id. <br>
     * - apiKey (required) - flag for project api key or organization-wide read-write api key. <br>
     * - branch (required) - flag to denote branch. If branch is not recorded yet, Reliza Hub will attempt to create it. <br>
     * - version (required) - version. Note that Reliza Hub will reject the call if a release with this exact version is already present for this project. <br>
     * - endPoint (optional) - flag to denote test endpoint URI. This would be useful for systems where every release gets test URI. <br>
     * - projectId (optional) - flag to denote project uuid. Required if organization-wide read-write key is used, ignored if project specific api key is used. <br>
     * - vcsUri (optional) - flag to denote vcs uri. Currently this flag is needed if we want to set a commit for the release. However, soon it will be needed only if the vcs uri is not yet set for the project. <br>
     * - vcsType (optional) - flag to denote vcs type. Supported values: git, svn, mercurial. As with vcsuri, this flag is needed if we want to set a commit for the release. However, soon it will be needed only if the vcs uri is not yet set for the project. <br>
     * - commitHash (optional) - flag to denote vcs commit id or hash. This is needed to provide source code entry metadata into the release. <br>
     * - dateActual (optional) - flag to denote date time with timezone when commit was made, iso strict formatting with timezone is required, i.e. for git use git log --date=iso-strict. <br>
     * - vcsTag (optional) - flag to denote vcs tag. This is needed to include vcs tag into commit, if present. <br>
     * - status (optional) - flag to denote release status. Supply "rejected" for failed releases, otherwise "completed" is used. <br>
     * - artId (optional) - flag to denote artifact identifier. This is required to add artifact metadata into release. <br>
     * - artBuildId (optional) - flag to denote artifact build id. This flag is optional and may be used to indicate build system id of the release (i.e., this could be circleci build number). <br>
     * - artBuildUri (optional) - flag to denote artifact build uri. This flag is optional and is used to denote the uri for where the build takes place. <br>
     * - artCiMeta (optional) - flag to denote artifact CI metadata. This flag is optional and like artbuildid may be used to indicate build system metadata in free form. <br>
     * - artType (optional) - flag to denote artifact type. This flag is used to denote artifact type. Types are based on CycloneDX spec. Supported values: Docker, File, Image, Font, Library, Application, Framework, OS, Device, Firmware. <br>
     * - dateStart (optional, if used there must be one datestart flag entry per artifact) - flag to denote artifact build start date and time, must conform to ISO strict date, i.e. "2021-01-12T19:43:32Z". <br>
     * - dateEnd (optional, if used there must be one datestart flag entry per artifact) - flag to denote artifact build end date and time, must conform to ISO strict date, i.e. "2021-01-12T19:43:32Z". <br>
     * - publisher (optional, if used there must be one publisher flag entry per artifact) - flag to denote artifact publisher. <br>
     * - version (optional, if used there must be one version flag entry per artifact) - flag to denote artifact version if different from release version. <br>
     * - package (optional, if used there must be one package flag entry per artifact) - flag to denote artifact package type according to CycloneDX spec: MAVEN, NPM, NUGET, GEM, PYPI, DOCKER. <br>
     * - group (optional, if used there must be one group flag entry per artifact) - flag to denote artifact group. <br>
     * - artDigests (optional) - flag to denote artifact digests. This flag is used to indicate artifact digests. By convention, digests must be prefixed with type followed by colon and then actual digest hash, i.e. <br>
     *                  sha256:4e8b31b19ef16731a6f82410f9fb929da692aa97b71faeb1596c55fbf663dcdd - here type is sha256 and digest is 4e8b31b19ef16731a6f82410f9fb929da692aa97b71faeb1596c55fbf663dcdd. Multiple digests are supported and must be comma separated. i.e. <br>
     *                  sha256:4e8b31b19ef16731a6f82410f9fb929da692aa97b71faeb1596c55fbf663dcdd,sha1:fe4165996a41501715ea0662b6a906b55e34a2a1 <br>
     * - tagKeys (optional, but every tag key must have corresponding tag value) - flag to denote keys of artifact tags. Multiple tag keys per artifact are supported and must be comma separated. I.e.:tagKeyArr(key1,key2) <br>
     * - tagVals (optional, but every tag value must have corresponding tag key) - flag to denote values of artifact tags. Multiple tag values per artifact are supported and must be comma separated. I.e.:tagValArr(val1,val2)
     * @return returns class ProjectMetadata if successful API call and null otherwise.
     */
    public ProjectMetadata addRelease() {
        Map<String, Object> body = new HashMap<>();
        body.put("branch", flags.getBranch());
        body.put("version", flags.getVersion());
        body.put("status", flags.getStatus());
        body.put("endpoint", flags.getEndPoint());
        body.put("project", flags.getProjectId());
        
        if (StringUtils.isNotEmpty(flags.getCommitHash())) {
            Map<String, String> commitMap = new HashMap<>();
            commitMap.put("commit", flags.getCommitHash());
            commitMap.put("uri", flags.getVcsUri());
            commitMap.put("type", flags.getVcsType());
            commitMap.put("vcsTag", flags.getVcsTag());
            commitMap.put("dateActual", flags.getDateActual());
            body.put("sourceCodeEntry", commitMap);
        }
        
        if (CollectionUtils.isNotEmpty(flags.getArtId())) {
            List<Map<String, Object>> artifacts = new ArrayList<Map<String, Object>>();
            for (String artId : flags.getArtId()) {
                if (artId == null) {
                log.error("artid flag cannot contain null values");
                return null;
                }
                Map<String, Object> artifact = new HashMap<>();
                artifact.put("identifier", artId);
                artifacts.add(artifact);
            }
            
            Map<String, List<String>> artFlags = new HashMap<String, List<String>>();
            artFlags.put("buildId", flags.getArtBuildId());
            artFlags.put("buildUri", flags.getArtBuildUri());
            artFlags.put("cicdMeta", flags.getArtCiMeta());
            artFlags.put("type", flags.getArtType());
            artFlags.put("artifactVersion", flags.getArtVersion());
            artFlags.put("publisher", flags.getArtPublisher());
            artFlags.put("packageType", flags.getArtPackage());
            artFlags.put("group", flags.getArtGroup());
            artFlags.put("dateFrom", flags.getDateStart());
            artFlags.put("dateTo", flags.getDateEnd());
            
            for (String key : artFlags.keySet()) {
                if (CollectionUtils.isNotEmpty(artFlags.get(key)) && artFlags.get(key).size() != flags.getArtId().size()) {
                    log.error("number of art" + key.toLowerCase() + " flags must be either zero or match number of artid flags");
                    return null;
                } else if (CollectionUtils.isNotEmpty(artFlags.get(key))) {
                    for (int j = 0; j < flags.getArtId().size(); j++) {
                        artifacts.get(j).put(key, artFlags.get(key).get(j));
                    }
                }
            }
            
            if (CollectionUtils.isNotEmpty(flags.getArtDigests()) && flags.getArtDigests().size() != flags.getArtId().size()) {
                log.error("number of artdigests flags must be either zero or match number of artid flags");
                return null;
            } else if (CollectionUtils.isNotEmpty(flags.getArtDigests())) {
                for (int i = 0; i < flags.getArtId().size(); i++) {
                    if (flags.getArtDigests().get(i) == null) { continue; }
                    artifacts.get(i).put("digests", Arrays.asList(flags.getArtDigests().get(i).split(",")));
                }
            }
            
            if (CollectionUtils.isNotEmpty(flags.getTagKeys()) && flags.getTagKeys().size() != flags.getArtId().size()) {
                log.error("number of tagkey flags must be either zero or match number of artid flags");
                return null;
            } else if (CollectionUtils.isNotEmpty(flags.getTagVals()) && flags.getTagVals().size() != flags.getArtId().size()) {
                log.error("number of tagval flags must be either zero or match number of artid flags");
                return null;
            } else if (CollectionUtils.isNotEmpty(flags.getTagKeys()) && CollectionUtils.isEmpty(flags.getTagVals())) {
                log.error("number of tagval and tagkey flags must be the same and must match number of artid flags");
                return null;
            } else if (CollectionUtils.isNotEmpty(flags.getTagKeys())) {
                for (int i = 0; i < flags.getTagKeys().size(); i++) {
                    if (flags.getTagKeys().get(i) == null || flags.getTagVals().get(i) == null) { continue; }
                    List<String> keys = Arrays.asList(flags.getTagKeys().get(i).split(","));
                    List<String> vals = Arrays.asList(flags.getTagVals().get(i).split(","));
                    if (CollectionUtils.isNotEmpty(keys) && CollectionUtils.isNotEmpty(vals) && keys.size() != vals.size()) {
                        log.error("number of keys and values per each tagval and tagkey flag must be the same");
                        return null;
                    }
                    Map<String, String> tagKeyToVal = new HashMap<>();
                    for (int j = 0; j < keys.size(); j++) {
                        tagKeyToVal.put(keys.get(j), vals.get(j));
                    }
                    artifacts.get(i).put("tags", tagKeyToVal);
                }
            }
            body.put("artifacts", artifacts);
        }
        Call<ProjectMetadata> call = rhs.addRelease(body);
        return execute(call);
    }
    
    /**
     * Method that denotes we are checking artifact hash. <p>
     * Method itself does not require parameters but requires that the Flags class passed during library initialization contains these parameters. <p>
     * - apiKeyId (required) - flag for project api id. <br>
     * - apiKey (required) - flag for project api key. <br>
     * - hash (required) - flag to denote actual hash. By convention, hash must include hashing algorithm as its first part, i.e. sha256: or sha512:
     * @return returns class ProjectMetadata if successful API call and null otherwise.
     */
    public ProjectMetadata checkHash() {
        Map<String, Object> body = new HashMap<>();
        body.put("hash", flags.getHash());
        Call<Map<String,ProjectMetadata>> call = rhs.checkHash(body);
        Map<String,ProjectMetadata> response = execute(call);
        if (response == null) {
            return null;
        }
        return response.get("release");
    }
    
    /**
     * Method that denotes that we are sending digest data from instance. <p>
     * Method itself does not require parameters but requires that the Flags class passed during library initialization contains these parameters. <p>
     * - apiKeyId (required) - flag for instance api id. <br>
     * - apiKey (required) - flag for instance api key. <br>
     * - imagesString (required) - flag which lists sha256 digests of images sent from the instances. Images must be white space separated. Note that sending full docker image URIs with digests is also accepted, i.e. it's ok to send images as relizaio/reliza-cli:latest@sha256:ebe68a0427bf88d748a4cad0a419392c75c867a216b70d4cd9ef68e8031fe7af  <br>
     * - namespace (optional, if not sent "default" namespace is used) - flag to denote namespace where we are sending images. Namespaces are useful to separate different products deployed on the same instance. <br>
     * - senderId (optional) - flag to denote unique sender within a single namespace. This is useful if say there are different nodes where each streams only part of application deployment data. In this case such nodes need to use same namespace but different senders so that their data does not stomp on each other. <br>
     * @return returns class InstanceMetadata if successful API call and null otherwise.
     */
    public InstanceMetadata instData() {
        Map<String, Object> body = new HashMap<>();
        if (StringUtils.isNotEmpty(flags.getImagesString())) {
            body.put("images", Arrays.asList(flags.getImagesString().split(" ")));
        } else if (flags.getImageInputStream() != null) {
            try {
                byte[] imageBytes = IOUtils.toByteArray(flags.getImageInputStream());
                body.put("images", Arrays.asList(new String(imageBytes, StandardCharsets.UTF_8).split(" ")));
            } catch (IOException e) {
                log.error("IO exception", e);
                return null;
            } catch (NullPointerException e) {
                log.error("NullPointerException", e);
                return null;
            }
        }
        body.put("timeSent", Instant.now().toString());
        body.put("namespace", flags.getNamespace());
        body.put("senderId", flags.getSenderId());
        Call<InstanceMetadata> call = rhs.instData(body);
        return execute(call);
    }
    
    /**
     * Method that denotes we are requesting release data for instance from Reliza Hub. <p>
     * Method itself does not require parameters but requires that the Flags class passed during library initialization contains these parameters. <p>
     * - apiKeyId (required) - flag for instance api id. <br>
     * - apiKey (required) - flag for instance api key. <br>
     * - namespace (optional, if not sent "default" namespace is used) - flag to denote namespace for which we are requesting release data. Namespaces are useful to separate different products deployed on the same instance.
     * @return returns class ReleaseMetadata if successful API call and null otherwise.
     */
    public List<ReleaseMetadata> getMyRelease() {
        Call<List<ReleaseMetadata>> call = rhs.getMyRelease(flags.getNamespace());
        return execute(call);
    }
    
    /**
     * Method that denotes we are requesting latest release data for Project or Product from Reliza Hub. <p>
     * Method itself does not require parameters but requires that the Flags class passed during library initialization contains these parameters. <p>
     * - apiKeyId (required) - flag for api id which can be either api id for this project or organization-wide read API. <br>
     * - apiKey (required) - flag for api key which can be either api key for this project or organization-wide read API. <br>
     * - projectId (required) - flag to denote UUID of specific Project or Product, UUID must be obtained from Reliza Hub. <br>
     * - product (optional) - flag to denote UUID of Product which packages Project or Product for which we inquiry about its version via --project flag, UUID must be obtained from Reliza Hub. <br>
     * - branch (optional, if not supplied settings from Reliza Hub UI are used) - flag to denote required branch of chosen Project or Product. <br>
     * - environment (optional) - flag to denote environment to which release approvals should match. Environment can be one of: DEV, BUILD, TEST, SIT, UAT, PAT, STAGING, PRODUCTION. If not supplied, latest release will be returned regardless of approvals. <br>
     * - tagKeys (optional, if provided tagval flag must also be supplied) - flag to denote tag key to use as a selector for artifact. Note that currently only single tag is supported. <br>
     * - tagVals (optional, if provided tagkey flag must also be supplied) - flag to denote tag value to use as a selector for artifact. <br>
     * - instance (optional, if supplied namespace flag is also used and env flag gets overrided by instance's environment) - flag to denote specific instance for which release should match. <br>
     * - namespace (optional) - flag to denote specific namespace within instance, if instance is supplied.
     * @return returns class ReleaseMetadata if successful API call and null otherwise.
     */
    public ReleaseMetadata getLatestRelease() {
        Map<String, Object> body = new HashMap<>();
        body.put("project", flags.getProjectId());
        body.put("environment", flags.getEnvironment());
        body.put("product", flags.getEnvironment());
        if (CollectionUtils.isNotEmpty(flags.getTagKeys()) && CollectionUtils.isNotEmpty(flags.getTagVals())) {
            body.put("tags", flags.getTagKeys().get(0) + " " + flags.getTagVals().get(0));
        }
        body.put("branch", flags.getBranch());
        body.put("instance", flags.getInstance());
        body.put("namespace", flags.getNamespace());
        Call<ReleaseMetadata> call = rhs.getLatestRelease(body);
        return execute(call);
    }
    
    /**
     * Method that denotes that we are approving release programmatically for the specific type. Must grant permission for approvalType beforehand. <p>
     * Method itself does not require parameters but requires that the Flags class passed during library initialization contains these parameters. <p>
     * - apiKeyId (required) - flag for api id. <br>
     * - apiKey (required) - flag for api key. <br>
     * - releaseId (either this flag or project id and release version is required) - flag to specify release uuid, which can be obtained from the release view or programmatically. <br>
     * - projectId (either this flag and release version or releaseid must be provided) - flag to specify project uuid, which can be obtained from the project settings on Reliza Hub UI. <br>
     * - releaseVersion (either this flag and project or releaseid must be provided) - flag to specify release string version with the project flag above. <br>
     * - approvalType (required) - approval type as per approval matrix on the Organization Settings page in Reliza Hub. <br>
     * - disapprove (optional) - flag to indicate disapproval event instead of approval.
     * @return returns class ReleaseMetadata if successful API call and null otherwise.
     */
    public ReleaseMetadata approveRelease() {
        Map<String, Object> body = new HashMap<>();
        Map<String, Boolean> approvalMap = new HashMap<>();
        approvalMap.put(flags.getApprovalType(), !flags.getDisapprove());
        body.put("approvals", approvalMap);
        body.put("uuid", flags.getReleaseId());
        body.put("version", flags.getVersion());
        body.put("project", flags.getProjectId());
        Call<ReleaseMetadata> call = rhs.approveRelease(body);
        return execute(call);
    }
    
    /**
     * Method for executing call and logging results
     * @param <T> - The response class of our call.
     * @param call - The call which we send to the API.
     * @return If call is successful return API response and return null otherwise.
     */
    private static <T> T execute(Call<T> call) {
        try {
            Response<T> resp = call.execute();
            if (resp.isSuccessful()) {
                log.debug(resp.body().toString());
                return resp.body();
            } else {
                log.error(resp.errorBody().string());
                return null;
            }
        } catch (IOException e) {
            log.error("IO exception", e);
            return null;
        }
    }
}