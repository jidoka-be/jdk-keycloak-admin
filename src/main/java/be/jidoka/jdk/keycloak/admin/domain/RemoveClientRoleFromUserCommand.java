package be.jidoka.jdk.keycloak.admin.domain;

public interface RemoveClientRoleFromUserCommand {

	String getUserId();

	String getClientId();

	String getRoleName();
}
