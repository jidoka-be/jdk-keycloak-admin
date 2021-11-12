package be.jidoka.jdk.keycloak.admin.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AddRealmRoleToUserCommandBuilder implements AddRealmRoleToUserCommand {

	private String userId;
	private String roleName;
}
