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
    private final String TEST_PROJECT_UUID = "b4d6324f-4985-49a8-afba-bdcffec32b03";
    private final String TEST_PROJECT_API_ID = "PROJECT__b4d6324f-4985-49a8-afba-bdcffec32b03";
    private final String TEST_PROJECT_API_KEY = "1367ff3aa5a38ef626607a7b38613e4913ac732775444a26db4bc6d8344ad626aab1f7801ff9b51a8cd76a2dd61d8dc4";
    private final String TEST_ORG_API_ID = "ORGANIZATION_RW__cb78b7e4-f38e-4b6f-b92d-214b0626e497";
    private final String TEST_ORG_API_KEY= "1b576f5836923017f23b37a28ec6ea314e46b0c6ac721b5c884d4d535c61485b4f518eea11e9a34704a58d807ac214d4";
    private final String TEST_INSTANCE_API_ID = "INSTANCE__a4b578b7-9094-4f81-a9dd-dc3116384300";
    private final String TEST_INSTANCE_API_KEY = "59cea9abbf10c24c500072274816cbe53da2c5344a414490047d8967276aa56cf02ee750c3b4ae7ac2075497e378f1cf";
    private final String TEST_APPROVAL_API_ID = "APPROVAL__dad681e9-c4f8-4631-b299-c852e1975392";
    private final String TEST_APPROVAL_API_KEY = "cc3885c2cfd92dc4a5f44b6b4ec828ffd4f4d487d1d80e2052c1d46c6649d3e96c23c0287f07b8c598549e9ec45f68ef";
    
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
        Library getVersionLibrary = new Library(getVersionFlags);
        ProjectVersion projectVersion = getVersionLibrary.getVersion();
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
            .hash("sha256:2")
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