package be.jidoka.jdk.keycloak.admin.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RemoveClientRoleBuilder implements RemoveClientRole {

	private String clientContainerId;
	private String roleName;
}
