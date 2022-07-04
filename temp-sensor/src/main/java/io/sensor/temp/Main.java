package io.sensor.temp;

import io.sensor.temp.verticles.SensorVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

    public static void main(String[] args) {
        Vertx.clusteredVertx(new VertxOptions())
            .onSuccess(vertx -> {
                deploySensorVerticle(vertx)
                    .onSuccess(s -> {
                        log.info("Sensor Deployed Successfully");
                        log.info("Verticle id: {}", s);
                    })
                    .onFailure(throwable -> {
                        log.error("Sensor Deployment Failed because: {}", throwable.getMessage());
                    });
            })
            .onFailure(failure -> {
                log.error("Something Went Wrong: ", failure);
            });


    }

    private static Future<String> deploySensorVerticle(Vertx vertx) {

        return vertx.deployVerticle(SensorVerticle.class.getName(),
            new DeploymentOptions()
                .setWorker(false)
                .setInstances(1));
    }

//    private static Future<String> deploySensorApiVerticle(Vertx vertx) {
//        return vertx.deployVerticle(SensorApiVerticle.class.getName(),
//            new DeploymentOptions()
//                .setInstances(1)
//                .setWorker(false));
//    }
}
