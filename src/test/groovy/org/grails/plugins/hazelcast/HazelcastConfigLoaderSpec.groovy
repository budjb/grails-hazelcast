package org.grails.plugins.hazelcast

import com.hazelcast.config.Config
import spock.lang.Specification

class HazelcastConfigLoaderSpec extends Specification {
    HazelcastConfigLoader hazelcastConfigLoader

    void setup() {
        hazelcastConfigLoader = new HazelcastConfigLoader()
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
        Config config = hazelcastConfigLoader.loadConfig(instanceConfiguration)

        then:
        hazelcastConfigLoader.getInstanceConfigurations().size() == 1

        config
        config.instanceName.equalsIgnoreCase(INSTANCE_NAME)
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
        hazelcastConfigLoader.parseConfig(instanceConfiguration)

        then:
        thrown IllegalArgumentException
    }
}
