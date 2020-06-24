package be.jidoka.jdk.keycloak.admin.domain;

import be.jidoka.jdk.keycloak.admin.domain.CreateClientRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateClientRoleBuilder implements CreateClientRole {

	private String clientContainerId;
	private String roleName;
}
