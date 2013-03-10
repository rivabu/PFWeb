<%@ page contentType="text/html" pageEncoding="utf-8"%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib  prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>  
<%@ taglib prefix="pfweb" uri="http://www.vbic.com/tags/pfweb"%>

<html>
  <link rel="stylesheet" type="text/css" href="static/css/global-0.52.css?foo"> 
 <link rel="stylesheet" type="text/css" href="static/css/navbar.css"> 
  
  
<head>

</head>
<body>
<%@ include file="navbar.jsp" %>




<a name="top"></a>
<c:forEach items="${dirs}" var="dir">
	<a href="Overview?dir=${dir}">${dir}</a><br/>
</c:forEach>
<br/>
<c:set var="element_per_row" value="${((fn:length(files) - (fn:length(files) % 4)) / 4) + 1}" />
<table >
	<tr>
		<c:forEach items="${files}" var="file" varStatus="index">
			<c:if test="${index.first || (((index.count - 1) % element_per_row) == 0)}" >
				<td width="25%" valign="top">
			</c:if>	
			${index.count} <a href="#${file.FUNDNAME}"> ${file.FUNDNAME}</a><br />
			<c:if test="${index.last || (((index.count - 1) % element_per_row) == (element_per_row - 1))}" >
				</td>
			</c:if>	
		</c:forEach>
	</tr>
</table>

<table>
<tr>
	<td>
	</td>
	<td>turningPoint=2 stepSize=0.75
	</td>
	<td>turningPoint=2 stepSize=1
	</td>
	<td>turningPoint=1 stepSize=1
	</td>
	<td>turningPoint=1 stepSize=1.5
	</td>
	<td>turningPoint=1 stepSize=2
	</td>
</tr>
	<c:forEach items="${files}" var="file" varStatus="index">
		<pfweb:PFChartTableRow file="${file}" dir="${dir}"/>
	</c:forEach>
</table>
<a href="#top">top</a>





</html>
