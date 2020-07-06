package be.jidoka.jdk.keycloak.admin.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RemoveClientRoleFromUserCommandBuilder implements RemoveClientRoleFromUserCommand {

	private String clientId;
	private String roleName;
	private String userId;
}
