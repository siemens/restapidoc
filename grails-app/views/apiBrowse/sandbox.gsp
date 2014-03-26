<h4>Request URL (${method})</h4>
<div class="block request_url">
    <pre>${url}</pre>
</div>
<h4>Response Code</h4>
<div class="block response_code">
    <pre>${statusCode}</pre>
</div>
<h4>Response Headers</h4>
<div class="block response_headers">
    <%-- <pre><g:join in="${respHeaders}" delimiter="_"/></pre> --%>
    <%--<pre>${respHeaders}</pre>--%>

    <table class="fullwidth">
        <tbody class="operation-status">
        <g:each in="${respHeaders}" var="respHeader">
            <tr>
                <td class="code">${respHeader.name}</td>
                <td class="code">${respHeader.value}</td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>
<h4>Response Body</h4>
<div class="block response_body">
    <pre>${respBody}</pre>
</div>
