package org.acme.security;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class ApiKeyService {

    @ConfigProperty(name = "quarkus.api-key.value")
    String configuredApiKey;

    public boolean isValid(String apiKey) {
        return apiKey != null && apiKey.equals(configuredApiKey);
    }

    // Método opcional para obter informações associadas a uma API key
    public ApiKeyInfo getApiKeyInfo(String apiKey) {
        if (isValid(apiKey)) {
            return new ApiKeyInfo("default-user", new String[]{"user"
            });
        }
        return null;
    }

    // Classe interna para representar informações da API key
    public static class ApiKeyInfo {
        private final String username;
        private final String[] roles;

        public ApiKeyInfo(String username, String[] roles) {
            this.username = username;
            this.roles = roles;
        }

        public String getUsername() {
            return username;
        }

        public String[] getRoles() {
            return roles;
        }
    }
}