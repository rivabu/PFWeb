<%@ page contentType="text/html" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
  
  
<html>
<head>
  <link rel="stylesheet" type="text/css" href="static/css/global-0.52.css?foo"> 
</head>
<body>
<table>
<tr >
<td>Id</td>
<td>:&nbsp;</td>
<td>${trans.buyId} 
</td>
</tr>
<tr>
<td>Turbo</td>
<td>:&nbsp;</td>
<td>${trans.fundName} 
</td>
</tr>
<tr>
<td>Real Fundname</td>
<td>:&nbsp;</td>
<td>${trans.realFundName} 
</td>
</tr>
<tr>
<td>Period</td>
<td>:&nbsp;</td>
<td>${trans.startDate} -  ${trans.endDate}
</td>
</tr>
<tr>
<td>Score</td>
<td>:&nbsp;</td>
<td>${trans.scoreAbsStr} ( ${trans.scorePercStr}% )
</td>
</tr>
</table>
   
<br />
<img class="decoded" src="http://127.0.0.1:8060/PFWeb/PFImage?id=${trans.buyId}&amp;type=trans&amp;turningPoint=1&amp;stepSize=0.5&amp;row=1&amp;maxcolumns=40">
<br />
<br />

</body>
</html>

