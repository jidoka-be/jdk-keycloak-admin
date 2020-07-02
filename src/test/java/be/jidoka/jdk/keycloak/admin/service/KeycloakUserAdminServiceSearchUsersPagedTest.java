package be.jidoka.jdk.keycloak.admin.service;

import be.jidoka.jdk.keycloak.admin.IntegrationTest;
import be.jidoka.jdk.keycloak.admin.domain.SearchUsersRequestBuilder;
import be.jidoka.jdk.keycloak.admin.domain.User;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static be.jidoka.jdk.keycloak.admin.domain.CreateUserFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

class KeycloakUserAdminServiceSearchUsersPagedTest extends IntegrationTest {

	@Autowired
	private KeycloakUserAdminService keycloakUserAdminService;

	@BeforeEach
	void setUp() {
		keycloakUserAdminService.createUser(aafkeBorrenbergs());
		keycloakUserAdminService.createUser(bertenBoedhoe());
		keycloakUserAdminService.createUser(davitaOttervanger());
		keycloakUserAdminService.createUser(eliseStelten());
		keycloakUserAdminService.createUser(eshelleHerrewijn());
	}

	@Test
	void returnsUsersMatchingSearchTermOnACertainPage() {
		SearchUsersRequestBuilder searchUsersRequest = SearchUsersRequestBuilder.builder()
				.search("rre")
				.pageable(PageRequest.of(1, 1))
				.build();

		Page<User> users = keycloakUserAdminService.searchUsers(searchUsersRequest);

		assertThat(users)
				.hasSize(1)
				.extracting("username", "firstName", "lastName", "email")
				.containsOnly(
						new Tuple("eshelle.herrewijn", "Eshelle", "Herrewijn", "eshelle.herrewijn@gmailc.om")
				);
	}
}