package be.jidoka.jdk.keycloak.admin.domain;

import be.jidoka.jdk.keycloak.admin.domain.AddClientRoleToUser;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddClientRoleToUserBuilder implements AddClientRoleToUser {

	String clientId;
	String roleName;
	String userId;
}
