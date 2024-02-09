package be.jidoka.jdk.keycloak.admin.service;

import be.jidoka.jdk.keycloak.admin.IntegrationTest;
import be.jidoka.jdk.keycloak.admin.domain.GetUserRequestBuilder;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static be.jidoka.jdk.keycloak.admin.domain.CreateUserCommandFixture.aafkeBorrenbergs;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KeycloakUserAdminServiceDeleteUserTest extends IntegrationTest {

	@Autowired
	private KeycloakUserAdminService keycloakUserAdminService;

	private String userId;

	@BeforeEach
	void setUp() {
		userId = keycloakUserAdminService.createUser(aafkeBorrenbergs());
	}

	@Test
	void deletesAUser() {
		GetUserRequestBuilder getUserRequest = GetUserRequestBuilder.builder().userId(userId).build();

		assertThat(keycloakUserAdminService.getUser(getUserRequest)).isNotNull();

		keycloakUserAdminService.deleteUser(userId);

		assertThatThrownBy(() -> keycloakUserAdminService.getUser(getUserRequest))
				.isInstanceOf(NotFoundException.class);
	}

	@Test
	void throwsExceptionWhenUserToDeleteIsNotFound() {
		String randomUserId = UUID.randomUUID().toString();

		assertThatThrownBy(() -> keycloakUserAdminService.deleteUser(randomUserId))
				.isInstanceOf(NotFoundException.class);
	}
}