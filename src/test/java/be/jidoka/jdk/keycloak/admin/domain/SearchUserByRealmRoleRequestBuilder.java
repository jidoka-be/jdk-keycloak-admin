package be.jidoka.jdk.keycloak.admin.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SearchUserByRealmRoleRequestBuilder implements  SearchUserByRealmRoleRequest{

	private String roleName;
}
