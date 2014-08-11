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
import org.codehaus.groovy.grails.commons.GrailsControllerClass
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import restapidoc.annotations.ApiOperation
import restapidoc.annotations.ApiParam
import restapidoc.annotations.ApiParams
import restapidoc.annotations.ApiResponse
import restapidoc.annotations.ApiResponses
import restapidoc.annotations.DeleteMethod
import restapidoc.annotations.ApiDescription
import restapidoc.annotations.GetMethod
import restapidoc.annotations.ApiIgnore
import restapidoc.annotations.PostMethod
import restapidoc.annotations.ApiProperty
import restapidoc.annotations.PutMethod

import java.lang.annotation.Annotation
import java.lang.reflect.Method

/**
 * Navigate through all Grails Controllers and search for RestfulControllers if they have any documenation information.
 * The resulting documantation will be stored in the global map doaminClasses for further usage.
 *
 * @author Derk Muenchhausen
 * @author Stephan Linkel
 * @since 0.1
 */
@Transactional
class ApiDocumentationService {
    def grailsApplication
    static IGNORE_PROPERTIES = ['class','errors','metaClass','version','']
    static Map<String, DomainDocumentation> domainClasses = [:]

    def init() {
        domainClasses = [:]
        for (GrailsControllerClass controller in grailsApplication.controllerClasses) {
            addDomainAndControllerClass(controller, grailsApplication, domainClasses)
        }
    }

    /**
     * Searches the class hierarchy for declared fields (including private)
     * @param clazz the class to inspect
     * @param name the name of the field to inspect
     * @param annotationClass the type of annotation we're interested in
     * @return
     */
    private static Annotation getAnnotationForClassField(Class clazz, String name, Class<Annotation> annotationClass) {
        while (clazz) {
            try {
                return clazz.getDeclaredField(name).getAnnotation(annotationClass)
            } catch(NoSuchFieldException) {
                clazz = clazz.superclass
            }
        }
        throw NoSuchFieldException(name)
    }

    protected static void addDomainAndControllerClass(GrailsControllerClass controller, grailsApplication, LinkedHashMap<String, DomainDocumentation> domainClasses) {
        // RestfulController do have the property resource
        def resource = controller.getPropertyValue('resource')

        // if it is a RestfulController
        if (!resource) {
            return
        }

        def domainClassName = resource.simpleName

        GrailsDomainClass domainClass = grailsApplication.domainClasses.find({ it.clazz.simpleName == domainClassName })

        ApiIgnore ignoreDomain = domainClass.clazz.getAnnotation(ApiIgnore)

        if ( ignoreDomain ) {
            return
        }

        if (!domainClasses[domainClassName]) {
            domainClasses[domainClassName] = new DomainDocumentation()
            domainClasses[domainClassName].domainClass = domainClass
            domainClasses[domainClassName].name = domainClassName
            ApiDescription desc = domainClass.clazz.getAnnotation(ApiDescription)
            if (desc) {
                domainClasses[domainClassName].name = desc.name()
                domainClasses[domainClassName].plural = desc.plural()
                domainClasses[domainClassName].description = desc.description()
            }
            domainClasses[domainClassName].domainProperties = []
            def persistentProps = domainClass.persistentProperties

            persistentProps.each { GrailsDomainClassProperty prop ->
                ApiIgnore ignore = getAnnotationForClassField(domainClass.clazz, prop.name, ApiIgnore)

                if (ignore || prop.name in IGNORE_PROPERTIES) {
                    return
                }
                DomainPropertyDocumentation dpd = new DomainPropertyDocumentation()
                dpd.name = prop.name
                dpd.type = prop.type
                dpd.isNullable = false
                if (prop.isOneToMany() || prop.isManyToMany()) {
                    if (prop.referencedDomainClass) {
                        dpd.type = prop.referencedDomainClass.clazz
                    }
                    dpd.isCollection = true
                } else {
                    dpd.isCollection = false
                }
                ApiProperty propAnn = getAnnotationForClassField(domainClass.clazz, prop.name, ApiProperty)

                if (propAnn) {
                    dpd.description = propAnn.description()
                    dpd.range = propAnn.range()
                }
                if (!dpd.range) {
                    ConstrainedProperty cp = domainClass.constrainedProperties[prop.name]
                    dpd.isNullable = cp.nullable
                    if (cp.inList) {
                        dpd.range = "in ${cp.inList.toString()}"
                    } else if (cp.min && cp.max) {
                        dpd.range = "between ${cp.min} and ${cp.max}"
                    } else if (cp.min) {
                        dpd.range = "min ${cp.min}"
                    } else if (cp.max) {
                        dpd.range = "max ${cp.max}"
                    } else if (cp.maxSize) {
                        dpd.range = "maxSize ${cp.maxSize}"
                    } else if (cp.minSize) {
                        dpd.range = "minSize ${cp.minSize}"
                    }
                }
                domainClasses[domainClassName].domainProperties << dpd
            }
        }

        def cd = new ControllerDocumentation(controllerClass: controller)

        ApiDescription desc = controller.clazz.getAnnotation(ApiDescription)
        if (desc) {
            cd.name = desc.name()
            cd.plural = desc.plural()
            cd.description = desc.description()
        }
        cd.name = cd.name ?: controller.naturalName
        cd.plural = cd.plural ?: controller.naturalName
        cd.logicalName = controller.logicalPropertyName

        cd.actions = []

        def translation = ['domainClass.name': domainClasses[domainClassName].name, 'domainClass.plural': (domainClasses[domainClassName].plural ?: domainClasses[domainClassName].name)]

        def methods = getSortedMethods(controller.clazz)
        def methodsToUris = [:]
        for (String uri in controller.getURIs()) {
            def methodName = controller.getMethodActionName(uri)
            if (!methodsToUris[methodName]) {
                methodsToUris[methodName] = []
            }
            methodsToUris[methodName] << uri
        }
        def uri2method = [:]
        for (methodName in methodsToUris.keySet()) {
            def shortestUrl = methodsToUris[methodName].sort({ it.size() })[0]
            uri2method[shortestUrl] = methodName
        }
        for (uri in uri2method.keySet().sort()) {
            def methodName = uri2method[uri]
            Method method = methods.find({ it.name == methodName })
            if (!method) {
                continue
            }

            def cad = new ControllerActionDocumentation(uri: uri, methodName: methodName, parameters: [], responses: [])

            // First check if it's a RestfulController
            if (RestfulController.isAssignableFrom(controller.clazz)) {
                if (methodName == 'index' || methodName == 'show') {
                    cad.httpMethod = 'get'
                } else if (methodName == 'save') {
                    cad.httpMethod = 'post'
                } else if (methodName == 'update') {
                    cad.httpMethod = 'put'
                } else if (methodName == 'delete') {
                    cad.httpMethod = methodName
                }
            }

            // Next, check for annotations. This could override the above
            if (method.isAnnotationPresent(ApiIgnore)) {
                continue
            } else if (method.isAnnotationPresent(GetMethod)) {
                cad.httpMethod = 'get'
            } else if (method.isAnnotationPresent(PostMethod)) {
                cad.httpMethod = 'post'
            } else if (method.isAnnotationPresent(PutMethod)) {
                cad.httpMethod = 'put'
            } else if (method.isAnnotationPresent(DeleteMethod)) {
                cad.httpMethod = 'delete'
            } else if (cad.httpMethod == null) {
                // If nobody assigned a httpMethod, skip this method
                continue
            }

            ApiOperation ao = method.getAnnotation(ApiOperation)
            if (ao) {
                cad.operation = translateString(ao.value(), translation)
                cad.notes = translateString(ao.notes(), translation)
                cad.sample = translateString(ao.sample(), translation)
            }

            ApiResponses ar = method.getAnnotation(ApiResponses)
            ar?.value().each { ApiResponse arr ->
                cad.responses << new ControllerActionResponseDocumentation(code: arr.code(), reason: translateString(arr.message(), translation))
            }

            def paramTypes = method.getParameterTypes()
            method.getParameterAnnotations().eachWithIndex { pAnns, i ->
                ApiParam ap = pAnns.find({ it instanceof ApiParam })
                ApiParams aps = pAnns.find({ it instanceof ApiParams })

                def params = aps?.value() ?: []
                if (ap) {
                    params << ap
                }

                params.each { it ->
                    def capd = new ControllerActionParameterDocumentation()
                    capd.name = it.name()
                    capd.description = translateString(it.value(), translation)
                    capd.isRequired = it.required()
                    capd.range = it.allowableValues()
                    capd.defaultValue = it.defaultValue()
                    capd.type = paramTypes[i]
                    cad.parameters << capd
                }
            }

            if (RestfulController.isAssignableFrom(controller.clazz)) {
                // make some special tweaks, if this is a default RestfulController
                if (methodName == 'index') {
                    if (!cad.parameters.find({ it.name == 'max' })) {
                        def capd = new ControllerActionParameterDocumentation()
                        capd.name = 'max'
                        capd.description = 'maximum count of results returned'
                        capd.isRequired = false
                        capd.range = "> 1"
                        capd.type = Long
                        cad.parameters << capd
                    }
                } else if (methodName == 'show') {
                    cad.outputDataType = domainClass.clazz
                } else if (methodName == 'save') {
                    cad.inputDataType = domainClass.clazz
                } else if (methodName == 'update') {
                    cad.inputDataType = domainClass.clazz
                    cad.outputDataType = domainClass.clazz
                } else if (methodName == 'delete') {
                }
            }

            cd.actions << cad
        }
        if (cd.actions) {
            domainClasses[domainClassName].controller = cd
        }
    }

    static getSortedMethods(Class aClass) {
        def r = []
        aClass.getDeclaredMethods().each { m ->
            r << m
        }
        if (aClass.superclass) {
            r.addAll getSortedMethods(aClass.superclass)
        }
        return r
    }

    static String translateString(String input, Map translations) {
        String output = input
        translations.each { k, v ->
            output = output.replaceAll('\\#\\{' + k + '\\}', v)
        }
        return output
    }
}
