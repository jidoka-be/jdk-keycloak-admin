package be.jidoka.jdk.keycloak.admin.service;

import be.jidoka.jdk.keycloak.admin.IntegrationTest;
import be.jidoka.jdk.keycloak.admin.domain.GetUsersRequestBuilder;
import be.jidoka.jdk.keycloak.admin.domain.User;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static be.jidoka.jdk.keycloak.admin.domain.CreateUserCommandFixture.aafkeBorrenbergs;
import static be.jidoka.jdk.keycloak.admin.domain.CreateUserCommandFixture.davitaOttervanger;
import static be.jidoka.jdk.keycloak.admin.domain.CreateUserCommandFixture.eshelleHerrewijn;
import static org.assertj.core.api.Assertions.assertThat;

class KeycloakUserAdminServiceGetUsersUnpagedTest extends IntegrationTest {

	@Autowired
	private KeycloakUserAdminService keycloakUserAdminService;

	@BeforeEach
	void setUp() {
		keycloakUserAdminService.createUser(aafkeBorrenbergs());
		keycloakUserAdminService.createUser(davitaOttervanger());
		keycloakUserAdminService.createUser(eshelleHerrewijn());
	}

	@Test
	void returnsAllUsers() {
		GetUsersRequestBuilder getUsersRequest = GetUsersRequestBuilder.builder()
				.pageable(Pageable.unpaged())
				.build();

		Page<User> users = keycloakUserAdminService.getUsers(getUsersRequest);

		assertThat(users.getTotalElements()).isEqualTo(3);
		assertThat(users)
				.hasSize(3)
				.extracting("username", "firstName", "lastName", "email")
				.containsOnly(
						new Tuple("aafke.borrenbergs", "Aafke", "Borrenbergs", "aafke.borrenbergs@hotmail.com"),
						new Tuple("davita.ottervange", "Davita", "Ottervanger", "davita.ottervanger@outlook.com"),
						new Tuple("eshelle.herrewijn", "Eshelle", "Herrewijn", "eshelle.herrewijn@gmailc.om")
				);
	}
}