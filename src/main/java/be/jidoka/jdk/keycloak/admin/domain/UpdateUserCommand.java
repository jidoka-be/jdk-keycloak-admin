package be.jidoka.jdk.keycloak.admin.domain;

import java.util.Optional;
import java.util.Set;

public interface UpdateUserCommand extends UserPersonalDataCommand {

	String getUserId();

	Optional<String> getUsername();

	Optional<String> getFirstName();

	Optional<String> getLastName();

	Optional<String> getEmail();

	Optional<Boolean> getEnabled();

	Set<UserAction> getRequiredUserActions();
}
