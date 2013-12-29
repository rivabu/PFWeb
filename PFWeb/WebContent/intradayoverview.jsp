<%@ page contentType="text/html" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<html>
<link rel="stylesheet" type="text/css"
	href="static/css/global-0.52.css?foo">
<link rel="stylesheet" type="text/css" href="static/css/overlay.css">
<link rel="stylesheet" type="text/css" href="static/css/navbar.css">


<head>

</head>
<body>
	<%@ include file="navbar.jsp"%>
	<a name="top"></a>
	<br />
	<table border="1">
		<tbody>
			<tr>
				<td>MAANDAG</td>
				<td>DINSDAG</td>
				<td>WOENSDAG</td>
				<td>DONDERDAG</td>
				<td>VRIJDAG</td>
			</tr>
			<c:forEach items="${matrix}" var="week">
				<c:forEach items="${week.value}" var="dag">
					<c:if test="${dag.key eq 1}">
	 					<tr>
					</c:if>
					<td width="300" valign="top">
						<c:if test="${not empty dag.value}">
		 					${dag.value}
		 					<img alt="" src="PFImage?fund=${dag.value}&dir=${dir}&turningPoint=1&stepSize=0.025">
		 				</c:if>
						</td>
					<c:if test="${dag.key eq 5}">
	 					</tr>
					</c:if>
				</c:forEach>
			</c:forEach>
		</tbody>
	</table>
	<a href="#top">top</a>

	<!-- overlayed element -->
	<div class="apple_overlay" id="overlay">
		<a class="close"> </a>
		<!-- the external content is loaded inside this tag -->
		<div class="contentWrap"></div>
	</div>
</html>
