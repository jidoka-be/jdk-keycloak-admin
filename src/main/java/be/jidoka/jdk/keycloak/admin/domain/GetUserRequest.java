package be.jidoka.jdk.keycloak.admin.domain;

public class GetUserRequest {

	private final String userId;
	private final String clientId;

	private GetUserRequest(String userId, String clientId) {
		this.userId = userId;
		this.clientId = clientId;
	}

	public static GetUserRequest withoutClientRoles(String userId) {
		return new GetUserRequest(userId, null);
	}

	public static GetUserRequest withClientRoles(String userId, String clientId) {
		return new GetUserRequest(userId, clientId);
	}

	public String getUserId() {
		return userId;
	}

	public String getClientId() {
		return clientId;
	}
}
