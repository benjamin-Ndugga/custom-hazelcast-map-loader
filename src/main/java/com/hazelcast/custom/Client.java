package com.hazelcast.custom;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

/**
 *
 * @author Benjamin E Ndugga
 */
public class Client {

    public static void main(String[] args) {
        HazelcastInstance hazelcastInstance = HazelcastClient.newHazelcastClient();
        IMap<Object, Object> map = hazelcastInstance.getMap("capitals");
        map.put("UGANDA", "KAMPALA");
        System.out.println("returned value from store: " + map.get("UGANDA"));
        hazelcastInstance.shutdown();
    }
}
