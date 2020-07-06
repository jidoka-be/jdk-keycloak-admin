package be.jidoka.jdk.keycloak.admin.service;

import be.jidoka.jdk.keycloak.admin.domain.AddClientRoleToUser;
import be.jidoka.jdk.keycloak.admin.domain.CreateUser;
import be.jidoka.jdk.keycloak.admin.domain.GetUserRequest;
import be.jidoka.jdk.keycloak.admin.domain.GetUsersRequest;
import be.jidoka.jdk.keycloak.admin.domain.RemoveClientRoleFromUser;
import be.jidoka.jdk.keycloak.admin.domain.SearchUserRequest;
import be.jidoka.jdk.keycloak.admin.domain.User;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static be.jidoka.jdk.keycloak.admin.domain.User.PICTURE_URL_ATTRIBUTE;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.keycloak.admin.client.CreatedResponseUtil.getCreatedId;

public class KeycloakUserAdminService {

	private final UsersResource usersResource;
	private final ClientsResource clientsResource;

	public KeycloakUserAdminService(UsersResource usersResource, ClientsResource clientsResource) {
		this.usersResource = usersResource;
		this.clientsResource = clientsResource;
	}

	public Page<User> getUsers(GetUsersRequest request) {
		Pageable pageable = request.getPageable();
		String clientId = request.getClientId().orElse(null);

		return retrieveUsers(
				() -> usersResource.list(getPage(pageable), getPageSize(pageable)),
				clientId,
				pageable
		);
	}

	public Page<User> searchUsers(SearchUserRequest request) {
		Pageable pageable = request.getPageable();
		String clientId = request.getClientId().orElse(null);

		return retrieveUsers(
				() -> usersResource.search(request.getSearch(), getPage(pageable), getPageSize(pageable)),
				clientId,
				pageable
		);
	}

	public User getUser(GetUserRequest request) {
		String clientId = request.getClientId().orElse(null);
		UserResource userResource = usersResource.get(request.getUserId());
		UserRepresentation userRepresentation = enhanceWithClientRoles(userResource.toRepresentation(), clientId);

		return new User(userRepresentation, clientId);
	}

	public String createUser(CreateUser createUser) {
		UserRepresentation user = new UserRepresentation();
		user.setEnabled(true);
		user.setUsername(createUser.getUsername());
		user.setFirstName(createUser.getFirstName());
		user.setLastName(createUser.getLastName());
		user.setEmail(createUser.getEmail());
		user.setAttributes(getPersonalData(createUser));

		Response response = usersResource.create(user);

		return getCreatedId(response);
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

	public void deleteUser(String userId) {
		UserResource user = usersResource.get(userId);

		user.remove();
	}

	private int getPage(Pageable pageable) {
		return pageable.isPaged()
				? (int) pageable.getOffset()
				: 0;
	}

	private int getPageSize(Pageable pageable) {
		return pageable.isPaged()
				? pageable.getPageSize()
				: Integer.MAX_VALUE;
	}

	private Page<User> retrieveUsers(Supplier<List<UserRepresentation>> userRepresentations, String clientId, Pageable pageable) {
		List<User> users = userRepresentations.get()
				.stream()
				.map(userRepresentation -> enhanceWithClientRoles(userRepresentation, clientId))
				.map(userRepresentation -> new User(userRepresentation, clientId))
				.collect(toList());

		return new PageImpl<>(users, pageable, usersResource.count());
	}

	private UserRepresentation enhanceWithClientRoles(UserRepresentation userRepresentation, String clientId) {
		if (StringUtils.isBlank(clientId)) {
			return userRepresentation;
		}

		UserResource userResource = usersResource.get(userRepresentation.getId());
		List<RoleRepresentation> clientRoles = userResource.roles().clientLevel(clientId).listEffective();
		UserRepresentation userWithRoles = userResource.toRepresentation();

		userWithRoles.setClientRoles(
				Map.of(clientId, clientRoles.stream()
						.map(RoleRepresentation::getName)
						.collect(Collectors.toList()))
		);

		return userWithRoles;
	}

	private Map<String, List<String>> getPersonalData(CreateUser createUser) {
		Map<String, List<String>> personalData = new HashMap<>(createUser.getPersonalData());

		createUser.getPictureUrl()
				.ifPresent(pictureUrl -> personalData.put(PICTURE_URL_ATTRIBUTE, singletonList(pictureUrl)));

		return personalData;
	}
}
