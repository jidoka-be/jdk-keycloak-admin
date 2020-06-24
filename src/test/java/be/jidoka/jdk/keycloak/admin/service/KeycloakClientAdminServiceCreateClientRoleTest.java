package be.jidoka.jdk.keycloak.admin.service;

import be.jidoka.jdk.keycloak.admin.IntegrationTest;
import be.jidoka.jdk.keycloak.admin.domain.Client;
import be.jidoka.jdk.keycloak.admin.domain.CreateClientRoleBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static be.jidoka.jdk.keycloak.admin.domain.PublicClientRequestFixture.aPublicClientRequest;
import static org.assertj.core.api.Assertions.assertThat;

class KeycloakClientAdminServiceCreateClientRoleTest extends IntegrationTest {

	@Autowired
	private KeycloakClientAdminService keycloakClientAdminService;

	@Test
	public void addsRolesToExistingClient() {
		keycloakClientAdminService.createPublicClient(aPublicClientRequest);
		Optional<Client> client = keycloakClientAdminService.getClient(aPublicClientRequest.getClientId());
		keycloakClientAdminService.createClientRole(CreateClientRoleBuilder.builder()
				.clientContainerId(client.get().getId())
				.roleName("employee")
				.build());
		assertThat(keycloakClientAdminService.getClientRoles(client.get()))
				.extracting("roleName")
				.containsExactly("employee");
	}
}