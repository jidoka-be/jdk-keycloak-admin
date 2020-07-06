package be.jidoka.jdk.keycloak.admin.service;

import be.jidoka.jdk.keycloak.admin.IntegrationTest;
import be.jidoka.jdk.keycloak.admin.domain.CreateUser;
import be.jidoka.jdk.keycloak.admin.domain.UpdateUserBuilder;
import be.jidoka.jdk.keycloak.admin.domain.UserAction;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Set;

import static be.jidoka.jdk.keycloak.admin.domain.CreateUserFixture.bertenBoedhoe;
import static be.jidoka.jdk.keycloak.admin.domain.CreateUserFixture.davitaOttervanger;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

class KeycloakUserAdminServiceUpdateUserTest extends IntegrationTest {

	@Autowired
	private KeycloakUserAdminService keycloakUserAdminService;

	@Autowired
	private UsersResource usersResource;

	@Test
	void updatesAttributesAndRequiredActionsOnly() {
		CreateUser createUser = bertenBoedhoe();
		String userId = keycloakUserAdminService.createUser(createUser);

		UpdateUserBuilder updateUser = UpdateUserBuilder.builder()
				.userId(userId)
				.build();

		assertUserExistsMatching(userId, createUser);

		keycloakUserAdminService.updateUser(updateUser);

		UserRepresentation user = usersResource.get(userId).toRepresentation();
		assertThat(user.getId()).isNotNull().isEqualTo(userId);
		assertThat(user.getUsername()).isNotNull().isEqualTo(createUser.getUsername());
		assertThat(user.getFirstName()).isNotNull().isEqualTo(createUser.getFirstName());
		assertThat(user.getLastName()).isNotNull().isEqualTo(createUser.getLastName());
		assertThat(user.getEmail()).isNotNull().isEqualTo(createUser.getEmail());
		assertThat(user.isEnabled()).isNotNull().isEqualTo(createUser.isEnabled());
		assertThat(user.getClientRoles()).isNull();
		assertThat(user.getAttributes()).isNull();
		assertThat(user.getRequiredActions()).isEmpty();
	}

	@Test
	void updatesEverything() {
		CreateUser createUser = davitaOttervanger();
		String userId = keycloakUserAdminService.createUser(createUser);

		UpdateUserBuilder updateUser = UpdateUserBuilder.builder()
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

		assertUserExistsMatching(userId, createUser);

		keycloakUserAdminService.updateUser(updateUser);

		UserRepresentation user = usersResource.get(userId).toRepresentation();
		assertThat(user.getId()).isNotNull().isEqualTo(userId);
		assertThat(user.getUsername()).isNotNull().isEqualTo(updateUser.getUsername().orElse(null));
		assertThat(user.getFirstName()).isNotNull().isEqualTo(updateUser.getFirstName().orElse(null));
		assertThat(user.getLastName()).isNotNull().isEqualTo(updateUser.getLastName().orElse(null));
		assertThat(user.getEmail()).isNotNull().isEqualTo(updateUser.getEmail().orElse(null));
		assertThat(user.isEnabled()).isNotNull().isEqualTo(updateUser.getEnabled().orElse(null));
		assertThat(user.getClientRoles()).isNull();
		assertThat(user.getAttributes())
				.hasSize(2)
				.containsOnly(
						Map.entry("pictureUrl", singletonList("http://localhost/api/persons/aafke_borrenbergs.thumbnail.png")),
						Map.entry("organisationId", singletonList("2"))
				);
		assertThat(user.getRequiredActions())
				.hasSize(4)
				.containsOnly(
						"CONFIGURE_TOTP", "UPDATE_PASSWORD", "UPDATE_PROFILE", "VERIFY_EMAIL"
				);
	}

	private void assertUserExistsMatching(String userId, CreateUser createUser) {
		UserRepresentation user = usersResource.get(userId).toRepresentation();
		assertThat(user.getId()).isNotNull().isEqualTo(userId);
		assertThat(user.getUsername()).isNotNull().isEqualTo(createUser.getUsername());
		assertThat(user.getFirstName()).isNotNull().isEqualTo(createUser.getFirstName());
		assertThat(user.getLastName()).isNotNull().isEqualTo(createUser.getLastName());
		assertThat(user.getEmail()).isNotNull().isEqualTo(createUser.getEmail());
		assertThat(user.isEnabled()).isNotNull().isEqualTo(createUser.isEnabled());
		assertThat(user.getClientRoles()).isNull();
		createUser.getPictureUrl().ifPresent(pictureUrl -> assertThat(user.getAttributes()).contains(Map.entry("pictureUrl", singletonList(pictureUrl))));
		createUser.getPersonalData()
				.entrySet()
				.forEach(personalDataEntry -> assertThat(user.getAttributes()).contains(personalDataEntry));
		assertThat(user.getRequiredActions())
				.isNotNull()
				.containsExactlyInAnyOrderElementsOf(createUser.getRequiredUserActions().stream().map(UserAction::name).collect(toList()));
	}
}
