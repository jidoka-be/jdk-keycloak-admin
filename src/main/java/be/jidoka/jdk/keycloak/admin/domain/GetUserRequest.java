package be.jidoka.jdk.keycloak.admin.domain;

public class GetUserRequest {

	private final String userId;
	private final String clientId;

	public GetUserRequest(String userId, String clientId) {
		this.userId = userId;
		this.clientId = clientId;
	}

	public String getUserId() {
		return userId;
	}

	public String getClientId() {
		return clientId;
	}
}
