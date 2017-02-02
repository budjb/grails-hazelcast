package com.budjb.hazelcast

import grails.plugins.Plugin
import groovy.util.logging.Slf4j

@Slf4j
class GrailsHazelcastGrailsPlugin extends Plugin {
    /**
     * The version or versions of Grails the plugin is designed for.
     */
    def grailsVersion = "3.1.9 > *"

    /**
     * Headline display name of the plugin.
     */
    def title = "Grails Hazelcast Plugin"

    /**
     * Plugin author.
     */
    def author = "Anil Manthina"

    /**
     * Author email address.
     */
    def authorEmail = "TODO"

    /**
     * Plugin description.
     */
    def description = 'A plugin that creates Hazelcast instances based on application configuration.'

    /**
     * URL to the plugin's documentation.
     */
    def documentation = "TODO"

    /**
     * Plugin license.
     */
    def license = "APACHE"

    /**
     * Additional developers.
     */
    def developers = [
        [name: "Bud Byrd", email: "bud.byrd@gmail.com"]
    ]

    /**
     * Location of the plugin's issue tracker.
     */
    def issueManagement = [system: "GITHUB", url: "TODO"]

    /**
     * Online location of the plugin's browseable source code.
     */
    def scm = [url: "TODO"]

    /**
     * Spring bean creation.
     */
    Closure doWithSpring() {
        { ->
            hazelcastInstanceService(HazelcastInstanceService, grailsApplication.config.hazelcast.instances ?: [])
        }
    }
}
