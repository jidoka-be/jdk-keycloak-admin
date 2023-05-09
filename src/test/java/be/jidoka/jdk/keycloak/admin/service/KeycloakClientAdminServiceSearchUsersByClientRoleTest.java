package be.jidoka.jdk.keycloak.admin.service;

import be.jidoka.jdk.keycloak.admin.IntegrationTest;
import be.jidoka.jdk.keycloak.admin.domain.AddClientRoleToUserCommand;
import be.jidoka.jdk.keycloak.admin.domain.AddClientRoleToUserCommandBuilder;
import be.jidoka.jdk.keycloak.admin.domain.Client;
import be.jidoka.jdk.keycloak.admin.domain.CreateClientRoleBuilder;
import be.jidoka.jdk.keycloak.admin.domain.CreateUserCommandBuilder;
import be.jidoka.jdk.keycloak.admin.domain.SearchUserByClientRoleRequest;
import be.jidoka.jdk.keycloak.admin.domain.SearchUserByClientRoleRequestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static be.jidoka.jdk.keycloak.admin.domain.PublicClientRequestFixture.aPublicClientRequest;
import static org.assertj.core.api.Assertions.assertThat;

class KeycloakClientAdminServiceSearchUsersByClientRoleTest extends IntegrationTest {

	public static final String ROLE_NAME = "employee";

	@Autowired
	private KeycloakUserAdminService keycloakUserAdminService;

	@Autowired
	private KeycloakClientAdminService keycloakClientAdminService;

	private String clientId;

	@BeforeEach
	void setUp() {
		keycloakClientAdminService.createPublicClient(aPublicClientRequest);
		Optional<Client> client = keycloakClientAdminService.getClient(aPublicClientRequest.getClientId());
		clientId = client.get().getId();
		keycloakClientAdminService.createClientRole(CreateClientRoleBuilder.builder()
				                                            .clientContainerId(clientId)
				                                            .roleName(ROLE_NAME)
				                                            .build());
	}

	@Test
	void returnsUsersWithRole() {
		Set<String> userIdWithGivenRole = createRandomUsers();

		SearchUserByClientRoleRequest searchRequest = SearchUserByClientRoleRequestBuilder.builder().clientId(clientId).roleName(ROLE_NAME).build();

		assertThat(keycloakUserAdminService.searchUsersByClientRole(searchRequest)).extracting("id").containsExactlyInAnyOrderElementsOf(userIdWithGivenRole);
	}

	private Set<String> createRandomUsers() {
		Set<String> userIds = new HashSet<>();

		for (int i = 0; i < 150; i++) {
			CreateUserCommandBuilder createUserRequest = CreateUserCommandBuilder.builder()
					.firstName("Jan" + i)
					.lastName("Pladijs" + i)
					.email("jan.pladijs" + i + "@jidoka.be")
					.username("jan.pladijs" + i)
					.pictureUrl("http://localhost:4200/janpladijs" + i + ".png")
					.build();

			String userId = keycloakUserAdminService.createUser(createUserRequest);

			if (i < 75) {
				AddClientRoleToUserCommand request = AddClientRoleToUserCommandBuilder.builder().clientId(clientId).roleName(ROLE_NAME).userId(userId).build();

				keycloakUserAdminService.addClientRoleToUser(request);

				userIds.add(userId);
			}
		}

		return userIds;
	}
}
