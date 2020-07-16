package be.jidoka.jdk.keycloak.admin.service;

import be.jidoka.jdk.keycloak.admin.IntegrationTest;
import be.jidoka.jdk.keycloak.admin.domain.AddRealmRoleToUserCommand;
import be.jidoka.jdk.keycloak.admin.domain.AddRealmRoleToUserCommandBuilder;
import be.jidoka.jdk.keycloak.admin.domain.SearchUserByRealmRoleRequest;
import be.jidoka.jdk.keycloak.admin.domain.SearchUserByRealmRoleRequestBuilder;
import be.jidoka.jdk.keycloak.admin.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

import static be.jidoka.jdk.keycloak.admin.domain.CreateUserCommandFixture.aafkeBorrenbergs;
import static org.assertj.core.api.Assertions.assertThat;

class KeycloakUserAdminServiceAddRealmRoleToUserTest extends IntegrationTest {

	@Autowired
	private KeycloakUserAdminService keycloakUserAdminService;

	private String userId;

	@BeforeEach
	public void setUp() {
		userId = keycloakUserAdminService.createUser(aafkeBorrenbergs());
	}

	@Test
	void addsRealmRoleToUser() {
		AddRealmRoleToUserCommand request = AddRealmRoleToUserCommandBuilder.builder().roleName("example").userId(userId).build();

		keycloakUserAdminService.addRealmRoleToUser(request);

		SearchUserByRealmRoleRequest searchUserByRealmRoleRequest = SearchUserByRealmRoleRequestBuilder.builder().roleName("example").build();

		Set<User> actual = keycloakUserAdminService.searchUsersByRealmRole(searchUserByRealmRoleRequest);
		assertThat(actual).hasSize(1);
		assertThat(List.copyOf(actual).get(0).getRealmRoles()).contains("example");
	}
}