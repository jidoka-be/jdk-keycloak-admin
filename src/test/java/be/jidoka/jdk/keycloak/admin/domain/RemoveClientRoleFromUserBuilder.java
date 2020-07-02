package be.jidoka.jdk.keycloak.admin.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RemoveClientRoleFromUserBuilder implements RemoveClientRoleFromUser {

	private String clientId;
	private String roleName;
	private String userId;
}
