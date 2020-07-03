package be.jidoka.jdk.keycloak.admin.domain;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface CreateUser {

	String getUsername();

	String getFirstName();

	String getLastName();

	String getEmail();

	boolean isEnabled();

	Optional<String> getPictureUrl();

	Map<String, List<String>> getPersonalData();

	Set<UserAction> getRequiredUserActions();
}
