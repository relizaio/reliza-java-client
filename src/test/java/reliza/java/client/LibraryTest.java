/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package reliza.java.client;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import reliza.java.client.responses.FullRelease;
import reliza.java.client.responses.ProjectVersion;
import reliza.java.client.responses.ReleaseData;

/**
 * Class for testing Library.java methods.
 */
public class LibraryTest {
	private final String BASE_URL = "https://test.relizahub.com";
	private final String TEST_PROJECT_UUID = "24625ac0-0256-4638-99d2-f245cc56ff8f";
	private final String TEST_PROJECT_API_ID = "PROJECT__24625ac0-0256-4638-99d2-f245cc56ff8f";
	private final String TEST_PROJECT_API_KEY = "5e1408d9c4f1d9768823002e327ba031fd18bedde140a68b03a4a6643afd64f4f50919fb2304af55357ed8ebe16561b5";
	private final String TEST_ORG_API_ID = "ORGANIZATION_RW__6dbea5c5-6b01-4df6-94dd-4ddc8a09dee1__ord__a7984c31-54ab-418b-a689-961289540332";
	private final String TEST_ORG_API_KEY= "cb0ba8ce28d353de3448fe5e5612199028d33185186bc842dcc0715b71511526192e59218c7541271bf8360ee0e56b25";
	private final String TEST_INSTANCE_API_ID = "INSTANCE__a7088288-f1d0-4435-8f18-5e6b07c092ed";
	private final String TEST_INSTANCE_API_KEY = "4972318a5ff0fbc14ad4d75453a5b442abcefd6ebdfcabc747ec32800070d1b907fc5b2d26a5663711d7913152baa333";
	private final String TEST_APPROVAL_API_ID = "APPROVAL__7a287711-41a4-43b4-bc7b-4e4f0963bdc9";
	private final String TEST_APPROVAL_API_KEY = "e1bcc9e53a682527f69ecd2d44fdce0117e218c27d0859c591d909bf3c995187883ac901a9d210feb66e77c5907a333a";
	
	/**
	 * Test for getVersion using org wide api key and id
	 */
	@Test public void testGetVersion() {
		Flags flags = Flags.builder().apiKeyId(TEST_ORG_API_ID)
			.apiKey(TEST_ORG_API_KEY)
			.branch("master")
			.projectId(UUID.fromString(TEST_PROJECT_UUID))
			.baseUrl(BASE_URL)
			.build();
		Library library = new Library(flags);
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
			.artDigests(Arrays.asList("sha256:123", "sha256:125"))
			.endPoint("https://github.com/relizaio/reliza-java-client")
			.status("complete")
			.tagKeys(Arrays.asList("prod", "prod"))
			.tagVals(Arrays.asList("true", "true"))
			.vcsType("git")
			.baseUrl(BASE_URL)
			.build();
		Library library = new Library(flags);
		ProjectVersion projectVersion = library.getVersion();
		Assert.assertNotNull(projectVersion);
		library.flags.setVersion(projectVersion.getVersion());
		ReleaseData releaseData = library.addRelease();
		Assert.assertNotNull(releaseData);
	}
	
	/**
	 * Test for checkHash method.
	 */
	@Test public void testCheckHash() {
		Flags flags = Flags.builder().apiKeyId(TEST_PROJECT_API_ID)
			.apiKey(TEST_PROJECT_API_KEY)
			.hash("sha256:52")
			.baseUrl(BASE_URL)
			.build();
		Library library = new Library(flags);
		ReleaseData releaseData = library.checkHash();
		Assert.assertNotNull(releaseData);
	}
	
	/**
	 * Test for instData method with an image sha256.
	 */
	@Test public void testInstDataImagesString() {
		Flags flags = Flags.builder().apiKeyId(TEST_INSTANCE_API_ID)
			.apiKey(TEST_INSTANCE_API_KEY)
			.imagesString("sha256:tests")
			.baseUrl(BASE_URL)
			.build();
		Library library = new Library(flags);
		Map<String, String> status = library.instData();
		Assert.assertNotNull(status);
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
			.baseUrl(BASE_URL)
			.build();
		Library library = new Library(flags);
		Map<String, String> status = library.instData();
		Assert.assertNotNull(status);
	}
	
	/**
	 * Test for getMyRelease method.
	 */
	@Test public void testGetMyRelease() {
		Flags flags = Flags.builder().apiKeyId(TEST_INSTANCE_API_ID)
			.apiKey(TEST_INSTANCE_API_KEY)
			.baseUrl(BASE_URL)
			.build();
		Library library = new Library(flags);
		List<FullRelease> fullReleases = library.getMyRelease();
		Assert.assertFalse(fullReleases.isEmpty());
	}
	
	/**
	 * Test for getLatestRelease method.
	 */
	@Test public void testGetLatestRelease() {
		Flags flags = Flags.builder().apiKeyId(TEST_PROJECT_API_ID)
			.apiKey(TEST_PROJECT_API_KEY)
			.projectId(UUID.fromString(TEST_PROJECT_UUID))
			.branch("master")
			.baseUrl(BASE_URL)
			.build();
		Library library = new Library(flags);
		FullRelease fullRelease = library.getLatestRelease();
		Assert.assertNotNull(fullRelease);
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
			.baseUrl(BASE_URL)
			.build();
		Library approveReleaseLibrary = new Library(approveReleaseFlags);
		ReleaseData releaseData = approveReleaseLibrary.approveRelease();
		Assert.assertNotNull(releaseData);
	}
}