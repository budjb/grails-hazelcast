package org.grails.plugins.hazelcast

import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

/**
 * Service that assists with creating and retrieving Hazelcast instances.
 */
@Slf4j
@CompileStatic
class HazelcastInstanceService {
    /**
     * Creates a new Hazelcast instance with the given configuration.
     *
     * @param config Hazelcast instance configuration.
     * @return The new Hazelcast instance.
     */
    HazelcastInstance createInstance(Config config) {
        return Hazelcast.newHazelcastInstance(config)
    }

    /**
     * Returns the Hazelcast instance associated with the given name.
     *
     * @param name Name of the Hazelcast instance.
     * @return Hazelcast instance associated with the given name.
     * @throws IllegalArgumentException when no instance exists for the given name.
     */
    HazelcastInstance getInstance(String name) throws IllegalArgumentException {
        HazelcastInstance instance = Hazelcast.getHazelcastInstanceByName(name)

        if (!instance) {
            throw new IllegalArgumentException("no Hazelcast instance with name \"$name\" exists")
        }

        return instance
    }

    /**
     * Returns all available Hazelcast instances.
     *
     * @return
     */
    Collection<HazelcastInstance> getInstances() {
        return Hazelcast.getAllHazelcastInstances()
    }

    /**
     * Shuts down a Hazelcast instance.
     *
     * @param name Name of the Hazelcast instance.
     * @throws IllegalArgumentException when no instance exists for the given name.
     */
    void shutdownInstance(String name) throws IllegalArgumentException {
        getInstance(name).shutdown()
    }

    /**
     * Shutdown all Hazelcast instances on <em>this</em> JVM.
     */
    void shutdownAll() {
        Hazelcast.shutdownAll()
    }
}
