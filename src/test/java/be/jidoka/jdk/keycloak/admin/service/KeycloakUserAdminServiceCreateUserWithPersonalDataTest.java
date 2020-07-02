package be.jidoka.jdk.keycloak.admin.service;

import be.jidoka.jdk.keycloak.admin.IntegrationTest;
import be.jidoka.jdk.keycloak.admin.domain.CreateUserBuilder;
import be.jidoka.jdk.keycloak.admin.domain.GetUserRequest;
import be.jidoka.jdk.keycloak.admin.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class KeycloakUserAdminServiceCreateUserWithPersonalDataTest extends IntegrationTest {

	private static final String FIRST_NAME = "Boudine";
	private static final String LAST_NAME = "Stiphout";
	private static final String USERNAME = "boudine.stiphout";
	private static final String EMAIL = "boudine.stiphout@gmail.com";

	@Autowired
	private KeycloakUserAdminService keycloakUserAdminService;

	@Test
	public void createsTheUser() {
		String organisationIdPersonalDataKey = "organisationId";
		Map<String, List<String>> personalData = Collections.singletonMap(organisationIdPersonalDataKey, singletonList("10002"));
		CreateUserBuilder createUserRequest = CreateUserBuilder.builder()
				.firstName(FIRST_NAME)
				.lastName(LAST_NAME)
				.email(EMAIL)
				.username(USERNAME)
				.personalData(personalData)
				.build();
		String userId = keycloakUserAdminService.createUser(createUserRequest);

		User user = keycloakUserAdminService.getUser(new GetUserRequest(userId, null));

		assertThat(user.getId()).isNotNull().isEqualTo(userId);
		assertThat(user.getUsername()).isNotNull().isEqualTo(USERNAME);
		assertThat(user.getFirstName()).isNotNull().isEqualTo(FIRST_NAME);
		assertThat(user.getLastName()).isNotNull().isEqualTo(LAST_NAME);
		assertThat(user.getEmail()).isNotNull().isEqualTo(EMAIL);
		assertThat(user.isEnabled()).isTrue();
		assertThat(user.getClientRoles()).isNotNull().isEmpty();
		assertThat(user.getPictureUrl()).isNull();
		assertThat(user.getSingleAttribute(organisationIdPersonalDataKey).map(Long::valueOf)).isPresent().contains(10002L);
	}
}