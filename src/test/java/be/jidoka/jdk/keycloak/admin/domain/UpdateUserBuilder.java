package be.jidoka.jdk.keycloak.admin.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Getter
@Builder
public class UpdateUserBuilder implements UpdateUser {

	private String userId;
	private String username;
	private String firstName;
	private String lastName;
	private String email;
	@Builder.Default
	private boolean enabled = true;
	private String pictureUrl;
	@Builder.Default
	private Map<String, List<String>> personalData = Collections.emptyMap();
	@Builder.Default
	private Set<UserAction> requiredUserActions = Collections.emptySet();

	@Override
	public Optional<String> getUsername() {
		return Optional.ofNullable(username);
	}

	@Override
	public Optional<String> getFirstName() {
		return Optional.ofNullable(firstName);
	}

	@Override
	public Optional<String> getLastName() {
		return Optional.ofNullable(lastName);
	}

	@Override
	public Optional<String> getEmail() {
		return Optional.ofNullable(email);
	}

	@Override
	public Optional<Boolean> getEnabled() {
		return Optional.ofNullable(enabled);
	}

	@Override
	public Optional<String> getPictureUrl() {
		return Optional.ofNullable(pictureUrl);
	}
}
