<%@ page contentType="text/html" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<body>
<h2>Hello World!</h2>
</body>
<c:set var="name" value="expression"/>

<c:out value="${name}" /> ${name}
</html>

<%@ page contentType="text/html" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<table border="1">
<table>
  <c:forEach items="${entryList}" var="blogEntry">
    <tr><td align="left" class="blogTitle">
      <c:out value="${blogEntry.title}" escapeXml="false"/>
    </td></tr>
    <tr><td align="left" class="blogText">
      <c:out value="${blogEntry.text}" escapeXml="false"/>
    </td></tr>
  </c:forEach>
</table>


<tr>
<td>
Tomtom
</td >
<td>
<img alt="" src="PFImage?fund=tomtom&turningPoint=2&stepSize=1" >
</td >
<td>
<img alt="" src="PFImage?fund=tomtom&turningPoint=1&stepSize=1" >
</td >
<td>
<img alt="" src="PFImage?fund=tomtom&turningPoint=1&stepSize=1.5" >
</td >
<td>
<img alt="" src="PFImage?fund=tomtom&turningPoint=1&stepSize=2" >
</td >
</tr>
<tr>
<td>
Ahold
</td >
<td>
<img alt="" src="file://d:/PFdata/images/ahold_2_1.0.png" >
</td >
<td>
<img alt="" src="file://d:/PFdata/images/ahold_1_1.0.png" >
</td >
<td>
<img alt="" src="file://d:/PFdata/images/ahold_1_1.5.png" >
</td >
<td>
<img alt="" src="file://d:/PFdata/images/ahold_1_2.0.png" >
</td >
</tr>
</table>

</body>
</html>