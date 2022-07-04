package io.store.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Message {
    private String uuid;
    private double value;
    @JsonProperty("tstamp")
    private Instant timestamp;

    public JsonObject toJsonObject() {
        return JsonObject.mapFrom(this);
    }

}
