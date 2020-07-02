package be.jidoka.jdk.keycloak.admin.domain;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CreateUser {

	String getUsername();

	String getFirstName();

	String getLastName();

	String getEmail();

	Optional<String> getPictureUrl();

	Map<String, List<String>> getPersonalData();

}
