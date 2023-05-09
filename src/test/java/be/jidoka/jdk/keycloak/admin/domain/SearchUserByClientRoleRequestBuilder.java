package be.jidoka.jdk.keycloak.admin.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SearchUserByClientRoleRequestBuilder implements SearchUserByClientRoleRequest {

	private String clientId;
	private String roleName;
}
