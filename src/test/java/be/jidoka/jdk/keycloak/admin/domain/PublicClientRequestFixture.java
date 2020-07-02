package be.jidoka.jdk.keycloak.admin.domain;

import java.util.List;

public final class PublicClientRequestFixture {

	private PublicClientRequestFixture() { }

	public static CreatePublicClient aPublicClientRequest = new CreatePublicClientRequest();

	private static class CreatePublicClientRequest implements CreatePublicClient {

		@Override
		public String getClientId() {
			return "cv-app";
		}

		@Override
		public String getRootUrl() {
			return "http://localhost:4200";
		}

		@Override
		public List<String> getRedirectUris() {
			return List.of("*");
		}

		@Override
		public List<String> getWebOrigins() {
			return List.of("*");
		}
	}
}
