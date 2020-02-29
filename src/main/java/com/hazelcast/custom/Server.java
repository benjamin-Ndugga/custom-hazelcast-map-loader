package com.hazelcast.custom;

import static com.hazelcast.core.Hazelcast.newHazelcastInstance;
import com.hazelcast.core.HazelcastInstance;

/**
 *
 * @author Benjamin E Ndugga
 */
public class Server {

    public static void main(String[] args) {

        HazelcastInstance hazelcastInstance = newHazelcastInstance();
    }

}
