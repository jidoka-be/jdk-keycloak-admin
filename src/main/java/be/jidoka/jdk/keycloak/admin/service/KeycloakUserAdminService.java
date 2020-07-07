package be.jidoka.jdk.keycloak.admin.service;

import be.jidoka.jdk.keycloak.admin.domain.AddClientRoleToUserCommand;
import be.jidoka.jdk.keycloak.admin.domain.CreateUserCommand;
import be.jidoka.jdk.keycloak.admin.domain.GetUserRequest;
import be.jidoka.jdk.keycloak.admin.domain.GetUsersRequest;
import be.jidoka.jdk.keycloak.admin.domain.RemoveClientRoleFromUserCommand;
import be.jidoka.jdk.keycloak.admin.domain.SearchUserRequest;
import be.jidoka.jdk.keycloak.admin.domain.SendUserActionEmailRequest;
import be.jidoka.jdk.keycloak.admin.domain.UpdateUserCommand;
import be.jidoka.jdk.keycloak.admin.domain.User;
import be.jidoka.jdk.keycloak.admin.domain.UserAction;
import be.jidoka.jdk.keycloak.admin.domain.UserPersonalDataCommand;
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
import java.util.function.IntSupplier;
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

	public String createUser(CreateUserCommand command) {
		UserRepresentation userRepresentation = new UserRepresentation();
		userRepresentation.setEnabled(command.isEnabled());
		userRepresentation.setUsername(command.getUsername());
		userRepresentation.setFirstName(command.getFirstName());
		userRepresentation.setLastName(command.getLastName());
		userRepresentation.setEmail(command.getEmail());
		userRepresentation.setAttributes(getPersonalData(command));
		userRepresentation.setRequiredActions(getActions(command.getRequiredUserActions()));

		return getCreatedId(usersResource.create(userRepresentation));
	}

	/**
	 * Does a PATCH of an existing User.
	 * Only following attributes will be overridden:
	 * - pictureUrl
	 * - personalData
	 * - requiredUserActions
	 * <p>
	 * Username can only be updated when enabled in the realm (editUsernameAllowed).
	 * Otherwise this will be silently discarded, no update on this field.
	 */
	public void updateUser(UpdateUserCommand command) {
		UserRepresentation userRepresentation = new UserRepresentation();
		command.getEnabled().ifPresent(userRepresentation::setEnabled);
		command.getUsername().ifPresent(userRepresentation::setUsername);
		command.getFirstName().ifPresent(userRepresentation::setFirstName);
		command.getLastName().ifPresent(userRepresentation::setLastName);
		command.getEmail().ifPresent(userRepresentation::setEmail);
		userRepresentation.setAttributes(getPersonalData(command));
		userRepresentation.setRequiredActions(getActions(command.getRequiredUserActions()));

		UserResource userResource = usersResource.get(command.getUserId());

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

	public void addClientRoleToUser(AddClientRoleToUserCommand command) {
		RoleRepresentation clientRole = clientsResource.get(command.getClientId())
				.roles()
				.get(command.getRoleName())
				.toRepresentation();

		usersResource.get(command.getUserId())
				.roles()
				.clientLevel(command.getClientId())
				.add(singletonList(clientRole));
	}

	public void removeClientRoleFromUser(RemoveClientRoleFromUserCommand command) {
		RoleRepresentation clientRole = clientsResource.get(command.getClientId())
				.roles()
				.get(command.getRoleName())
				.toRepresentation();

		usersResource.get(command.getUserId())
				.roles()
				.clientLevel(command.getClientId())
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

	private Page<User> retrieveUsers(Supplier<List<UserRepresentation>> userRepresentations, IntSupplier userCount, String clientId, Pageable pageable) {
		List<User> users = userRepresentations.get()
				.stream()
				.map(userRepresentation -> enhanceWithClientRoles(userRepresentation, clientId))
				.map(userRepresentation -> new User(userRepresentation, clientId))
				.collect(toList());
		int total = userCount.getAsInt();

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

	private Map<String, List<String>> getPersonalData(UserPersonalDataCommand command) {
		Map<String, List<String>> personalData = new HashMap<>();

		if (command.getPersonalData() != null) {
			personalData.putAll(command.getPersonalData());
		}

		command.getPictureUrl()
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
