package be.jidoka.jdk.keycloak.admin.domain;

public interface SearchUserByClientRoleRequest {

	String getClientId();

	String getRoleName();
}
