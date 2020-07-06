package be.jidoka.jdk.keycloak.admin.service;

import be.jidoka.jdk.keycloak.admin.IntegrationTest;
import be.jidoka.jdk.keycloak.admin.domain.SendUserActionEmailRequest;
import be.jidoka.jdk.keycloak.admin.domain.SendUserActionEmailRequestBuilder;
import be.jidoka.jdk.keycloak.admin.domain.UserAction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static be.jidoka.jdk.keycloak.admin.domain.CreateUserFixture.bertenBoedhoe;
import static org.assertj.core.api.Assertions.assertThatCode;

class KeycloakUserAdminServiceSendUserActionEmailsTest extends IntegrationTest {

	@Autowired
	private KeycloakUserAdminService keycloakUserAdminService;

	private String userId;

	@BeforeEach
	public void setUp() {
		userId = keycloakUserAdminService.createUser(bertenBoedhoe());
		mailhog.start();
	}

	@AfterEach
	void tearDown() {
		mailhog.stop();
	}

	@Test
	void doesNotThrowExceptionWhenSMTPIsConfigured() {
		SendUserActionEmailRequest sendUserActionEmailRequest = SendUserActionEmailRequestBuilder.builder()
				.userId(userId)
				.userActions(Set.of(UserAction.values()))
				.build();

		assertThatCode(() -> keycloakUserAdminService.sendUserActionEmails(sendUserActionEmailRequest))
				.doesNotThrowAnyException();
	}
}
