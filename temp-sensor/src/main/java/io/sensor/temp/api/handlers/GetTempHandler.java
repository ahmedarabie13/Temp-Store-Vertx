package io.sensor.temp.api.handlers;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.sensor.temp.verticles.SensorVerticle;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class GetTempHandler implements Handler<RoutingContext> {
    private final SensorVerticle sensorVerticle ;

    @Override
    public void handle(RoutingContext context) {
        log.info("Processing a request from {}", context.request().remoteAddress());
        var jsonObject = sensorVerticle.getData();

        context
            .response()
            .putHeader("Content-Type", HttpHeaderValues.APPLICATION_JSON)
            .end(jsonObject.encodePrettily());
    }
}
