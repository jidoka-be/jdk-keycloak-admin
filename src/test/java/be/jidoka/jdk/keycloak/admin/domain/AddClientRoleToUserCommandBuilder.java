package be.jidoka.jdk.keycloak.admin.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddClientRoleToUserCommandBuilder implements AddClientRoleToUserCommand {

	String clientId;
	String roleName;
	String userId;
}
