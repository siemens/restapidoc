class RestapidocGrailsPlugin {
    def version = "0.1"
    def grailsVersion = "2.3 > *"
    def title = "Restapidoc Plugin"
    def description = '''a RESTful API documentation plugin for the Grails web application framework.
Very much inspired by Swagger API documentation, this plugin reuses the available information
of Grails Domain classes and Controllers to minimize documentation effort and to improve consistency.
'''
    def documentation = "https://github.com/siemens/restapidoc"
    def license = "APACHE"
    def developers = [
        [name: 'Derk Muenchhausen', email: "derk@muenchhausen.de"]
    ]
    def issueManagement = [system: 'GITHUB', url: 'https://github.com/siemens/restapidoc/issues']
    def scm = [url: 'https://github.com/siemens/restapidoc']

    def doWithApplicationContext = { ctx ->
        ctx.apiDocumentationService.init()
    }
}
