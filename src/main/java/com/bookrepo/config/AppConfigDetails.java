package com.bookrepo.config;

import com.bookrepo.Constants;

public class AppConfigDetails {
    private static final String application = System.getenv().get(Constants.APP_CONFIG_APPLICATION);
    private static final String environment = System.getenv().get(Constants.APP_CONFIG_ENVIRONMENT);
    private static final String configProfileName = System.getenv().get(Constants.APP_CONFIG_PROFILE);
    private static final String strategy = System.getenv().get(Constants.APP_CONFIG_READ_STRATEGY);

    public static String getApplication() {
        return application;
    }

    public static String getEnvironment() {
        return environment;
    }

    public static String getConfigProfileName() {
        return configProfileName;
    }

    public static String getStrategy() {
        return strategy;
    }
}
