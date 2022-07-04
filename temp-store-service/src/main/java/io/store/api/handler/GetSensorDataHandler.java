package io.store.api.handler;

import io.store.db.repo.TemperatureRepo;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class GetSensorDataHandler implements Handler<RoutingContext> {
    private TemperatureRepo repo;

    @Override
    public void handle(RoutingContext context) {
        var uuid = context.pathParam("uuid");
        repo.getTemperature(uuid)
            .onSuccess(messages -> {
                var listOfData = new JsonArray();
                messages.forEach(msg -> listOfData.add(msg.toJsonObject()));
                if (messages.size() == 0 )
                    throw new RuntimeException("No Sensor with this id is Found");

                context.response()
                    .putHeader("Content-Type", "application/json")
                    .setStatusCode(200)
                    .end(new JsonObject().put("data", listOfData).encode());
            })
            .onFailure(error -> log.error("Error due to: ", error));
    }
}
