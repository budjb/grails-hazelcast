package org.grails.plugins.hazelcast

import com.hazelcast.config.Config
import grails.plugins.Plugin

class HazelcastGrailsPlugin extends Plugin {
    /**
     * The version or versions of Grails the plugin is designed for.
     */
    def grailsVersion = '3.1.9 > *'

    /**
     * Headline display name of the plugin.
     */
    def title = 'Grails Hazelcast Plugin'

    /**
     * Plugin author.
     */
    def author = 'Anil Manthina'

    /**
     * Author email address.
     */
    def authorEmail = 'anil.manthina@gmail.com'

    /**
     * Plugin description.
     */
    def description = 'A plugin that creates Hazelcast instances based on application configuration.'

    /**
     * URL to the plugin's documentation.
     */
    def documentation = 'https://budjb.github.io/grails-hazelcast/latest'

    /**
     * Plugin license.
     */
    def license = 'APACHE'

    /**
     * Additional developers.
     */
    def developers = [
        [name: 'Bud Byrd', email: 'bud.byrd@gmail.com'],
        [name: 'Aaron Brown', email: 'aaron@aarocode.com']
    ]

    /**
     * Location of the plugin's issue tracker.
     */
    def issueManagement = [system: 'GITHUB', url: 'https://github.com/budjb/grails-hazelcast/issues']

    /**
     * Online location of the plugin's browseable source code.
     */
    def scm = [url: 'https://github.com/budjb/grails-hazelcast']

    @Override
    Closure doWithSpring() {
        { ->
            hazelcastConfigLoader(HazelcastConfigLoader, grailsApplication.config.hazelcast.instances ?: [])
            hazelcastInstanceService(HazelcastInstanceService)
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void onStartup(Map<String, Object> event) {
        HazelcastInstanceService hazelcastInstanceService = applicationContext.getBean('hazelcastInstanceService', HazelcastInstanceService)
        HazelcastConfigLoader hazelcastConfigLoader = applicationContext.getBean('hazelcastConfigLoader', HazelcastConfigLoader)

        for (Config config : hazelcastConfigLoader.getInstanceConfigurations()) {
            hazelcastInstanceService.createInstance(config)
        }

        hazelcastConfigLoader.getInstanceConfigurations().clear()
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void onShutdown(Map<String, Object> event) {
        applicationContext.getBean('hazelcastInstanceService', HazelcastInstanceService).shutdownAll()
    }
}
