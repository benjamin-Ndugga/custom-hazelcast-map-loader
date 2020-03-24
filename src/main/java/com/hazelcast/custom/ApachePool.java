package com.hazelcast.custom;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public final class ApachePool implements Pool {
    private final PoolingDataSource poolingDataSource;

    public ApachePool(Properties properties) {
        GenericObjectPool.Config conPoolCfg = new GenericObjectPool.Config();
        conPoolCfg.maxActive = 15;
        conPoolCfg.maxIdle = 8;
        conPoolCfg.maxWait = 60000;
        conPoolCfg.minIdle = 2;

        String JDBC_DB_URL = (String) properties.get("dburl");
        String JDBC_USER = (String) properties.get("dbuser");
        String JDBC_PASS = (String) properties.get("dbpass");

        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(JDBC_DB_URL, JDBC_USER, JDBC_PASS);

        ObjectPool connectionPool = new GenericObjectPool(null, conPoolCfg);
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true);

        poolingDataSource = new PoolingDataSource(connectionPool);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return poolingDataSource.getConnection();
    }
}
