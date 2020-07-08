package be.jidoka.jdk.keycloak.admin.service;

import be.jidoka.jdk.keycloak.admin.IntegrationTest;
import be.jidoka.jdk.keycloak.admin.domain.Client;
import be.jidoka.jdk.keycloak.admin.domain.CreateClientRoleBuilder;
import be.jidoka.jdk.keycloak.admin.domain.RemoveClientRoleBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static be.jidoka.jdk.keycloak.admin.domain.PublicClientRequestFixture.aPublicClientRequest;
import static org.assertj.core.api.Assertions.assertThat;

class KeycloakClientAdminServiceRemoveClientRoleTest extends IntegrationTest {

	@Autowired
	private KeycloakClientAdminService keycloakClientAdminService;

	@Test
	void deletesRoleFromClient() {
		keycloakClientAdminService.createPublicClient(aPublicClientRequest);
		Optional<Client> client = keycloakClientAdminService.getClient(aPublicClientRequest.getClientId());
		CreateClientRoleBuilder createClientRole = CreateClientRoleBuilder.builder()
				.clientContainerId(client.get().getId())
				.roleName("testing")
				.build();
		keycloakClientAdminService.createClientRole(createClientRole);
		assertThat(keycloakClientAdminService.getClientRoles(client.get())).extracting("roleName").containsExactly("testing");

		RemoveClientRoleBuilder removeClientRole = RemoveClientRoleBuilder.builder()
				.clientContainerId(client.get().getId())
				.roleName("testing")
				.build();
		keycloakClientAdminService.removeClientRole(removeClientRole);

		assertThat(keycloakClientAdminService.getClientRoles(client.get())).isEmpty();
	}
}