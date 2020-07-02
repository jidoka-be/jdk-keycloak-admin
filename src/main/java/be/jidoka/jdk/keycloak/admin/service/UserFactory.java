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
		return new User(
				userRepresentation.getId(),
				userRepresentation.getUsername(),
				userRepresentation.getFirstName(),
				userRepresentation.getLastName(),
				userRepresentation.getEmail(),
				userRepresentation.isEnabled(),
				userRepresentation.getAttributes(),
				getRoles(userRepresentation.getClientRoles(), clientId)
		);
	}

	private Set<String> getRoles(Map<String, List<String>> clientRoles, String clientId) {
		if (StringUtils.isBlank(clientId)) {
			return emptySet();
		}

		return new HashSet<>(clientRoles.get(clientId));
	}
}
