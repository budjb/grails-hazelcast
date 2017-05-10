package org.grails.plugins.hazelcast

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

    /**
     * Spring bean creation.
     */
    Closure doWithSpring() {
        { ->
            // Creating the Hazelcast instances must happen very early in the initialization
            // process so that other beans that require it (such as servlet filters) have their
            // requires instances already created.
            HazelcastInstanceInstantiator instantiator = new HazelcastInstanceInstantiator()
            instantiator.createInstances(grailsApplication.config.hazelcast.instances ?: [])

            hazelcastInstanceService(HazelcastInstanceService) {
                delegate.instantiator = instantiator
            }
        }
    }

    /**
     * Shutdown handler.
     *
     * @param event
     */
    @Override
    void onShutdown(Map<String, Object> event) {
        applicationContext.getBean('hazelcastInstanceService', HazelcastInstanceService).shutdownAll()
    }
}
