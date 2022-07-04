package io.store.api.handler;

import io.store.db.repo.TemperatureRepo;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class GetAllDataHandler implements Handler<RoutingContext> {

    private TemperatureRepo repo;

    @Override
    public void handle(RoutingContext context) {
        log.info("Entering handling from {}", GetAllDataHandler.class.getSimpleName());
        repo.getAllData()
            .onSuccess(rows -> {
                var listOfData = new JsonArray();
                rows.forEach(msg -> listOfData.add(msg.toJsonObject()));

                context.response()
                    .putHeader("Content-Type", "application/json")
                    .setStatusCode(200)
                    .end(new JsonObject().put("data", listOfData).encode());
            })
            .onFailure(error -> log.error("Failed due to: ", error));
    }


}
