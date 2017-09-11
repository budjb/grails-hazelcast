package org.grails.plugins.hazelcast

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.ISet
import org.grails.plugins.hazelcast.beans.HazelcastSetFactoryBean
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
        ISet set = Mock(ISet)

        HazelcastInstance hazelcastInstance = Mock(HazelcastInstance)
        hazelcastInstance.getSet('foo') >> set

        HazelcastInstanceService hazelcastInstanceService = Mock(HazelcastInstanceService)
        hazelcastInstanceService.getInstance('tmp') >> hazelcastInstance

        HazelcastSetFactoryBean factoryBean = new HazelcastSetFactoryBean()
        factoryBean.hazelcastInstanceService = hazelcastInstanceService
        factoryBean.instanceName = 'tmp'
        factoryBean.setName = 'foo'

        when:
        Object object = factoryBean.getObject()

        then:
        object.is(set)
    }
}
