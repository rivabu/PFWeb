<%@ page contentType="text/html" pageEncoding="utf-8"%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib  prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>  
<html>
  <link rel="stylesheet" type="text/css" href="static/css/global-0.52.css?foo"> 
  <link rel="stylesheet" type="text/css" href="static/css/overlay.css">
 <link rel="stylesheet" type="text/css" href="static/css/navbar.css"> 
  
  
<head>
<script src="static/js/jquery.tools.min.js">
</script>
<style>
	/* use a semi-transparent image for the overlay */ 
	#overlay { background-image:url(http://static.flowplayer.org/img/overlay/transparent.png); 
		color:#efefef; 
		height:450px; 
	} 
	/* container for external content. uses vertical scrollbar, if needed */ 
	div.contentWrap { 
		height:441px; 
		overflow-y:auto; 
	}
</style>
</head>
<body onload="loaded();">
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
			<c:if test="${index.first || ((index.count % element_per_row) == 0)}" >
				<td width="25%" valign="top">
			</c:if>	
			${index.count} <a href="#${file.FUNDNAME}"> ${file.FUNDNAME}</a><br />
			<c:if test="${index.last || ((index.count % element_per_row) == (element_per_row - 1))}" >
				</td>
			</c:if>	
		</c:forEach>
	</tr>
</table>

<table >
	<tr>
		<td>
		<img alt="" src="RSIImage?&dir=${dir}">
		</td>
	</tr>
</table>

<table >
	<tr>
		<td>
		<img alt="" src="MatrixImage?&dir=${dir}&type=higherlower">
		</td>
	</tr>
	<tr>
		<td>
		<br />
		</td>
	</tr>
	<tr>
		<td>
		<img alt="" src="MatrixImage?&dir=${dir}&type=updown">
		</td>
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
		<tr>
			<td width="300" valign="top">
				<p>
					<a id="${file.FUNDNAME}" name="${file.FUNDNAME}"></a>
					<c:if test="${((index.count % 2) == 0)}" >
						<a href="#top">top</a><br /><br />
					</c:if>	
					${file.FUNDNAME} ${file.LASTRATE} (${file.PROC_VERSCHIL}%) <br />
					${file.LASTDATE} <br /> <br />
					<a href="overlay.jsp?fund=${file.FUNDNAME}&dir=${dir}&turningPoint=2&stepSize=0.75&row=1" rel="#overlay" style="text-decoration: none;">
				  	modelscore
					</a><br />
					<p STYLE="font-size: 8pt;">
						<c:forEach var="i" begin="0" end="9">
							<c:if test="${file.lastTenDays[i][2] eq 'highest'}" >
								<span style="color: green">${file.lastTenDays[i][0]}&nbsp;${file.lastTenDays[i][1]}&nbsp;</span><br />
							</c:if>	
							<c:if test="${file.lastTenDays[i][2] eq 'lowest'}" >
								<span style="color: red">${file.lastTenDays[i][0]}&nbsp;${file.lastTenDays[i][1]}&nbsp;</span><br />
							</c:if>	
							<c:if test="${file.lastTenDays[i][2] eq 'normal'}" >
								${file.lastTenDays[i][0]}&nbsp;${file.lastTenDays[i][1]}&nbsp;<br />
							</c:if>	
						</c:forEach>
					</p>
					<p>
					
					</p>
				</p>
			</td>
			<td><img alt="" 
				src="PFImage?fund=${file.FUNDNAME}&dir=${dir}&turningPoint=${file.graphParameters[0][0]}&stepSize=${file.graphParameters[0][1]}&row=1"><br />
				<img alt="" src="RSIImage?fund=${file.FUNDNAME}&dir=${dir}">
			</td>
			<td><img alt="" 
				src="PFImage?fund=${file.FUNDNAME}&dir=${dir}&turningPoint=${file.graphParameters[1][0]}&stepSize=${file.graphParameters[1][1]}&row=1">
			</td>
			<td><img alt="" 
				src="PFImage?fund=${file.FUNDNAME}&dir=${dir}&turningPoint=${file.graphParameters[2][0]}&stepSize=${file.graphParameters[2][1]}&row=2">
			</td>
			<td><img alt="" 
				src="PFImage?fund=${file.FUNDNAME}&dir=${dir}&turningPoint=${file.graphParameters[3][0]}&stepSize=${file.graphParameters[3][1]}&row=3">
			</td>
			<td><img alt="" 
				src="PFImage?fund=${file.FUNDNAME}&dir=${dir}&turningPoint=${file.graphParameters[4][0]}&stepSize=${file.graphParameters[4][1]}&row=4">
			</td>
		</tr>
	</c:forEach>
</table>
<a href="#top">top</a>

<!-- overlayed element -->
<div class="apple_overlay" id="overlay">
	<a class="close">
	</a>
	<!-- the external content is loaded inside this tag -->
	<div class="contentWrap">
	</div>
</div>


<script>


	
$(function() {
// if the function argument is given to overlay,
// it is assumed to be the onBeforeLoad event listener
$("a[rel]").overlay({
mask: 'white',
effect: 'apple',
onBeforeLoad: function() {
// grab wrapper element inside content
var wrap = this.getOverlay().find(".contentWrap");
// load the page specified in the trigger
wrap.load(this.getTrigger().attr("href"));
}
});
});
</script>
</html>
