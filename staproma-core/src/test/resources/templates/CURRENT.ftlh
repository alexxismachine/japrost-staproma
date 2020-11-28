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
<#macro printLeafTask task level>
  <#-- <p>printLeafTask ${task} ${level} ${openUl?c} ${inBlock?c} : ${task.description}</p>-->
  <li class='priority${task.priority}'>${task.description}</li>
</#macro>

<#macro printContainerTask task level>
  <#-- <p>printContainerTask ${task} ${level} : ${task.description}</p>-->
  <#assign prioClasses><#list task.priorities() as prio>priority${prio} </#list></#assign>
  <h${level} class='${prioClasses}'>${task.description}</h${level}>
</#macro>

<#macro processChildren task level>
  <#assign inUl = false>
  <#list task.iterator() as subTask>
    <#if !subTask.isContainer()>
      <#if !inUl>
        <ul>
        <#assign inUl = true>
      </#if>
      <@printLeafTask task=task level=level/>
      <#continue>
    </#if>
    <#if inUl>
      </ul>
      <#assign inUl = false>
    </#if>
    <@walkSubTree task=subTask level=(level+1)/>
  </#list>
    <#if inUl>
      </ul>
      <#assign inUl = false>
    </#if>
</#macro>

<#macro walkSubTree task level>
  <#-- <p>walkSubTree ${task} ${level} : ${task.description}</p>-->
  <#if !task.isInState(taskStateFileName.taskState())>
    <#return>
  </#if>
  <#if (level > 0)>
    <@printContainerTask task=task level=level/>
    <@processChildren task=task level=level/>
  <#else><#-- walk subtree of root taskt -->
    <@processChildren task=task level=level/>
  </#if>
</#macro>


<@walkSubTree task=tasks level=0/>
<#-- END: contents -->
</form>
<#-- START: footer -->
<div class='generationTime'>${.now}</div>
<#-- END: footer -->
</body>
</html>
