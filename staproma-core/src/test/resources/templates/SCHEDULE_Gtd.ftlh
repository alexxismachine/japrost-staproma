<!DOCTYPE html>
<html>
<head>
<title>${title} (FM)</title>
<link type='text/css' href='style.css' rel='stylesheet'/>
<meta http-equiv='content-type' content='text/html; charset=UTF-8'/>
<!-- including style.css not implemented -->		
</style>
</head>
<body>
<#-- START: navigation between files -->
<div class='navigation'>
<#list taskStateFileName.values() as file >
	<a href='ftl-${file.fileName()}'>${file.taskState()}</a> 
</#list>		
</div>
<#-- END: navigation between files -->
<hr/>
<div class='title'>${title} Items</div>
<hr/>
<form>
<#-- START: contents -->
<#macro renderParent task>
  <#if task.parent??>
  <#assign parent = task.parent>
    <#if parent.path??>
     : <a href='${parent.path}'>${parent.description}</a>
    <#else>
     : ${parent.description}
    </#if>
    <#if parent.parent??><#-- only traverse if parent.parent is not available -->
      <#if parent.parent.parent??><#-- only traverse if parent.parent is not ROOT -->
        <@renderParent parent/>
      </#if>
    </#if>
  </#if>
</#macro>

<#macro renderLeaf task>
  <#assign taskDate = task.description?keep_before(" ")>  
  <#assign taskDescription = task.description?keep_after(" ")>  
  <dt>${taskDate}</dt>
  <dd>${taskDescription}
  <#-- TODO parent path (with links) -->
  <@renderParent task/>
  </dd>
</#macro>

<#macro renderFlat>
  <#list leafsFlat?sort_by("description") as leaf>
    <#if !leaf.isInState(taskStateFileName.taskState())>
      <#continue>
    </#if>
    <@renderLeaf task=leaf/>
  </#list>
</#macro>
<dl>

<#if !tasks.isContainer()><#-- root is not a container ??? -->
  <@renderLeaf task=tasks/>
<#else>
  <@renderFlat/>
</#if>

</dl>
<#-- END: contents -->
</form>
<#-- START: footer -->
<div class='generationTime'>${.now}</div>
<#-- END: footer -->
</body>
</html>
