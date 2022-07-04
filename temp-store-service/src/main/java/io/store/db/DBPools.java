package io.store.db;

import io.store.config.DbConfig;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;

public class DBPools {

    public static Pool createPgPool(DbConfig dbConfig, Vertx vertx) {
        var connectionsOpts = new PgConnectOptions()
            .setHost(dbConfig.getHost())
            .setPort(dbConfig.getPort())
            .setUser(dbConfig.getUser())
            .setPassword(dbConfig.getPassword())
            .setDatabase(dbConfig.getDatabase());
        var poolOpts = new PoolOptions()
            .setMaxSize(4);
        return PgPool.pool(vertx, connectionsOpts, poolOpts);
    }
}
