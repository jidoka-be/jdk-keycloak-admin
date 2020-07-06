package be.jidoka.jdk.keycloak.admin.domain;

import java.util.Set;

public interface CreateUser extends HasUserPersonalData {

	String getUsername();

	String getFirstName();

	String getLastName();

	String getEmail();

	boolean isEnabled();

	Set<UserAction> getRequiredUserActions();
}
