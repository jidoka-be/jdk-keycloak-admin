package be.jidoka.jdk.keycloak.admin.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

public class User {

	public static final String PICTURE_URL_ATTRIBUTE = "pictureUrl";

	private final String userId;
	private final String username;
	private final String firstName;
	private final String lastName;
	private final String email;
	private final String pictureUrl;
	private final List<String> clientRoles;

	public User(String userId, String username, String firstName, String lastName, String email, Map<String, List<String>> attributes, List<String> clientRoles) {
		this.userId = userId;
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		if (attributes != null && attributes.containsKey(PICTURE_URL_ATTRIBUTE)) {
			this.pictureUrl = attributes.get(PICTURE_URL_ATTRIBUTE).get(0);
		} else {
			this.pictureUrl = null;
		}
		this.clientRoles = clientRoles;
	}

	public User(String userId, String username, String firstName, String lastName, String email, Map<String, List<String>> attributes) {
		this(userId, username, firstName, lastName, email, attributes, emptyList());
	}

	public String getUserId() {
		return userId;
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

	public String getPictureUrl() {
		return pictureUrl;
	}

	public List<String> getClientRoles() {
		return unmodifiableList(clientRoles);
	}

	@Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
