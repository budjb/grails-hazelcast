package com.budjb.hazelcast.beans

import com.budjb.hazelcast.HazelcastInstanceService
import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast
import spock.lang.Specification

class HazelcastSetFactoryBeanSpec extends Specification {
    void 'When a HazelcastSetFactoryBean is created but is missing the instanceName, an IllegalArgumentException is thrown'() {
        setup:
        HazelcastSetFactoryBean factoryBean = new HazelcastSetFactoryBean()
        factoryBean.setName = 'foo'

        when:
        factoryBean.afterPropertiesSet()

        then:
        thrown IllegalArgumentException
    }

    void 'When a HazelcastSetFactoryBean is created but is missing the setName, an IllegalArgumentException is thrown'() {
        setup:
        HazelcastSetFactoryBean factoryBean = new HazelcastSetFactoryBean()
        factoryBean.instanceName = 'foo'

        when:
        factoryBean.afterPropertiesSet()

        then:
        thrown IllegalArgumentException
    }

    void 'When a HazelcastSetFactoryBean is created, it returns the correct Hazelcast set'() {
        setup:
        Config config = new Config()
        config.instanceName = 'tmp'
        config.networkConfig.join.multicastConfig.enabled = false

        HazelcastInstanceService hazelcastInstanceService = new HazelcastInstanceService([])
        hazelcastInstanceService.createInstance(config)

        HazelcastSetFactoryBean factoryBean = new HazelcastSetFactoryBean()
        factoryBean.hazelcastInstanceService = hazelcastInstanceService
        factoryBean.instanceName = 'tmp'
        factoryBean.setName = 'foo'
        factoryBean.afterPropertiesSet()

        when:
        Object object = factoryBean.getObject()

        then:
        object instanceof Set
        object == Hazelcast.getHazelcastInstanceByName('tmp').getSet('foo')

        cleanup:
        hazelcastInstanceService.shutdownInstance(factoryBean.instanceName)
    }
}
