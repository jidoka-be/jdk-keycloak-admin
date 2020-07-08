package be.jidoka.jdk.keycloak.admin.service;

import be.jidoka.jdk.keycloak.admin.IntegrationTest;
import be.jidoka.jdk.keycloak.admin.domain.CreateUserCommandBuilder;
import be.jidoka.jdk.keycloak.admin.domain.UserAction;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static be.jidoka.jdk.keycloak.admin.service.UserResourceAssertions.assertRequiredActions;
import static be.jidoka.jdk.keycloak.admin.service.UserResourceAssertions.assertUserAttribute;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class KeycloakUserAdminServiceCreateUserWithPersonalDataTest extends IntegrationTest {

	private static final String FIRST_NAME = "Boudine";
	private static final String LAST_NAME = "Stiphout";
	private static final String USERNAME = "boudine.stiphout";
	private static final String EMAIL = "boudine.stiphout@gmail.com";

	@Autowired
	private KeycloakUserAdminService keycloakUserAdminService;

	@Autowired
	private UsersResource usersResource;

	@Test
	void createsTheUser() {
		String organisationIdPersonalDataKey = "organisationId";
		Map<String, List<String>> personalData = Collections.singletonMap(organisationIdPersonalDataKey, singletonList("10002"));
		CreateUserCommandBuilder createUserRequest = CreateUserCommandBuilder.builder()
				.firstName(FIRST_NAME)
				.lastName(LAST_NAME)
				.email(EMAIL)
				.username(USERNAME)
				.personalData(personalData)
				.enabled(false)
				.requiredUserActions(Set.of(UserAction.values()))
				.build();

		String userId = keycloakUserAdminService.createUser(createUserRequest);

		UserRepresentation user = usersResource.get(userId).toRepresentation();
		assertThat(user.getId()).isNotNull().isEqualTo(userId);
		assertThat(user.getUsername()).isNotNull().isEqualTo(USERNAME);
		assertThat(user.getFirstName()).isNotNull().isEqualTo(FIRST_NAME);
		assertThat(user.getLastName()).isNotNull().isEqualTo(LAST_NAME);
		assertThat(user.getEmail()).isNotNull().isEqualTo(EMAIL);
		assertThat(user.isEnabled()).isFalse();
		assertThat(user.getClientRoles()).isNull();
		assertUserAttribute(user, "organisationId", "10002");
		assertRequiredActions(user, Set.of(UserAction.values()));
	}
}