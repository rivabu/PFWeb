<%@ page contentType="text/html" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="pfweb" uri="http://www.vbic.com/tags/pfweb"%>


<html>
<head>

</head>
<body>


	<table border="0" align="left">
		<c:forEach items="${files}" var="file" varStatus="index">
			<tr align="left">
				<td colspan="4">
					<p STYLE="font-size: 10pt;">
						${file.FUNDNAME} ${file.LASTRATE} (${file.PROC_VERSCHIL}%)
				</td>
			</tr>
			<tr align="left">
				<td align="left" valign="top">
					<p STYLE="font-size: 8pt;">
						<c:forEach var="i" begin="0" end="9">
							<c:if test="${file.lastTenDays[i][2] eq 'highest'}">
								<span style="color: green">${file.lastTenDays[i][0]}&nbsp;${file.lastTenDays[i][1]}&nbsp;</span>
								<br />
							</c:if>
							<c:if test="${file.lastTenDays[i][2] eq 'lowest'}">
								<span style="color: red">${file.lastTenDays[i][0]}&nbsp;${file.lastTenDays[i][1]}&nbsp;</span>
								<br />
							</c:if>
							<c:if test="${file.lastTenDays[i][2] eq 'normal'}">
								${file.lastTenDays[i][0]}&nbsp;${file.lastTenDays[i][1]}&nbsp;<br />
							</c:if>
						</c:forEach>
					</p>

					</p></td>
				<td width="10px">&nbsp;</td>
				<td valign="top"><img alt=""
					src="http://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/PFImage?type=default&fund=${file.FUNDNAME}&dir=${dir}&turningPoint=${file.graphParameters[0][0]}&stepSize=${file.graphParameters[0][1]}&row=1&maxcolumns=22"><br />
				</td>
				<td valign="top"><img alt=""
					src="http://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/PFImage?type=default&fund=${file.FUNDNAME}&dir=${dir}&turningPoint=${file.graphParameters[1][0]}&stepSize=${file.graphParameters[1][1]}&row=1&maxcolumns=22"><br />
				</td>
			</tr>
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>
		</c:forEach>
	</table>
</body>

</html>
