<%@ page contentType="text/html" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="pfweb" uri="http://www.vbic.com/tags/pfweb"%>
<html>
<link rel="stylesheet" type="text/css" href="static/css/global-0.52.css?foo">
<link rel="stylesheet" type="text/css" href="static/css/navbar.css">


<head>

</head>
<body>

	<%@ include file="navbar.jsp"%>
	<br>
	<table border="1">
	<tr>
		<td width="10%">AEX</td>
		<td width="90%" colspan="3"><img src="http://charts.quoteweb.fr/chart/intraday?width=660&height=400&issueid=12272&type=2&count=0" width="700px" height="400px" /></td>
	</tr>
	<tr>
		<td width="10%">Gold</td>
		<td width="40"><img src="http://www.kitco.com/images/live/gold.gif" width="600px" height="350px" /></td>
		<td width="10%">Silver</td>
		<td width="40"><img src="http://www.kitco.com/images/live/silver.gif" width="600px" height="350px" /></td>
	</tr>
	<tr>
		<td width="10%">Platinum</td>
		<td width="40%"><img src="http://www.kitco.com/images/live/plati.gif" width="600px" height="350px" /></td>
		<td width="10%">Palladium</td>
		<td width="40%"><img src="http://www.kitco.com/images/live/plad.gif" width="600px" height="350px" /></td>
	</tr>
	
	
	
	</table>
	
	
</body>
</html>
