package be.jidoka.jdk.keycloak.admin.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Getter
@Builder
public class SearchUsersRequestBuilder implements SearchUserRequest {

	private String search;
	private String clientId;
	private Pageable pageable;

	@Override
	public Optional<String> getClientId() {
		return Optional.ofNullable(clientId);
	}
}
