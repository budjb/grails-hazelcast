package org.grails.plugins.hazelcast.beans

import org.grails.plugins.hazelcast.HazelcastInstanceService
import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired

/**
 * A {@link FactoryBean} that exposes a distributed Hazelcast list as a Spring bean.
 *
 * @param < T >
 */
class HazelcastListFactoryBean<T> implements FactoryBean<List<T>>, InitializingBean {
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
     * Name of the hazelcast list.
     */
    String listName

    /**
     * {@inheritDoc}
     */
    @Override
    List<T> getObject() throws Exception {
        return hazelcastInstanceService.getInstance(instanceName).getList(listName)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Class<?> getObjectType() {
        return List.class
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
        if (!listName) {
            throw new IllegalArgumentException('name of the Hazelcast list may not be null or empty')
        }

        if (!instanceName) {
            throw new IllegalArgumentException('name of the hazelcast instance may not be null or empty')
        }
    }
}
