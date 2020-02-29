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

        //connnect to the HZ-Instance  
        HazelcastInstance hazelcastInstance = HazelcastClient.newHazelcastClient();
        IMap<Object, Object> map_country_codes = hazelcastInstance.getMap("country_codes");
        map_country_codes.put("UGANDA", "UG");
        map_country_codes.put("KENYA", "KE");
        map_country_codes.put("TANZANIA", "TZ");
       
        hazelcastInstance.shutdown();

    }

}
