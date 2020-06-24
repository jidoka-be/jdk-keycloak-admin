package be.jidoka.jdk.keycloak.admin.service;

import be.jidoka.jdk.keycloak.admin.domain.Client;
import be.jidoka.jdk.keycloak.admin.domain.ClientRole;
import be.jidoka.jdk.keycloak.admin.domain.CreateClientRole;
import be.jidoka.jdk.keycloak.admin.domain.CreatePublicClient;
import be.jidoka.jdk.keycloak.admin.domain.RemoveClientRole;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class KeycloakClientAdminService {

	private final ClientsResource clientsResource;

	public KeycloakClientAdminService(ClientsResource clientsResource) {
		this.clientsResource = clientsResource;
	}

	public Set<Client> getClients() {
		return clientsResource.findAll().stream()
				.map(clientRepresentation -> new Client(clientRepresentation.getId(), clientRepresentation.getName(), clientRepresentation.getClientId()))
				.collect(Collectors.toSet());
	}

	public Optional<Client> getClient(String clientId) {
		return clientsResource.findByClientId(clientId).stream()
				.findFirst()
				.map(clientRepresentation -> new Client(clientRepresentation.getId(), clientRepresentation.getName(), clientRepresentation.getClientId()));
	}

	public Set<ClientRole> getClientRoles(Client client) {
		return clientsResource.get(client.getId()).roles().list().stream()
				.map(roleRepresentation -> new ClientRole(roleRepresentation.getName(), roleRepresentation.getId()))
				.collect(Collectors.toSet());
	}

	public void createPublicClient(CreatePublicClient createPublicClient) {
		ClientRepresentation clientRepresentation = new ClientRepresentation();
		clientRepresentation.setClientId(createPublicClient.getClientId());
		clientRepresentation.setProtocol("openid-connect");
		clientRepresentation.setEnabled(true);
		clientRepresentation.setRootUrl(createPublicClient.getRootUrl());
		clientRepresentation.setPublicClient(true);
		clientRepresentation.setRedirectUris(createPublicClient.getRedirectUris());
		clientRepresentation.setWebOrigins(createPublicClient.getWebOrigins());
		clientRepresentation.setStandardFlowEnabled(true);
		clientRepresentation.setImplicitFlowEnabled(false);
		clientRepresentation.setDirectAccessGrantsEnabled(false);
		clientsResource.create(clientRepresentation);
	}

	public void createClientRole(CreateClientRole createClientRole) {
		RoleRepresentation roleRepresentation = new RoleRepresentation();
		roleRepresentation.setName(createClientRole.getRoleName());
		roleRepresentation.setClientRole(true);
		roleRepresentation.setContainerId(createClientRole.getClientContainerId());
		clientsResource.get(createClientRole.getClientContainerId()).roles().create(roleRepresentation);
	}

	public void removeClientRole(RemoveClientRole removeClientRole) {
		clientsResource.get(removeClientRole.getClientContainerId()).roles()
				.deleteRole(removeClientRole.getRoleName());
	}
}
