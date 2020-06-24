package be.jidoka.jdk.keycloak.admin.domain;

public class Client {

	private String id;
	private String clientName;
	private String clientId;

	public Client(String id, String clientName, String clientId) {
		this.id = id;
		this.clientName = clientName;
		this.clientId = clientId;
	}

	public String getId() {
		return id;
	}

	public String getClientName() {
		return clientName;
	}

	public String getClientId() {
		return clientId;
	}
}
