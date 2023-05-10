package be.jidoka.jdk.keycloak.admin.service;

import be.jidoka.jdk.keycloak.admin.IntegrationTest;
import be.jidoka.jdk.keycloak.admin.domain.CreateUserCommandBuilder;
import be.jidoka.jdk.keycloak.admin.domain.GetGroupMembersRequest;
import be.jidoka.jdk.keycloak.admin.domain.GetGroupMembersRequestBuilder;
import be.jidoka.jdk.keycloak.admin.domain.User;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class KeycloakGroupAdminServiceGetMembersTest extends IntegrationTest {

	public static final String GROUP_ID = "8c531922-fe67-4183-b2a3-50e79a310d1a";

	@Autowired
	private KeycloakUserAdminService keycloakUserAdminService;

	@Autowired
	private KeycloakGroupAdminService keycloakGroupAdminService;

	@Autowired
	private UsersResource usersResource;

	private final Set<String> userIds = new HashSet<>();

	@Test
	void returnsTheMembersOfAGroup() {
		createRandomUsers();

		GetGroupMembersRequest searchRequest = GetGroupMembersRequestBuilder.builder().groupId(GROUP_ID).build();

		Set<User> actual = keycloakGroupAdminService.getMembers(searchRequest);
		assertThat(actual.size()).isEqualTo(userIds.size());
		assertThat(actual).extracting("id").containsExactlyInAnyOrderElementsOf(userIds);
	}

	private void createRandomUsers() {
		for (int i = 0; i < 75; i++) {
			CreateUserCommandBuilder createUserRequest = CreateUserCommandBuilder.builder()
					.firstName("Jan" + i)
					.lastName("Pladijs" + i)
					.email("jan.pladijs" + i + "@jidoka.be")
					.username("jan.pladijs" + i)
					.pictureUrl("http://localhost:4200/janpladijs" + i + ".png")
					.build();

			String userId = keycloakUserAdminService.createUser(createUserRequest);

			UserResource userResource = usersResource.get(userId);
			userResource.joinGroup(GROUP_ID);
			userIds.add(userId);
		}
	}
}
