<html>
<head>
    <title>API Browser</title>
    <meta name="layout" content="api"/>
</head>

<body>

<div id="swagger-ui-container" class="swagger-ui-wrap">
    <%--
    <div id="api_info" class="info">

        <div class="info_title">REST API</div>

        <g:if test="${params.q}">
            <div class="info_description">searched for '${params.q}'</div>
        </g:if>
 	</div>
       --%>

    <div id="resources_container" class="container">
        <g:include controller="apiBrowse" action="domainClasses"/>
    </div>
</div>

</body>
</html>
