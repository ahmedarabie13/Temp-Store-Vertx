package io.store.config;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.pointer.JsonPointer;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;


@Builder
@Value
@ToString
@Slf4j
public class StoreConfig {


    int serverPort;
    String version;
    DbConfig dbConfig;

    public static StoreConfig from(final JsonObject config) {
        var serverPort = (Integer) JsonPointer.from(ConfigLoader.SERVER_PORT)
            .queryJson(config);
        if (Objects.isNull(serverPort)) {
            throw new RuntimeException(ConfigLoader.SERVER_PORT + " not configured!");
        }
        final String version = (String) JsonPointer.from(ConfigLoader.VERSION)
            .queryJson(config);
        if (Objects.isNull(version)) {
            throw new RuntimeException("version is not configured in config file!");
        }
        return StoreConfig.builder()
            .serverPort(serverPort)
            .version(version)
            .dbConfig(parseDbConfig(config))
            .build();
    }

    private static DbConfig parseDbConfig(final JsonObject config) {
        return DbConfig.builder()
            .host((String) JsonPointer.from(ConfigLoader.DB_HOST)
                .queryJson(config))
            .port((Integer) JsonPointer.from(ConfigLoader.DB_PORT)
                .queryJson(config))
            .database((String) JsonPointer.from(ConfigLoader.DB_DATABASE)
                .queryJson(config))
            .user((String) JsonPointer.from(ConfigLoader.DB_USER)
                .queryJson(config))
            .password((String) JsonPointer.from(ConfigLoader.DB_PASSWORD)
                .queryJson(config))
            .build();
    }

}
