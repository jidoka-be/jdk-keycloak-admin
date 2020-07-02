package be.jidoka.jdk.keycloak.admin.service;

import be.jidoka.jdk.keycloak.admin.IntegrationTest;
import be.jidoka.jdk.keycloak.admin.domain.CreateUserBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class KeycloakUserAdminServiceCreateUserTest extends IntegrationTest {

	private static final String FIRST_NAME = "Jeroen";
	private static final String LAST_NAME = "Bastijns";
	private static final String USERNAME = "jeroen.bastijns";
	private static final String PICTURE_URL = "http://localhost:4200/jeroen.png";
	private static final String EMAIL = "jeroen.bastijns@jidoka.be";

	@Autowired
	private KeycloakUserAdminService keycloakUserAdminService;

	@Test
	public void createsTheUser() {
		assertThat(keycloakUserAdminService.getUsers()).isEmpty();

		CreateUserBuilder createUserRequest = CreateUserBuilder.builder()
				.firstname(FIRST_NAME)
				.lastName(LAST_NAME)
				.email(EMAIL)
				.username(USERNAME)
				.pictureUrl(PICTURE_URL)
				.build();
		keycloakUserAdminService.createUser(createUserRequest);

		assertThat(keycloakUserAdminService.getUsers())
				.extracting("username", "firstName", "lastName", "email", "pictureUrl", "clientRoles")
				.contains(tuple(USERNAME, FIRST_NAME, LAST_NAME, EMAIL, PICTURE_URL, Collections.emptyList()));
	}

}