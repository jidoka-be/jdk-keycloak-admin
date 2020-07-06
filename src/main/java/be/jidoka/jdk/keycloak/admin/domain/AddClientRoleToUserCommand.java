package be.jidoka.jdk.keycloak.admin.domain;

public interface AddClientRoleToUserCommand {

	String getUserId();

	String getClientId();

	String getRoleName();
}
