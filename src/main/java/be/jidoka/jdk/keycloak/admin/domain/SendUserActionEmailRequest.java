package be.jidoka.jdk.keycloak.admin.domain;

import java.util.Set;

public interface SendUserActionEmailRequest {

	String getUserId();

	Set<UserAction> getUserActions();
}
