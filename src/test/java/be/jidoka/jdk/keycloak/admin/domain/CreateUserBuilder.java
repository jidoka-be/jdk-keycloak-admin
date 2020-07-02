package be.jidoka.jdk.keycloak.admin.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
@Builder
public class CreateUserBuilder implements CreateUser {

	private String username;
	private String firstName;
	private String lastName;
	private String email;
	private String pictureUrl;
	@Builder.Default
	private Map<String, List<String>> personalData = Collections.emptyMap();

	@Override
	public Optional<String> getPictureUrl() {
		return Optional.ofNullable(pictureUrl);
	}
}
