class RestapidocGrailsPlugin {
    def version = "0.1"
    def grailsVersion = "2.3 > *"
    def title = "Restapi Plugin"
    def description = '???????????'
    def documentation = "http://grails.org/plugin/restapidoc"
    def license = "APACHE"
    def developers = [
        [name: 'Derk Muenchhausen', email: "???????"]
    ]
    def issueManagement = [system: 'GITHUB', url: 'https://github.com/siemens/restapidoc/issues']
    def scm = [url: 'https://github.com/siemens/restapidoc']

    def doWithApplicationContext = { ctx ->
        ctx.apiDocumentationService.init()
    }
}
