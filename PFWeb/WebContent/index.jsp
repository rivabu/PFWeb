<%@ page contentType="text/html" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<body>
<h2>Hello World!</h2>
</body>
<c:set var="name" value="expression"/>

<c:out value="${name}" /> ${name}
</html>
