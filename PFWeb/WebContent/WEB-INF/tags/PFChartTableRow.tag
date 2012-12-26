<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ attribute name="file" required="true" type="java.util.Properties"%>
<%@ attribute name="dir" required="true" %>
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
