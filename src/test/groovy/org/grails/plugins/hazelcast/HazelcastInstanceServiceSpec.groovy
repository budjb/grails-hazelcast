package org.grails.plugins.hazelcast

import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import spock.lang.Specification

class HazelcastInstanceServiceSpec extends Specification {
    HazelcastInstanceService hazelcastInstanceService
    HazelcastInstanceInstantiator instantiator

    def setup() {
        instantiator = new HazelcastInstanceInstantiator()
        hazelcastInstanceService = new HazelcastInstanceService()
        hazelcastInstanceService.instantiator = instantiator
    }

    void "When a map configuration is parsed, the proper Hazelcast configuration is returned"() {
        setup:
        final String INSTANCE_NAME = 'instance1'

        Map instanceConfiguration = [
            instanceName : INSTANCE_NAME,
            groupConfig  : [
                name    : 'group1',
                password: 'pwd1'
            ],
            networkConfig: [
                join: [
                    multicastConfig: [
                        enabled: false
                    ]
                ]
            ],
            mapConfig    : [
                [
                    name             : 'foomap',
                    timeToLiveSeconds: 5000
                ]
            ]
        ]

        when:
        Config config = instantiator.parseConfig(instanceConfiguration)

        then:
        config
        config.instanceName.equalsIgnoreCase("instance1")
        config.getGroupConfig().name.equalsIgnoreCase("group1")
        config.getGroupConfig().password.equalsIgnoreCase("pwd1")
        !config.networkConfig.join.multicastConfig.enabled
    }

    void "When a map configuration is parsed but an invalid property is encountered, an IllegalArgumentException is thrown"() {
        setup:
        Map instanceConfiguration = [
            instanceName: 'instance1',
            groupInfo   : [
                name    : 'group1',
                password: 'pwd1'
            ]
        ]

        when:
        instantiator.parseConfig(instanceConfiguration)

        then:
        thrown IllegalArgumentException
    }

    void "When an application starts, all Hazelcast instances are created"() {
        setup:
        final String INSTANCE_NAME = 'instance1'

        List<Map> instanceConfigurations = [
            [
                instanceName : INSTANCE_NAME,
                groupConfig  : [
                    name    : 'group1',
                    password: 'pwd1'
                ],
                networkConfig: [
                    join: [
                        multicastConfig: [
                            enabled: false
                        ]
                    ]
                ]
            ]
        ]

        when:
        instantiator.createInstances(instanceConfigurations)

        then:
        HazelcastInstance instance = Hazelcast.getHazelcastInstanceByName('instance1')
        instance.name == INSTANCE_NAME
        hazelcastInstanceService.getInstance(INSTANCE_NAME) == instance

        cleanup:
        Hazelcast.getHazelcastInstanceByName(INSTANCE_NAME).shutdown()
    }

    void 'When shutdownInstance() is called, the specified instance is shut down'() {
        setup:
        final String INSTANCE_NAME = 'instance1'

        List<Map> instanceConfigurations = [
            [
                instanceName : INSTANCE_NAME,
                groupConfig  : [
                    name    : 'group1',
                    password: 'pwd1'
                ],
                networkConfig: [
                    join: [
                        multicastConfig: [
                            enabled: false
                        ]
                    ]
                ]
            ]
        ]
        instantiator.createInstances(instanceConfigurations)

        when:
        hazelcastInstanceService.shutdownInstance(INSTANCE_NAME)

        then:
        !Hazelcast.getHazelcastInstanceByName(INSTANCE_NAME)
    }
}
