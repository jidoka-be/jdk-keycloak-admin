package be.jidoka.jdk.keycloak.admin.service;

import be.jidoka.jdk.keycloak.admin.IntegrationTest;
import be.jidoka.jdk.keycloak.admin.domain.Client;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class KeycloakClientAdminServiceGetClientsTest extends IntegrationTest {

	@Autowired
	private KeycloakClientAdminService keycloakClientAdminService;

	@Test
	void returnsAllClientsConfiguredInKeycloak() {
		Set<Client> clients = keycloakClientAdminService.getClients();
		assertThat(clients).hasSize(7);
		assertThat(clients).extracting("clientId").contains("idm-client");
	}
}