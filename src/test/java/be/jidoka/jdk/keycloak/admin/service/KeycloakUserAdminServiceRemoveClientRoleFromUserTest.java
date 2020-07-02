package be.jidoka.jdk.keycloak.admin.service;

import be.jidoka.jdk.keycloak.admin.IntegrationTest;
import be.jidoka.jdk.keycloak.admin.domain.AddClientRoleToUserBuilder;
import be.jidoka.jdk.keycloak.admin.domain.Client;
import be.jidoka.jdk.keycloak.admin.domain.CreateClientRoleBuilder;
import be.jidoka.jdk.keycloak.admin.domain.CreateUserBuilder;
import be.jidoka.jdk.keycloak.admin.domain.GetUserRequest;
import be.jidoka.jdk.keycloak.admin.domain.RemoveClientRoleFromUserBuilder;
import be.jidoka.jdk.keycloak.admin.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static be.jidoka.jdk.keycloak.admin.domain.PublicClientRequestFixture.aPublicClientRequest;
import static org.assertj.core.api.Assertions.assertThat;

class KeycloakUserAdminServiceRemoveClientRoleFromUserTest extends IntegrationTest {

	private static final String FIRST_NAME = "Jeroen";
	private static final String LAST_NAME = "Bastijns";
	private static final String USERNAME = "jeroen.bastijns";
	private static final String PICTURE_URL = "http://localhost:4200/jeroen.png";
	private static final String EMAIL = "jeroen.bastijns@jidoka.be";
	private static final String ROLE_NAME = "employee";

	@Autowired
	private KeycloakClientAdminService keycloakClientAdminService;

	@Autowired
	private KeycloakUserAdminService keycloakUserAdminService;

	private String userId;
	private String clientId;

	@BeforeEach
	public void setUp() {
		keycloakClientAdminService.createPublicClient(aPublicClientRequest);
		Optional<Client> client = keycloakClientAdminService.getClient(aPublicClientRequest.getClientId());
		clientId = client.get().getId();
		keycloakClientAdminService.createClientRole(CreateClientRoleBuilder.builder()
				.clientContainerId(clientId)
				.roleName(ROLE_NAME)
				.build());
		userId = keycloakUserAdminService.createUser(CreateUserBuilder.builder()
				.firstName(FIRST_NAME)
				.lastName(LAST_NAME)
				.email(EMAIL)
				.username(USERNAME)
				.pictureUrl(PICTURE_URL)
				.build());
		AddClientRoleToUserBuilder addClientRoleToUserRequest = AddClientRoleToUserBuilder.builder()
				.userId(userId)
				.clientId(clientId)
				.roleName(ROLE_NAME)
				.build();
		keycloakUserAdminService.addClientRoleToUser(addClientRoleToUserRequest);
	}

	@Test
	public void addsTheRoleToTheUser() {
		GetUserRequest getUserRequest = new GetUserRequest(userId, clientId);

		assertThat(keycloakUserAdminService.getUser(getUserRequest).getClientRoles()).containsExactly(ROLE_NAME);

		RemoveClientRoleFromUserBuilder removeClientRoleFromUserRequest = RemoveClientRoleFromUserBuilder.builder()
				.clientId(clientId)
				.roleName(ROLE_NAME)
				.userId(userId)
				.build();
		keycloakUserAdminService.removeClientRoleFromUser(removeClientRoleFromUserRequest);

		User user = keycloakUserAdminService.getUser(getUserRequest);

		assertThat(user.getClientRoles()).isEmpty();
	}
}