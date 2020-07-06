package be.jidoka.jdk.keycloak.admin.domain;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface HasUserPersonalData {

	Optional<String> getPictureUrl();

	Map<String, List<String>> getPersonalData();
}
