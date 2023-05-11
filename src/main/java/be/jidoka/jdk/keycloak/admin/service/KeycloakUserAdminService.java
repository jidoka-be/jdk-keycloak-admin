package be.jidoka.jdk.keycloak.admin.service;

import be.jidoka.jdk.keycloak.admin.domain.AddClientRoleToUserCommand;
import be.jidoka.jdk.keycloak.admin.domain.AddRealmRoleToUserCommand;
import be.jidoka.jdk.keycloak.admin.domain.CreateUserCommand;
import be.jidoka.jdk.keycloak.admin.domain.GetUserRequest;
import be.jidoka.jdk.keycloak.admin.domain.GetUsersRequest;
import be.jidoka.jdk.keycloak.admin.domain.RemoveClientRoleFromUserCommand;
import be.jidoka.jdk.keycloak.admin.domain.SearchUserByClientRoleRequest;
import be.jidoka.jdk.keycloak.admin.domain.SearchUserByRealmRoleRequest;
import be.jidoka.jdk.keycloak.admin.domain.SearchUserRequest;
import be.jidoka.jdk.keycloak.admin.domain.SendUserActionEmailRequest;
import be.jidoka.jdk.keycloak.admin.domain.UpdateUserCommand;
import be.jidoka.jdk.keycloak.admin.domain.User;
import be.jidoka.jdk.keycloak.admin.domain.UserAction;
import be.jidoka.jdk.keycloak.admin.domain.UserPersonalDataCommand;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static be.jidoka.jdk.keycloak.admin.domain.User.PICTURE_URL_ATTRIBUTE;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.keycloak.admin.client.CreatedResponseUtil.getCreatedId;

public class KeycloakUserAdminService {

	private final UsersResource usersResource;
	private final ClientsResource clientsResource;
	private final RolesResource rolesResource;
	private final GroupsResource groupsResource;

	public KeycloakUserAdminService(UsersResource usersResource, ClientsResource clientsResource, RolesResource rolesResource, GroupsResource groupsResource) {
		this.usersResource = usersResource;
		this.clientsResource = clientsResource;
		this.rolesResource = rolesResource;
		this.groupsResource = groupsResource;
	}

	public Page<User> getUsers(GetUsersRequest request) {
		var pageable = request.getPageable();
		var clientId = request.getClientId().orElse(null);

		return retrieveUsers(
				() -> usersResource.list(getPage(pageable), getPageSize(pageable)),
				usersResource::count,
				clientId,
				pageable
		);
	}

	public Page<User> searchUsers(SearchUserRequest request) {
		var pageable = request.getPageable();
		var clientId = request.getClientId().orElse(null);

		return retrieveUsers(
				() -> usersResource.search(request.getSearch(), getPage(pageable), getPageSize(pageable)),
				() -> usersResource.count(request.getSearch()),
				clientId,
				pageable
		);
	}

	public Set<User> searchUsersByClientRole(SearchUserByClientRoleRequest request) {
		return Stream.concat(
						getUserRepresentationsWithExplicitRole(request),
						getUserRepresentationsWithImplicitRole(request)
				)
				.map(user -> enhanceWithClientRoles(user, request.getClientId()))
				.map(user -> new User(user, request.getClientId()))
				.collect(Collectors.toSet());
	}

	private Stream<UserRepresentation> getUserRepresentationsWithExplicitRole(SearchUserByClientRoleRequest request) {
		var clientId = request.getClientId();
		var roleName = request.getRoleName();
		var clientRoles = clientsResource.get(clientId).roles();

		if (clientRoles.list().stream().anyMatch(roleRepresentation -> roleRepresentation.getName().equals(roleName))) {
			return clientRoles.get(roleName)
					.getRoleUserMembers()
					.stream();
		}

		return Stream.empty();
	}

	private Stream<UserRepresentation> getUserRepresentationsWithImplicitRole(SearchUserByClientRoleRequest request) {
		return groupsResource.groups()
				.stream()
				.map(groupRepresentation -> enhanceWithClientRoles(groupRepresentation, request.getClientId()))
				.filter(groupRepresentation -> groupRepresentation.getClientRoles().get(request.getClientId()).contains(request.getRoleName()))
				.toList().stream()
				.map(groupRepresentation -> groupsResource.group(groupRepresentation.getId()))
				.map(GroupResource::members)
				.flatMap(Collection::stream);
	}

	public Set<User> searchUsersByRealmRole(SearchUserByRealmRoleRequest request) {
		var page = 0;
		var pageSize = 100;
		int currentPageSize;
		var roleUserMembers = new HashSet<UserRepresentation>();

		do {
			Pageable pageable = PageRequest.of(page, pageSize);
			Set<UserRepresentation> currentPage = rolesResource.get(request.getRoleName()).getRoleUserMembers(getPage(pageable), getPageSize(pageable));
			roleUserMembers.addAll(currentPage);
			currentPageSize = currentPage.size();
			page++;
		} while (currentPageSize == pageSize);

		return roleUserMembers.stream()
				.map(this::enhanceWithRealmRoles)
				.map(roleUserMember -> new User(roleUserMember, null))
				.collect(Collectors.toSet());
	}

	public User getUser(GetUserRequest request) {
		var clientId = request.getClientId().orElse(null);
		var userResource = usersResource.get(request.getUserId());
		var userRepresentation = enhanceWithClientRoles(userResource.toRepresentation(), clientId);

		return new User(userRepresentation, clientId);
	}

	public String createUser(CreateUserCommand command) {
		var userRepresentation = new UserRepresentation();
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
		var userRepresentation = new UserRepresentation();
		command.getEnabled().ifPresent(userRepresentation::setEnabled);
		command.getUsername().ifPresent(userRepresentation::setUsername);
		command.getFirstName().ifPresent(userRepresentation::setFirstName);
		command.getLastName().ifPresent(userRepresentation::setLastName);
		command.getEmail().ifPresent(userRepresentation::setEmail);
		userRepresentation.setAttributes(getPersonalData(command));
		userRepresentation.setRequiredActions(getActions(command.getRequiredUserActions()));

		var userResource = usersResource.get(command.getUserId());

		userResource.update(userRepresentation);
	}

	/**
	 * Sends emails for the specified user actions to the User.
	 *
	 * @throws javax.ws.rs.InternalServerErrorException when SMTP is not configured or accessible.
	 */
	public void sendUserActionEmails(SendUserActionEmailRequest request) {
		var userResource = usersResource.get(request.getUserId());

		userResource.executeActionsEmail(getActions(request.getUserActions()));
	}

	public void addRealmRoleToUser(AddRealmRoleToUserCommand command) {
		var realmRole = rolesResource.get(command.getRoleName())
				.toRepresentation();
		usersResource.get(command.getUserId()).roles().realmLevel().add(singletonList(realmRole));
	}

	public void addClientRoleToUser(AddClientRoleToUserCommand command) {
		var clientRole = clientsResource.get(command.getClientId())
				.roles()
				.get(command.getRoleName())
				.toRepresentation();

		usersResource.get(command.getUserId())
				.roles()
				.clientLevel(command.getClientId())
				.add(singletonList(clientRole));
	}

	public void removeClientRoleFromUser(RemoveClientRoleFromUserCommand command) {
		var clientRole = clientsResource.get(command.getClientId())
				.roles()
				.get(command.getRoleName())
				.toRepresentation();

		usersResource.get(command.getUserId())
				.roles()
				.clientLevel(command.getClientId())
				.remove(singletonList(clientRole));
	}

	public void deleteUser(String userId) {
		var user = usersResource.get(userId);

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
		var users = userRepresentations.get()
				.stream()
				.map(userRepresentation -> enhanceWithClientRoles(userRepresentation, clientId))
				.map(userRepresentation -> new User(userRepresentation, clientId))
				.toList();
		int total = userCount.getAsInt();

		return new PageImpl<>(users, pageable, total);
	}

	private UserRepresentation enhanceWithRealmRoles(UserRepresentation userRepresentation) {
		var userResource = usersResource.get(userRepresentation.getId());
		var roleRepresentations = userResource.roles().realmLevel().listEffective();
		var userWithRoles = userResource.toRepresentation();

		userWithRoles.setRealmRoles(
				roleRepresentations.stream()
						.map((RoleRepresentation::getName))
						.toList()
		);

		return userWithRoles;
	}


	private UserRepresentation enhanceWithClientRoles(UserRepresentation userRepresentation, String clientId) {
		if (StringUtils.isBlank(clientId)) {
			return userRepresentation;
		}

		var userResource = usersResource.get(userRepresentation.getId());
		var clientRoles = userResource.roles().clientLevel(clientId).listEffective();
		var userWithRoles = userResource.toRepresentation();

		userWithRoles.setClientRoles(
				Map.of(clientId, clientRoles.stream()
						.map(RoleRepresentation::getName)
						.toList())
		);

		return userWithRoles;
	}

	private GroupRepresentation enhanceWithClientRoles(GroupRepresentation groupRepresentation, String clientId) {
		var groupResource = groupsResource.group(groupRepresentation.getId());
		var clientRoles = groupResource.roles().clientLevel(clientId).listEffective();
		var groupWithRoles = groupResource.toRepresentation();

		groupWithRoles.setClientRoles(
				Map.of(clientId, clientRoles.stream()
						.map(RoleRepresentation::getName)
						.toList())
		);

		return groupWithRoles;
	}

	private Map<String, List<String>> getPersonalData(UserPersonalDataCommand command) {
		var personalData = new HashMap<String, List<String>>();

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
				.toList();
	}
}
