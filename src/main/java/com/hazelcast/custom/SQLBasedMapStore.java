package com.hazelcast.custom;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MapLoaderLifecycleSupport;
import com.hazelcast.core.MapStore;
import java.sql.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

/**
 * @since 20/02/2020
 * @version 1.0
 * @author Benjamin E Ndugga
 */
public class SQLBasedMapStore implements MapStore<String, String>, MapLoaderLifecycleSupport {

    private String key_name, value_name, table_name;
    private PoolingDataSource poolingDataSource;

    @Override
    public void init(HazelcastInstance hi, Properties properties, String mapName) {
        Connection connection = null;
        try {

            //load the database driver
            Class.forName(properties.getProperty("dbdriver"));

            //read properties for this map
            String JDBC_DB_URL = (String) properties.get("dburl");
            String JDBC_USER = (String) properties.get("dbuser");
            String JDBC_PASS = (String) properties.get("dbpass");
            //String dbschema = (String) properties.get("dbschema");

            //fetch the key and value details
            key_name = (String) properties.get("key");
            value_name = (String) properties.get("value");
            //table_name = (dbschema + "." + mapName);
            table_name = mapName;

            GenericObjectPool.Config conPoolCfg = new GenericObjectPool.Config();
            conPoolCfg.maxActive = 15;
            conPoolCfg.maxIdle = 8;
            conPoolCfg.maxWait = 60000;
            conPoolCfg.minIdle = 2;

            ObjectPool connectionPool = new GenericObjectPool(null, conPoolCfg);

            ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(JDBC_DB_URL, JDBC_USER, JDBC_PASS);

            PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true);

            poolingDataSource = new PoolingDataSource(connectionPool);

            connection = poolingDataSource.getConnection();

            DatabaseMetaData databaseMetaData = connection.getMetaData();

            ResultSet resultSet = databaseMetaData.getTables(null, null, mapName, new String[]{"TABLE", "VIEW"});

            boolean tableExists = false;

            while (resultSet.next()) {

                tableExists = true;

                System.out.println(
                        "Table Exists:  " + resultSet.getString("TABLE_CAT")
                        + ", " + resultSet.getString("TABLE_SCHEM")
                        + ", " + resultSet.getString("TABLE_NAME")
                        + ", " + resultSet.getString("TABLE_TYPE")
                        + ", " + resultSet.getString("REMARKS"));
            }

            if (!tableExists) {

                System.out.println("Creating Table: " + mapName + ".");

                //create table using the mapName as the table name
                connection.createStatement().execute("CREATE TABLE " + mapName + "(" + key_name + " VARCHAR(100)," + value_name + " VARCHAR(100), PRIMARY KEY (" + key_name + "))");
            }
            System.out.println("initialising table for this map <" + mapName + "> done...");

        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace(System.err);
            System.exit(1);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace(System.err);
                }
            }
        }
    }

    @Override
    public void destroy() {
        System.out.println("destroy...");
    }

    @Override
    public void store(String key, String value) {
        Connection connection = null;
        try {
            connection = poolingDataSource.getConnection();

            connection.createStatement().executeUpdate(String.format("INSERT INTO %s VALUES ('%s','%s')", table_name, key, value));
            connection.commit();

        } catch (java.sql.SQLIntegrityConstraintViolationException ex) {
            ex.printStackTrace(System.err);
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace(System.err);
            }
        }
    }

    @Override
    public void storeAll(Map<String, String> map) {

        Set<Map.Entry<String, String>> entrySet = map.entrySet();

        entrySet.forEach((entry) -> {
            store(entry.getKey(), entry.getValue());
        });
    }

    @Override
    public void delete(String key) {

        Connection connection = null;
        try {
            connection = poolingDataSource.getConnection();

            connection.createStatement().executeUpdate(String.format("DELETE FROM %s WHERE %s = %s", table_name, key_name, key));
            connection.commit();

        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace(System.err);
            }
        }

    }

    @Override
    public void deleteAll(Collection<String> keys) {

        //alternatively we could fire a 'truncate table' sql command
        keys.forEach((key) -> {
            delete(key);
        });
    }

    @Override
    public String load(String key) {
        Connection connection = null;
        String value = null;
        try {

            connection = poolingDataSource.getConnection();

            ResultSet resultSet = connection.createStatement().executeQuery(String.format("SELECT %s FROM %s WHERE %s = '%s'", value_name, table_name, key_name, key));

            while (resultSet.next()) {
                value = resultSet.getString(1);
            }

            return value;
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
            return value;
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace(System.err);
            }
        }
    }

    @Override
    public Map<String, String> loadAll(Collection<String> keys) {

        Map<String, String> result = new HashMap<>();

        keys.forEach((key) -> {
            result.put(key, load(key));
        });

        return result;
    }

    @Override
    public Iterable<String> loadAllKeys() {
        Connection connection = null;

        String key;
        String value;

        try {
            connection = poolingDataSource.getConnection();

            Map<String, String> result = new HashMap<>();

            ResultSet resultSet = connection.createStatement().executeQuery(String.format("SELECT %s,%s FROM %s ", key_name, value_name, table_name));

            while (resultSet.next()) {
                key = resultSet.getString(1);
                value = resultSet.getString(2);

                result.put(key, value);
            }

            return result.keySet();
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
            return null;
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace(System.out);
            }
        }
    }

}
