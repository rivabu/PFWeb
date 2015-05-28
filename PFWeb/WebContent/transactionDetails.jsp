<%@ page contentType="text/html" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
  
  
<html>
<head>
<style>
table {
    border: 1px solid #666;
    width: 100%;
    margin: 20px 0 20px 0 !important;
}
</style>
</head>
<body>

<table width="100%">
<tr >
<td>Id</td>
<td>:&nbsp;</td>
<td>${trans.buyId} 
</td>
</tr>
<tr>
<td>Real Fundname</td>
<td>:&nbsp;</td>
<td>${trans.realFundName} 
</td>
<td>&nbsp;</td>
<td>${trans.type} </td>
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
<img class="decoded" src="/PFWeb/PFImage?id=${trans.buyId}&amp;type=trans&amp;turningPoint=2&amp;stepSize=2&amp;row=1&amp;maxcolumns=60">
<br />
<br />

</body>
</html>

