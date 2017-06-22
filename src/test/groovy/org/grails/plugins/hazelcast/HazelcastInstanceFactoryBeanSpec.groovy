package org.grails.plugins.hazelcast

import com.hazelcast.config.Config
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
        Config config = new Config()
        config.instanceName = 'tmp'
        config.networkConfig.join.multicastConfig.enabled = false

        HazelcastInstance instance = HazelcastInstanceService.getInstance().createInstance(config)

        HazelcastInstanceFactoryBean factoryBean = new HazelcastInstanceFactoryBean()
        factoryBean.hazelcastInstanceService = HazelcastInstanceService.getInstance()
        factoryBean.instanceName = 'tmp'
        factoryBean.afterPropertiesSet()

        when:
        Object object = factoryBean.getObject()

        then:
        object instanceof HazelcastInstance
        ((HazelcastInstance) object).config.instanceName == 'tmp'
        object.is(instance)

        cleanup:
        instance.shutdown()
    }
}
