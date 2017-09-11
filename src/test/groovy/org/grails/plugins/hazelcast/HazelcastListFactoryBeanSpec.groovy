package org.grails.plugins.hazelcast

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IList
import org.grails.plugins.hazelcast.beans.HazelcastListFactoryBean
import spock.lang.Specification

class HazelcastListFactoryBeanSpec extends Specification {
    void 'When a HazelcastListFactoryBean is created but is missing the instanceName, an IllegalArgumentException is thrown'() {
        setup:
        HazelcastListFactoryBean factoryBean = new HazelcastListFactoryBean()
        factoryBean.listName = 'foo'

        when:
        factoryBean.afterPropertiesSet()

        then:
        thrown IllegalArgumentException
    }

    void 'When a HazelcastListFactoryBean is created but is missing the listName, an IllegalArgumentException is thrown'() {
        setup:
        HazelcastListFactoryBean factoryBean = new HazelcastListFactoryBean()
        factoryBean.instanceName = 'foo'

        when:
        factoryBean.afterPropertiesSet()

        then:
        thrown IllegalArgumentException
    }

    void 'When a HazelcastListFactoryBean is created, it returns the correct Hazelcast list'() {
        setup:
        IList list = Mock(IList)

        HazelcastInstance hazelcastInstance = Mock(HazelcastInstance)
        hazelcastInstance.getList('foo') >> list

        HazelcastInstanceService hazelcastInstanceService = Mock(HazelcastInstanceService)
        hazelcastInstanceService.getInstance('tmp') >> hazelcastInstance

        HazelcastListFactoryBean factoryBean = new HazelcastListFactoryBean()
        factoryBean.hazelcastInstanceService = hazelcastInstanceService
        factoryBean.instanceName = 'tmp'
        factoryBean.listName = 'foo'

        when:
        Object object = factoryBean.getObject()

        then:
        object.is(list)
    }
}
