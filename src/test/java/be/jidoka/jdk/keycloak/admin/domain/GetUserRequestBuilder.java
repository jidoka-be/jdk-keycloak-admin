package be.jidoka.jdk.keycloak.admin.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Getter
@Builder
public class GetUserRequestBuilder implements GetUserRequest {

	private String userId;
	private String clientId;

	@Override
	public Optional<String> getClientId() {
		return Optional.ofNullable(clientId);
	}
}
