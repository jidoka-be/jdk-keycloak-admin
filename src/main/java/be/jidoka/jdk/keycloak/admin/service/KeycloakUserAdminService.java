package be.jidoka.jdk.keycloak.admin.service;

import be.jidoka.jdk.keycloak.admin.domain.AddClientRoleToUser;
import be.jidoka.jdk.keycloak.admin.domain.CreateUser;
import be.jidoka.jdk.keycloak.admin.domain.GetUserRequest;
import be.jidoka.jdk.keycloak.admin.domain.GetUsersRequest;
import be.jidoka.jdk.keycloak.admin.domain.HasUserPersonalData;
import be.jidoka.jdk.keycloak.admin.domain.RemoveClientRoleFromUser;
import be.jidoka.jdk.keycloak.admin.domain.SearchUserRequest;
import be.jidoka.jdk.keycloak.admin.domain.SendUserActionEmailRequest;
import be.jidoka.jdk.keycloak.admin.domain.UpdateUser;
import be.jidoka.jdk.keycloak.admin.domain.User;
import be.jidoka.jdk.keycloak.admin.domain.UserAction;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static be.jidoka.jdk.keycloak.admin.domain.User.PICTURE_URL_ATTRIBUTE;
import static java.util.Collections.emptyList;
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
				usersResource::count,
				clientId,
				pageable
		);
	}

	public Page<User> searchUsers(SearchUserRequest request) {
		Pageable pageable = request.getPageable();
		String clientId = request.getClientId().orElse(null);

		return retrieveUsers(
				() -> usersResource.search(request.getSearch(), getPage(pageable), getPageSize(pageable)),
				() -> usersResource.count(request.getSearch()),
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
		UserRepresentation userRepresentation = new UserRepresentation();
		userRepresentation.setEnabled(createUser.isEnabled());
		userRepresentation.setUsername(createUser.getUsername());
		userRepresentation.setFirstName(createUser.getFirstName());
		userRepresentation.setLastName(createUser.getLastName());
		userRepresentation.setEmail(createUser.getEmail());
		userRepresentation.setAttributes(getPersonalData(createUser));
		userRepresentation.setRequiredActions(getActions(createUser.getRequiredUserActions()));

		return getCreatedId(usersResource.create(userRepresentation));
	}

	/**
	 * Does a PATCH of an existing User.
	 * Only following attributes will be overridden:
	 * - pictureUrl
	 * - personalData
	 * - requiredUserActions
	 *
	 * Username can only be updated when enabled in the realm (editUsernameAllowed).
	 * Otherwise this will be silently discarded, no update on this field.
	 */
	public void updateUser(UpdateUser updateUser) {
		UserRepresentation userRepresentation = new UserRepresentation();
		updateUser.getEnabled().ifPresent(userRepresentation::setEnabled);
		updateUser.getUsername().ifPresent(userRepresentation::setUsername);
		updateUser.getFirstName().ifPresent(userRepresentation::setFirstName);
		updateUser.getLastName().ifPresent(userRepresentation::setLastName);
		updateUser.getEmail().ifPresent(userRepresentation::setEmail);
		userRepresentation.setAttributes(getPersonalData(updateUser));
		userRepresentation.setRequiredActions(getActions(updateUser.getRequiredUserActions()));

		UserResource userResource = usersResource.get(updateUser.getUserId());

		userResource.update(userRepresentation);
	}

	/**
	 * Sends emails for the specified user actions to the User.
	 *
	 * @throws javax.ws.rs.InternalServerErrorException when SMTP is not configured or accessible.
	 */
	public void sendUserActionEmails(SendUserActionEmailRequest request) {
		UserResource userResource = usersResource.get(request.getUserId());

		userResource.executeActionsEmail(getActions(request.getUserActions()));
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

	private Page<User> retrieveUsers(Supplier<List<UserRepresentation>> userRepresentations, Supplier<Integer> userCount, String clientId, Pageable pageable) {
		List<User> users = userRepresentations.get()
				.stream()
				.map(userRepresentation -> enhanceWithClientRoles(userRepresentation, clientId))
				.map(userRepresentation -> new User(userRepresentation, clientId))
				.collect(toList());
		Integer total = userCount.get();

		return new PageImpl<>(users, pageable, total);
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

	private Map<String, List<String>> getPersonalData(HasUserPersonalData hasUserPersonalData) {
		Map<String, List<String>> personalData = new HashMap<>();

		if (hasUserPersonalData.getPersonalData() != null) {
			personalData.putAll(hasUserPersonalData.getPersonalData());
		}

		hasUserPersonalData.getPictureUrl()
				.ifPresent(pictureUrl -> personalData.put(PICTURE_URL_ATTRIBUTE, singletonList(pictureUrl)));

		return personalData;
	}

	private List<String> getActions(Set<UserAction> userActions) {
		if (userActions == null) {
			return emptyList();
		}

		return userActions
				.stream()
				.map(UserAction::name)
				.collect(toList());
	}
}
