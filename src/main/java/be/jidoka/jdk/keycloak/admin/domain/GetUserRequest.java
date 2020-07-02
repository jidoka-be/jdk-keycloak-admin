package be.jidoka.jdk.keycloak.admin.domain;

import java.util.Optional;

public interface GetUserRequest {

	String getUserId();

	Optional<String> getClientId();
}
