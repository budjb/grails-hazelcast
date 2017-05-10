package org.grails.plugins.hazelcast

import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast
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
        Config config = new Config()
        config.instanceName = 'tmp'
        config.networkConfig.join.multicastConfig.enabled = false

        HazelcastInstanceService hazelcastInstanceService = new HazelcastInstanceService()
        hazelcastInstanceService.instantiator = new HazelcastInstanceInstantiator()
        hazelcastInstanceService.createInstance(config)

        HazelcastListFactoryBean factoryBean = new HazelcastListFactoryBean()
        factoryBean.hazelcastInstanceService = hazelcastInstanceService
        factoryBean.instanceName = 'tmp'
        factoryBean.listName = 'foo'
        factoryBean.afterPropertiesSet()

        when:
        Object object = factoryBean.getObject()

        then:
        object instanceof List
        object == Hazelcast.getHazelcastInstanceByName('tmp').getList('foo')

        cleanup:
        hazelcastInstanceService.shutdownInstance(factoryBean.instanceName)
    }
}
