package org.grails.plugins.hazelcast

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.ILock
import org.grails.plugins.hazelcast.beans.HazelcastLockFactoryBean
import spock.lang.Specification

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
        ILock lock = Mock(ILock)

        HazelcastInstance hazelcastInstance = Mock(HazelcastInstance)
        hazelcastInstance.getLock('foo') >> lock

        HazelcastInstanceService hazelcastInstanceService = Mock(HazelcastInstanceService)
        hazelcastInstanceService.getInstance('tmp') >> hazelcastInstance

        HazelcastLockFactoryBean factoryBean = new HazelcastLockFactoryBean()
        factoryBean.hazelcastInstanceService = hazelcastInstanceService
        factoryBean.instanceName = 'tmp'
        factoryBean.lockName = 'foo'

        when:
        Object object = factoryBean.getObject()

        then:
        object.is(lock)
    }
}
