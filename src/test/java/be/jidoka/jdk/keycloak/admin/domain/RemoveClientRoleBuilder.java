package be.jidoka.jdk.keycloak.admin.domain;

import be.jidoka.jdk.keycloak.admin.domain.RemoveClientRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RemoveClientRoleBuilder implements RemoveClientRole {

	private String clientContainerId;
	private String roleName;
}
