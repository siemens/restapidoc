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

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.artefact.Artefact
import grails.persistence.Entity
import grails.rest.RestfulController
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import grails.transaction.Transactional
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
import spock.lang.Specification

/**
 * @author Derk Muenchhausen
 * @author Kai Toedter
 * @since 0.1
 */
@TestMixin(GrailsUnitTestMixin)
@TestFor(ProductController)
@Mock([Product,ProductController])
class ApiDocumentationServiceSpec extends Specification {


    void "Test that DomainDocumentation includes everything from a DomainClass"() {
        given: "a DomainClass Product"
            Map<String, DomainDocumentation> domainClasses = [:]
            def grailsApplication = GrailsUnitTestMixin.grailsApplication

        when: "registering them for documentation"
            grailsApplication.controllerClasses.each { it ->
                ApiDocumentationService.addDomainAndControllerClass(it, grailsApplication, domainClasses)
            }
            def doc = domainClasses.get("Product")

        then: "DomainDocumentation must not be empty"
            domainClasses.size() > 0

        then: "DomainDocumentation must be correct"
            doc instanceof DomainDocumentation
            doc.description == "Health Products"
            doc.name == "HProduct"
            doc.plural == "HProducts"
            doc.description == "Health Products"

        then: "DomainDocumentation must not include property privateReference"
            !doc.domainProperties.findAll { it -> it.name == "privateReference" }

        when: "checking property numberInStock in detail"
            def prop = doc.domainProperties.find { it -> it.name == "numberInStock" }

        then: "range must be correct"
            prop != null
            prop.range == "between 1 and 20"

    }

    void "Test that DomainDocumentation includes everything from a Controller"() {
        given: "a Product DomainClass and a ProductController"
            Map<String, DomainDocumentation> domainClasses = [:]
            def grailsApplication = GrailsUnitTestMixin.grailsApplication

        when: "registering it for documentation"
            grailsApplication.controllerClasses.each { it ->
                ApiDocumentationService.addDomainAndControllerClass(it, grailsApplication, domainClasses)
            }
            def controller = domainClasses.get("Product").controller

        then: "ControllerDocumentation must be set"
            controller instanceof ControllerDocumentation
            controller.name == "Product Controller"
            controller.description=="Health Products Controller"

        when: "checking method index"
            def indexMethod = controller.actions.find {it.methodName == "index"}

        then: "parameter max and q must exist"
            indexMethod.methodName == "index"
            indexMethod.parameters[0].name == "max"
            indexMethod.parameters[1].name == "q"

        when: "checking method show"
            def showMethod = controller.actions.find {it.methodName == "show"}

        then: "parameter id must exist"
            showMethod.parameters[0].name == "id"

    }

    void "Test that response definition of Domain Property is correctly extracted"() {
        given: "a Product DomainClass and a ProductController"
            Map<String, DomainDocumentation> domainClasses = [:]
            def grailsApplication = GrailsUnitTestMixin.grailsApplication

        when: "registering it for documentation"
            grailsApplication.controllerClasses.each { it ->
                ApiDocumentationService.addDomainAndControllerClass(it, grailsApplication, domainClasses)
            }
            def controller = domainClasses.get("Product").controller

        and: "extracting the response code of method update"
            def method = controller.actions.find {it.methodName == "update"}
            def responses = method.responses

        then: "response code 404 must be included"
            responses.find {it.code == 404} != null

    }

}


@Entity
@ApiDescription(name="HProduct", plural="HProducts", description = "Health Products")
class Product {
    @ApiProperty(description = "the Product name")
    String name

    @ApiProperty(description = "available count of products in stock")
    Integer numberInStock

    @ApiIgnore
    Integer privateReference

    Category category

    static embedded = ['category']

    static constraints = {
        numberInStock min: 1, max: 20
    }
}

@Entity
class Category {
    @ApiProperty(description = "available count of products in stock")
    String name

    static constraints = {
        name inList: ["Plaster", "Cream", "Injection"]
    }
}

@Artefact("Controller")
@ApiDescription(description="Health Products Controller")
class ProductController extends RestfulController {
    static responseFormats = ['hal','json']

    ProductController() {
        super(Product)
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

}

