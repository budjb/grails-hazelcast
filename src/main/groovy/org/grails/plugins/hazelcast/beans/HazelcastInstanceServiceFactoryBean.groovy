package org.grails.plugins.hazelcast.beans

import org.grails.plugins.hazelcast.HazelcastInstanceService
import org.springframework.beans.factory.FactoryBean

class HazelcastInstanceServiceFactoryBean implements FactoryBean<HazelcastInstanceService> {
    @Override
    HazelcastInstanceService getObject() throws Exception {
        return HazelcastInstanceService.getInstance()
    }

    @Override
    Class<?> getObjectType() {
        return HazelcastInstanceService
    }

    @Override
    boolean isSingleton() {
        return true
    }
}
