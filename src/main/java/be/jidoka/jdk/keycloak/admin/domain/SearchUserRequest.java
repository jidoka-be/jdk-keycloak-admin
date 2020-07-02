package be.jidoka.jdk.keycloak.admin.domain;

import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SearchUserRequest {

	String getSearch();

	Optional<String> getClientId();

	Pageable getPageable();
}
