/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package reliza.java.client;

import java.io.File;
import java.util.Arrays;
import java.util.UUID;
import org.junit.Test;

public class LibraryTest {
    @Test public void testGetProjectVersion() {    
        Flags flags = Flags.builder().apiKeyId("PROJECT__6ba5691c-05e3-4ecd-a45a-18b382419f40")
            .apiKey("0828b0fabf663fc17a604b527992965ee2abeb4831319125f1692d9ec111ea078dcc8261ed0b9aaf353ce2d003b823b7")
            .branch("ho").build();
        Library library = new Library(flags);
        library.getVersion();
    }
    
    @Test public void testGetOrganizationVersion() {
        Flags flags = Flags.builder().apiKeyId("ORGANIZATION_RW__359e867c-d493-48e3-a6b5-e5a52d259265")
                .apiKey("98b6ff3ac04df3ce5325b9a8b188a9bdafcb21a9ba220ec683d7c783235abddb1e92f600ad157ebc00aee9dab3e9fccd")
                .branch("ho").projectId(UUID.fromString("6ba5691c-05e3-4ecd-a45a-18b382419f40")).build();
        Library library = new Library(flags);
        library.getVersion();
    }
    
    //addRelease only goes through once for a given version
    @Test public void testAddRelease() {
        Flags flags = Flags.builder().apiKeyId("PROJECT__6ba5691c-05e3-4ecd-a45a-18b382419f40")
          .apiKey("0828b0fabf663fc17a604b527992965ee2abeb4831319125f1692d9ec111ea078dcc8261ed0b9aaf353ce2d003b823b7")
          .branch("ho").version("ho.36")
          .commitHash("b92b48da3779e3807862cf38d56f789e411af577").artCiMeta(Arrays.asList("Github Actions", "Github Actions"))
          .artGroup(Arrays.asList("io.reliza", "io.reliza")).artId(Arrays.asList("relizaio/reliza-cli", "relizaio/reliza-cli"))
          .artPackage(Arrays.asList("DOCKER", "DOCKER")).artPublisher(Arrays.asList("Rasa", "Rasa")).artType(Arrays.asList("Docker", "Docker"))
          .dateActual("2021-01-11T19:43:32.286086002Z").dateEnd(Arrays.asList("2021-01-12T19:43:32.286086002Z", "2021-01-12T19:43:32.286086002Z"))
          .dateStart(Arrays.asList("2021-01-11T19:43:32.286086002Z", "2021-01-11T19:43:32.286086002Z"))
          .endPoint("https://github.com/relizaio/reliza-java-client").status("completed")
          .tagKeyArr(Arrays.asList("prod", "prod")).tagValArr(Arrays.asList("true", "true")).vcsType("git").build();
        Library library = new Library(flags);
        library.addRelease();
    }
    
    @Test public void testCheckHash() {
        Flags flags = Flags.builder().apiKeyId("PROJECT__6ba5691c-05e3-4ecd-a45a-18b382419f40")
          .apiKey("0828b0fabf663fc17a604b527992965ee2abeb4831319125f1692d9ec111ea078dcc8261ed0b9aaf353ce2d003b823b7")
          .hash("sha256:2").build();        
        Library library = new Library(flags);
        library.checkHash();
    }

    @Test public void testInstDataImageString() {
      Flags flags = Flags.builder().apiKeyId("INSTANCE__ff253dbd-9654-4a39-963b-15f16b003f61")
          .apiKey("f6350246a5e450cf918f134435542d9af70e90d9f57d09072a5a13f464a33c2751da6c2c9a034c8dd1d7bbf5f945ac8a")
          .imageString("sha256:poke").build();
      Library library = new Library(flags);
      library.instData();
    }
    
    @Test public void testInstDataImageFile() {
      Flags flags = Flags.builder().apiKeyId("INSTANCE__ff253dbd-9654-4a39-963b-15f16b003f61")
          .apiKey("f6350246a5e450cf918f134435542d9af70e90d9f57d09072a5a13f464a33c2751da6c2c9a034c8dd1d7bbf5f945ac8a")
          .imageFilePath(new File("C:\\Users\\welli\\Documents\\Rasa.txt")).namespace("spacename").senderId("Idsender").build();
      Library library = new Library(flags);
      library.instData();
    }

    @Test public void testGetMyRelease() {
      Flags flags = Flags.builder().apiKeyId("INSTANCE__ff253dbd-9654-4a39-963b-15f16b003f61")
          .apiKey("f6350246a5e450cf918f134435542d9af70e90d9f57d09072a5a13f464a33c2751da6c2c9a034c8dd1d7bbf5f945ac8a").build();
      Library library = new Library(flags);
      library.getMyRelease();
    }
    
    @Test public void testGetLatestRelease() {
        Flags flags = Flags.builder().apiKeyId("PROJECT__6ba5691c-05e3-4ecd-a45a-18b382419f40")
            .apiKey("0828b0fabf663fc17a604b527992965ee2abeb4831319125f1692d9ec111ea078dcc8261ed0b9aaf353ce2d003b823b7")
            .projectId(UUID.fromString("6ba5691c-05e3-4ecd-a45a-18b382419f40")).branch("ho").build();
        Library library = new Library(flags);
        library.getLatestRelease();
    }
    
    @Test public void testApproveRelease() {
        Flags flags = Flags.builder().apiKeyId("APPROVAL__6b9019d6-f437-46a3-b56f-62cca6382372")
            .apiKey("d4a2501e431f5360b0499988981401f0380869c839dcfc5363cac714f0188ddfe9fb00fdeb4f6e3248ddc5fe0a225c27")
            .projectId(UUID.fromString("6ba5691c-05e3-4ecd-a45a-18b382419f40")).releaseVersion("ho.26").approvalType("PM").build();
        Library library = new Library(flags);
        library.approveRelease();
    }
}