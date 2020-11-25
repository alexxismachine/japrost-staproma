<!DOCTYPE html>
<html>
<head>
<title>${title} (FM)</title>
<link type='text/css' href='style.css' rel='stylesheet'/>
<meta http-equiv='content-type' content='text/html; charset=UTF-8'/>
<style>
<#-- START: configure style for priority switches -->
<#list 1..9 as prio>
.priority${prio} {
  max-height: 0;
  overflow: hidden;
}
<#list 2..6 as level>
#prio${prio}:checked ~h${level}.priority${prio} {
  max-height: 100%;
}
</#list>		
#prio${prio}:checked ~ul li.priority${prio} {
  max-height: 100%;
}
</#list>
<#-- END: configure style for priority switches -->
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
<#-- START: priority switches -->
<#list 1..9 as prio>
<input type='checkbox' id='prio${prio}' ${(prio < 5)?then('checked','')}>
<label class='priority${prio}' for='prio${prio}'>Prio ${prio}</label>
</#list>		
<#-- END: priority switches -->
<#-- START: contents -->
<#macro printLeafTask task level openUl inBlock>
  <#-- <p>printLeafTask ${task} ${level} ${openUl?c} ${inBlock?c} : ${task.description}</p>-->
  <li class='priority${task.priority}'>${task.description}</li>
</#macro>

<#macro printOtherTask task level openUl inBlock>
  <#-- <p>printOtherTask ${task} ${level} ${openUl?c} ${inBlock?c} : ${task.description}</p>-->
  <#assign prioClasses><#list task.priorities() as prio>priority${prio} </#list></#assign>
  <h${level} class='${prioClasses}'>${task.description}</h${level}>
</#macro>

<#macro walkSubTree task level openUl inBlock>
  <#-- <p>walkSubTree ${task} ${level} ${openUl?c} ${inBlock?c} : ${task.description}</p>-->
  <#if !task.isInState(taskStateFileName.taskState())>
    <#return>
  </#if>
  <#if (level > 0)>
    <#if task.hasChildren()>
      <@printOtherTask task=task level=level openUl=openUl inBlock=inBlock/>
      <ul>
      <#list task.iterator() as subTask>
        <@walkSubTree task=subTask level=(level+1) openUl=false inBlock=false/>
      </#list>
      </ul>
    <#else>
      <@printLeafTask task=task level=level openUl=openUl inBlock=inBlock/>
    </#if>
  <#else>
    <#list task.iterator() as subTask>
      <@walkSubTree task=subTask level=(level+1) openUl=false inBlock=false/>
    </#list>
  </#if>
</#macro>
<#assign level = 0>
<@walkSubTree task=tasks level=0 openUl=false inBlock=false/>
<#-- END: contents -->
</form>
<#-- START: footer -->
<div class='generationTime'>${.now}</div>
<#-- END: footer -->
</body>
</html>
