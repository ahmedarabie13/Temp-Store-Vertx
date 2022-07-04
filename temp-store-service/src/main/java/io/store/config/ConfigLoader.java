package io.store.config;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class ConfigLoader {


    public static final String CONFIG_FILE = "application.yaml";
    public static final String SERVER_PORT = "/server/port";
    public static final String VERSION = "/version";
    public static final String DB_HOST = "/datasource/url/host";
    public static final String DB_PORT = "/datasource/url/port";
    public static final String DB_DATABASE = "/datasource/url/database";
    public static final String DB_USER = "/datasource/username";
    public static final String DB_PASSWORD = "/datasource/password";
    static final List<String> EXPOSED_ENVIRONMENT_VARIABLES = Arrays.asList(SERVER_PORT, VERSION,
        DB_HOST, DB_PORT, DB_DATABASE, DB_USER, DB_PASSWORD);

    public static Future<StoreConfig> load(Vertx vertx) {
        final var exposedKeys = new JsonArray();
        EXPOSED_ENVIRONMENT_VARIABLES.forEach(exposedKeys::add);
        log.debug("Fetch configuration for {}", exposedKeys.encode());

//        var envStore = new ConfigStoreOptions()
//            .setType("env")
//            .setConfig(new JsonObject().put("keys", exposedKeys));
//
//        var propertyStore = new ConfigStoreOptions()
//            .setType("sys")
//            .setConfig(new JsonObject().put("cache", false));

        var yamlStore = new ConfigStoreOptions()
            .setType("file")
            .setFormat("yaml")
            .setConfig(new JsonObject().put("path", CONFIG_FILE));

        var retriever = ConfigRetriever.create(vertx,
            new ConfigRetrieverOptions()
//                .addStore(propertyStore)
//                .addStore(envStore)
                .addStore(yamlStore)
        );

        return retriever.getConfig().map(StoreConfig::from);
    }

}

