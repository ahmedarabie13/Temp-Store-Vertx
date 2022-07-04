package io.store.api;

import io.store.api.handler.GetAllDataHandler;
import io.store.api.handler.GetLast5MinutesDataHandler;
import io.store.api.handler.GetSensorDataHandler;
import io.store.db.repo.TemperatureRepo;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TemperatureRestApi{

    public static void attach(Router router,final TemperatureRepo repo) {
        router.get("/allData")
            .handler(new GetAllDataHandler(repo));

        router.get("/for/:uuid")
            .handler(new GetSensorDataHandler(repo))
            .failureHandler(context -> {
                context.response()
                    .putHeader("Content-Type", "application/json")
                    .setStatusCode(500)
                    .end();
            });

        router.get("/last5minutes")
            .handler(new GetLast5MinutesDataHandler(repo));
    }
}
