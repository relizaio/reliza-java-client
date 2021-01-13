package reliza.java.client;

import java.io.File;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Builder @Getter
public class Flags{
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
    private List<String> artId;
    private List<String> artBuildId;
    private List<String> artCiMeta;
    private List<String> artType;
    private List<String> artVersion;
    private List<String> artPublisher;
    private List<String> artPackage;
    private List<String> artGroup;
    private List<String> dateStart;
    private List<String> dateEnd;
    private List<String> artDigests;
    private List<String> tagKeyArr;
    private List<String> tagValArr;
    private String hash;
    private String imageString;
    @Builder.Default File imageFilePath = new File("/resources/images.txt");
    @Builder.Default private String namespace= "default";
    @Builder.Default private String senderId = "default";
}