package be.jidoka.jdk.keycloak.admin.service;

import be.jidoka.jdk.keycloak.admin.IntegrationTest;
import be.jidoka.jdk.keycloak.admin.domain.AddRealmRoleToUserCommand;
import be.jidoka.jdk.keycloak.admin.domain.AddRealmRoleToUserCommandBuilder;
import be.jidoka.jdk.keycloak.admin.domain.CreateUserCommandBuilder;
import be.jidoka.jdk.keycloak.admin.domain.SearchUserByRealmRoleRequest;
import be.jidoka.jdk.keycloak.admin.domain.SearchUserByRealmRoleRequestBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class KeycloakClientAdminServiceSearchUsersByRealmRoleTest extends IntegrationTest {

	@Autowired
	private KeycloakUserAdminService keycloakUserAdminService;

	@Test
	void returnsUsersWithRole() {
		Set<String> userIds = createRandomUsers(150);

		SearchUserByRealmRoleRequest searchRequest = SearchUserByRealmRoleRequestBuilder.builder().roleName("example").build();

		assertThat(keycloakUserAdminService.searchUsersByRealmRole(searchRequest)).extracting("id").containsExactlyInAnyOrderElementsOf(userIds);
	}

	private Set<String> createRandomUsers(int amount) {
		Set<String> userIds = new HashSet<>();

		for(int i = 0; i < amount; i++) {
			CreateUserCommandBuilder createUserRequest = CreateUserCommandBuilder.builder()
					.firstName("Jan" + i)
					.lastName("Pladijs" + i)
					.email("jan.pladijs" + i + "@jidoka.be")
					.username("jan.pladijs" + i)
					.pictureUrl("http://localhost:4200/janpladijs" + i + ".png")
					.build();

			String userId = keycloakUserAdminService.createUser(createUserRequest);
			userIds.add(userId);

			AddRealmRoleToUserCommand request = AddRealmRoleToUserCommandBuilder.builder().roleName("example").userId(userId).build();

			keycloakUserAdminService.addRealmRoleToUser(request);
		}

		return userIds;
	}
}
