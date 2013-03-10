<%@ page contentType="text/html" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="pfweb" uri="http://www.vbic.com/tags/pfweb"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
<head>
<link rel="stylesheet" href="static/css/displaytag.css" type="text/css">
<link rel="stylesheet" href="static/css/navbar.css" type="text/css">
<link rel="stylesheet" href="static/css/screen.css" type="text/css">
<link rel="stylesheet" href="static/css/site.css" type="text/css">
<link rel="stylesheet" href="static/css/lightbox.css" type="text/css">
<script src="static/js/jquery.js">
</script>
<script language="javascript">

$( function () {
	$("a.lightbox").click(function(event) {
		event.preventDefault();
		$("#content").load(this.href);
		$("div#lightbox").fadeIn('slow');
		$("div#content").fadeIn('slow');
	});
	
	$("div#lightbox").css('cursor', 'pointer').click(function() {
		$("div#lightbox").fadeOut('slow');
		$("div#content").fadeOut('slow');
	});

	$(document).keydown(function(e) {
		if (e.keyCode == '27') {
			$("div#lightbox").fadeOut('slow');
			$("div#content").fadeOut('slow');
		}
	});
		
});



</script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Transactions</title>
</head>
<body>
	<%@ include file="navbar.jsp"%>
	<display:table decorator="org.rients.com.pfweb.controllers.RientsWrapper" name="sessionScope.transactions" pagesize="25"
		export="true" sort="list" defaultsort="1" defaultorder="descending">
		<display:column property="startDate" title="Start Date" sortable="true" headerClass="sortable" />
		<display:column property="endDate" title="End Date" sortable="true" headerClass="sortable" />
		<display:column property="pieces" title="Pieces" sortable="true" headerClass="sortable" />
		<display:column property="fundName" title="Fund" sortable="true" headerClass="sortable" />
		<display:column property="buyId" title="BuyId" sortable="true" headerClass="sortable" />
		<display:column property="startRate" title="Start Rate" sortable="true" headerClass="sortable" />
		<display:column property="endRate" title="End Rate" sortable="true" headerClass="sortable" />
		<display:column property="type" title="Type" sortable="true" headerClass="sortable" />
		<display:column property="scorePercBD" title="Score perc" sortable="true" headerClass="sortable" />
		<display:column property="scoreAbsBD" title="Score abs" sortable="true" headerClass="sortable" />
		<display:column property="buyId"  title="Link" escapeXml="false" decorator="org.rients.com.pfweb.controllers.HREFFormatter"/>
	</display:table>
		<br />
	<table>
		<tr>
			<td valign="top"><img alt="" src="TransactionImage"></td>
		</tr>
	</table>
	<div id="lightbox"></div>
    <div id="content">Hier komt de content</div>
	
</body>
</html>