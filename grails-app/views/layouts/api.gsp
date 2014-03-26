<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<title><g:layoutTitle default="API Browser"/></title>
	<g:javascript src="jquery-1.11.0.min.js"/>
	<link type="text/css" rel="stylesheet" media="screen" href="${resource(dir: 'css', file: 'api.css')}"/>
	<g:layoutHead/>
</head>

<body>

<%--
<div id="header">
	 <div class="swagger-ui-wrap"> --%>
		<%-- <g:link controller="apiBrowse" action="index" elementId="logo">REST API</g:link> --%>

        <%--
         <g:form controller="apiBrowse" action="index" name="api_selector">
             <div class="input"><input type="text" name="q" id="input_apiKey" placeholder="search..."></div>

             <div class="input"><a href="#" id="explore" onclick="jQuery('#api_selector').submit();">Explore</a></div>
         </g:form>
         --%>
	<%-- </div>
</div>
<div class="swagger-ui-wrap message-success" id="message-bar">${(flash.message ?: '')}</div>
--%>
<g:layoutBody/>

</body>
</html>