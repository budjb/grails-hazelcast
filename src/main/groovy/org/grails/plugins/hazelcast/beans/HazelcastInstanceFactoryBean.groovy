package org.grails.plugins.hazelcast.beans

import com.hazelcast.config.Config
import com.hazelcast.core.HazelcastInstance
import org.grails.plugins.hazelcast.HazelcastConfigLoader
import org.grails.plugins.hazelcast.HazelcastInstanceService
import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired

/**
 * A {@link FactoryBean} that exposes a Hazelcast instance as a Spring bean.
 */
class HazelcastInstanceFactoryBean implements FactoryBean<HazelcastInstance>, InitializingBean {
    /**
     * Hazelcast instance service.
     */
    @Autowired
    HazelcastInstanceService hazelcastInstanceService

    /**
     * Hazelcast configuration loader.
     */
    @Autowired
    HazelcastConfigLoader hazelcastConfigLoader

    /**
     * Name of the hazelcast instance that should contain the map.
     */
    String instanceName

    /**
     * {@inheritDoc}
     */
    @Override
    HazelcastInstance getObject() throws Exception {
        try {
            return hazelcastInstanceService.getInstance(instanceName)
        }
        catch (IllegalArgumentException e) {
            Config config = hazelcastConfigLoader.retrieveAndRemoveInstanceConfiguration(instanceName)

            if (config != null) {
                return hazelcastInstanceService.createInstance(config)
            }

            throw e
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Class<?> getObjectType() {
        return HazelcastInstance.class
    }

    /**
     * {@inheritDoc}
     */
    @Override
    boolean isSingleton() {
        return true
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void afterPropertiesSet() throws Exception {
        if (!instanceName) {
            throw new IllegalArgumentException('name of the hazelcast instance may not be null or empty')
        }
    }
}
