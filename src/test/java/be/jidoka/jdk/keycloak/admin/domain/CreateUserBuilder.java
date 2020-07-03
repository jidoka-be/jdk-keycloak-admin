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
public class CreateUserBuilder implements CreateUser {

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
	public Optional<String> getPictureUrl() {
		return Optional.ofNullable(pictureUrl);
	}
}
