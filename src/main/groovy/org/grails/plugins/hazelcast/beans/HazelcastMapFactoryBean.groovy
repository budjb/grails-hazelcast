package org.grails.plugins.hazelcast.beans

import org.grails.plugins.hazelcast.HazelcastInstanceService
import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired

/**
 * A {@link FactoryBean} that exposes a distributed Hazelcast map as a Spring bean.
 *
 * @param < K >
 * @param < V >
 */
class HazelcastMapFactoryBean<K, V> implements FactoryBean<Map<K, V>>, InitializingBean {
    /**
     * Hazelcast instance service.
     */
    @Autowired
    HazelcastInstanceService hazelcastInstanceService

    /**
     * Name of the hazelcast instance that should contain the map.
     */
    String instanceName

    /**
     * Name of the hazelcast map.
     */
    String mapName

    @Override
    Map<K, V> getObject() throws Exception {
        return hazelcastInstanceService.getInstance(instanceName).getMap(mapName)
    }

    @Override
    Class<?> getObjectType() {
        return Map.class
    }

    @Override
    boolean isSingleton() {
        return true
    }

    @Override
    void afterPropertiesSet() throws Exception {
        if (!mapName) {
            throw new IllegalArgumentException('name of the Hazelcast map may not be null or empty')
        }

        if (!instanceName) {
            throw new IllegalArgumentException('name of the hazelcast instance may not be null or empty')
        }
    }
}
