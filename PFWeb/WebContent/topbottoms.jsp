<%@ page contentType="text/html" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="pfweb" uri="http://www.vbic.com/tags/pfweb"%>
<html>
<link rel="stylesheet" type="text/css"
	href="static/css/global-0.52.css?foo">
<link rel="stylesheet" type="text/css" href="static/css/overlay.css">
<link rel="stylesheet" type="text/css" href="static/css/navbar.css">


<head>

</head>
<body>
	<%@ include file="navbar.jsp"%>
	<br />
	<table>
		<tr>
			<c:forEach items="${categories}" var="categorie">
				<td width="150px" valign="top">
					<a href="TopBottoms?topbottomscategorie=${categorie.naam}"><b>${categorie.naam}</b></a><br/>
					<c:forEach items="${categorie.items}" var="item">
						${item.naam}<br/>
					</c:forEach>
				</td>
			</c:forEach>
		</tr>
	</table>

		<table>
			<c:forEach items="${files}" var="file" varStatus="index">
				<pfweb:PFChartTableRow file="${file}" dir="${dir}"/>
			</c:forEach>
		</table>
</body>
</html>
