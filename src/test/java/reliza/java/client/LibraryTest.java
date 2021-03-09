/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package reliza.java.client;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import reliza.java.client.responses.InstanceMetadata;
import reliza.java.client.responses.ProjectMetadata;
import reliza.java.client.responses.ProjectVersion;
import reliza.java.client.responses.ReleaseMetadata;

/**
 * Class for testing Library.java methods.
 */
public class LibraryTest {
    private final String TEST_PROJECT_UUID = "314c0886-0f41-4f92-a4ef-59c2cbb0e3b0";
    private final String TEST_PROJECT_API_ID = "PROJECT__314c0886-0f41-4f92-a4ef-59c2cbb0e3b0";
    private final String TEST_PROJECT_API_KEY = "58f7bee5fb50919b055708b0936179ca39083f8e9ec3626d68ccee78e75613f8c0e804dbe41376afe005355eeffc22d4";
    private final String TEST_ORG_API_ID = "ORGANIZATION_RW__c3c606a8-4fa2-4115-b731-a8ae513bc302";
    private final String TEST_ORG_API_KEY= "6380ad96fa8157caa908a6ddf067274d6abe3558ab08ee52850fad5f1342fe497d695f393fe668ea69ac65ab8cc21b30";
    private final String TEST_INSTANCE_API_ID = "INSTANCE__ba118a3c-c83d-453a-9515-b90b2868d0f3";
    private final String TEST_INSTANCE_API_KEY = "cf9a27900e54c5cf207cd7c3a767e4c31af3676ecad3aa58ed1481340448966bc3f2ecd88c5f094fb2544a5972d385f8";
    private final String TEST_APPROVAL_API_ID = "APPROVAL__a828881c-a7d9-4fc4-9ebb-b90cde845b73";
    private final String TEST_APPROVAL_API_KEY = "4eada132ad2eed3c40fee1998e97162b4896f00a9dde367ac098f08009a2639f266f44db079b6970e459c3af4b031147";
    
    /**
     * Test for getVersion using org wide api key and id
     */
    @Test public void testGetVersion() {
          Flags getVersionFlags = Flags.builder().apiKeyId(TEST_ORG_API_ID)
              .apiKey(TEST_ORG_API_KEY)
              .branch("master")
              .projectId(UUID.fromString(TEST_PROJECT_UUID))
              .baseUrl("https://test.relizahub.com")
              .build();
        Library library = new Library(getVersionFlags);
        ProjectVersion projectVersion = library.getVersion();
        Assert.assertNotNull(projectVersion);
    }
    
    /**
     * Test for setting a new release and pushing project details.
     */
    @Test public void testAddRelease() {
        Flags flags = Flags.builder().apiKeyId(TEST_PROJECT_API_ID)
            .apiKey(TEST_PROJECT_API_KEY)
            .branch("master")
            .commitHash("b92b48da3779e3807862cf38d56f789e411af577")
            .artCiMeta(Arrays.asList("Github Actions", "Github Actions"))
            .artGroup(Arrays.asList("io.reliza", "io.reliza"))
            .artId(Arrays.asList("relizaio/reliza-cli", "relizaio/reliza-cli"))
            .artPackage(Arrays.asList("DOCKER", "DOCKER"))
            .artPublisher(Arrays.asList("Rasa", "Rasa"))
            .artType(Arrays.asList("Docker", "Docker"))
            .dateActual("2021-01-11T19:43:32.286086002Z")
            .dateEnd(Arrays.asList("2021-01-12T19:43:32.286086002Z", "2021-01-12T19:43:32.286086002Z"))
            .dateStart(Arrays.asList("2021-01-11T19:43:32.286086002Z", "2021-01-11T19:43:32.286086002Z"))
            .endPoint("https://github.com/relizaio/reliza-java-client")
            .status("completed")
            .tagKeys(Arrays.asList("prod", "prod"))
            .tagVals(Arrays.asList("true", "true"))
            .vcsType("git")
            .baseUrl("https://test.relizahub.com")
            .build();
        Library library = new Library(flags);
        ProjectVersion projectVersion = library.getVersion();
        Assert.assertNotNull(projectVersion);
        library.flags.setVersion(projectVersion.getVersion());
        ProjectMetadata projectMetadata = library.addRelease();
        Assert.assertNotNull(projectMetadata);
    }
    
    /**
     * Test for checkHash method.
     */
    @Test public void testCheckHash() {
        Flags flags = Flags.builder().apiKeyId(TEST_PROJECT_API_ID)
            .apiKey(TEST_PROJECT_API_KEY)
            .hash("sha256:52")
            .baseUrl("https://test.relizahub.com")
            .build();
        Library library = new Library(flags);
        ProjectMetadata projectMetadata = library.checkHash();
        Assert.assertNotNull(projectMetadata);
    }
    
    /**
     * Test for instData method with an image sha256.
     */
    @Test public void testInstDataImagesString() {
      Flags flags = Flags.builder().apiKeyId(TEST_INSTANCE_API_ID)
          .apiKey(TEST_INSTANCE_API_KEY)
          .imagesString("sha256:poke")
          .baseUrl("https://test.relizahub.com")
          .build();
      Library library = new Library(flags);
      InstanceMetadata instanceMetadata = library.instData();
      Assert.assertNotNull(instanceMetadata);
    }
    
    /**
     * Test for instData method using file path.
     */
    @Test public void testInstDataImageStream() {
      Flags flags = Flags.builder().apiKeyId(TEST_INSTANCE_API_ID)
          .apiKey(TEST_INSTANCE_API_KEY)
          .imageInputStream(IOUtils.toInputStream("this is my input stream", StandardCharsets.UTF_8))
          .namespace("spacename")
          .senderId("Idsender")
          .baseUrl("https://test.relizahub.com")
          .build();
      Library library = new Library(flags);
      InstanceMetadata instanceMetadata = library.instData();
      Assert.assertNotNull(instanceMetadata);
    }
    
    /**
     * Test for getMyRelease method.
     */
    @Test public void testGetMyRelease() {
      Flags flags = Flags.builder().apiKeyId(TEST_INSTANCE_API_ID)
          .apiKey(TEST_INSTANCE_API_KEY)
          .baseUrl("https://test.relizahub.com")
          .build();
      Library library = new Library(flags);
      List<ReleaseMetadata> releaseMetadata = library.getMyRelease();
      Assert.assertNotNull(releaseMetadata);
    }
    
    /**
     * Test for getLatestRelease method.
     */
    @Test public void testGetLatestRelease() {
        Flags flags = Flags.builder().apiKeyId(TEST_PROJECT_API_ID)
            .apiKey(TEST_PROJECT_API_KEY)
            .projectId(UUID.fromString(TEST_PROJECT_UUID))
            .branch("master")
            .baseUrl("https://test.relizahub.com")
            .build();
        Library library = new Library(flags);
        ReleaseMetadata releaseMetadata = library.getLatestRelease();
        Assert.assertNotNull(releaseMetadata);
    }
    
    /**
     * Test for approveRelease method.
     */
    @Test public void testApproveRelease() {
        Flags approveReleaseFlags = Flags.builder().apiKeyId(TEST_APPROVAL_API_ID)
            .apiKey(TEST_APPROVAL_API_KEY)
            .version("0.0.1")
            .projectId(UUID.fromString(TEST_PROJECT_UUID))
            .approvalType("PM")
            .baseUrl("https://test.relizahub.com")
            .build();
        Library approveReleaseLibrary = new Library(approveReleaseFlags);
        ReleaseMetadata releaseMetadata = approveReleaseLibrary.approveRelease();
        Assert.assertNotNull(releaseMetadata);
    }
}