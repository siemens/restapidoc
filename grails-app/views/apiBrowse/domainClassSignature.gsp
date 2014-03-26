<table>
	<thead>
	<tr>
		<th>property</th>
		<th>description</th>
		<th>data type</th>
		<th>value range</th>
	</tr>
	</thead>
	<tbody>
	<g:each in="${domainClass.domainProperties}" var="dp">
		<tr>
			<td>
				${dp.name}
				<g:if test="${!dp.isNullable}"><span class="mandatory">*</span></g:if>
			</td>
			<td>${dp.description}</td>
			<td>
				<g:if test="${dp.isCollection}">
					List<${dp.type.simpleName}>
				</g:if>
				<g:else>
					${dp.type.simpleName}
				</g:else>
			</td>
			<td>${dp.range}</td>
		</tr>
	</g:each>
	</tbody>
</table>