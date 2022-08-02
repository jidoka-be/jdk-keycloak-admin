package be.jidoka.jdk.keycloak.admin.service;

import be.jidoka.jdk.keycloak.admin.IntegrationTest;
import be.jidoka.jdk.keycloak.admin.domain.SearchUsersRequestBuilder;
import be.jidoka.jdk.keycloak.admin.domain.User;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static be.jidoka.jdk.keycloak.admin.domain.CreateUserCommandFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

class KeycloakUserAdminServiceSearchUsersUnpagedTest extends IntegrationTest {

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
	void returnsUsersMatchingSearchTerm() {
		SearchUsersRequestBuilder searchUsersRequest = SearchUsersRequestBuilder.builder()
				.search("bo")
				.pageable(Pageable.unpaged())
				.build();

		Page<User> users = keycloakUserAdminService.searchUsers(searchUsersRequest);

		assertThat(users.getTotalElements()).isEqualTo(2);
		assertThat(users)
				.hasSize(2)
				.extracting("username", "firstName", "lastName", "email")
				.containsOnly(
						new Tuple("aafke.borrenbergs", "Aafke", "Borrenbergs", "aafke.borrenbergs@hotmail.com"),
						new Tuple("berten.boedhoe", "Berten", "Boedhoe", "berten.boedhoe@gmail.com")
				);
	}
}