package be.jidoka.jdk.keycloak.admin.domain;

import org.springframework.data.domain.Pageable;

public class SearchUserRequest {

	private final String search;
	private final String clientId;
	private final Pageable pageable;

	private SearchUserRequest(String search, String clientId, Pageable pageable) {
		this.search = search;
		this.clientId = clientId;
		this.pageable = pageable;
	}

	public static SearchUserRequest withoutClientRoles(String search, Pageable pageRequest) {
		return new SearchUserRequest(search, null, pageRequest);
	}

	public static SearchUserRequest withClientRoles(String search, String clientId, Pageable pageRequest) {
		return new SearchUserRequest(search, clientId, pageRequest);
	}

	public String getSearch() {
		return search;
	}

	public String getClientId() {
		return clientId;
	}

	public Pageable getPageable() {
		return pageable;
	}
}
