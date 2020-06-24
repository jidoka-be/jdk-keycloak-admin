package be.jidoka.jdk.keycloak.admin.service;

import be.jidoka.jdk.keycloak.admin.domain.AddClientRoleToUser;
import be.jidoka.jdk.keycloak.admin.domain.CreateUser;
import be.jidoka.jdk.keycloak.admin.domain.RemoveClientRoleFromUser;
import be.jidoka.jdk.keycloak.admin.domain.User;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static be.jidoka.jdk.keycloak.admin.domain.User.PICTURE_URL_ATTRIBUTE;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

public class KeycloakUserAdminService {

	public static final String USER_ID_FROM_PATH_REGEX = ".*/([^/]+)$";

	private final UsersResource usersResource;
	private final ClientsResource clientsResource;

	public KeycloakUserAdminService(UsersResource usersResource, ClientsResource clientsResource) {
		this.usersResource = usersResource;
		this.clientsResource = clientsResource;
	}

	public Set<User> getUsers() {
		return usersResource.list().stream()
				.map(userRepresentation -> new User(
						userRepresentation.getId(),
						userRepresentation.getUsername(),
						userRepresentation.getFirstName(),
						userRepresentation.getLastName(),
						userRepresentation.getEmail(),
						userRepresentation.getAttributes()
				))
				.collect(Collectors.toSet());
	}

	public User getUser(String userId, String clientContainerId) {
		UserResource userResource = usersResource.get(userId);
		UserRepresentation userRepresentation = userResource.toRepresentation();
		List<String> roles = userResource.roles()
				.clientLevel(clientContainerId)
				.listEffective()
				.stream()
				.map(RoleRepresentation::getName)
				.collect(Collectors.toList());

		return new User(
				userRepresentation.getId(),
				userRepresentation.getUsername(),
				userRepresentation.getFirstName(),
				userRepresentation.getLastName(),
				userRepresentation.getEmail(),
				userRepresentation.getAttributes(),
				roles);
	}

	public String createUser(CreateUser createUser) {
		UserRepresentation user = new UserRepresentation();
		user.setEnabled(true);
		user.setUsername(createUser.getUsername());
		user.setFirstName(createUser.getFirstname());
		user.setLastName(createUser.getLastName());
		user.setEmail(createUser.getEmail());
		user.setAttributes(singletonMap(PICTURE_URL_ATTRIBUTE, singletonList(createUser.getPictureUrl())));

		Response response = usersResource.create(user);
		String userId = response.getLocation().getPath().replaceAll(USER_ID_FROM_PATH_REGEX, "$1");

		return userId;
	}

	public void addClientRoleToUser(AddClientRoleToUser addClientRoleToUser) {
		RoleRepresentation clientRole = clientsResource.get(addClientRoleToUser.getClientId())
				.roles()
				.get(addClientRoleToUser.getRoleName())
				.toRepresentation();

		usersResource.get(addClientRoleToUser.getUserId())
				.roles()
				.clientLevel(addClientRoleToUser.getClientId())
				.add(singletonList(clientRole));
	}

	public void removeClientRoleFromUser(RemoveClientRoleFromUser removeClientRoleFromUser) {
		RoleRepresentation clientRole = clientsResource.get(removeClientRoleFromUser.getClientId())
				.roles()
				.get(removeClientRoleFromUser.getRoleName())
				.toRepresentation();

		usersResource.get(removeClientRoleFromUser.getUserId())
				.roles()
				.clientLevel(removeClientRoleFromUser.getClientId())
				.remove(singletonList(clientRole));
	}
}
