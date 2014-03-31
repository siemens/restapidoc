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
        runtime('org.codehaus.groovy.modules.http-builder:http-builder:0.5.1') {
            excludes 'xalan'
            excludes 'xml-apis'
            excludes 'groovy'
        }
    }

    plugins {
        build ":tomcat:7.0.52.1"
    }
}
