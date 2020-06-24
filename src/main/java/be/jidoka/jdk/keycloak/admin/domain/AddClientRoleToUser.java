package be.jidoka.jdk.keycloak.admin.domain;

public interface AddClientRoleToUser {

	String getClientId();
	String getRoleName();
	String getUserId();
}
