package restapidoc

import grails.artefact.Artefact
import grails.persistence.Entity
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import restapidoc.annotations.ApiDescription
import restapidoc.annotations.ApiProperty
import spock.lang.Specification
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

/**
 * @author Derk Muenchhausen
 * @since 0.1.2
 */

@TestMixin(GrailsUnitTestMixin)
@TestFor(CorporateProductController)
@Mock([CorporateProduct,CorporateProductController])
class ApiDocumentationServiceSuperSpec extends Specification {
    void "Test that DomainDocumentation includes everything from a super DomainClass"() {
        given: "a super DomainClass CorporateProduct"
        Map<String, DomainDocumentation> domainClasses = [:]

        when: "registering them for documentation"
        grailsApplication.controllerClasses.each { it ->
            ApiDocumentationService.addDomainAndControllerClass(it, grailsApplication, domainClasses)
        }
        def doc = domainClasses.get("CorporateProduct")

        then: "DomainDocumentation must not be empty"
        domainClasses.size() > 0

        then: "DomainDocumentation must be correct"
        doc instanceof DomainDocumentation
        doc.description == "Corporate Product Description"
        doc.name == "Corporate Product"
        doc.plural == "Corporate Products"

        then: "own property orderNumber must be in domain doc"
            doc.domainProperties.find { it -> it.name == "orderNumber" }

        then: "super property name must be in domain doc"
            doc.domainProperties.find { it -> it.name == "name" }

    }
}

@Entity
@ApiDescription(name="Corporate Product", plural="Corporate Products", description = "Corporate Product Description")
class CorporateProduct extends Product {
    @ApiProperty(description = "unique order number for corporate customers")
    Integer orderNumber
}

@Artefact("Controller")
@ApiDescription(description="Corporate Products Controller")
class CorporateProductController {
    Class resource = CorporateProduct
}
