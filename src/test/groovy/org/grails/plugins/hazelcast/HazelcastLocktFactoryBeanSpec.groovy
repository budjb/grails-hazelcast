package org.grails.plugins.hazelcast

import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast
import org.grails.plugins.hazelcast.beans.HazelcastLockFactoryBean
import spock.lang.Specification

import java.util.concurrent.locks.Lock

class HazelcastLocktFactoryBeanSpec extends Specification {
    void 'When a HazelcastLockFactoryBean is created but is missing the instanceName, an IllegalArgumentException is thrown'() {
        setup:
        HazelcastLockFactoryBean factoryBean = new HazelcastLockFactoryBean()
        factoryBean.lockName = 'foo'

        when:
        factoryBean.afterPropertiesSet()

        then:
        thrown IllegalArgumentException
    }

    void 'When a HazelcastLockFactoryBean is created but is missing the lockName, an IllegalArgumentException is thrown'() {
        setup:
        HazelcastLockFactoryBean factoryBean = new HazelcastLockFactoryBean()
        factoryBean.instanceName = 'foo'

        when:
        factoryBean.afterPropertiesSet()

        then:
        thrown IllegalArgumentException
    }

    void 'When a HazelcastLockFactoryBean is created, it returns the correct Hazelcast lock'() {
        setup:
        Config config = new Config()
        config.instanceName = 'tmp'
        config.networkConfig.join.multicastConfig.enabled = false

        HazelcastInstanceService hazelcastInstanceService = new HazelcastInstanceService([])
        hazelcastInstanceService.createInstance(config)

        HazelcastLockFactoryBean factoryBean = new HazelcastLockFactoryBean()
        factoryBean.hazelcastInstanceService = hazelcastInstanceService
        factoryBean.instanceName = 'tmp'
        factoryBean.lockName = 'foo'
        factoryBean.afterPropertiesSet()

        when:
        Object object = factoryBean.getObject()

        then:
        object instanceof Lock
        object == Hazelcast.getHazelcastInstanceByName('tmp').getLock('foo')

        cleanup:
        hazelcastInstanceService.shutdownInstance(factoryBean.instanceName)
    }
}
