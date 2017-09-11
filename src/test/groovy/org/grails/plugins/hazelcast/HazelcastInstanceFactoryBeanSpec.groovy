package org.grails.plugins.hazelcast

import com.hazelcast.core.HazelcastInstance
import org.grails.plugins.hazelcast.beans.HazelcastInstanceFactoryBean
import spock.lang.Specification

class HazelcastInstanceFactoryBeanSpec extends Specification {
    void 'When a HazelcastInstanceFactoryBean is created but is missing the instanceName, an IllegalArgumentException is thrown'() {
        setup:
        HazelcastInstanceFactoryBean factoryBean = new HazelcastInstanceFactoryBean()

        when:
        factoryBean.afterPropertiesSet()

        then:
        thrown IllegalArgumentException
    }

    void 'When a HazelcastInstanceFactoryBean is created, it returns the correct Hazelcast instance'() {
        setup:
        HazelcastInstance hazelcastInstance = Mock(HazelcastInstance)

        HazelcastInstanceService hazelcastInstanceService = Mock(HazelcastInstanceService)
        hazelcastInstanceService.getInstance('tmp') >> hazelcastInstance

        HazelcastInstanceFactoryBean factoryBean = new HazelcastInstanceFactoryBean()
        factoryBean.hazelcastInstanceService = hazelcastInstanceService
        factoryBean.instanceName = 'tmp'

        when:
        Object object = factoryBean.getObject()

        then:
        object.is(hazelcastInstance)
    }
}
