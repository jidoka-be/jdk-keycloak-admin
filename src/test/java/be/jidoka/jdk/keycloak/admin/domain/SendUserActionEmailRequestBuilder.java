package be.jidoka.jdk.keycloak.admin.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class SendUserActionEmailRequestBuilder implements SendUserActionEmailRequest {

	private String userId;
	private Set<UserAction> userActions;
}
