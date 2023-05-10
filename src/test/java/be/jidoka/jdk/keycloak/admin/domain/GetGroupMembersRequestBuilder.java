package be.jidoka.jdk.keycloak.admin.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GetGroupMembersRequestBuilder implements GetGroupMembersRequest {

	private String groupId;
}
