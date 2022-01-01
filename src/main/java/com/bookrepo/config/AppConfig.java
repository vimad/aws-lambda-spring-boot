package com.bookrepo.config;

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

    private static final Map<String, Object> configMap;

    static {
        String configProfile;
        String strategy = AppConfigDetails.getStrategy();
        if (!StringUtils.isNullOrEmpty(strategy) && Constants.APP_CONFIG_READ_STRATEGY_SDK.equals(strategy)) {
            configProfile = getConfigProfileFromSDK();
        } else {
            configProfile = getConfigProfileFromExtension();
        }
        Yaml yaml = new Yaml();
        configMap = yaml.load(configProfile);
    }

    private static String getConfigProfileFromExtension() {
        try {
            String url = String.format("http://localhost:2772/applications/%s/environments/%s/configurations/%s",
                    AppConfigDetails.getApplication(),
                    AppConfigDetails.getEnvironment(),
                    AppConfigDetails.getConfigProfileName());
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
            throw new RuntimeException("Unable to fetch data from app config");
        }
        return "";
    }

    private static String getConfigProfileFromSDK() {
        AppConfigClient client = AppConfigClient.create();
        GetConfigurationResponse configuration = client.getConfiguration(GetConfigurationRequest.builder()
                .application(AppConfigDetails.getApplication())
                .environment(AppConfigDetails.getEnvironment())
                .configuration(AppConfigDetails.getConfigProfileName())
                .clientId(UUID.randomUUID().toString())
                .build());
        return configuration.content().asUtf8String();
    }

    @SuppressWarnings("unchecked")
    public static <T> T getOrDefault(String key, T defaultValue) {
        return (T)configMap.getOrDefault(key, defaultValue);
    }
}
