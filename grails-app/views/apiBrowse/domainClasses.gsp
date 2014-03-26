<ul class="resources" id="resources">
	<g:each in="${domainClasses}" var="dc">
		<li class="resource">
			<div class="heading" onclick="jQuery('#domain-${dc.key}').toggle();">
				<h2>${dc.key} : ${dc.value.description}</h2>
			</div>
			<div id="domain-${dc.key}" style="display: none;">
				<!--TODO: could be lazy -->
				<g:include controller="apiBrowse" action="methods" params="${[domainClass: dc.key]}"/>
			</div>
		</li>
	</g:each>
</ul>