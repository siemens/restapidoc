<ul class="endpoints">
    <g:each in="${controllerClasses}" var="controllerDoc">
        <p>${controllerDoc.description}</p>
        <g:each in="${controllerDoc.actions}" var="actionDoc">
            <li class="endpoint">
                <ul class="operations">
                    <li class="operation ${actionDoc.httpMethod}">
                        <div class="heading" onclick="jQuery('#details-${controllerDoc.logicalName}-${actionDoc.methodName}').toggle();">
                            <h3>
                                <span class="http_method">${actionDoc.httpMethod}</span>
                                <span class="path">${actionDoc.uriView}</span>
                            </h3>
                            <ul class="options">
                                <li>${actionDoc.operation}</li>
                            </ul>
                        </div>

                        <div class="content" id="details-${controllerDoc.logicalName}-${actionDoc.methodName}" style="display: none;">
                            <h4>Implementation Details</h4>

                            <p>${actionDoc.notes}</p>

                            <g:if test="${actionDoc.inputDataType || actionDoc.outputDataType}">
                                <h4>Model Schema</h4>
                                <div class="model-signature">
                                    <ul class="signature-nav">
                                        <g:if test="${actionDoc.inputDataType}">
                                            <li> <a class="description-link responseTab-${controllerDoc.logicalName}-${actionDoc.methodName}" onclick="jQuery('div.response-${controllerDoc.logicalName}-${actionDoc.methodName}').hide();jQuery('#response_desc-${controllerDoc.logicalName}-${actionDoc.methodName}').show();jQuery('.responseTab-${controllerDoc.logicalName}-${actionDoc.methodName}').removeClass('selected');jQuery(this).addClass('selected');">Request</a></li>
                                        </g:if>
                                        <g:if test="${actionDoc.outputDataType}">
                                            <li> <a class="snippet-link responseTab-${controllerDoc.logicalName}-${actionDoc.methodName}" onclick="jQuery('div.response-${controllerDoc.logicalName}-${actionDoc.methodName}').hide();jQuery('#response_sig-${controllerDoc.logicalName}-${actionDoc.methodName}').show();jQuery('.responseTab-${controllerDoc.logicalName}-${actionDoc.methodName}').removeClass('selected');jQuery(this).addClass('selected');">Response</a></li>
                                        </g:if>
                                    </ul>
                                    <div class="signature-container">
                                        <g:if test="${actionDoc.inputDataType}">
                                            <div class="description response-${controllerDoc.logicalName}-${actionDoc.methodName}" style="display: none;" id="response_desc-${controllerDoc.logicalName}-${actionDoc.methodName}">
                                                The Request Model Schema Class is ${actionDoc.inputDataType.name}. It has the following properties:
                                                <g:include controller="apiBrowse" action="domainClassSignature" params="${[dc: actionDoc.inputDataType]}"/>
                                            </div>
                                        </g:if>
                                        <g:if test="${actionDoc.outputDataType}">
                                            <div class="snippet response-${controllerDoc.logicalName}-${actionDoc.methodName}" style="display: none;" id="response_sig-${controllerDoc.logicalName}-${actionDoc.methodName}">
                                                The Response Model Schema Class is ${actionDoc.outputDataType.name}. It has the following properties:
                                                <g:include controller="apiBrowse" action="domainClassSignature" params="${[dc: actionDoc.outputDataType]}"/>
                                            </div>
                                        </g:if>
                                    </div>
                                </div>
                            </g:if>

                            <g:if test="${actionDoc.responses.size()}">
                                <h4>Error Status Code Documentation</h4>
                                <table class="fullwidth">
                                    <thead>
                                    <tr>
                                        <th>HTTP Status Code</th>
                                        <th>Reason</th>
                                    </tr>
                                    </thead>
                                    <tbody class="operation-status">
                                    <g:each in="${actionDoc.responses}" var="respDoc">
                                        <tr>
                                            <td width="15%" class="code">${respDoc.code}</td>
                                            <td>${respDoc.reason}</td>
                                        </tr>
                                    </g:each>
                                    </tbody>
                                </table>
                            </g:if>
                            <br/>
                            <div style="margin:0;padding:0;display:inline"></div>

                            <g:formRemote class="sandbox" method="post" controller="apiBrowse" action="sandbox" url="${[controller: 'apiBrowse', action: 'sandbox']}" update="sandbox-${controllerDoc.logicalName}-${actionDoc.methodName}" name="sandboxForm-${controllerDoc.logicalName}-${actionDoc.methodName}" before="jQuery('#loader-${controllerDoc.logicalName}-${actionDoc.methodName}').show();" after="jQuery('#loader-${controllerDoc.logicalName}-${actionDoc.methodName}').hide();">
                                <input type="hidden" name="restController" value="${controllerDoc.logicalName}"/>
                                <input type="hidden" name="restAction" value="${actionDoc.methodName}"/>
                                <input type="hidden" name="restMethod" value="${actionDoc.httpMethod}"/>

                                <g:if test="${actionDoc.parameters.size()}">

                                    <div style="margin:0;padding:0;display:inline"></div>

                                    <h4>URL Parameters</h4>
                                    <table class="fullwidth">
                                        <thead>
                                        <tr>
                                            <th style="width: 100px; max-width: 100px">Parameter</th>
                                            <th style="width: 310px; max-width: 310px">Value</th>
                                            <th style="width: 200px; max-width: 200px">Description</th>
                                            <th style="width: 100px; max-width: 100px">Parameter Type</th>
                                            <th style="width: 220px; max-width: 230px">Data Type</th>
                                        </tr>
                                        </thead>
                                        <tbody class="operation-params">
                                        <g:each in="${actionDoc.parameters}" var="paramDoc">
                                            <tr>
                                                <td class="code required">${paramDoc.name}</td>
                                                <td>
                                                    <input type="hidden" name="_param" value="${paramDoc.name}"/>
                                                    <input type="text" value=""
                                                           placeholder="${paramDoc.isRequired ? '(required)' : ''}"
                                                           name="${paramDoc.name}"
                                                           class="parameter ${paramDoc.isRequired ? 'required' : ''}">
                                                </td>
                                                <td>
                                                    <strong>${paramDoc.description}</strong>
                                                </td>
                                                <td>${paramDoc.type.simpleName}</td>
                                                <td><span class="model-signature">${paramDoc.range}</span></td>
                                            </tr>
                                        </g:each>
                                        </tbody>
                                    </table>
                                </g:if>
                                <g:if test="${actionDoc.inputDataType}">
                                    <h4>Post Field</h4>
                                    <g:textArea name="_postData" id="tryPost-${controllerDoc.logicalName}-${actionDoc.methodName}" value="${actionDoc.sample}" rows="5" cols="60"/>
                                </g:if>
                                <br/>
                                <div class="response-content-type">
                                    <div>
                                        <label>Response Content Type</label>
                                        <select name="_responseContentType">
                                            <option value=" application/hal+json">application/hal+json</option>
                                            <option value="application/json">application/json</option>
                                        </select>
                                    </div>
                                </div>
                                <br/>
                                <div style="margin:0;padding:0;display:inline"></div>
                                <div class="sandbox_header">
                                    <input type="submit" value="Try it out!" name="commit" class="submit">
                                    <g:img style="display:none" dir="images" file="throbber.gif" class="response_throbber" id="loader-${controllerDoc.logicalName}-${actionDoc.methodName}" alt="Throbber"/>
                                </div>
                            </g:formRemote>
                            <div class="response" id="sandbox-${controllerDoc.logicalName}-${actionDoc.methodName}"></div>
                        </div>
                    </li>
                </ul>
            </li>
        </g:each>
    </g:each>
</ul>