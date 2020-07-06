package be.jidoka.jdk.keycloak.admin.service;

import be.jidoka.jdk.keycloak.admin.domain.UserAction;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.Map;
import java.util.Set;

import static be.jidoka.jdk.keycloak.admin.domain.User.PICTURE_URL_ATTRIBUTE;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public final class UserResourceAssertions {

	private UserResourceAssertions() { }

	public static void assertProfilePicture(UserRepresentation user, String expectedProfilePicture) {
		assertUserAttribute(user, PICTURE_URL_ATTRIBUTE, expectedProfilePicture);
	}

	public static void assertUserAttribute(UserRepresentation user, String attributeKey, String expectedAttributeValue) {
		assertThat(user.getAttributes()).contains(Map.entry(attributeKey, singletonList(expectedAttributeValue)));
	}

	public static void assertRequiredActions(UserRepresentation user, Set<UserAction> expectedUserActions) {
		assertThat(user.getRequiredActions())
				.isNotNull()
				.containsExactlyInAnyOrderElementsOf(expectedUserActions.stream().map(UserAction::name).collect(toList()));
	}
}
