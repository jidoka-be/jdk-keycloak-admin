package be.jidoka.jdk.keycloak.admin.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateUserBuilder implements CreateUser {

	private String username;
	private String firstname;
	private String lastName;
	private String email;
	private String pictureUrl;
}
