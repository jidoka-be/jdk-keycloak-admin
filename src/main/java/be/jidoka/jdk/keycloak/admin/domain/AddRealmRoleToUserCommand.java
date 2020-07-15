package be.jidoka.jdk.keycloak.admin.domain;

public interface AddRealmRoleToUserCommand {

	String getUserId();

	String getRoleName();
}
