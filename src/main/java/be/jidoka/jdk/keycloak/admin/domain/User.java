package be.jidoka.jdk.keycloak.admin.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;

public class User {

	public static final String PICTURE_URL_ATTRIBUTE = "pictureUrl";

	private final String id;
	private final String username;
	private final String firstName;
	private final String lastName;
	private final String email;
	private final boolean enabled;
	private final Map<String, List<String>> attributes;
	private final Set<String> clientRoles;

	private User(Builder builder) {
		this.id = builder.id;
		this.username = builder.username;
		this.firstName = builder.firstName;
		this.lastName = builder.lastName;
		this.email = builder.email;
		this.enabled = builder.enabled;
		this.attributes = builder.attributes;
		this.clientRoles = builder.clientRoles;
	}

	public String getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public String getPictureUrl() {
		return this.getSingleAttribute(PICTURE_URL_ATTRIBUTE)
				.orElse(null);
	}

	public Set<String> getClientRoles() {
		return unmodifiableSet(clientRoles);
	}

	public Optional<String> getSingleAttribute(String key) {
		if (attributes != null && attributes.containsKey(key)) {
			return Optional.of(attributes.get(key).get(0));
		}

		return Optional.empty();
	}

	@Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	public static final class Builder {

		private String id;
		private String username;
		private String firstName;
		private String lastName;
		private String email;
		private boolean enabled;
		private Map<String, List<String>> attributes;
		private Set<String> clientRoles;

		private Builder() {}

		public static Builder anUserWith() {
			return new Builder();
		}

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder username(String username) {
			this.username = username;
			return this;
		}

		public Builder firstName(String firstName) {
			this.firstName = firstName;
			return this;
		}

		public Builder lastName(String lastName) {
			this.lastName = lastName;
			return this;
		}

		public Builder email(String email) {
			this.email = email;
			return this;
		}

		public Builder enabled(boolean enabled) {
			this.enabled = enabled;
			return this;
		}

		public Builder attributes(Map<String, List<String>> attributes) {
			this.attributes = attributes;
			return this;
		}

		public Builder clientRoles(Set<String> clientRoles) {
			this.clientRoles = clientRoles;
			return this;
		}

		public User build() {
			return new User(this);
		}
	}
}
