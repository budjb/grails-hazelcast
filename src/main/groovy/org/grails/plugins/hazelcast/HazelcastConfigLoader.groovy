package org.grails.plugins.hazelcast

import com.hazelcast.config.Config

/**
 * Loads Hazelcast configuration from the Grails config and stores them until
 * the service is ready to create the actual instances. Other beans may access
 * the loader before the services does to create instances themselves, depending
 * on bean load order.
 */
class HazelcastConfigLoader {
    /**
     * List of Hazelcast {@link Config} objects loaded from map configurations.
     */
    final List<Config> instanceConfigurations = []

    /**
     * Base constructor.
     */
    HazelcastConfigLoader() {

    }

    /**
     * Creates Hazelcast {@link Config} objects from the given map configuration.
     *
     * @param grailsConfig List of Maps containing Hazelcast instance configurations.
     */
    HazelcastConfigLoader(List<Map> grailsConfig) {
        for (Map config : grailsConfig) {
            loadConfig(config)
        }
    }

    /**
     * Creates a Hazelcast {@link Config} object from the given map configuration.
     *
     * @param grailsConfig
     * @return
     */
    Config loadConfig(Map grailsConfig) {
        Config config = parseConfig(grailsConfig)
        instanceConfigurations.add(config)
        return config
    }

    /**
     * Returns the instance with the given name, or null if it doesn't exist.
     *
     * @param instanceName
     * @return
     */
    Config retrieveInstanceConfiguration(String instanceName) {
        for (Config config : getInstanceConfigurations()) {
            if (config.getInstanceName() == instanceName) {
                return config
            }
        }

        return null
    }

    /**
     * Returns the instance with the given name, or null if it doesn't exist.
     * If the configuration does exist, remove it from the loader registry.
     *
     * @param instanceName
     * @return
     */
    Config retrieveAndRemoveInstanceConfiguration(String instanceName) {
        Config config = retrieveInstanceConfiguration(instanceName)

        if (config != null) {
            instanceConfigurations.remove(config)
        }

        return config
    }

    /**
     * Creates a Hazelcast {@link com.hazelcast.config.Config} object from the configuration contained the given map.
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
