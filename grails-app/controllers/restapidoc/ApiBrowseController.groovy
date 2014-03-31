/*
 * Copyright (c) Siemens AG, 2013
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package restapidoc

import groovyx.net.http.RESTClient

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Controller for displaying and testing documentation that is collected by ApiDocumentationService
 *
 * @author Stephan Linkel
 * @author Derk Muenchhausen
 * @since 0.1
 */
class ApiBrowseController {

    def apiDocumentationService

    Logger logger = LoggerFactory.getLogger('restapidoc')

    def index() {
    }

    def init() {
        render "Ok"
    }

    def domainClasses() {
        def domainClasses = apiDocumentationService.domainClasses
        if (params.q) {
            params.q = params.q.toLowerCase()
            domainClasses = domainClasses.findAll({ it.name.toLowerCase().contains(params.q) })
        }
        domainClasses = domainClasses.sort()
        return [domainClasses: domainClasses]
    }

    def methods() {
        def domainClasses = apiDocumentationService.domainClasses.keySet()

        def dc = params.domainClass
        def domainClass = apiDocumentationService.domainClasses[dc]
        if (!domainClass) {
            render ""
            return
        }

        return [controllerClasses: domainClass.controller, domainClass: domainClass, domainClasses: domainClasses]
    }

    def domainClassSignature() {
        def domainClasses = apiDocumentationService.domainClasses.keySet()

        def dc = params.domainClass
        def domainClass = apiDocumentationService.domainClasses[dc]
        if (!domainClass) {
            render ""
            return
        }

        return [domainClass: domainClass, domainClasses: domainClasses]
    }

    def sandbox() {
        logger.debug("ApiBrowseController sandbox params=${params}")
        def rController = params.restController
        def rAction = params.restAction
        def rParams = [:]
        def postData = params['_postData']
        params.list('_param').each { p ->
            if (params[p]) {
                rParams[p] = params[p]
            }
        }

        def urlBase = grailsApplication?.config?.resourcemodule?.sandboxUrlPrefix

        def rest = new RESTClient(g.createLink(controller: rController, base: urlBase, absolute: true))

        def respStatus = 501
        def respData
        def respHeaders

        def url = g.createLink(controller: rController, action: rAction, params: rParams, base: urlBase, absolute: true)

        try {
            if (postData) {
                def resp = rest."${params.restMethod}"(uri: url, headers: [Accept: "${params._responseContentType}, */*"], contentType: 'application/json', body: postData)
                respStatus = resp.status
                respData = resp.data
                respHeaders = resp.headers
            } else {
                def resp = rest."${params.restMethod}"(uri: url, headers: [Accept: "${params._responseContentType}, */*"], contentType: 'application/x-www-form-urlencoded')
                respStatus = resp.status
                respData = resp.data
                respHeaders = resp.headers
            }
        } catch( ex ) {
            try {
                respStatus = ex?.response?.status
                respData = ex?.response?.data
                respHeaders = ex?.response?.headers
            } catch ( ex2 ) {
                respData = ex.message
            }
        }
        return [method: params.restMethod, url: url, contentType: '', body: '', statusCode: respStatus, respBody: respData, respHeaders: respHeaders]
    }
}
