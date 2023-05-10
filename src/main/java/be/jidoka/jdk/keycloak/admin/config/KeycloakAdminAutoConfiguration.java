package be.jidoka.jdk.keycloak.admin.config;

import be.jidoka.jdk.keycloak.admin.service.KeycloakClientAdminService;
import be.jidoka.jdk.keycloak.admin.service.KeycloakGroupAdminService;
import be.jidoka.jdk.keycloak.admin.service.KeycloakUserAdminService;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class KeycloakAdminAutoConfiguration {

	@Value("${keycloak-admin.auth-server-url:http://localhost:8080/auth}")
	private String keycloakAuthServer;

	@Value("${keycloak-admin.realm}")
	private String keycloakRealm;

	@Value("${keycloak-admin.client-id}")
	private String adminClientId;

	@Value("${keycloak-admin.client-secret}")
	private String adminClientSecret;

	@Bean
	@ConditionalOnMissingBean
	public KeycloakClientAdminService keycloakClientAdminService(ClientsResource keycloakClientsResource) {
		return new KeycloakClientAdminService(keycloakClientsResource);
	}

	@Bean
	@ConditionalOnMissingBean
	public KeycloakUserAdminService keycloakUserAdminService(UsersResource keycloakUsersResource, ClientsResource keycloakClientsResource, RolesResource rolesResource, GroupsResource groupsResource) {
		return new KeycloakUserAdminService(keycloakUsersResource, keycloakClientsResource, rolesResource, groupsResource);
	}

	@Bean
	@ConditionalOnMissingBean
	public KeycloakGroupAdminService keycloakGroupAdminService(GroupsResource keycloakGroupsResource) {
		return new KeycloakGroupAdminService(keycloakGroupsResource);
	}

	@Bean
	@ConditionalOnMissingBean
	public Keycloak keycloakClient() {
		return KeycloakBuilder.builder()
				.serverUrl(keycloakAuthServer)
				.realm(keycloakRealm)
				.grantType(OAuth2Constants.CLIENT_CREDENTIALS)
				.clientId(adminClientId)
				.clientSecret(adminClientSecret)
				.build();
	}

	@Bean
	@ConditionalOnMissingBean
	public RealmResource keycloakRealmResource(Keycloak keycloakClient) {
		return keycloakClient.realm(keycloakRealm);
	}

	@Bean
	@ConditionalOnMissingBean
	public UsersResource keycloakUsersResource(RealmResource keycloakRealmResource) {
		return keycloakRealmResource.users();
	}

	@Bean
	@ConditionalOnMissingBean
	public ClientsResource keycloakClientsResource(RealmResource keycloakRealmResource) {
		return keycloakRealmResource.clients();
	}

	@Bean
	@ConditionalOnMissingBean
	public RolesResource keycloakRolesResource(RealmResource keycloakRealmResource) {
		return keycloakRealmResource.roles();
	}

	@Bean
	@ConditionalOnMissingBean
	public GroupsResource keycloakGroupsResource(RealmResource keycloakRealmResource) {
		return keycloakRealmResource.groups();
	}
}
