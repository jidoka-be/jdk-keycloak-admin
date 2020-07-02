package be.jidoka.jdk.keycloak.admin.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddClientRoleToUserBuilder implements AddClientRoleToUser {

	String clientId;
	String roleName;
	String userId;
}
