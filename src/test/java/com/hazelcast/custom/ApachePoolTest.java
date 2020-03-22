package com.hazelcast.custom;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MySQLContainer;

import static org.junit.Assert.*;

public class ApachePoolTest {

    @Rule
    public final MySQLContainer mysql = new MySQLContainer("mysql:5.5")
            .withUsername("hazelcast")
            .withPassword("pass123");

    @Test
    public void testMapStoreInstantiation() {
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();
        IMap<Integer, String> map = hazelcastInstance.getMap("capitals");
        map.put(0, "foo");

        String s = map.get(0);
        assertEquals("foo", s);
    }

    @Test
    public void testStoreAndLoad() {
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance();
        IMap<Integer, String> map1 = instance1.getMap("capitals");
        map1.put(0, "foo");
        map1.flush();
        instance1.shutdown();

        // shutdown the first Hazelcast instance and start a new one.
        // the new instance is using the same backing mysql store as the
        // first one. Hence it should see the value inserted above.
        HazelcastInstance instance2 = Hazelcast.newHazelcastInstance();
        IMap<Integer, String> map2 = instance2.getMap("capitals");
        String s = map2.get(0);
        assertEquals("foo", s);
    }

}