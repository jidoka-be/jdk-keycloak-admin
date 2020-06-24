package be.jidoka.jdk.keycloak.admin.domain;

public interface RemoveClientRoleFromUser {

	String getClientId();
	String getRoleName();
	String getUserId();
}
