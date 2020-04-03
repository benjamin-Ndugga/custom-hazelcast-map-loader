package com.hazelcast.custom;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MySQLContainer;

import static org.junit.Assert.*;

public class ApachePoolTest {

    @Rule
    public final MySQLContainer mysql = new MySQLContainer("mysql:5.5")
            .withPassword("pass123")
            .withDatabaseName("mydb");

    private HazelcastInstance instance;

    @Before
    public void setUp() {
        // this mysql.port variable is referenced in hazelcast.xml
        // we need the variable to point to a local port where the mysql running inside the docker exposed.
        System.setProperty("mysql.port", mysql.getFirstMappedPort().toString());

        instance = Hazelcast.newHazelcastInstance();
    }

    @After
    public void tearDown() {
        instance.shutdown();
    }

    @Test
    public void testMapStoreInstantiation() {
        IMap<String, String> map = instance.getMap("capitals");
        map.put("key", "value");

        String s = map.get("key");
        assertEquals("value", s);
    }

    @Test
    public void testStoreAndLoad() {
        IMap<String, String> map1 = instance.getMap("capitals");
        map1.put("key", "value");
        map1.flush();
        instance.shutdown();

        // shutdown the first Hazelcast instance and start a new one.
        // the new instance is using the same backing mysql store as the
        // first one. Hence it should see the value inserted above.
        instance = Hazelcast.newHazelcastInstance();
        IMap<String, String> map2 = instance.getMap("capitals");
        String s = map2.get("key");
        assertEquals("value", s);
    }

}