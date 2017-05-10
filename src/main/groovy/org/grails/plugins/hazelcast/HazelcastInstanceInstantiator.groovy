package org.grails.plugins.hazelcast

import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance

class HazelcastInstanceInstantiator {
    /**
     * Creates any instances configured by the given list/map structure.
     *
     * @param configuration
     */
    void createInstances(List<Map> configuration) {
        configuration?.each { config ->
            createInstance(config)
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

    /**
     * Creates a Hazelcast {@link Config} object from the configuration contained the given map.
     *
     * @param configuration Map containing the configuration of the Hazelcast instance.
     * @return
     */
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
            Object value = map.get(key)

            if (metaClass.hasProperty(object, key)) {
                if (value instanceof Map) {
                    assign(metaClass.getProperty(object, key), value)
                }
                else {
                    metaClass.setProperty(object, key, value)
                }
            }
            else if (metaClass.getMethods().find { it.name == "add${key.capitalize()}".toString() }) {
                if (!List.isInstance(value)) {
                    throw new IllegalArgumentException("Expected List for property '${key}' but got '${value.getClass().getName()}'")
                }

                MetaMethod method = metaClass.getMethods().find { it.getName() == "add${key.capitalize()}".toString() }

                if (method.getNativeParameterTypes().size() != 1) {
                    throw new UnsupportedOperationException("can not process Hazelcast property '${key}'")
                }

                Class argumentType = method.getNativeParameterTypes()[0]

                for (Map subMap : (List<Map>) value) {
                    Object sub = argumentType.newInstance()
                    assign(sub, subMap)
                    method.invoke(object, sub)
                }
            }
            else {
                throw new IllegalArgumentException("Property '${key}' is not a valid property for class '${object.getClass().getName()}'. Please check your configuration.")
            }
        }
    }
}
