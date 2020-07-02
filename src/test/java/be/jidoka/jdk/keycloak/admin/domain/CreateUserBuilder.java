package be.jidoka.jdk.keycloak.admin.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class CreateUserBuilder implements CreateUser {

	private String username;
	private String firstName;
	private String lastName;
	private String email;
	private String pictureUrl;
	private Map<String, List<String>> personalData;
}
