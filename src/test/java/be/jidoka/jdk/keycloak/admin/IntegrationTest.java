package be.jidoka.jdk.keycloak.admin;

import be.jidoka.jdk.keycloak.admin.config.KeycloakAdminAutoConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { KeycloakAdminAutoConfiguration.class }, initializers = IntegrationTest.Initializer.class)
@DirtiesContext //To initialize a new context and create a new Keycloak container for each test.
public abstract class IntegrationTest {

	private static Network network = Network.newNetwork();

	protected static GenericContainer mailhog = new GenericContainer<>("mailhog/mailhog:v1.0.0")
			.withExposedPorts(1025)
			.withNetworkAliases("mailhog")
			.withNetwork(network)
			.waitingFor(Wait.forListeningPort());

	private static GenericContainer keycloak = new GenericContainer<>("jboss/keycloak:9.0.3")
			.withExposedPorts(8080)
			.withEnv("DB_VENDOR", "h2")
			.withEnv("KEYCLOAK_USER", "admin")
			.withEnv("KEYCLOAK_PASSWORD", "admin")
			.withEnv("KEYCLOAK_IMPORT", "/tmp/realm-export.json")
			.withClasspathResourceMapping("realm-export.json", "/tmp/realm-export.json", BindMode.READ_ONLY)
			.withNetwork(network)
			.waitingFor(Wait.forHttp("/auth"));

	static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

		@Override
		public void initialize(ConfigurableApplicationContext applicationContext) {
			keycloak.start();
			TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
					applicationContext,
					"keycloak-admin.auth-server-url=http://" + keycloak.getContainerIpAddress() + ":" + keycloak.getFirstMappedPort() + "/auth",
					"keycloak-admin.realm=keycloak-admin-service",
					"keycloak-admin.client-id=idm-client",
					"keycloak-admin.client-secret=5fbe8f96-a638-4bea-8f54-210c83f3ad63"
			);
		}
	}

	@AfterAll
	static void afterAll() {
		keycloak.stop();
	}
}
