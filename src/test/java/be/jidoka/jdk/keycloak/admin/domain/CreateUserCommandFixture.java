package be.jidoka.jdk.keycloak.admin.domain;

import java.util.Set;

import static be.jidoka.jdk.keycloak.admin.domain.UserAction.UPDATE_PASSWORD;
import static be.jidoka.jdk.keycloak.admin.domain.UserAction.VERIFY_EMAIL;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

public final class CreateUserCommandFixture {

	private CreateUserCommandFixture() { }

	public static CreateUserCommand aafkeBorrenbergs() {
		return CreateUserCommandBuilder.builder()
				.firstName("Aafke")
				.lastName("Borrenbergs")
				.username("aafke.borrenbergs")
				.email("aafke.borrenbergs@hotmail.com")
				.build();
	}

	public static CreateUserCommand bertenBoedhoe() {
		return CreateUserCommandBuilder.builder()
				.firstName("Berten")
				.lastName("Boedhoe")
				.username("bertan.boedhoe")
				.email("bertan.boedhoe@gmail.com")
				.pictureUrl("http://localhost/api/persons/bertan_boedhoe.thumbnail.png")
				.personalData(singletonMap("organisationId", singletonList("2")))
				.requiredUserActions(Set.of(UPDATE_PASSWORD, VERIFY_EMAIL))
				.build();
	}

	public static CreateUserCommand davitaOttervanger() {
		return CreateUserCommandBuilder.builder()
				.firstName("Davita")
				.lastName("Ottervanger")
				.username("davita.ottervange")
				.email("davita.ottervanger@outlook.com")
				.personalData(singletonMap("tenantId", singletonList("10001")))
				.build();
	}

	public static CreateUserCommand eliseStelten() {
		return CreateUserCommandBuilder.builder()
				.firstName("Elise")
				.lastName("Stelten")
				.username("elise.stelten")
				.email("elise.stelten@gmail.clom")
				.build();
	}

	public static CreateUserCommand eshelleHerrewijn() {
		return CreateUserCommandBuilder.builder()
				.firstName("Eshelle")
				.lastName("Herrewijn")
				.username("eshelle.herrewijn")
				.email("eshelle.herrewijn@gmailc.om")
				.build();
	}
}
