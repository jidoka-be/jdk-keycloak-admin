package be.jidoka.jdk.keycloak.admin.domain;

import be.jidoka.jdk.keycloak.admin.domain.CreateUser;
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
