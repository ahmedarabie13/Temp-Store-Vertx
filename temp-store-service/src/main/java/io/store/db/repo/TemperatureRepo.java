package io.store.db.repo;

import io.store.model.Message;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.templates.SqlTemplate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Slf4j
public class TemperatureRepo {
    //todo: refactor to factory method pattern or singleton
    private Pool pgPool;

    public Future<RowSet<Row>> persistMsg(JsonObject msg) {
        return pgPool.preparedQuery("INSERT INTO temperature_records (uuid, tstamp, value) VALUES ($1, $2, $3)")
            .execute(Tuple.tuple(List.of(msg.getString("uuid"), OffsetDateTime.parse(msg.getString("timestamp")),
                msg.getDouble("temperature"))));
    }

    public Future<RowSet<Message>> getAllData() {
        log.info("Requesting All Data");
        return SqlTemplate.forQuery(pgPool, "SELECT * from temperature_records")
            .mapTo(Message.class)
            .execute(Collections.emptyMap());
    }

    public Future<RowSet<Message>> getTemperature(String uuid) {
        log.info("Requesting Temps for sensor with id: {}", uuid);
        return SqlTemplate.forQuery(pgPool, "SELECT * from temperature_records where uuid = #{uuid}")
            .mapTo(Message.class)
            .execute(Collections.singletonMap("uuid", uuid));
    }

    public Future<RowSet<Message>> getLast5MinutesData() {
        log.info("Retrieving last 5 minutes temperature data");
        return SqlTemplate.forQuery(pgPool, "SELECT * from temperature_records where tstamp >= #{set_point}")
            .mapTo(Message.class)
            .execute(Collections.singletonMap("set_point", OffsetDateTime.now().minus(Duration.ofMinutes(5))));
    }

}
