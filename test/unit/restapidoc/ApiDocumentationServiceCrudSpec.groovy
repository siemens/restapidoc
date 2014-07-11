/*
 * Copyright (c) Siemens AG, 2014
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

import grails.artefact.Artefact
import grails.persistence.Entity
import grails.rest.RestfulController
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import restapidoc.annotations.*
import spock.lang.Specification

/**
 * @author Derk Muenchhausen
 * @since 0.1.2
 */

@TestMixin(GrailsUnitTestMixin)
@TestFor(SimpleProductController)
@Mock([SimpleProduct,SimpleProductController])
class ApiDocumentationServiceCrudSpec extends Specification {

    /*
     * see https://github.com/siemens/restapidoc/issues/5
     */
    void "Test that ControllerDocumentation include method documentation for CRUD standard names even if no Annotation is present"() {
        given: "a simple DomainClass and a simple non REST Controller"
            Map<String, DomainDocumentation> domainClasses = [:]

        when: "registering it for documentation"
            grailsApplication.controllerClasses.each { it ->
                ApiDocumentationService.addDomainAndControllerClass(it, grailsApplication, domainClasses)
            }
            def controller = domainClasses.get("SimpleProduct").controller

        then: "ControllerDocumentation must be set"
            controller instanceof ControllerDocumentation
            controller.name == "Simple Product Controller"
            controller.description=="Simple Products Controller"

        when: "checking method index"
            def indexMethods = controller.actions.findAll {it.methodName == "index"}

        then: "parameter max and q must exist"
            indexMethods.size() == 1
            indexMethods[0].methodName == "index"
            indexMethods[0].parameters.size() == 2
            indexMethods[0].parameters[0].name == "max"
            indexMethods[0].parameters[1].name == "q"

        when: "checking method update"
            def updateMethods = controller.actions.findAll {it.methodName == "update"}

        then: "parameter id must exist"
            updateMethods.size() ==1

        when: "checking method delete"
            def deleteMethod = controller.actions.find {it.methodName == "delete"}

        then: "parameter id must exist"
            deleteMethod.parameters[0].name == "id"

    }
}

@Entity
@ApiDescription(name="Simple Product", plural="Simple Products", description = "Simple Product Description")
class SimpleProduct {
    @ApiProperty(description = "unique order number for simple customers")
    Integer orderNumber
}

@Artefact("Controller")
@ApiDescription(description="Simple Products Controller")
class SimpleProductController extends RestfulController {
    static responseFormats = ['hal','json']

    SimpleProductController() {
        super(SimpleProduct)
    }
    @ApiOperation(value = "List #{domainClass.plural}", notes = "returns a complete list of #{domainClass.name} objects")
    def index(@ApiParams(value = [
    @ApiParam(name = "max", value = "max count result entries to be fetched", allowableValues = "long", required = false),
    @ApiParam(name = "q", value = "query parameter e.g. q=remark:test", allowableValues = "long", required = false)
    ]) Integer max) {
    }

    @ApiOperation(value = "Create #{domainClass.name}", notes = "creates a new #{domainClass.name} object")
    def save() {
    }

    @ApiOperation(value = "Update #{domainClass.name}", notes = "changes a specific #{domainClass.name} object")
    @ApiResponses(value = [ @ApiResponse(code = 404, message = "#{domainClass.name} not found") ])
    def update(@ApiParam(name = "id", value = "ID of #{domainClass.name} to be updated", allowableValues = "long", required = true) String id) {
    }

    @ApiOperation(value = "Delete #{domainClass.name}", notes = "deletes a specific #{domainClass.name} object")
    @ApiResponses(value = [ @ApiResponse(code = 404, message = "#{domainClass.name} not found") ])
    def delete(@ApiParam(name = "id", value = "ID of #{domainClass.name} to be deleted", allowableValues = "long", required = true) String id) {
    }
}