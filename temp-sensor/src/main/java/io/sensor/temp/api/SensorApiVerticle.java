package io.sensor.temp.api;

import io.vertx.core.AbstractVerticle;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SensorApiVerticle extends AbstractVerticle {
//    private static final Integer httpPort = Integer.parseInt(System.getenv().getOrDefault("HTTP_PORT", "9091"));
//
//    @Override
//    public void start(Promise<Void> startPromise) {
//        deploySensorVerticle(vertx)
//            .onFailure(throwable -> {
//                log.error("Sensor Deployment Failed because: {}", throwable.getMessage());
//            })
//            .onSuccess(s -> {
//                log.info("Sensor Deployed Successfully");
//                log.info("verticle id: {}",s);
//            })
//            .compose(s -> startSensorServer(startPromise, httpPort))
//            .onSuccess(httpServer -> {
//                log.info("Server runs Successfully on port: " + httpServer.actualPort());
//                startPromise.complete();
//            })
//            .onFailure(startPromise::fail);;
//    }
//
//    private Future<HttpServer> startSensorServer(Promise<Void> startPromise, Integer httpPort) {
//        var router = Router.router(vertx);
//
//        router
//            .get("/data")
//            .handler(new GetTempHandler());
//
//        return vertx.createHttpServer()
//            .requestHandler(router)
//            .listen(httpPort);
//    }
//
//    private static Future<String> deploySensorVerticle(Vertx vertx) {
//
//        return vertx.deployVerticle(SensorVerticle.class.getName(),
//            new DeploymentOptions()
//                .setWorker(false)
//                .setInstances(2));
//    }

}
