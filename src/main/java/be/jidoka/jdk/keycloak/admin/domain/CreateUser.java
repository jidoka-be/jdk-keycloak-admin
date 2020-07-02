package be.jidoka.jdk.keycloak.admin.domain;

import java.util.List;
import java.util.Map;

public interface CreateUser {

	String getUsername();

	String getFirstName();

	String getLastName();

	String getEmail();

	String getPictureUrl();

	Map<String, List<String>> getPersonalData();

}
