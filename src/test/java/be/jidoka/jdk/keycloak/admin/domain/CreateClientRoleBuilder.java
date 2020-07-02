package be.jidoka.jdk.keycloak.admin.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateClientRoleBuilder implements CreateClientRole {

	private String clientContainerId;
	private String roleName;
}
