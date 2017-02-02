package com.budjb.hazelcast.beans

import com.budjb.hazelcast.HazelcastInstanceService
import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast
import spock.lang.Specification

class HazelcastMapFactoryBeanSpec extends Specification {
    void 'When a HazelcastMapFactoryBean is created but is missing the instanceName, an IllegalArgumentException is thrown'() {
        setup:
        HazelcastMapFactoryBean factoryBean = new HazelcastMapFactoryBean()
        factoryBean.mapName = 'foo'

        when:
        factoryBean.afterPropertiesSet()

        then:
        thrown IllegalArgumentException
    }

    void 'When a HazelcastMapFactoryBean is created but is missing the mapName, an IllegalArgumentException is thrown'() {
        setup:
        HazelcastMapFactoryBean factoryBean = new HazelcastMapFactoryBean()
        factoryBean.instanceName = 'foo'

        when:
        factoryBean.afterPropertiesSet()

        then:
        thrown IllegalArgumentException
    }

    void 'When a HazelcastMapFactoryBean is created, it returns the correct Hazelcast map'() {
        setup:
        Config config = new Config()
        config.instanceName = 'tmp'
        config.networkConfig.join.multicastConfig.enabled = false

        HazelcastInstanceService hazelcastInstanceService = new HazelcastInstanceService([])
        hazelcastInstanceService.createInstance(config)

        HazelcastMapFactoryBean factoryBean = new HazelcastMapFactoryBean()
        factoryBean.hazelcastInstanceService = hazelcastInstanceService
        factoryBean.instanceName = 'tmp'
        factoryBean.mapName = 'foo'
        factoryBean.afterPropertiesSet()

        when:
        Object object = factoryBean.getObject()

        then:
        object instanceof Map
        object == Hazelcast.getHazelcastInstanceByName('tmp').getMap('foo')
    }
}
