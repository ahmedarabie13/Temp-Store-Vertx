package io.sensor.temp.verticles;

import io.sensor.temp.api.handlers.GetTempHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;

import static io.sensor.temp.util.Constants.TEMPERATURE_UPDATES_ADDRESS;

@Slf4j
public class SensorVerticle extends AbstractVerticle {
    private final String uuid = UUID.randomUUID().toString();
    private double temperature = 24.0;
    private final Random random = new Random();

    private static final Integer httpPort = Integer.parseInt(System.getenv().getOrDefault("HTTP_PORT", "9091"));


    @Override
    public void start(Promise<Void> startPromise) {
        vertx.setPeriodic(2000, this::updateTemp);
        startSensorServer(startPromise, httpPort).onSuccess(httpServer -> {
                log.info("Server runs Successfully on port: " + httpServer.actualPort());
                startPromise.complete();
            })
            .onFailure(startPromise::fail);
        ;
        ;
    }

    private void updateTemp(Long id) {
        temperature += (random.nextGaussian() / 2.0);
//        var map = vertx.sharedData()
//            .<String, JsonObject>getAsyncMap("tempData");
//        var data = getData();
//        map.onSuccess(asyncMap -> {
//            asyncMap.put("temp", data);
//        });
        log.info("Temperature Updated: {} and UUID: {}", temperature, uuid);
        vertx.eventBus()
            .publish(TEMPERATURE_UPDATES_ADDRESS, getData());
        log.info("Temperature Updates published to Event Bus at address: {}", TEMPERATURE_UPDATES_ADDRESS);
    }

    public JsonObject getData() {
        return new JsonObject()
            .put("uuid", uuid)
            .put("temperature", temperature)
            .put("timestamp", Instant.now().toString());

    }

    private Future<HttpServer> startSensorServer(Promise<Void> startPromise, Integer port) {
        var router = Router.router(vertx);

        router
            .get("/data")
            .handler(new GetTempHandler(this));

        return vertx.createHttpServer()
            .requestHandler(router)
            .listen(port);
    }

}
