package be.jidoka.jdk.keycloak.admin.service;

import be.jidoka.jdk.keycloak.admin.IntegrationTest;
import be.jidoka.jdk.keycloak.admin.domain.AddClientRoleToUserCommand;
import be.jidoka.jdk.keycloak.admin.domain.AddClientRoleToUserCommandBuilder;
import be.jidoka.jdk.keycloak.admin.domain.Client;
import be.jidoka.jdk.keycloak.admin.domain.CreateClientRoleBuilder;
import be.jidoka.jdk.keycloak.admin.domain.CreateUserCommandBuilder;
import be.jidoka.jdk.keycloak.admin.domain.SearchUserByClientRoleRequest;
import be.jidoka.jdk.keycloak.admin.domain.SearchUserByClientRoleRequestBuilder;
import be.jidoka.jdk.keycloak.admin.domain.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static be.jidoka.jdk.keycloak.admin.domain.PublicClientRequestFixture.aPublicClientRequest;
import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KeycloakClientAdminServiceSearchUsersByClientRoleTest extends IntegrationTest {

	public static final String ANOTHER_ROLE_NAME = "ANOTHER_TEST_MANAGER";
	public static final String TEST_APP_ROLE_NAME = "TEST_MANAGER";
	public static final String TEST_APP_CLIENT_ID = "ec57c160-51f3-48dd-81f8-49abcc070fd5";

	@Autowired
	private KeycloakUserAdminService keycloakUserAdminService;

	@Autowired
	private KeycloakClientAdminService keycloakClientAdminService;

	@Autowired
	private UsersResource usersResource;

	private String clientId;
	private final Set<String> userIdsWithImplicitRole = new HashSet<>();
	private final Set<String> userIdsWithExplicitRole = new HashSet<>();

	@BeforeAll
	void setUpAll() {
		keycloakClientAdminService.createPublicClient(aPublicClientRequest);
		Optional<Client> client = keycloakClientAdminService.getClient(aPublicClientRequest.getClientId());
		clientId = client.get().getId();
		keycloakClientAdminService.createClientRole(CreateClientRoleBuilder.builder()
				                                            .clientContainerId(clientId)
				                                            .roleName(ANOTHER_ROLE_NAME)
				                                            .build());

		createRandomUsers();
	}

	@Test
	void returnsUsersWithExplicitRole() {
		SearchUserByClientRoleRequest searchRequest = SearchUserByClientRoleRequestBuilder.builder().clientId(clientId).roleName(ANOTHER_ROLE_NAME).build();

		Set<User> actual = keycloakUserAdminService.searchUsersByClientRole(searchRequest);
		assertThat(actual).hasSameSizeAs(userIdsWithExplicitRole);
		assertThat(actual).extracting("id").containsExactlyInAnyOrderElementsOf(userIdsWithExplicitRole);
	}

	@Test
	void returnsUsersWithImplicitRole() {
		SearchUserByClientRoleRequest searchRequest = SearchUserByClientRoleRequestBuilder.builder().clientId(TEST_APP_CLIENT_ID).roleName(TEST_APP_ROLE_NAME).build();

		Set<User> actual = keycloakUserAdminService.searchUsersByClientRole(searchRequest);
		assertThat(actual).hasSameSizeAs(userIdsWithImplicitRole);
		assertThat(actual).extracting("id").containsExactlyInAnyOrderElementsOf(userIdsWithImplicitRole);
	}

	private void createRandomUsers() {
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
				AddClientRoleToUserCommand request = AddClientRoleToUserCommandBuilder.builder().clientId(clientId).roleName(ANOTHER_ROLE_NAME).userId(userId).build();

				keycloakUserAdminService.addClientRoleToUser(request);
				userIdsWithExplicitRole.add(userId);
			} else {
				UserResource userResource = usersResource.get(userId);
				userResource.joinGroup("8c531922-fe67-4183-b2a3-50e79a310d1a");
				userIdsWithImplicitRole.add(userId);
			}
		}
	}
}
