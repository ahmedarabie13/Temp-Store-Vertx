package io.store.api.handler;

import io.store.db.repo.TemperatureRepo;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GetLast5MinutesDataHandler implements Handler<RoutingContext> {
    private final TemperatureRepo repo;
    public GetLast5MinutesDataHandler(TemperatureRepo repo) {
        this.repo = repo;
    }

    @Override
    public void handle(RoutingContext context) {
        repo.getLast5MinutesData()
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
