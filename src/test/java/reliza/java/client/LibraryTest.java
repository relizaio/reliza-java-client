/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package reliza.java.client;

import java.io.File;
import java.util.Arrays;
import java.util.UUID;
import org.junit.Test;
import reliza.java.client.responses.InstanceMetadata;
import reliza.java.client.responses.ProjectMetadata;
import reliza.java.client.responses.ProjectVersion;
import reliza.java.client.responses.ReleaseMetadata;

/**
 * Class for testing Library.java methods
 */
//TODO Make tests fail when http request errors instead of just logging the errors
public class LibraryTest {
    /**
     * Test for getVersion using project api key and id
     */
    @Test public void testGetProjectVersion() {    
        Flags flags = Flags.builder().apiKeyId("PROJECT__6ba5691c-05e3-4ecd-a45a-18b382419f40")
            .apiKey("0828b0fabf663fc17a604b527992965ee2abeb4831319125f1692d9ec111ea078dcc8261ed0b9aaf353ce2d003b823b7")
            .branch("ho").baseUrl("https://test.relizahub.com").build();
        Library library = new Library(flags);
        ProjectVersion projectVersion = library.getVersion();
    }
    
    /**
     * Separate test for getVersion using org wide api key and id
     */
    @Test public void testGetOrganizationVersion() {
        Flags flags = Flags.builder().apiKeyId("ORGANIZATION_RW__359e867c-d493-48e3-a6b5-e5a52d259265")
                .apiKey("98b6ff3ac04df3ce5325b9a8b188a9bdafcb21a9ba220ec683d7c783235abddb1e92f600ad157ebc00aee9dab3e9fccd")
                .branch("ho").projectId(UUID.fromString("6ba5691c-05e3-4ecd-a45a-18b382419f40")).baseUrl("https://test.relizahub.com").build();
        Library library = new Library(flags);
        ProjectVersion projectVersion = library.getVersion();
    } 
    
    /**
     * Test for addRelease method, can only use addRelease once per project version
     */
    @Test public void testAddRelease() {
        Flags flags = Flags.builder().apiKeyId("PROJECT__6ba5691c-05e3-4ecd-a45a-18b382419f40")
            .apiKey("0828b0fabf663fc17a604b527992965ee2abeb4831319125f1692d9ec111ea078dcc8261ed0b9aaf353ce2d003b823b7")
            .branch("ho")
            .version("ho.37")
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
            .vcsType("git").baseUrl("https://test.relizahub.com").build();
        Library library = new Library(flags);
        ProjectMetadata projectMetadata = library.addRelease();
    }
    
    /**
     * Test for checkHash method
     */
    @Test public void testCheckHash() {
        Flags flags = Flags.builder().apiKeyId("PROJECT__6ba5691c-05e3-4ecd-a45a-18b382419f40")
            .apiKey("0828b0fabf663fc17a604b527992965ee2abeb4831319125f1692d9ec111ea078dcc8261ed0b9aaf353ce2d003b823b7")
            .hash("sha256:2").baseUrl("https://test.relizahub.com").build();        
        Library library = new Library(flags);
        ProjectMetadata projectMetadata = library.checkHash();
    }
    
    /**
     * Test for instData method with an image sha256
     */
    @Test public void testInstDataImagesString() {
      Flags flags = Flags.builder().apiKeyId("INSTANCE__ff253dbd-9654-4a39-963b-15f16b003f61")
          .apiKey("f6350246a5e450cf918f134435542d9af70e90d9f57d09072a5a13f464a33c2751da6c2c9a034c8dd1d7bbf5f945ac8a")
          .imagesString("sha256:poke").baseUrl("https://test.relizahub.com").build();
      Library library = new Library(flags);
      InstanceMetadata instanceMetadata = library.instData();
    }
    
    /**
     * Test for instData method using file path
     */
    @Test public void testInstDataImageFile() {
      Flags flags = Flags.builder().apiKeyId("INSTANCE__ff253dbd-9654-4a39-963b-15f16b003f61")
          .apiKey("f6350246a5e450cf918f134435542d9af70e90d9f57d09072a5a13f464a33c2751da6c2c9a034c8dd1d7bbf5f945ac8a")
          .imageFilePath(new File("C:\\Users\\welli\\Documents\\Rasa.txt"))
          .namespace("spacename")
          .senderId("Idsender").baseUrl("https://test.relizahub.com").build();
      Library library = new Library(flags);
      InstanceMetadata instanceMetadata = library.instData();
    }

    /**
     * Test for getMyRelease method
     */
    @Test public void testGetMyRelease() {
      Flags flags = Flags.builder().apiKeyId("INSTANCE__ff253dbd-9654-4a39-963b-15f16b003f61")
          .apiKey("f6350246a5e450cf918f134435542d9af70e90d9f57d09072a5a13f464a33c2751da6c2c9a034c8dd1d7bbf5f945ac8a")
          .baseUrl("https://test.relizahub.com").build();
      Library library = new Library(flags);
      ReleaseMetadata releaseMetadata = library.getMyRelease();
    }
    
    /**
     * Test for getLatestRelease method
     */
    @Test public void testGetLatestRelease() {
        Flags flags = Flags.builder().apiKeyId("PROJECT__6ba5691c-05e3-4ecd-a45a-18b382419f40")
            .apiKey("0828b0fabf663fc17a604b527992965ee2abeb4831319125f1692d9ec111ea078dcc8261ed0b9aaf353ce2d003b823b7")
            .projectId(UUID.fromString("6ba5691c-05e3-4ecd-a45a-18b382419f40"))
            .branch("ho").baseUrl("https://test.relizahub.com").build();
        Library library = new Library(flags);
        ReleaseMetadata releaseMetadata = library.getLatestRelease();
    }
    
    /**
     * Test for approveRelease method
     */
    @Test public void testApproveRelease() {
        Flags flags = Flags.builder().apiKeyId("APPROVAL__6b9019d6-f437-46a3-b56f-62cca6382372")
            .apiKey("d4a2501e431f5360b0499988981401f0380869c839dcfc5363cac714f0188ddfe9fb00fdeb4f6e3248ddc5fe0a225c27")
            .projectId(UUID.fromString("6ba5691c-05e3-4ecd-a45a-18b382419f40"))
            .releaseVersion("ho.26")
            .approvalType("PM").baseUrl("https://test.relizahub.com").build();
        Library library = new Library(flags);
        ReleaseMetadata releaseMetadata = library.approveRelease();
    }
}