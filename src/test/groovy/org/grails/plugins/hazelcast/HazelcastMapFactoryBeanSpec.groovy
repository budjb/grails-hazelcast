package org.grails.plugins.hazelcast

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap
import org.grails.plugins.hazelcast.beans.HazelcastMapFactoryBean
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
        IMap map = Mock(IMap)

        HazelcastInstance hazelcastInstance = Mock(HazelcastInstance)
        hazelcastInstance.getMap('foo') >> map

        HazelcastInstanceService hazelcastInstanceService = Mock(HazelcastInstanceService)
        hazelcastInstanceService.getInstance('tmp') >> hazelcastInstance

        HazelcastMapFactoryBean factoryBean = new HazelcastMapFactoryBean()
        factoryBean.hazelcastInstanceService = hazelcastInstanceService
        factoryBean.instanceName = 'tmp'
        factoryBean.mapName = 'foo'

        when:
        Object object = factoryBean.getObject()

        then:
        object.is(map)
    }
}
