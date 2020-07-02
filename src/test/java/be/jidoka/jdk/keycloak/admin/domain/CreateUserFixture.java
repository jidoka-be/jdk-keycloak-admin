package be.jidoka.jdk.keycloak.admin.domain;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

public final class CreateUserFixture {

	private CreateUserFixture() { }

	public static CreateUser aafkeBorrenbergs() {
		return CreateUserBuilder.builder()
				.firstName("Aafke")
				.lastName("Borrenbergs")
				.username("aafke.borrenbergs")
				.email("aafke.borrenbergs@hotmail.com")
				.build();
	}

	public static CreateUser bertenBoedhoe() {
		return CreateUserBuilder.builder()
				.firstName("Bertan")
				.lastName("Boedhoe")
				.username("bertan.boedhoe")
				.email("bertan.boedhoe@gmail.com")
				.pictureUrl("http://localhost/api/persons/bertan_boedhoe.thumbnail.png")
				.build();
	}

	public static CreateUser davitaOttervanger() {
		return CreateUserBuilder.builder()
				.firstName("Davita")
				.lastName("Ottervanger")
				.username("davita.ottervange")
				.email("davita.ottervanger@outlook.com")
				.personalData(singletonMap("tenantId", singletonList("10001")))
				.build();
	}

	public static CreateUser eliseStelten() {
		return CreateUserBuilder.builder()
				.firstName("Elise")
				.lastName("Stelten")
				.username("elise.stelten")
				.email("elise.stelten@gmail.clom")
				.build();
	}

	public static CreateUser eshelleHerrewijn() {
		return CreateUserBuilder.builder()
				.firstName("Eshelle")
				.lastName("Herrewijn")
				.username("eshelle.herrewijn")
				.email("eshelle.herrewijn@gmailc.om")
				.build();
	}
}
