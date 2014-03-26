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

import grails.persistence.Entity
import grails.test.mixin.Mock
import restapidoc.annotations.ApiDescription
import restapidoc.annotations.ApiIgnore
import restapidoc.annotations.ApiProperty
import spock.lang.Ignore
import spock.lang.Specification
import grails.test.mixin.TestFor

import java.text.SimpleDateFormat

/**
 * @author Derk Muenchhausen
 * @since 0.1
 */
@TestFor(PetController)
@Mock([Pet,Food])
class DocumentedRestfulControllerSpec extends Specification {

    void "Test that calling getParametersToBind with simple values returns the expected values" () {
        given: "sample data in params map"
            controller.params['id']=4711
            controller.params['name'] = 'Duck'

        when: "getParametersToBind is executed on controller"
            def data = controller.getParametersToBind()

        then: "the result must be correct"
            data == [id: 4711, name: 'Duck']

    }

    void "Test that calling getParametersToBind with ignored properties are left out" () {
        given: "sample data in params map with an ignored property"
            controller.params['id']=4711
            controller.params['name'] = 'Duck'
            controller.params['soldCount'] = 17

        when: "getParametersToBind is executed on controller"
            def data = controller.getParametersToBind()

        then: "the result must not include soldCount"
            data == [id: 4711, name: 'Duck']
    }

    void "Test that calling getParametersToBind with unknown properties are left out" () {
        given: "sample data in params map with an ignored property"
            controller.params['id']=4711
            controller.params['name'] = 'Duck'
            controller.params['unknown'] = 17

        when: "getParametersToBind is executed on controller"
            def data = controller.getParametersToBind()

        then: "the result must not include soldCount"
            data == [id: 4711, name: 'Duck']
    }

    void "Test that calling getParametersToBind with a Date field with TZ in ISO8601 returns the expected values" () {
        given: "a sample date with TZ"
            def date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse("2014-03-10T17:12:01+0100")

        when: "getParametersToBind is executed with a date value"
            controller.params['available'] = '2014-03-10T17:12:01+01:00'
            def data = controller.getParametersToBind()

        then: "the result must be correct"
            data == [available: date]
    }

    void "Test that calling getParametersToBind with a Date field with format returns the expected values" () {
        given: "a sample date - format"
            def date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2014-03-10T17:12:01")

        when: "getParametersToBind is executed with a date value with format"
            controller.params['available'] = [value: '2014-03-10 17:12:01', format: 'yyyy-MM-dd HH:mm:ss']
            def data = controller.getParametersToBind()

        then: "the result must be correct"
            data == [available: date]
    }


    // not implemented yet
    @Ignore
    void "Test that calling getParametersToBind with complex values returns the expected values" () {
        given: "map in map sample data in params"
            controller.params['id']=4711
            controller.params['name'] = 'Duck'
            controller.params['favoriteFood'] = [name: 'wheat', price: 12]

        when: "getParametersToBind is executed on controller"
            def data = controller.getParametersToBind()

        then: "the result must be correct"
            data == [id: 4711, name: 'Duck', favoriteFood: [name: 'wheat', price: 12]]

    }

}

@ApiDescription(description="Pets and Animals")
class PetController extends DocumentedRestfulController  {
    PetController() {
        super(Pet)
    }
}

@Entity
class Pet {
    @ApiProperty(description = "name of the animal")
    String name

    @ApiProperty(description = "available in the shop since")
    Date available

    @ApiIgnore
    Integer soldCount

    @ApiProperty(description = "favorite Food")
    Food favoriteFood
}

@Entity
class Food {
    @ApiProperty(description = "Animal Food")
    String name

    @ApiProperty(description = "price per package for day portion")
    Integer price
}
