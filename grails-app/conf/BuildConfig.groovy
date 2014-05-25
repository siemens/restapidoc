grails.project.work.dir = 'target'

grails.project.fork = [
        // configure settings for the test-app JVM, uses the daemon by default
        test: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, daemon:true]
]

grails.project.dependency.resolution = {

    inherits 'global'
    log 'warn'

    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        /* runtime('org.codehaus.groovy.modules.http-builder:http-builder:0.5.1') {
            excludes 'xalan'
            excludes 'xml-apis'
            excludes 'groovy'
        }
        */
        runtime "org.springframework:spring-expression:$springVersion"
        runtime "org.springframework:spring-aop:$springVersion"
    }

    plugins {
	    compile ":rest:0.8"
        build ":tomcat:7.0.53"
        build ':release:3.0.1', ':rest-client-builder:1.0.3', {
            export = false
        }
    }
}
