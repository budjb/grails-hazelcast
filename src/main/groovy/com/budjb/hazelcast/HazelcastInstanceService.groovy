package com.budjb.hazelcast

import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.InitializingBean

/**
 * Service that assists with creating and retrieving Hazelcast instances.
 */
@Slf4j
class HazelcastInstanceService implements InitializingBean {
    /**
     * Initial configuration.
     */
    List<Map> initialConfiguration

    /**
     * Constructor.
     *
     * @param initialConfiguration
     */
    HazelcastInstanceService(List<Map> initialConfiguration) {
        this.initialConfiguration = initialConfiguration
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void afterPropertiesSet() throws Exception {
        initialConfiguration?.each { instanceConfig ->
            createInstance(instanceConfig)
        }
    }

    /**
     * Creates a new Hazelcast instance from the given configuration.
     *
     * @param configuration
     * @return HazelcastInstance
     */
    HazelcastInstance createInstance(Config configuration) {
        return Hazelcast.newHazelcastInstance(configuration)
    }

    /**
     * This method will be used to create the hazelcast config.
     *
     * @param instanceConfiguration
     * @return Config
     */
    HazelcastInstance createInstance(Map instanceConfiguration) {
        return createInstance(parseConfig(instanceConfiguration))
    }

    Config parseConfig(Map configuration) {
        Config config = new Config()
        assign(config, configuration)
        return config
    }

    /**
     * Assigns properties contained in the give map to the given object.
     * If keys in the map do not exist, an IllegalArgumentException is thrown.
     *
     * @param object
     * @param map
     */
    protected void assign(Object object, Map<String, ?> map) {
        MetaClass metaClass = object.getMetaClass()

        for (String key : map.keySet()) {
            if (!metaClass.hasProperty(object, key)) {
                throw new IllegalArgumentException("Property '${key}' is not a valid property for class '${object.getClass().getName()}'. Please check your configuration.")
            }

            Object value = map.get(key)

            if (value instanceof Map) {
                assign(metaClass.getProperty(object, key), value)
            }
            else {
                metaClass.setProperty(object, key, value)
            }
        }
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
}
