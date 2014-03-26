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

/**
 * @author Derk Muenchhausen
 * @author Stephan Linkel
 * @since 0.1
 */
class ControllerActionDocumentation {
	String uri
	String methodName
	String httpMethod
	String operation
	String notes
    String sample

	def inputDataType
	def outputDataType
	List<ControllerActionParameterDocumentation> parameters
	List<ControllerActionResponseDocumentation> responses

	String getUriView() {
		if (parameters.find({ it.name == 'id' && it.isRequired == true })) {
			def u = uri
			if (u.endsWith('/')) {
				return "${u}{id}"
			} else {
				return "${u}/{id}"
			}
		}
		return uri
	}
}