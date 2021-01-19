package reliza.java.client;

import java.io.File;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

//lombok docs - https://projectlombok.org/features/all
/**
 * Class for storing all flags for use in Library.java methods
 * Base url is https://app.relizahub.com and can be modified in builder
 */
@Builder @Getter
public class Flags{
    @Builder.Default private String baseUrl =  "https://app.relizahub.com";
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
    @Singular("artId") private List<String> artId;
    @Singular("artBuildId") private List<String> artBuildId;
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
    @Builder.Default File imageFilePath = new File("/resources/images.txt");
    @Builder.Default private String namespace = "default";
    @Builder.Default private String senderId = "default";
    private UUID product;
    private String environment;
    private String instance;
    private UUID releaseId;
    private String releaseVersion;
    private String approvalType;
    @Builder.Default private Boolean disapprove = false;
}