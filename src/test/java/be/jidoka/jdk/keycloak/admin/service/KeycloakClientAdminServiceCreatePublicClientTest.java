package be.jidoka.jdk.keycloak.admin.service;

import be.jidoka.jdk.keycloak.admin.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static be.jidoka.jdk.keycloak.admin.domain.PublicClientRequestFixture.aPublicClientRequest;
import static org.assertj.core.api.Assertions.assertThat;

class KeycloakClientAdminServiceCreatePublicClientTest extends IntegrationTest {

	@Autowired
	private KeycloakClientAdminService keycloakClientAdminService;

	@Test
	public void createsPublicClientCorrectly() {
		keycloakClientAdminService.createPublicClient(aPublicClientRequest);
		assertThat(keycloakClientAdminService.getClients()).extracting("clientId").contains("cv-app");
	}
}