package be.jidoka.jdk.keycloak.admin.service;

import be.jidoka.jdk.keycloak.admin.IntegrationTest;
import be.jidoka.jdk.keycloak.admin.domain.SendUserActionEmailRequest;
import be.jidoka.jdk.keycloak.admin.domain.SendUserActionEmailRequestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.InternalServerErrorException;
import java.util.Set;

import static be.jidoka.jdk.keycloak.admin.domain.CreateUserFixture.bertenBoedhoe;
import static be.jidoka.jdk.keycloak.admin.domain.UserAction.UPDATE_PASSWORD;
import static be.jidoka.jdk.keycloak.admin.domain.UserAction.VERIFY_EMAIL;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KeycloakUserAdminServiceSendUserActionEmailsTest extends IntegrationTest {

	@Autowired
	private KeycloakUserAdminService keycloakUserAdminService;

	private String userId;

	@BeforeEach
	public void setUp() {
		userId = keycloakUserAdminService.createUser(bertenBoedhoe());
	}

	@Test
	void throwsExceptionWhenSMTPIsNotConfigured() {
		SendUserActionEmailRequest sendUserActionEmailRequest = SendUserActionEmailRequestBuilder.builder()
				.userId(userId)
				.userActions(Set.of(UPDATE_PASSWORD, VERIFY_EMAIL))
				.build();

		assertThatThrownBy(() -> keycloakUserAdminService.sendUserActionEmails(sendUserActionEmailRequest))
				.isInstanceOf(InternalServerErrorException.class);
	}
}
