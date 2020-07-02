package be.jidoka.jdk.keycloak.admin.service;

import be.jidoka.jdk.keycloak.admin.domain.User;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptySet;

class UserFactory {

	public User create(UserRepresentation userRepresentation, String clientId) {
		return User.Builder.anUserWith()
				.id(userRepresentation.getId())
				.username(userRepresentation.getUsername())
				.firstName(userRepresentation.getFirstName())
				.lastName(userRepresentation.getLastName())
				.email(userRepresentation.getEmail())
				.enabled(userRepresentation.isEnabled())
				.attributes(userRepresentation.getAttributes())
				.clientRoles(getClientRoles(userRepresentation.getClientRoles(), clientId))
				.build();
	}

	private Set<String> getClientRoles(Map<String, List<String>> clientRoles, String clientId) {
		if (StringUtils.isBlank(clientId)) {
			return emptySet();
		}

		return new HashSet<>(clientRoles.get(clientId));
	}
}
