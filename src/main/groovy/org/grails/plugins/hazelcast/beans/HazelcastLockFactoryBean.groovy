package org.grails.plugins.hazelcast.beans

import org.grails.plugins.hazelcast.HazelcastInstanceService
import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired

import java.util.concurrent.locks.Lock

/**
 * A {@link FactoryBean} that exposes a distributed Hazelcast lock as a Spring bean.
 */
class HazelcastLockFactoryBean implements FactoryBean<Lock>, InitializingBean {
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
     * Name of the hazelcast lock.
     */
    String lockName

    /**
     * {@inheritDoc}
     */
    @Override
    Lock getObject() throws Exception {
        return hazelcastInstanceService.getInstance(instanceName).getLock(lockName)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Class<?> getObjectType() {
        return Lock.class
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
        if (!lockName) {
            throw new IllegalArgumentException('name of the Hazelcast lock may not be null or empty')
        }

        if (!instanceName) {
            throw new IllegalArgumentException('name of the hazelcast instance may not be null or empty')
        }
    }
}
