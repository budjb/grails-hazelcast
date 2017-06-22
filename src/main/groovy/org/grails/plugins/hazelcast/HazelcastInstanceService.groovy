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
@Singleton
@CompileStatic
class HazelcastInstanceService {
    /**
     * Hazelcast instance instantiator.
     */
    HazelcastInstanceInstantiator instantiator

    /**
     * Potentially lazy loads the hazelcast instance instantiator.
     *
     * @return
     */
    HazelcastInstanceInstantiator getInstantiator() {
        if (!instantiator) {
            instantiator = HazelcastInstanceInstantiator.getInstance()
        }
        return instantiator
    }

    /**
     * Creates a new Hazelcast instance from the given configuration.
     *
     * @param configuration
     * @return HazelcastInstance
     */
    HazelcastInstance createInstance(Config configuration) {
        return getInstantiator().createInstance(configuration)
    }

    /**
     * This method will be used to create the hazelcast config.
     *
     * @param instanceConfiguration
     * @return Config
     */
    HazelcastInstance createInstance(Map instanceConfiguration) {
        return getInstantiator().createInstance(instanceConfiguration)
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
        Hazelcast.getAllHazelcastInstances()
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
