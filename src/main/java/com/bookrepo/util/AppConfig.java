package com.bookrepo.util;

import com.amazonaws.util.StringUtils;
import com.bookrepo.Constants;
import org.yaml.snakeyaml.Yaml;
import software.amazon.awssdk.services.appconfig.AppConfigClient;
import software.amazon.awssdk.services.appconfig.model.GetConfigurationRequest;
import software.amazon.awssdk.services.appconfig.model.GetConfigurationResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.UUID;

public class AppConfig {

    private static Map<String, Object> configMap;

    static {
        String application = System.getenv().get(Constants.APP_CONFIG_APPLICATION);
        String environment = System.getenv().get(Constants.APP_CONFIG_ENVIRONMENT);
        String configProfileName = System.getenv().get(Constants.APP_CONFIG_PROFILE);
        String strategy = System.getenv().get(Constants.APP_CONFIG_READ_STRATEGY);
        String configProfile = "";
        if (!StringUtils.isNullOrEmpty(strategy) && Constants.APP_CONFIG_READ_STRATEGY_SDK.equals(strategy)) {
            configProfile = getConfigProfileFromSDK(application, environment, configProfileName);
        } else {
            configProfile = getConfigProfileFromExtension(application, environment, configProfileName);
        }
        Yaml yaml = new Yaml();
        configMap = (Map<String, Object>) yaml.load(configProfile);
    }

    private static String getConfigProfileFromExtension(String application, String environment, String configProfileName) {
        try {
            String url = String.format("http://localhost:2772/applications/%s/environments/%s/configurations/%s", application, environment, configProfileName);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();
            HttpResponse<String> response = HttpClient.newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            }
        } catch (IOException | InterruptedException | URISyntaxException exception) {

        }
        return "";
    }

    private static String getConfigProfileFromSDK(String application, String environment, String configProfileName) {
        AppConfigClient client = AppConfigClient.create();
        GetConfigurationResponse configuration = client.getConfiguration(GetConfigurationRequest.builder()
                .application(application)
                .environment(environment)
                .configuration(configProfileName)
                .clientId(UUID.randomUUID().toString())
                .build());
        return configuration.content().asUtf8String();
    }

    public static <T> T getOrDefault(String key, T defaultValue) {
        return (T)configMap.getOrDefault(key, defaultValue);
    }
}
