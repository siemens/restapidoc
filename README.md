restapidoc [![Build Status](https://travis-ci.org/siemens/restapidoc.svg?branch=master)](https://travis-ci.org/siemens/restapidoc)
==========

This is a RESTful API documentation plugin for the [Grails][Grails] web application framework.
Very much inspired by [Swagger][Swagger] API documentation, this plugin reuses the available information of
Grails Domain classes and Controllers to minimize documentation effort and to improve consistency.
This approach aims to be a deeper and less narrative Grails integration than the original [Swagger][Swagger] JAX-RS idea. It
* is extending the Grails RestController
* is using Grails HalRenderer
* uses Annotations just for documentation purpose and not for semantic information
* is as concise as possible

[Grails]: http://grails.org/
[plugins]: http://grails.org/plugins/
[Swagger]: https://github.com/wordnik/swagger-core

**Current Version 0.1.4**

![Grails restapidoc](https://github.com/siemens/restapidoc/blob/master/screenshot1.png?raw=true)


Quick Start
-----------
* it is recommended to use Grails >= 2.3.7. It is best tested with Grails 2.4.4. Please install it as required by Grails or use the grailsw wrapper.
* EITHER Use the Binary by adding restapidoc it to your grails-app/conf/BuildConfig.groovy:
    ```
    compile ":restapidoc:0.1.4"
    ```
* OR: Download this project by git clone e.g. to directory restapidoc.
    * cd restapidoc
    * to test it:
    ```
    grails test-app --echoOut
    ```
    * The project provides a Grails wrapper, so if you don't have Grails installed, run the tests with
    ```
    grailsw test-app --echoOut
    ```

    * Create a new Grails project parallel to restapidoc. Edit BuildConfig.groovy, add:
    ```
	grails.plugin.location.'restapidoc'="../restapidoc"
    ```

* edit your spring/resources.groovy, add e.g. HalJsonRenderer:

```Groovy
	import grails.rest.render.hal.HalJsonCollectionRenderer
	import grails.rest.render.hal.HalJsonRenderer
	beans = {
		halPCollectionRenderer(HalJsonCollectionRenderer, Pet)
		halPRenderer(HalJsonRenderer, Pet)
	}
```

* add documentation to your Grails Domains e.g.:

```Groovy
	import restapidoc.annotations.ApiDescription
	import restapidoc.annotations.ApiProperty

	@ApiDescription(description = "Endangered Animals")
	class Pet {
		@ApiProperty(description = "Binomial name")
    	String name 
	}
```

* add documentation to your Grails Controller. If you want some generic Documentation for RestfulControllers, you can extend DocumentedRestfulController e.g. here PetController:

```Groovy
	import restapidoc.DocumentedRestfulController
	import restapidoc.annotations.ApiDescription

	@ApiDescription(description="Unrealistic shop for endangered animals")
	class PetController extends DocumentedRestfulController  {
		static responseFormats = ['hal','json']
		PetController() {
			super(Pet)
		}
	}
```

* start your Grails app and open the Api Controller, e.g. http://localhost:8080/HalTest/apiBrowse/index
* you can find this sample as fully running Grails Application under https://github.com/muenchhausen/HalTest

API Documentation
-----------------
The following Annotations - similar like Swagger - are available:
* ApiIgnore: element will be ignored in documentation
* ApiDescription: used for Domain classes and Controller documentation
* ApiProperty: domain property
* ApiParam: paramter of a controller
* ApiParams: a list of ApiParam
* ApiOperation: controller operation
* ApiResponse: controller operation response
* ApiResponses: List of ApiResponse
* DeleteMethod / GetMethod / PostMethod / PutMethod: marks controller operation as RESTful CRUD 


History
-------
* 0.1.4 Grails 2.4.4 tested
* 0.1.3 Grails 2.4.2 tested, issue#4 and issue#5, inherited domain attributes
* 0.1.2 Grails 2.4.0 support
* 0.1.1 publish binary version of this plugin
* 0.1 initial version

License
-------

Copyright (c) Siemens AG, 2013

This restapidoc plugin is licensed under the terms of the [Apache License, Version 2.0][Apache License, Version 2.0].
[Apache License, Version 2.0]: http://www.apache.org/licenses/LICENSE-2.0.html
