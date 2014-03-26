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

import grails.rest.RestfulController
import grails.transaction.Transactional
import org.apache.log4j.Logger
import restapidoc.annotations.ApiIgnore
import restapidoc.annotations.ApiOperation
import restapidoc.annotations.ApiParam
import restapidoc.annotations.ApiParams
import restapidoc.annotations.ApiResponse
import restapidoc.annotations.ApiResponses
import restapidoc.annotations.DeleteMethod
import restapidoc.annotations.GetMethod
import restapidoc.annotations.PostMethod
import restapidoc.annotations.PutMethod

/**
 * Convenience Controller that covers generic documentation and a JSON getParametersToBind implementation
 * This controller can be used as basis for generic documentation
 *
 * @author Stephan Linkel
 * @author Derk Muenchhausen
 * @since 0.1
 */
class DocumentedRestfulController<T> extends RestfulController {
    def logger = Logger.getLogger('restapidoc')

    DocumentedRestfulController(Class<T> resource) {
        super(resource)
    }


    @Override
    protected List<T> listAllResources(Map params) {
        // todo: same with grails withCriteria based queries

        def query = params?.q
        if (query) {
            def domainClass = grailsApplication.getDomainClass(resource.name)
            def persistentProps = domainClass.persistentProperties

            def queryMap = [:]
            query.split('\\+').each {expr ->
                def nv = expr.split(':')
                if (nv[0] in persistentProps)
                    queryMap.put (nv[0],nv[1])
                else
                    assert new java.lang.IllegalArgumentException("query parameter unknown. It must be one of ${persistentProps}")
            }
            return resource.findAllWhere(queryMap)
        } else {
            return resource.list(params)
        }
    }

    @Override
    @GetMethod
    @ApiOperation(value = "List #{domainClass.plural}", notes = "returns a complete list of #{domainClass.name} objects")
    def index(@ApiParams(value = [
        @ApiParam(name = "max", value = "max count result entries to be fetched", allowableValues = "long", required = false),
        @ApiParam(name = "q", value = "query parameter e.g. q=remark:test", allowableValues = "long", required = false)
    ]) Integer max) {
        super.index(max)
    }

    @Override
    @GetMethod
    @ApiOperation(value = "Retrieve #{domainClass.name}", notes = "returns a specific #{domainClass.name} object")
    @ApiResponses(value = [ @ApiResponse(code = 404, message = "#{domainClass.name} not found") ])
    def show(@ApiParam(name = "id", value = "ID of #{domainClass.name} to be fetched", allowableValues = "long", required = true) String id) {
        super.show()
    }

    @Override
    @Transactional
    @PostMethod
    @ApiOperation(value = "Create #{domainClass.name}", notes = "creates a new #{domainClass.name} object")
    def save() {
        super.save()
    }

    @Override
    @Transactional
    @PutMethod
    @ApiOperation(value = "Update #{domainClass.name}", notes = "changes a specific #{domainClass.name} object")
    @ApiResponses(value = [ @ApiResponse(code = 404, message = "#{domainClass.name} not found") ])
    def update(@ApiParam(name = "id", value = "ID of #{domainClass.name} to be updated", allowableValues = "long", required = true) String id) {
        super.update()
    }

    @Override
    @Transactional
    @DeleteMethod
    @ApiOperation(value = "Delete #{domainClass.name}", notes = "deletes a specific #{domainClass.name} object")
    @ApiResponses(value = [ @ApiResponse(code = 404, message = "#{domainClass.name} not found") ])
    def delete(@ApiParam(name = "id", value = "ID of #{domainClass.name} to be deleted", allowableValues = "long", required = true) String id) {
        super.delete()
    }

    @Override
    protected Map getParametersToBind() {
        super
        def rawData

        def asJSON = request.getJSON()
        if (asJSON && asJSON instanceof Map) {
            rawData = asJSON
        } else {
            rawData = params.clone()
        }

        def domainClass = grailsApplication.getDomainClass(resource.name)
        def persistentProps = domainClass.persistentProperties

        def data = [:]
        rawData.each { key, value ->
            if (key.toString() in ApiDocumentationService.IGNORE_PROPERTIES) {
                return
            }
            def p = persistentProps.find({ it.name == key.toString() })
            def ignore
            try {
                ignore = domainClass.clazz.getDeclaredField(key.toString()).getAnnotation(ApiIgnore)
            } catch (NoSuchFieldException ex) {
                return
            }
            if (ignore)
                return

            if (p && p.type == Date && value && !(value instanceof Date)) {
                if (value instanceof Map) {
                    data[key] = DateUtils.parseDate(value.value, value.format)
                } else if (value instanceof String) {
                    data[key] = DateUtils.parseDate(value)
                }
            } else if (p && (p.isOneToOne() || p.isManyToOne())) {
                logger.debug("getParametersToBind: isOneToOne and isManyToOne domains are not implemented yet. Property ${key.toString()} ignored.")
            } else if (p && (p.isOneToMany() || p.isManyToMany())) {
                logger.debug("getParametersToBind: isOneToMany and isManyToMany domains are not implemented yet. Property ${key.toString()} ignored.")
            } else {
                data[key] = value
            }
        }
        return data
    }
}
