package be.jidoka.jdk.keycloak.admin.service;

import be.jidoka.jdk.keycloak.admin.IntegrationTest;
import be.jidoka.jdk.keycloak.admin.domain.GetUsersRequestBuilder;
import be.jidoka.jdk.keycloak.admin.domain.User;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static be.jidoka.jdk.keycloak.admin.domain.CreateUserFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

class KeycloakUserAdminServiceGetUsersPagedTest extends IntegrationTest {

	@Autowired
	private KeycloakUserAdminService keycloakUserAdminService;

	@BeforeEach
	void setUp() {
		keycloakUserAdminService.createUser(aafkeBorrenbergs());
		keycloakUserAdminService.createUser(bertenBoedhoe());
		keycloakUserAdminService.createUser(davitaOttervanger());
		keycloakUserAdminService.createUser(eshelleHerrewijn());
		keycloakUserAdminService.createUser(eliseStelten());
	}

	@Test
	void returnsAllUsersOnACertainPage() {
		GetUsersRequestBuilder getUsersRequest = GetUsersRequestBuilder.builder()
				.pageable(PageRequest.of(1, 2))
				.build();

		Page<User> users = keycloakUserAdminService.getUsers(getUsersRequest);

		assertThat(users.getTotalElements()).isEqualTo(5);
		assertThat(users)
				.hasSize(2)
				.extracting("username", "firstName", "lastName", "email")
				.containsOnly(
						new Tuple("davita.ottervange", "Davita", "Ottervanger", "davita.ottervanger@outlook.com"),
						new Tuple("elise.stelten", "Elise", "Stelten", "elise.stelten@gmail.clom")
				);
	}
}