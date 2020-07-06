package be.jidoka.jdk.keycloak.admin.service;

import be.jidoka.jdk.keycloak.admin.IntegrationTest;
import be.jidoka.jdk.keycloak.admin.domain.CreateUserCommand;
import be.jidoka.jdk.keycloak.admin.domain.UpdateUserCommandBuilder;
import be.jidoka.jdk.keycloak.admin.domain.UserAction;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static be.jidoka.jdk.keycloak.admin.domain.CreateUserCommandFixture.bertenBoedhoe;
import static be.jidoka.jdk.keycloak.admin.domain.CreateUserCommandFixture.davitaOttervanger;
import static be.jidoka.jdk.keycloak.admin.domain.UserAction.UPDATE_PASSWORD;
import static be.jidoka.jdk.keycloak.admin.domain.UserAction.VERIFY_EMAIL;
import static be.jidoka.jdk.keycloak.admin.service.UserResourceAssertions.assertUserAttribute;
import static be.jidoka.jdk.keycloak.admin.service.UserResourceAssertions.assertProfilePicture;
import static be.jidoka.jdk.keycloak.admin.service.UserResourceAssertions.assertRequiredActions;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

class KeycloakUserAdminServiceUpdateUserTest extends IntegrationTest {

	@Autowired
	private KeycloakUserAdminService keycloakUserAdminService;

	@Autowired
	private UsersResource usersResource;

	@Test
	void resetAttributesAndRequiredActionsUpOnEmptyRequest() {
		CreateUserCommand createUser = bertenBoedhoe();
		String userId = keycloakUserAdminService.createUser(createUser);
		UserRepresentation user = usersResource.get(userId).toRepresentation();

		assertUserExistsMatching(user, userId, createUser);
		assertProfilePicture(user, "http://localhost/api/persons/bertan_boedhoe.thumbnail.png");
		assertUserAttribute(user, "organisationId", "2");
		assertRequiredActions(user, Set.of(UPDATE_PASSWORD, VERIFY_EMAIL));

		UpdateUserCommandBuilder updateUser = UpdateUserCommandBuilder.builder()
				.userId(userId)
				.build();

		keycloakUserAdminService.updateUser(updateUser);

		UserRepresentation updatedUser = usersResource.get(userId).toRepresentation();
		assertThat(updatedUser.getId()).isNotNull().isEqualTo(userId);
		assertThat(updatedUser.getUsername()).isNotNull().isEqualTo(createUser.getUsername());
		assertThat(updatedUser.getFirstName()).isNotNull().isEqualTo(createUser.getFirstName());
		assertThat(updatedUser.getLastName()).isNotNull().isEqualTo(createUser.getLastName());
		assertThat(updatedUser.getEmail()).isNotNull().isEqualTo(createUser.getEmail());
		assertThat(updatedUser.isEnabled()).isNotNull().isEqualTo(createUser.isEnabled());
		assertThat(updatedUser.getClientRoles()).isNull();
		assertThat(updatedUser.getAttributes()).isNull();
		assertThat(updatedUser.getRequiredActions()).isEmpty();
	}

	@Test
	void updatesEverything() {
		CreateUserCommand createUser = davitaOttervanger();
		String userId = keycloakUserAdminService.createUser(createUser);
		UserRepresentation user = usersResource.get(userId).toRepresentation();

		assertUserExistsMatching(user, userId, createUser);
		assertUserAttribute(user, "tenantId", "10001");
		assertThat(user.getRequiredActions()).isEmpty();

		UpdateUserCommandBuilder updateUser = UpdateUserCommandBuilder.builder()
				.userId(userId)
				.firstName("Aafke")
				.lastName("Borrenbergs")
				.username("aafke.borrenbergs")
				.email("aafke.borrenbergs@hotmail.com")
				.enabled(false)
				.pictureUrl("http://localhost/api/persons/aafke_borrenbergs.thumbnail.png")
				.personalData(singletonMap("organisationId", singletonList("2")))
				.requiredUserActions(Set.of(UserAction.values()))
				.build();

		keycloakUserAdminService.updateUser(updateUser);

		UserRepresentation updatedUser = usersResource.get(userId).toRepresentation();
		assertThat(updatedUser.getId()).isNotNull().isEqualTo(userId);
		assertThat(updatedUser.getUsername()).isNotNull().isEqualTo(updateUser.getUsername().orElse(null));
		assertThat(updatedUser.getFirstName()).isNotNull().isEqualTo(updateUser.getFirstName().orElse(null));
		assertThat(updatedUser.getLastName()).isNotNull().isEqualTo(updateUser.getLastName().orElse(null));
		assertThat(updatedUser.getEmail()).isNotNull().isEqualTo(updateUser.getEmail().orElse(null));
		assertThat(updatedUser.isEnabled()).isNotNull().isEqualTo(updateUser.getEnabled().orElse(null));
		assertThat(updatedUser.getClientRoles()).isNull();
		assertProfilePicture(updatedUser, "http://localhost/api/persons/aafke_borrenbergs.thumbnail.png");
		assertUserAttribute(updatedUser, "organisationId", "2");
		assertRequiredActions(updatedUser, Set.of(UserAction.values()));
	}

	private void assertUserExistsMatching(UserRepresentation user, String userId, CreateUserCommand createUser) {
		assertThat(user.getId()).isNotNull().isEqualTo(userId);
		assertThat(user.getUsername()).isNotNull().isEqualTo(createUser.getUsername());
		assertThat(user.getFirstName()).isNotNull().isEqualTo(createUser.getFirstName());
		assertThat(user.getLastName()).isNotNull().isEqualTo(createUser.getLastName());
		assertThat(user.getEmail()).isNotNull().isEqualTo(createUser.getEmail());
		assertThat(user.isEnabled()).isNotNull().isEqualTo(createUser.isEnabled());
		assertThat(user.getClientRoles()).isNull();
	}
}
