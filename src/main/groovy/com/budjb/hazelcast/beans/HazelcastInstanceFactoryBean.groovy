package com.budjb.hazelcast.beans

import com.budjb.hazelcast.HazelcastInstanceService
import com.hazelcast.core.HazelcastInstance
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
     * Name of the hazelcast instance that should contain the map.
     */
    String instanceName

    @Override
    HazelcastInstance getObject() throws Exception {
        return hazelcastInstanceService.getInstance(instanceName)
    }

    @Override
    Class<?> getObjectType() {
        return HazelcastInstance.class
    }

    @Override
    boolean isSingleton() {
        return true
    }

    @Override
    void afterPropertiesSet() throws Exception {
        if (!instanceName) {
            throw new IllegalArgumentException('name of the hazelcast instance may not be null or empty')
        }
    }
}
