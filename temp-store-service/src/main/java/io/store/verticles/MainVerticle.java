package io.store.verticles;

import io.store.config.ConfigLoader;
import io.store.config.StoreConfig;
import io.store.db.DBPools;
import io.store.db.repo.TemperatureRepo;
import io.store.util.Constants;
import io.vertx.core.*;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainVerticle extends AbstractVerticle {
    private StoreConfig storeConfig;
    private TemperatureRepo tempRepo;

    public static void main(String[] args) {
        DatabindCodec.mapper().findAndRegisterModules();
        Vertx.clusteredVertx(new VertxOptions())
            .onSuccess(vertx -> {
                log.info("Clustered Vertx: {}", vertx.isClustered());
            })
            .compose(vertx -> vertx.deployVerticle(new MainVerticle())
                .onSuccess(s -> log.info("{} Deployed Successfully with ID: {}", MainVerticle.class.getSimpleName(), s))
                .onFailure(failure -> log.error("SomeThing Went Wrong: ", failure))
                .compose(s -> Future.succeededFuture(vertx)))
            .compose(vertx -> vertx.deployVerticle(new RestApiVerticle()))
            .onSuccess(s -> log.info("{} Deployed Successfully with ID: {}", RestApiVerticle.class.getSimpleName(), s))
            .onFailure(failure -> log.error("SomeThing Went Wrong: ", failure));
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        ConfigLoader.load(vertx)
            .onSuccess(configs -> {
                log.info("Retrieved Configs: " + configs.toString());
            })
            .onFailure(failure -> {
                log.error("Something went wrong: ", failure);
            })
            .compose(storeConfig -> {
                globalizeConfigs(storeConfig);
                var pgPool = DBPools.createPgPool(storeConfig.getDbConfig(), vertx);
                tempRepo = new TemperatureRepo(pgPool);

                log.info("PgPool Size: {}", pgPool.size());
                log.info("clustered Vertex: {}", vertx.isClustered());
                vertx.executeBlocking(event -> {
                    vertx.eventBus()
                        .consumer(Constants.TEMPERATURE_UPDATES_ADDRESS, this::logTemps);
                });
                return Future.succeededFuture(tempRepo);
            })
            .compose(temperatureRepo -> startHttpServer(temperatureRepo, storeConfig))
            .onSuccess(event -> {
                startPromise.complete();
            });

    }

    private Future<HttpServer> startHttpServer(TemperatureRepo repo, StoreConfig storeConfig) {
        var router = Router.router(vertx);
        return vertx
            .createHttpServer()
            .requestHandler(router)
            .listen(storeConfig.getServerPort());
    }

    private void globalizeConfigs(StoreConfig storeConfig) {
        this.storeConfig = storeConfig;
    }

    private void logTemps(Message<JsonObject> message) {

        JsonObject msg = message.body();
        log.info("Received Message: {}", msg);

        tempRepo.persistMsg(msg)
            .onSuccess(event -> {

                log.info("Insertion Done Successfully");
            })
            .onFailure(event -> log.error("Error: " + event));
    }

}
