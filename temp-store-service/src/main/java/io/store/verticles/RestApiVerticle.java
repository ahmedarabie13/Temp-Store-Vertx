package io.store.verticles;

import io.store.api.TemperatureRestApi;
import io.store.config.ConfigLoader;
import io.store.config.StoreConfig;
import io.store.db.DBPools;
import io.store.db.repo.TemperatureRepo;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestApiVerticle extends AbstractVerticle {
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        ConfigLoader.load(vertx)
            .onSuccess(configs -> log.info("Configs Loaded Successfully"))
            .onFailure(error -> log.error("Something went wrong: ", error))
            .compose(storeConfig ->
                startHttpServer(new TemperatureRepo(DBPools.createPgPool(storeConfig.getDbConfig(), vertx))
                    , storeConfig))
            .onSuccess(server -> {
                log.info("Server Started at Port: {}", server.actualPort());
                startPromise.complete();
            })
            .onFailure(startPromise::fail);
    }

    private Future<HttpServer> startHttpServer(TemperatureRepo repo, StoreConfig storeConfig) {
        var router = Router.router(vertx);

        router.route()
            .handler(BodyHandler.create())
            .failureHandler(handleFailure());
        TemperatureRestApi.attach(router, repo);
        return vertx
            .createHttpServer()
            .requestHandler(router)
            .listen(storeConfig.getServerPort());
    }

    private Handler<RoutingContext> handleFailure() {
        return errorContext -> {
            if (errorContext.response().ended()) {
                return;
            }
            log.error("Route Error:", errorContext.failure());
            errorContext.response()
                .setStatusCode(500)
                .end(new JsonObject().put("message", "Something went wrong :(").toBuffer());
        };
    }
}
