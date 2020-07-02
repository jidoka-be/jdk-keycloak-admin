package be.jidoka.jdk.keycloak.admin.domain;

import org.springframework.data.domain.Pageable;

public class GetUsersRequest {

	private final String clientId;
	private final Pageable pageable;

	private GetUsersRequest(String clientId, Pageable pageable) {
		this.clientId = clientId;
		this.pageable = pageable;
	}

	public static GetUsersRequest withoutClientRoles(Pageable pageRequest) {
		return new GetUsersRequest(null, pageRequest);
	}

	public static GetUsersRequest withClientRoles(String clientId, Pageable pageRequest) {
		return new GetUsersRequest(clientId, pageRequest);
	}

	public String getClientId() {
		return clientId;
	}

	public Pageable getPageable() {
		return pageable;
	}
}
