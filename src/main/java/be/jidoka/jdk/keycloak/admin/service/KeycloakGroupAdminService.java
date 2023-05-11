package be.jidoka.jdk.keycloak.admin.service;

import be.jidoka.jdk.keycloak.admin.domain.GetGroupMembersRequest;
import be.jidoka.jdk.keycloak.admin.domain.User;
import org.keycloak.admin.client.resource.GroupsResource;

import java.util.Set;
import java.util.stream.Collectors;

public class KeycloakGroupAdminService {

	private final GroupsResource groupsResource;

	public KeycloakGroupAdminService(GroupsResource groupsResource) {
		this.groupsResource = groupsResource;
	}

	public Set<User> getMembers(GetGroupMembersRequest request) {
		return groupsResource.group(request.getGroupId()).members().stream()
				.map(userRepresentation -> new User(userRepresentation, null))
				.collect(Collectors.toSet());
	}
}
